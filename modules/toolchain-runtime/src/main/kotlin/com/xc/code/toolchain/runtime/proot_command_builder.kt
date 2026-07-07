package com.xc.code.toolchain.runtime

class proot_command_builder(
    private val paths: toolchain_runtime_paths,
    private val mount_provider: toolchain_mount_provider = toolchain_mount_provider(paths)
) {
    fun base_args(
        working_dir: String = toolchain_guest_paths.home,
        include_xcode_mount: Boolean = true,
        extra_mounts: List<proot_bind_mount> = emptyList()
    ): List<String> {
        return buildList {
            add("--android-profile")
            add("--link2symlink")
            add("--kill-on-exit")
            add("-0")
            add("-r")
            add(paths.ubuntu_base_dir.absolutePath)
            add("-w")
            add(working_dir.ifBlank { toolchain_guest_paths.home })
            addAll(
                mount_provider.base_mount_args(
                    include_xcode_mount = include_xcode_mount,
                    extra_mounts = extra_mounts
                )
            )
        }
    }

    fun command(
        shell_command: String,
        working_dir: String = toolchain_guest_paths.home,
        include_xcode_mount: Boolean = true,
        extra_mounts: List<proot_bind_mount> = emptyList(),
        extra_environment: Map<String, String> = emptyMap()
    ): List<String> {
        val dollar = '$'
        val wrapper = "cd -- \"${dollar}1\" && eval \"${dollar}2\""
        val resolved_working_dir = working_dir.ifBlank { toolchain_guest_paths.home }
        return listOf(paths.proot_file.absolutePath) +
            base_args(resolved_working_dir, include_xcode_mount, extra_mounts) +
            clean_shell_env_args(extra_environment = extra_environment) +
            listOf(
                "/bin/bash",
                "-l",
                "-c",
                wrapper,
                "xcode",
                resolved_working_dir,
                shell_command
            )
    }

    fun interactive_args(
        working_dir: String = toolchain_guest_paths.home,
        include_xcode_mount: Boolean = true,
        extra_mounts: List<proot_bind_mount> = emptyList()
    ): Array<String> {
        return (base_args(working_dir, include_xcode_mount, extra_mounts) +
            clean_shell_env_args() +
            listOf("/bin/bash")).toTypedArray()
    }
}
