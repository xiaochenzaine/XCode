package com.xc.code.toolchain.runtime

data class proot_environment(
    val path: String,
    val extra: Map<String, String> = emptyMap()
) {
    fun as_map(paths: toolchain_runtime_paths): Map<String, String> {
        return host_process_environment(paths, path, extra)
    }

    fun as_array(paths: toolchain_runtime_paths): Array<String> {
        return as_map(paths).map { (key, value) -> "$key=$value" }.toTypedArray()
    }
}

fun host_process_environment(
    paths: toolchain_runtime_paths,
    path: String,
    extra_environment: Map<String, String> = emptyMap()
): Map<String, String> {
    return linkedMapOf(
        "HOME" to toolchain_guest_paths.home,
        "LANG" to proot_lang,
        "TERM" to proot_term,
        "PROOT_TMP_DIR" to paths.proot_tmp_dir.absolutePath,
        "PATH" to path
    ).apply {
        put("PROOT_LOADER", paths.proot_loader_file.absolutePath)
        putAll(extra_environment.filter_environment_names())
    }
}

fun guest_shell_environment(
    tmpdir: String = toolchain_guest_paths.tmp,
    extra_environment: Map<String, String> = emptyMap()
): Map<String, String> {
    return linkedMapOf(
        "HOME" to toolchain_guest_paths.home,
        "PATH" to proot_default_path,
        "TERM" to proot_term,
        "LANG" to proot_lang,
        "LC_ALL" to proot_lang,
        "TMPDIR" to tmpdir
    ).apply {
        putAll(extra_environment.filter_environment_names())
    }
}

internal fun clean_shell_env_args(
    tmpdir: String = toolchain_guest_paths.tmp,
    extra_environment: Map<String, String> = emptyMap()
): List<String> {
    val environment = guest_shell_environment(tmpdir, extra_environment)
    return listOf("/usr/bin/env", "-i") + environment.map { (key, value) -> "$key=$value" }
}

private fun Map<String, String>.filter_environment_names(): Map<String, String> {
    return filterKeys { key -> key.matches(environment_name_regex) }
}

private val environment_name_regex = Regex("[A-Za-z_][A-Za-z0-9_]*")

internal const val proot_lang = "C.UTF-8"
internal const val proot_term = "xterm-256color"
internal const val proot_default_path = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
