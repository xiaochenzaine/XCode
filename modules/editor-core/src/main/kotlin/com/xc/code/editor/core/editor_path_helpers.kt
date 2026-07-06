package com.xc.code.editor.core

import java.io.File

fun replace_path_prefix(path: String, old_prefix: String, new_prefix: String): String {
    val normalized_old = old_prefix.trimEnd(File.separatorChar)
    return when {
        path == normalized_old -> new_prefix
        path.startsWith(normalized_old + File.separator) -> new_prefix + path.removePrefix(normalized_old)
        else -> path
    }
}

fun is_same_or_child_path(path: String, parent_path: String): Boolean {
    val normalized_parent = parent_path.trimEnd(File.separatorChar)
    return path == normalized_parent || path.startsWith(normalized_parent + File.separator)
}

fun is_readable_project_file(project_dir: File, file: File): Boolean {
    if (!file.exists() || !file.isFile) return false
    return runCatching {
        val root_path = project_dir.canonicalFile.toPath()
        val file_path = file.canonicalFile.toPath()
        file_path.startsWith(root_path)
    }.getOrDefault(false)
}

fun is_c_family_file(file_path: String?): Boolean {
    if (file_path.isNullOrBlank()) return true
    val name = File(file_path).name
    return name.endsWith(".c", ignoreCase = true) ||
        name.endsWith(".cc", ignoreCase = true) ||
        name.endsWith(".cpp", ignoreCase = true) ||
        name.endsWith(".cxx", ignoreCase = true) ||
        name.endsWith(".h", ignoreCase = true) ||
        name.endsWith(".hh", ignoreCase = true) ||
        name.endsWith(".hpp", ignoreCase = true) ||
        name.endsWith(".hxx", ignoreCase = true)
}

fun is_json_file(file_path: String?): Boolean {
    if (file_path.isNullOrBlank()) return false
    return File(file_path).name.endsWith(".json", ignoreCase = true)
}

fun is_yaml_file(file_path: String?): Boolean {
    if (file_path.isNullOrBlank()) return false
    val name = File(file_path).name
    return name.endsWith(".yaml", ignoreCase = true) || name.endsWith(".yml", ignoreCase = true)
}

fun is_cmake_file(file_path: String?): Boolean {
    if (file_path.isNullOrBlank()) return false
    val name = File(file_path).name
    return name.equals("CMakeLists.txt", ignoreCase = true) || name.endsWith(".cmake", ignoreCase = true)
}

fun is_shell_file(file_path: String?): Boolean {
    if (file_path.isNullOrBlank()) return false
    val name = File(file_path).name
    return name.equals(".bashrc", ignoreCase = true) ||
        name.equals(".bash_profile", ignoreCase = true) ||
        name.equals(".bash_login", ignoreCase = true) ||
        name.equals(".profile", ignoreCase = true) ||
        name.equals(".zshrc", ignoreCase = true) ||
        name.endsWith(".sh", ignoreCase = true) ||
        name.endsWith(".bash", ignoreCase = true) ||
        name.endsWith(".zsh", ignoreCase = true)
}

fun is_tree_sitter_supported_file(file_path: String?): Boolean {
    return is_c_family_file(file_path) ||
        is_json_file(file_path) ||
        is_yaml_file(file_path) ||
        is_cmake_file(file_path) ||
        is_shell_file(file_path)
}
