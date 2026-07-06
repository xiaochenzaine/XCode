package com.xc.code.ui.screens.editor

import com.xc.code.project_file_tree.R

fun editor_file_icon_res(name: String): Int {
    val lower_name = name.lowercase()
    return when {
        lower_name == "cmakelists.txt" || lower_name == "cmakecache.txt" || lower_name.endsWith(".cmake") -> R.drawable.ic_file_cmake
        lower_name.endsWith(".yaml") || lower_name.endsWith(".yml") -> R.drawable.ic_file_yaml
        lower_name == ".bashrc" || lower_name == ".bash_profile" || lower_name == ".bash_login" || lower_name == ".profile" || lower_name == ".zshrc" || lower_name.endsWith(".sh") || lower_name.endsWith(".bash") || lower_name.endsWith(".zsh") -> R.drawable.ic_file_shell
        lower_name.endsWith(".json") -> R.drawable.ic_file_json
        lower_name.endsWith(".cpp") || lower_name.endsWith(".cc") || lower_name.endsWith(".cxx") -> R.drawable.ic_file_cpp
        lower_name.endsWith(".c") -> R.drawable.ic_file_cpp
        lower_name.endsWith(".h") || lower_name.endsWith(".hpp") || lower_name.endsWith(".hh") || lower_name.endsWith(".hxx") -> R.drawable.ic_file_h
        else -> R.drawable.ic_file_generic
    }
}
