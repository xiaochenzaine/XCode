package me.rerere.rikkahub.data.ai.tools

import com.xc.code.editor.agent.agent_edit_mode
import com.xc.code.editor.agent.agent_file_change_result
import com.xc.code.editor.agent.agent_text_replacement
import com.xc.code.editor.agent.agent_workspace_edit_type
import com.xc.code.editor.agent.agent_workspace_edit_request
import com.xc.code.editor.agent.agent_workspace_edit_result
import com.xc.code.editor.agent.editor_agent_edit_controller
import com.xc.code.editor.core.workspace_file_change_event
import com.xc.code.editor.core.workspace_file_change_notifier
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.DiffMetadata
import me.rerere.ai.ui.UIMessagePart
import me.rerere.ai.ui.toMetadata
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.data.repository.WorkspaceRepository
import me.rerere.rikkahub.utils.generateUnifiedDiff
import me.rerere.workspace.WorkspaceCommandResult
import me.rerere.workspace.WorkspaceFileEntry
import me.rerere.workspace.WorkspaceManager
import org.koin.java.KoinJavaComponent.getKoin
import java.util.Base64

private const val SHELL_TIMEOUT_MAX_SECONDS = 600L
private const val MAX_READ_FILE_BYTES = 8L * 1024 * 1024

val WorkspaceToolDefaultApprovals: Map<String, Boolean> = mapOf(
    "workspace_read_file" to false,
    "workspace_write_file" to false,
    "workspace_edit_file" to false,
    "apply_workspace_edits" to false,
    "workspace_shell" to true,
)

fun resolveWorkspaceToolApproval(name: String, overrides: Map<String, Boolean>): Boolean =
    overrides[name] ?: WorkspaceToolDefaultApprovals[name] ?: false

suspend fun createWorkspaceTools(
    workspaceId: String?,
    workspaceRepository: WorkspaceRepository,
    cwd: String? = null,
): List<Tool> {
    if (workspaceId.isNullOrBlank()) return emptyList()
    val approvalOverrides = workspaceRepository.getById(workspaceId)?.toolApprovalOverrides().orEmpty()
    fun needsApproval(name: String) = resolveWorkspaceToolApproval(name, approvalOverrides)

    val shellCwd = cwd?.removePrefix("/workspace/")?.removePrefix("/workspace")

    return listOf(
        createReadFileTool(workspaceId, ::needsApproval, workspaceRepository),
        createWriteFileTool(workspaceId, ::needsApproval, workspaceRepository),
        createApplyWorkspaceEditsTool(workspaceId, ::needsApproval, workspaceRepository),
        createEditFileTool(workspaceId, ::needsApproval, workspaceRepository),
        createShellTool(workspaceId, ::needsApproval, workspaceRepository, shellCwd),
    )
}

private val IMAGE_EXTENSIONS = setOf("png", "jpg", "jpeg", "gif", "webp", "bmp", "svg")

private fun String.isImagePath(): Boolean =
    substringAfterLast('.', "").lowercase() in IMAGE_EXTENSIONS

private fun createReadFileTool(
    workspaceId: String,
    needsApproval: (String) -> Boolean,
    workspaceRepository: WorkspaceRepository,
) = Tool(
    name = "workspace_read_file",
    description = """
        Read a file using the assistant's bound workspace Rootfs. Paths must be absolute inside Rootfs.
        Use /workspace for the workspace files area.
        Supports UTF-8 text files and image files (png, jpg, jpeg, gif, webp, bmp).
    """.trimIndent().replace("\n", " "),
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                putPathProperty(required = true)
            },
            required = listOf("path"),
        )
    },
    needsApproval = { needsApproval("workspace_read_file") },
    execute = {
        val path = it.jsonObject.absolutePath("path")
        if (path.isImagePath()) {
            workspaceRepository.readImageInRootfs(workspaceId, path)
        } else {
            val text = workspaceRepository.readTextInRootfs(workspaceId, path)
            listOf(
                UIMessagePart.Text(
                    buildJsonObject {
                        put("path", path)
                        put("text", text)
                    }.toString()
                )
            )
        }
    },
)

private fun createWriteFileTool(
    workspaceId: String,
    needsApproval: (String) -> Boolean,
    workspaceRepository: WorkspaceRepository,
) = Tool(
    name = "workspace_write_file",
    description = """
        Write a UTF-8 text file using the assistant's bound workspace Rootfs. Paths must be absolute inside Rootfs.
        Use /workspace for the workspace files area.
    """.trimIndent().replace("\n", " "),
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                putPathProperty(required = true)
                put("text", buildJsonObject {
                    put("type", "string")
                    put("description", "UTF-8 text content to write")
                })
                put("overwrite", buildJsonObject {
                    put("type", "boolean")
                    put("description", "Whether to overwrite an existing file. Defaults to true.")
                })
            },
            required = listOf("path", "text"),
        )
    },
    needsApproval = { needsApproval("workspace_write_file") || it.pathOutsideWritableRoots("path") },
    execute = {
        val params = it.jsonObject
        val path = params.absolutePath("path")
        val text = params.string("text") ?: error("text is required")
        val overwrite = params["overwrite"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: true
        val entry = workspaceRepository.writeTextInRootfs(workspaceId, path, text, overwrite)
        notify_workspace_file_changed(path)
        listOf(UIMessagePart.Text(entry.toJson().toString()))
    },
)

private fun createEditFileTool(
    workspaceId: String,
    needsApproval: (String) -> Boolean,
    workspaceRepository: WorkspaceRepository,
) = Tool(
    name = "workspace_edit_file",
    description = """
        Legacy precise text replacement tool. Prefer apply_workspace_edits for modifying existing code files because it is tracked by the IDE transaction layer.
        This tool is kept for compatibility and internally tries the same IDE transaction path first, then falls back to workspace disk editing.
        Paths must be absolute inside Rootfs. Provide old_text and new_text. By default old_text must occur exactly once; set replace_all=true to replace every occurrence.
        If no exact match is found in disk fallback, whitespace-tolerant line matching is attempted automatically.
    """.trimIndent().replace("\n", " "),
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                putPathProperty(required = true)
                put("old_text", buildJsonObject {
                    put("type", "string")
                    put("description", "Exact text to replace")
                })
                put("new_text", buildJsonObject {
                    put("type", "string")
                    put("description", "Replacement text")
                })
                put("replace_all", buildJsonObject {
                    put("type", "boolean")
                    put("description", "Whether to replace every occurrence. Defaults to false.")
                })
            },
            required = listOf("path", "old_text", "new_text"),
        )
    },
    needsApproval = { needsApproval("workspace_edit_file") || it.pathOutsideWritableRoots("path") },
    execute = {
        val params = it.jsonObject
        val path = params.absolutePath("path")
        val oldText = params.string("old_text") ?: error("old_text is required")
        val newText = params.string("new_text") ?: error("new_text is required")
        val replaceAll = params["replace_all"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: false
        require(oldText.isNotEmpty()) { "old_text must not be empty" }

        val request = agent_workspace_edit_request(
            edits = listOf(
                agent_text_replacement(
                    path = path,
                    old_text = oldText,
                    new_text = newText,
                    replace_all = replaceAll,
                )
            )
        )
        val ideResult = editor_agent_edit_controller.apply_workspace_edits(request)
        if (ideResult != null) {
            val diff = ideResult.changed_files.mapNotNull { change -> change.diff }.joinToString("\n").ifBlank { null }
            listOf(
                UIMessagePart.Text(
                    text = ideResult.toJson(usedIdeTransaction = true).toString(),
                    metadata = diff?.let { d -> DiffMetadata(diff = d).toMetadata() },
                )
            )
        } else {
            val original = workspaceRepository.readTextInRootfs(workspaceId, path)
            // 逐级尝试 exact -> line_trimmed -> block_anchor 替换器, 见 TextReplacers.kt
            val result = try {
                replaceText(original, oldText, newText, replaceAll)
            } catch (e: IllegalArgumentException) {
                error("${e.message} (path: $path)")
            }
            val entry = workspaceRepository.writeTextInRootfs(workspaceId, path, result.updated, overwrite = true)
            notify_workspace_file_changed(path)
            val diff = generateUnifiedDiff(original, result.updated, entry.path)
            listOf(
                UIMessagePart.Text(
                    text = buildJsonObject {
                        put("path", entry.path)
                        put("replacements", result.replacements)
                        if (result.strategy != ExactReplacer.name) put("matchStrategy", result.strategy)
                        put("sizeBytes", entry.sizeBytes)
                        put("updatedAt", entry.updatedAt)
                        put("usedIdeTransaction", false)
                    }.toString(),
                    // diff 存入 metadata 供 UI 渲染 diff view, 不会随工具结果发送给 API
                    metadata = diff?.let { d -> DiffMetadata(diff = d).toMetadata() },
                )
            )
        }
    },
)


private fun createApplyWorkspaceEditsTool(
    workspaceId: String,
    needsApproval: (String) -> Boolean,
    workspaceRepository: WorkspaceRepository,
) = Tool(
    name = "apply_workspace_edits",
    description = """
        Apply IDE-tracked text edits to workspace files. Prefer this over workspace_edit_file/write_file when changing project code.
        Supports multiple old_text/new_text replacements, preview/apply mode, dirty-buffer conflict checks, and diff metadata.
        Paths must be absolute inside Rootfs. Use /workspace for the workspace files area.
    """.trimIndent().replace("\n", " "),
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                put("edits", buildJsonObject {
                    put("type", "array")
                    put("description", "List of text replacements to apply as one edit request")
                    put("items", buildJsonObject {
                        put("type", "object")
                        put("properties", buildJsonObject {
                            putPathProperty(required = true)
                            put("type", buildJsonObject {
                                put("type", "string")
                                put("description", "replace or create. Defaults to replace.")
                                put("enum", buildJsonArray {
                                    addString("replace")
                                    addString("create")
                                })
                            })
                            put("old_text", buildJsonObject {
                                put("type", "string")
                                put("description", "Exact text to replace. Required for replace edits.")
                            })
                            put("new_text", buildJsonObject {
                                put("type", "string")
                                put("description", "Replacement text for replace edits.")
                            })
                            put("text", buildJsonObject {
                                put("type", "string")
                                put("description", "File content for create edits.")
                            })
                            put("replace_all", buildJsonObject {
                                put("type", "boolean")
                                put("description", "Whether to replace every occurrence. Defaults to false.")
                            })
                        })
                        put("required", buildJsonArray {
                            addString("path")
                        })
                    })
                })
                put("mode", buildJsonObject {
                    put("type", "string")
                    put("description", "preview or apply. Defaults to apply.")
                    put("enum", buildJsonArray {
                        addString("preview")
                        addString("apply")
                    })
                })
            },
            required = listOf("edits"),
        )
    },
    needsApproval = { params ->
        needsApproval("apply_workspace_edits") || runCatching {
            params.jsonObject["edits"]?.jsonArray.orEmpty().any { edit -> edit.pathOutsideWritableRoots("path") }
        }.getOrDefault(true)
    },
    execute = { element ->
        val params = element.jsonObject
        val edits = params["edits"]?.jsonArray?.map { edit_element ->
            val edit = edit_element.jsonObject
            val editType = edit.workspaceEditType()
            val oldText = edit.string("old_text").orEmpty()
            if (editType == agent_workspace_edit_type.Replace) {
                require(oldText.isNotEmpty()) { "old_text must not be empty for replace edits" }
                require(edit.string("new_text") != null) { "new_text is required for replace edits" }
            } else {
                require(edit.string("text") != null || edit.string("new_text") != null) { "text is required for create edits" }
            }
            agent_text_replacement(
                path = edit.absolutePath("path"),
                old_text = oldText,
                new_text = edit.string("new_text").orEmpty(),
                replace_all = edit["replace_all"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: false,
                type = editType,
                text = edit.string("text"),
            )
        }.orEmpty()
        require(edits.isNotEmpty()) { "edits must not be empty" }
        val mode = when (params.string("mode")?.lowercase()) {
            null, "apply" -> agent_edit_mode.Apply
            "preview" -> agent_edit_mode.Preview
            else -> error("mode must be preview or apply")
        }
        val request = agent_workspace_edit_request(edits = edits, mode = mode)
        val ideResult = editor_agent_edit_controller.apply_workspace_edits(request)
        val result = ideResult ?: apply_workspace_edits_on_disk(workspaceId, workspaceRepository, request)
        if (result.applied) {
            result.changed_files.forEach { change -> notify_workspace_file_changed(change.path) }
        }
        val diff = result.changed_files.mapNotNull { it.diff }.joinToString("\n").ifBlank { null }
        listOf(
            UIMessagePart.Text(
                text = result.toJson(usedIdeTransaction = ideResult != null).toString(),
                metadata = diff?.let { d -> DiffMetadata(diff = d).toMetadata() },
            )
        )
    },
)

private data class planned_disk_workspace_edit(
    val edit: agent_text_replacement,
    val original: String,
    val updated: String,
    val replacements: Int,
)

private suspend fun apply_workspace_edits_on_disk(
    workspaceId: String,
    workspaceRepository: WorkspaceRepository,
    request: agent_workspace_edit_request,
): agent_workspace_edit_result {
    val changes = mutableListOf<agent_file_change_result>()
    val planned = mutableListOf<planned_disk_workspace_edit>()
    for (edit in request.edits) {
        when (edit.type) {
            agent_workspace_edit_type.Create -> {
                val text = edit.text ?: edit.new_text
                planned += planned_disk_workspace_edit(
                    edit = edit,
                    original = "",
                    updated = text,
                    replacements = 1,
                )
            }
            agent_workspace_edit_type.Replace -> {
                val original = workspaceRepository.readTextInRootfs(workspaceId, edit.path)
                val replaced = try {
                    replaceText(original, edit.old_text, edit.new_text, edit.replace_all)
                } catch (e: IllegalArgumentException) {
                    error("${e.message} (path: ${edit.path})")
                }
                planned += planned_disk_workspace_edit(
                    edit = edit,
                    original = original,
                    updated = replaced.updated,
                    replacements = replaced.replacements,
                )
            }
        }
    }
    if (request.mode == agent_edit_mode.Preview) {
        for (change in planned) {
            val sizeBytes = change.updated.toByteArray(Charsets.UTF_8).size.toLong()
            changes += agent_file_change_result(
                path = change.edit.path,
                replacements = change.replacements,
                size_bytes = sizeBytes,
                updated_at = System.currentTimeMillis(),
                diff = generateUnifiedDiff(change.original, change.updated, change.edit.path),
            )
        }
        return agent_workspace_edit_result(applied = false, changed_files = changes)
    }
    for (change in planned) {
        val overwrite = change.edit.type != agent_workspace_edit_type.Create
        val entry = workspaceRepository.writeTextInRootfs(workspaceId, change.edit.path, change.updated, overwrite = overwrite)
        changes += agent_file_change_result(
            path = entry.path,
            replacements = change.replacements,
            size_bytes = entry.sizeBytes,
            updated_at = entry.updatedAt,
            diff = generateUnifiedDiff(change.original, change.updated, entry.path),
        )
    }
    return agent_workspace_edit_result(applied = true, changed_files = changes)
}

private fun agent_workspace_edit_result.toJson(usedIdeTransaction: Boolean) = buildJsonObject {
    put("applied", applied)
    put("usedIdeTransaction", usedIdeTransaction)
    put("changedFiles", buildJsonArray {
        changed_files.forEach { change ->
            addJsonObject {
                put("path", change.path)
                put("replacements", change.replacements)
                put("sizeBytes", change.size_bytes)
                put("updatedAt", change.updated_at)
                put("hasDiff", change.diff != null)
            }
        }
    })
    put("conflicts", buildJsonArray {
        conflicts.forEach { conflict ->
            addJsonObject {
                put("path", conflict.path)
                put("reason", conflict.reason.name)
                put("message", conflict.message)
            }
        }
    })
}

private fun createShellTool(
    workspaceId: String,
    needsApproval: (String) -> Boolean,
    workspaceRepository: WorkspaceRepository,
    defaultCwd: String? = null,
) = Tool(
    name = "workspace_shell",
    description = buildString {
        append("Run a shell command in the assistant's bound workspace Rootfs. The workspace files area is mounted at /workspace. ")
        append("Use cwd for a path relative to the workspace files root. ")
        if (!defaultCwd.isNullOrBlank()) {
            append("Defaults to '$defaultCwd'. ")
        }
        append("Requires Rootfs to be installed and ready.")
    },
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                put("command", buildJsonObject {
                    put("type", "string")
                    put("description", "Shell command to run")
                })
                put("cwd", buildJsonObject {
                    put("type", "string")
                    put(
                        "description",
                        if (!defaultCwd.isNullOrBlank()) {
                            "Working directory relative to the workspace files root. Defaults to '$defaultCwd'."
                        } else {
                            "Working directory relative to the workspace files root. Defaults to root."
                        }
                    )
                })
                put("timeout", buildJsonObject {
                    put("type", "integer")
                    put(
                        "description",
                        "Command timeout in seconds. Defaults to 30, max $SHELL_TIMEOUT_MAX_SECONDS."
                    )
                })
            },
            required = listOf("command"),
        )
    },
    needsApproval = { needsApproval("workspace_shell") },
    execute = {
        val params = it.jsonObject
        val command = params.string("command") ?: error("command is required")
        val cwd = (params.string("cwd") ?: defaultCwd.orEmpty())
            .removePrefix("/workspace/").removePrefix("/workspace")
        val timeoutMillis = params.string("timeout")?.toLongOrNull()
            ?.coerceIn(1L, SHELL_TIMEOUT_MAX_SECONDS)
            ?.times(1_000L)
            ?: WorkspaceManager.DEFAULT_COMMAND_TIMEOUT_MS
        val result = workspaceRepository.executeCommand(workspaceId, command, cwd, timeoutMillis)
        notify_workspace_maybe_changed(cwd)
        listOf(
            UIMessagePart.Text(
                buildJsonObject {
                    put("exitCode", result.exitCode)
                    put("stdout", result.stdout)
                    put("stderr", result.stderr)
                    put("timedOut", result.timedOut)
                    if (result.truncated) put("truncated", true)
                }.toString()
            )
        )
    },
)


private fun notify_workspace_file_changed(path: String) {
    workspace_file_change_notifier.notify(workspace_file_change_event.changed(path))
}

private fun notify_workspace_maybe_changed(cwd: String) {
    val path = cwd.trim().trim('/')
    workspace_file_change_notifier.notify(
        workspace_file_change_event.workspace_maybe_changed(
            path = if (path.isBlank()) "/workspace" else "/workspace/$path"
        )
    )
}

private fun kotlinx.serialization.json.JsonObject.string(name: String): String? =
    this[name]?.jsonPrimitive?.contentOrNull

private fun kotlinx.serialization.json.JsonObject.workspaceEditType(): agent_workspace_edit_type {
    return when (string("type")?.lowercase()) {
        null, "replace" -> agent_workspace_edit_type.Replace
        "create" -> agent_workspace_edit_type.Create
        else -> error("edit type must be replace or create")
    }
}

private suspend fun WorkspaceRepository.readTextInRootfs(
    workspaceId: String,
    path: String,
): String {
    val pathArg = path.shellQuote()
    val result = runRootfsCommand(
        workspaceId = workspaceId,
        action = "Read file",
        command = """
            if [ ! -e $pathArg ]; then
              printf '%s\n' ${"File does not exist: $path".shellQuote()} >&2
              exit 1
            fi
            if [ ! -f $pathArg ]; then
              printf '%s\n' ${"Path is not a file: $path".shellQuote()} >&2
              exit 1
            fi
            size=${'$'}(stat -c '%s' -- $pathArg) || exit 1
            if [ "${'$'}size" -gt $MAX_READ_FILE_BYTES ]; then
              printf '%s\n' ${"File is too large to read: $path (max ${MAX_READ_FILE_BYTES / 1024 / 1024}MB). Use shell commands like head, tail, or grep to read parts of it.".shellQuote()} >&2
              exit 1
            fi
            cat -- $pathArg
        """.trimIndent(),
    )
    return result.stdout
}

private suspend fun WorkspaceRepository.readImageInRootfs(
    workspaceId: String,
    path: String,
): List<UIMessagePart> {
    val pathArg = path.shellQuote()
    val result = runRootfsCommand(
        workspaceId = workspaceId,
        action = "Read image",
        command = """
            if [ ! -e $pathArg ]; then
              printf '%s\n' ${"File does not exist: $path".shellQuote()} >&2
              exit 1
            fi
            if [ ! -f $pathArg ]; then
              printf '%s\n' ${"Path is not a file: $path".shellQuote()} >&2
              exit 1
            fi
            size=${'$'}(stat -c '%s' -- $pathArg) || exit 1
            if [ "${'$'}size" -gt $MAX_READ_FILE_BYTES ]; then
              printf '%s\n' ${"File is too large to read: $path (max ${MAX_READ_FILE_BYTES / 1024 / 1024}MB).".shellQuote()} >&2
              exit 1
            fi
            base64 -w 0 -- $pathArg
        """.trimIndent(),
    )
    val bytes = Base64.getDecoder().decode(result.stdout.trim())

    val filesManager = getKoin().get<FilesManager>()
    val uris = filesManager.createChatFilesByByteArrays(listOf(bytes))
    return listOf(
        UIMessagePart.Image(url = uris.first().toString()),
        UIMessagePart.Text(
            buildJsonObject {
                put("path", path)
                put("description", "Image file read successfully")
            }.toString()
        ),
    )
}

private suspend fun WorkspaceRepository.writeTextInRootfs(
    workspaceId: String,
    path: String,
    text: String,
    overwrite: Boolean,
): WorkspaceFileEntry {
    val pathArg = path.shellQuote()
    val result = runRootfsCommand(
        workspaceId = workspaceId,
        action = "Write file",
        command = """
            if [ -e $pathArg ] && [ ${(!overwrite).shellFlag()} = 1 ]; then
              printf '%s\n' ${"File already exists: $path".shellQuote()} >&2
              exit 1
            fi
            if [ -e $pathArg ] && [ ! -f $pathArg ]; then
              printf '%s\n' ${"Path is not a file: $path".shellQuote()} >&2
              exit 1
            fi
            parent=${'$'}(dirname -- $pathArg) || exit 1
            mkdir -p -- "${'$'}parent" || exit 1
            cat > $pathArg || exit 1
            ${statEntryCommand(path)}
        """.trimIndent(),
        stdin = text.toByteArray(Charsets.UTF_8),
    )
    return result.stdout.parseRootfsEntry()
}

private suspend fun WorkspaceRepository.runRootfsCommand(
    workspaceId: String,
    action: String,
    command: String,
    stdin: ByteArray? = null,
): WorkspaceCommandResult {
    val result = executeCommand(
        id = workspaceId,
        command = command,
        timeoutMillis = WorkspaceManager.DEFAULT_COMMAND_TIMEOUT_MS,
        stdin = stdin,
    )
    if (result.timedOut) {
        error("$action timed out")
    }
    if (result.exitCode != 0) {
        val message = result.stderr.ifBlank { result.stdout }.trim()
        error(if (message.isBlank()) "$action failed with exit code ${result.exitCode}" else message)
    }
    if (result.truncated) {
        error("$action output is too large")
    }
    return result
}

private fun statEntryCommand(path: String): String {
    val pathArg = path.shellQuote()
    return """
        if [ -d $pathArg ]; then entry_type=d; else entry_type=f; fi
        entry_size=${'$'}(stat -c '%s' -- $pathArg) || exit 1
        entry_mtime=${'$'}(stat -c '%Y' -- $pathArg) || exit 1
        printf '%s\0%s\0%s\0%s\0' "${'$'}entry_type" "${'$'}entry_size" "${'$'}entry_mtime" $pathArg
    """.trimIndent()
}

private fun String.parseRootfsEntry(): WorkspaceFileEntry =
    parseRootfsEntries().singleOrNull() ?: error("Invalid file metadata output")

private fun String.parseRootfsEntries(): List<WorkspaceFileEntry> {
    val fields = split('\u0000').dropLastWhile { it.isEmpty() }
    require(fields.size % 4 == 0) { "Invalid file metadata output" }
    return fields.chunked(4).map { chunk ->
        val type = chunk[0]
        val size = chunk[1].toLongOrNull() ?: error("Invalid file size: ${chunk[1]}")
        val updatedAt = (chunk[2].toLongOrNull() ?: error("Invalid file mtime: ${chunk[2]}")) * 1_000L
        val path = chunk[3]
        WorkspaceFileEntry(
            path = path,
            name = path.rootfsName(),
            isDirectory = type == "d",
            sizeBytes = size,
            updatedAt = updatedAt,
        )
    }
}

private fun kotlinx.serialization.json.JsonObject.absolutePath(name: String): String {
    val path = string(name)?.replace('\\', '/')?.trim() ?: error("$name is required")
    require(path.isNotBlank()) { "$name is required" }
    require(path.startsWith("/")) { "$name must be an absolute path inside Rootfs" }
    require(!path.contains('\u0000')) { "$name contains invalid character" }
    return path
}

// 免强制审批的可写安全区: 工作区文件目录, 以及临时目录 /tmp
private val WRITABLE_ROOT_PREFIXES = listOf("/workspace", "/tmp")

private fun kotlinx.serialization.json.JsonElement.pathOutsideWritableRoots(name: String): Boolean =
    runCatching {
        jsonObject.absolutePath(name).isOutsideWritableRoots()
    }.getOrDefault(true)

private fun String.isOutsideWritableRoots(): Boolean {
    val normalized = trimEnd('/').ifBlank { "/" }
    return WRITABLE_ROOT_PREFIXES.none { prefix ->
        normalized == prefix || normalized.startsWith("$prefix/")
    }
}

private fun String.rootfsName(): String =
    trimEnd('/').substringAfterLast('/').ifBlank { "/" }

private fun String.shellQuote(): String =
    "'" + replace("'", "'\"'\"'") + "'"

private fun Boolean.shellFlag(): Int = if (this) 1 else 0


private fun JsonArrayBuilder.addString(value: String) {
    add(JsonPrimitive(value))
}

private fun JsonArrayBuilder.addJsonObject(
    builderAction: JsonObjectBuilder.() -> Unit,
) {
    add(buildJsonObject(builderAction))
}

private fun JsonObjectBuilder.putPathProperty(required: Boolean) {
    put("path", buildJsonObject {
        put("type", "string")
        put(
            "description",
            if (required) {
                "Absolute path inside Rootfs. Use /workspace for the workspace files area."
            } else {
                "Optional absolute path inside Rootfs. Use /workspace for the workspace files area."
            }
        )
    })
}

private fun WorkspaceFileEntry.toJson() = buildJsonObject {
    put("path", path)
    put("name", name)
    put("isDirectory", isDirectory)
    put("sizeBytes", sizeBytes)
    put("updatedAt", updatedAt)
}
