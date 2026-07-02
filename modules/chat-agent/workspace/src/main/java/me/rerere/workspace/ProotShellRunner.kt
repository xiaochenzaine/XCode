package me.rerere.workspace

import java.io.File

data class WorkspaceBindMount(
    val source: File,
    val target: String,
) {
    init {
        require(target.startsWith("/")) { "Bind mount target must be absolute: $target" }
    }
}

class ProotShellRunner(
    private val nativeLibraryDir: File,
    private val extraBindMounts: List<WorkspaceBindMount> = emptyList(),
    private val patcher: RootfsPatcher = RootfsPatcher(),
) : WorkspaceShellRunner {
    override fun execute(context: WorkspaceShellContext): WorkspaceCommandResult {
        if (!context.linuxDir.hasUsableRootfs()) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "Rootfs is not installed",
            )
        }

        val proot = File(nativeLibraryDir, PROOT_EXEC)
        val loader = File(nativeLibraryDir, PROOT_LOADER)
        if (!proot.isFile) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "proot executable not found: ${proot.absolutePath}",
            )
        }
        if (!loader.isFile) {
            return WorkspaceCommandResult(
                exitCode = 127,
                stdout = "",
                stderr = "proot loader not found: ${loader.absolutePath}",
            )
        }

        context.tempDir.mkdirs()
        context.xcodeHomeDir().mkdirs()
        patcher.patch(context.linuxDir)

        val process = ProcessBuilder(buildCommand(context, proot))
            .directory(context.filesDir)
            .redirectErrorStream(false)
            .apply {
                environment()["PROOT_LOADER"] = loader.absolutePath
                environment()["PROOT_TMP_DIR"] = context.tempDir.absolutePath
                environment()["TMPDIR"] = context.tempDir.absolutePath
            }
            .start()

        return process.readResult(context.timeoutMillis, context.stdin)
    }

    private fun buildCommand(
        context: WorkspaceShellContext,
        proot: File,
    ): List<String> {
        val command = mutableListOf(
            proot.absolutePath,
            "--android-profile",
            "--link2symlink",
            "--kill-on-exit",
            "-0",
            "-r",
            context.linuxDir.absolutePath,
            "-b",
            "/sys",
            "-b",
            "/dev",
            "-b",
            "/proc",
            "-w",
            context.prootCwd(),
            "-b",
            "${context.xcodeHomeDir().absolutePath}:$HOME_DIR",
            "-b",
            "${File(context.xcodeHomeDir(), "xcode").absolutePath}:$XCODE_HOME_DIR",
            "-b",
            "${context.filesDir.absolutePath}:$WORKSPACE_DIR",
        )
        extraBindMounts.forEach { mount ->
            if (mount.source.exists()) {
                prepareWorkspaceBindTarget(context, mount)
                command += "-b"
                command += "${mount.source.absolutePath}:${mount.target.trimEnd('/')}"
            }
        }

        command += listOf(
            "/usr/bin/env",
            "-i",
            "HOME=$HOME_DIR",
            "PATH=$DEFAULT_PROOT_PATH",
            "TERM=xterm-256color",
            "LANG=C.UTF-8",
            "LC_ALL=C.UTF-8",
            "TMPDIR=/tmp",
            "/bin/bash",
            "-l",
            "-c",
            // 命令通过位置参数传入，避免自行拼接转义；eval "$2" 与 bash -c "$cmd" 行为一致。
            "cd -- \"\$1\" && eval \"\$2\"",
            "rikkahub",
            context.prootCwd(),
            context.command,
        )
        return command
    }

    private fun prepareWorkspaceBindTarget(context: WorkspaceShellContext, mount: WorkspaceBindMount) {
        val target = mount.target.trimEnd('/')
        if (target == WORKSPACE_DIR) return
        if (target.startsWith("$WORKSPACE_DIR/")) {
            File(context.filesDir, target.removePrefix("$WORKSPACE_DIR/")).mkdirs()
        }
    }

    private fun WorkspaceShellContext.xcodeHomeDir(): File =
        if (linuxDir.name == "ubuntu-base" && linuxDir.parentFile?.name == "xcode") {
            linuxDir.parentFile?.parentFile ?: File(linuxDir, "home")
        } else {
            File(linuxDir, "home")
        }

    private fun WorkspaceShellContext.prootCwd(): String {
        val normalized = cwd.trim().trim('/')
        return if (normalized.isBlank()) {
            WORKSPACE_DIR
        } else {
            "$WORKSPACE_DIR/$normalized"
        }
    }

    private fun File.hasUsableRootfs(): Boolean =
        isDirectory && File(this, "bin/sh").isFile

    private companion object {
        private const val PROOT_EXEC = "libproot_exec.so"
        private const val PROOT_LOADER = "libproot_loader.so"
        private const val HOME_DIR = "/home"
        private const val XCODE_HOME_DIR = "/home/xcode"
        private const val WORKSPACE_DIR = "/workspace"
        private const val DEFAULT_PROOT_PATH =
            "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
    }
}
