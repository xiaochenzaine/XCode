package com.xc.code.toolchain.runtime

class toolchain_mount_provider(
    private val paths: toolchain_runtime_paths
) {
    fun base_mount_args(
        include_xcode_mount: Boolean = true,
        extra_mounts: List<proot_bind_mount> = emptyList()
    ): List<String> {
        return buildList {
            add_bind("/sys")
            add_bind("/dev")
            add_bind("/proc")
            add_bind(paths.home_dir.absolutePath, toolchain_guest_paths.home)

            if (include_xcode_mount) {
                add_bind(paths.xcode_dir.absolutePath, toolchain_guest_paths.tool_home)
            }

            paths.external_storage_dir
                ?.takeIf { it.exists() }
                ?.let { external_storage ->
                    add_bind(external_storage.absolutePath, toolchain_guest_paths.external_storage)
                }

            extra_mounts
                .filter { it.source.exists() }
                .forEach { mount -> add_bind(mount.as_argument()) }
        }
    }

    private fun MutableList<String>.add_bind(source: String, target: String? = null) {
        add("-b")
        add(if (target.isNullOrBlank()) source else "$source:${target.trimEnd('/')}")
    }
}
