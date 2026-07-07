package com.xc.code.editor.agent

/** Agent 请求 IDE 执行的文本编辑事务。 */
data class agent_workspace_edit_request(
    val edits: List<agent_text_replacement>,
    val mode: agent_edit_mode = agent_edit_mode.Apply,
)

/** Agent 文本编辑操作：替换已有文本，或创建新文件。 */
data class agent_text_replacement(
    val path: String,
    val old_text: String = "",
    val new_text: String = "",
    val replace_all: Boolean = false,
    val type: agent_workspace_edit_type = agent_workspace_edit_type.Replace,
    val text: String? = null,
)

enum class agent_workspace_edit_type {
    Replace,
    Create,
}

enum class agent_edit_mode {
    Preview,
    Apply,
}

data class agent_workspace_edit_result(
    val applied: Boolean,
    val changed_files: List<agent_file_change_result> = emptyList(),
    val conflicts: List<agent_edit_conflict> = emptyList(),
)

data class agent_file_change_result(
    val path: String,
    val replacements: Int,
    val size_bytes: Long,
    val updated_at: Long,
    val diff: String? = null,
)

data class agent_edit_conflict(
    val path: String,
    val reason: agent_edit_conflict_reason,
    val message: String,
)

enum class agent_edit_conflict_reason {
    OutsideProject,
    DirtyBuffer,
    FileMissing,
    FileAlreadyExists,
    NotAFile,
    OldTextEmpty,
    OldTextNotFound,
    AmbiguousOldText,
    TooLarge,
    IoError,
}

interface agent_workspace_edit_handler {
    suspend fun apply_workspace_edits(request: agent_workspace_edit_request): agent_workspace_edit_result
}

/**
 * Agent 与当前 IDE 编辑器之间的轻量桥接。
 *
 * editor_activity 存活时注册 handler，Agent 工具优先走 IDE 事务；没有打开编辑器时再回退到磁盘工具。
 */
object editor_agent_edit_controller {
    @Volatile
    var handler: agent_workspace_edit_handler? = null

    suspend fun apply_workspace_edits(request: agent_workspace_edit_request): agent_workspace_edit_result? {
        return handler?.apply_workspace_edits(request)
    }
}
