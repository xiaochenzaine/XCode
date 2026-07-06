package com.xc.code.lsp.clangd

import com.xc.code.toolchain.runtime.proot_bind_mount
import com.xc.code.toolchain.runtime.proot_command_builder
import com.xc.code.toolchain.runtime.proot_environment
import com.xc.code.toolchain.runtime.rootfs_patcher
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class clangd_stream_connection_provider(
    private val config: clangd_lsp_config,
    private val patcher: rootfs_patcher = rootfs_patcher()
) : StreamConnectionProvider {
    private var process: Process? = null
    private var stderr_thread: Thread? = null

    override val inputStream: InputStream
        get() = require_process().inputStream

    override val outputStream: OutputStream
        get() = require_process().outputStream

    override val isClosed: Boolean
        get() = process?.isAlive != true

    @Throws(IOException::class)
    override fun start() {
        if (process?.isAlive == true) return
        validate_runtime()
        patcher.patch(config.runtime_paths)
        ensure_build_dir_exists()
        ensure_compile_command_directories_exist()
        ensure_guest_mount_points_exist()
        ensure_android_tzdata_mount_points_exist()

        val command = build_proot_clangd_command()
        val process_environment = proot_environment(
            path = config.path,
            extra = config.extra_environment
        ).as_map(config.runtime_paths)

        val started = ProcessBuilder(command)
            .directory(config.project_dir)
            .apply { environment().putAll(process_environment) }
            .start()

        process = started
        stderr_thread = create_stderr_thread(started).also { it.start() }
    }

    override fun close() {
        val running = process ?: return
        runCatching { running.outputStream.close() }
        if (running.isAlive) {
            running.destroy()
            runCatching {
                if (!running.waitFor(500, TimeUnit.MILLISECONDS) && running.isAlive) {
                    running.destroyForcibly()
                }
            }.onFailure {
                if (running.isAlive) running.destroyForcibly()
            }
        }
        stderr_thread?.join(500)
        stderr_thread = null
        process = null
    }

    private fun require_process(): Process {
        return requireNotNull(process) { "clangd process has not been started" }
    }

    private fun validate_runtime() {
        val paths = config.runtime_paths
        require(paths.proot_file.isFile) { "proot not found: ${paths.proot_file.absolutePath}" }
        require(paths.proot_loader_file.isFile) { "proot loader not found: ${paths.proot_loader_file.absolutePath}" }
        require(paths.ubuntu_base_dir.isDirectory) { "Ubuntu rootfs not found: ${paths.ubuntu_base_dir.absolutePath}" }
    }

    private fun ensure_build_dir_exists() {
        require(config.build_dir.mkdirs() || config.build_dir.isDirectory) {
            "Failed to create clangd build directory: ${config.build_dir.absolutePath}"
        }
    }

    private fun ensure_compile_command_directories_exist() {
        runCatching {
            compile_command_directories().forEach { directory ->
                if (!directory.exists()) directory.mkdirs()
            }
        }.onFailure { error ->
            config.on_stderr("failed to prepare compile_commands directories: ${error.message}")
        }
    }

    private fun compile_command_directories(): Set<File> {
        val compile_commands = File(config.build_dir, "compile_commands.json")
        if (!compile_commands.isFile) return emptySet()
        return compile_commands.readText()
            .let(::extract_compile_command_directories)
            .map { File(it).absoluteFile }
            .toCollection(linkedSetOf())
    }

    private fun ensure_guest_mount_points_exist() {
        val paths = config.runtime_paths
        val guest_paths = compile_command_directories() + listOf(config.project_dir, config.build_dir)
        guest_paths.forEach { guest_path ->
            val relative = guest_path.absolutePath.removePrefix("/")
            File(paths.ubuntu_base_dir, relative).mkdirs()
        }
    }

    private fun ensure_android_tzdata_mount_points_exist() {
        android_tzdata_mounts().forEach { mount ->
            val target = mount.target ?: mount.source.absolutePath
            File(config.runtime_paths.ubuntu_base_dir, target.removePrefix("/")).mkdirs()
        }
    }

    private fun android_tzdata_mounts(): List<proot_bind_mount> {
        return listOf(
            proot_bind_mount(File("/apex/com.android.tzdata")),
            proot_bind_mount(File("/system/usr/share/zoneinfo"))
        ).filter { it.source.exists() }
    }

    private fun should_use_android_timezone(): Boolean {
        return File("/apex/com.android.tzdata/etc/tz/tzdata").isFile ||
            File("/system/usr/share/zoneinfo/tzdata").isFile
    }

    private fun extract_compile_command_directories(json: String): Set<String> {
        val directories = linkedSetOf<String>()
        val regex = Regex(""""directory"\s*:\s*"((?:\\.|[^"\\])*)"""")
        regex.findAll(json).forEach { match ->
            val raw = match.groupValues[1]
            val decoded = raw
                .replace("\\/", "/")
                .replace("\\\\", "\\")
                .replace("\\\"", "\"")
            if (decoded.isNotBlank()) directories += decoded
        }
        return directories
    }

    private fun build_proot_clangd_command(): List<String> {
        val builder = proot_command_builder(config.runtime_paths)
        val environment = linkedMapOf(
            "HOME" to "/home",
            "PATH" to config.path,
            "TERM" to "xterm-256color",
            "LANG" to "C.UTF-8",
            "LC_ALL" to "C.UTF-8",
            "TMPDIR" to "/tmp"
        ).apply {
            if (should_use_android_timezone()) put("TZ", "Asia/Shanghai")
            putAll(config.extra_environment.filterKeys { it.matches(Regex("[A-Za-z_][A-Za-z0-9_]*")) && it != "TZ" })
        }
        val project_mounts = (listOf(
            proot_bind_mount(config.project_dir),
            proot_bind_mount(config.build_dir)
        ) + android_tzdata_mounts()).distinctBy { it.source.absolutePath }
        return listOf(config.runtime_paths.proot_file.absolutePath) +
            builder.base_args(
                working_dir = config.project_dir.absolutePath,
                extra_mounts = project_mounts
            ) +
            listOf("/usr/bin/env", "-i") +
            environment.map { (key, value) -> "$key=$value" } +
            config.arguments()
    }

    private fun create_stderr_thread(process: Process): Thread {
        return Thread {
            runCatching {
                process.errorStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
                    lines.forEach { line ->
                        if (line.isNotBlank()) config.on_stderr(line)
                    }
                }
            }
        }.apply { isDaemon = true }
    }
}
