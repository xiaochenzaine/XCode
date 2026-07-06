package com.xc.code.editor.treesitter

import android.content.Context
import com.itsaky.androidide.treesitter.TSLanguage
import com.itsaky.androidide.treesitter.TreeSitter
import com.itsaky.androidide.treesitter.bash.TSLanguageBash
import com.itsaky.androidide.treesitter.cmake.TSLanguageCmake
import com.itsaky.androidide.treesitter.cpp.TSLanguageCpp
import com.itsaky.androidide.treesitter.json.TSLanguageJson
import com.itsaky.androidide.treesitter.yaml.TSLanguageYaml
import com.xc.code.editor.core.is_c_family_file
import com.xc.code.editor.core.is_cmake_file
import com.xc.code.editor.core.is_json_file
import com.xc.code.editor.core.is_shell_file
import com.xc.code.editor.core.is_tree_sitter_supported_file
import com.xc.code.editor.core.is_yaml_file
import io.github.rosemoe.sora.editor.ts.TsAnalyzeManager
import io.github.rosemoe.sora.editor.ts.TsLanguage
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import io.github.rosemoe.sora.editor.ts.TsThemeBuilder
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.styling.TextStyle

internal object editor_tree_sitter_language_factory {
    @Volatile
    private var native_loaded = false

    private const val COLOR_NAMESPACE = 300
    private const val COLOR_TYPE = 301
    private const val COLOR_FUNCTION = 302
    private const val COLOR_PARAMETER = 303
    private const val COLOR_VARIABLE = 304
    private const val COLOR_PROPERTY = 305
    private const val COLOR_MACRO = 306
    private const val COLOR_ENUM_MEMBER = 307
    private const val COLOR_METHOD = 308
    private const val COLOR_KEYWORD = 309
    private const val COLOR_OPERATOR = 310
    private const val COLOR_COMMENT = 311
    private const val COLOR_STRING = 312
    private const val COLOR_NUMBER = 313
    private const val COLOR_MODIFIER = 314
    private const val COLOR_TYPE_PARAMETER = 315
    private const val COLOR_CLASS = 316
    private const val COLOR_ENUM = 317
    private const val COLOR_PUNCTUATION = 318

    fun create(
        context: Context,
        file_path: String,
        on_error: (Throwable) -> Unit = {}
    ): Language? {
        val config = language_config(file_path) ?: return null
        TsAnalyzeManager.error_reporter = on_error
        return runCatching {
            ensure_native_loaded()
            val highlights = read_asset(context, "treesitter/${config.asset_name}/highlights.scm")
            val code_blocks = read_asset(context, "treesitter/${config.asset_name}/blocks.scm")
            val brackets = read_asset(context, "treesitter/${config.asset_name}/brackets.scm")
            val locals = read_asset(context, "treesitter/${config.asset_name}/locals.scm")
            TsLanguage(
                languageSpec = BasicTsLanguageSpec(
                    language = config.language(),
                    highlight_scm_source = highlights,
                    code_blocks_scm_source = code_blocks,
                    brackets_scm_source = brackets,
                    locals_scm_source = locals
                ),
                tab = config.use_tab,
                themeDescription = { build_basic_theme() }
            )
        }.onFailure { error ->
            on_error(error)
        }.getOrNull()
    }

    fun supports(file_path: String?): Boolean {
        return is_tree_sitter_supported_file(file_path)
    }

    private fun language_config(file_path: String): tree_sitter_language_config? {
        return when {
            is_c_family_file(file_path) -> tree_sitter_language_config("cpp", true) { TSLanguageCpp.getInstance() }
            is_json_file(file_path) -> tree_sitter_language_config("json", false) { TSLanguageJson.getInstance() }
            is_yaml_file(file_path) -> tree_sitter_language_config("yaml", false) { TSLanguageYaml.getInstance() }
            is_cmake_file(file_path) -> tree_sitter_language_config("cmake", false) { TSLanguageCmake.getInstance() }
            is_shell_file(file_path) -> tree_sitter_language_config("bash", true) { TSLanguageBash.getInstance() }
            else -> null
        }
    }

    private fun read_asset(context: Context, path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }

    @Synchronized
    private fun ensure_native_loaded() {
        if (native_loaded) return
        // android-tree-sitter 的 TSParser/TSQuery/TSNode 等 JNI 方法在 libandroid-tree-sitter.so 中。
        // grammar 自己的 libtree-sitter-cpp.so 只提供 tree_sitter_xxx()，不能替代基础 binding。
        TreeSitter.loadLibrary()
        native_loaded = true
    }

    private fun TsThemeBuilder.build_basic_theme() {
        style(COLOR_COMMENT) applyTo arrayOf("comment")
        style(COLOR_KEYWORD) applyTo arrayOf("keyword", "keyword.directive", "keyword.function", "keyword.conditional", "keyword.repeat", "keyword.return", "keyword.operator")
        style(COLOR_MODIFIER) applyTo arrayOf("keyword.modifier")
        style(COLOR_OPERATOR) applyTo arrayOf("operator", "punctuation.special")
        style(COLOR_PUNCTUATION) applyTo arrayOf("punctuation", "punctuation.bracket", "delimiter")
        style(COLOR_STRING) applyTo arrayOf("string", "string.special", "escape")
        style(COLOR_NUMBER) applyTo arrayOf("number", "boolean")
        style(COLOR_ENUM_MEMBER) applyTo arrayOf("constant", "constant.builtin")
        style(COLOR_TYPE) applyTo arrayOf("type", "type.builtin")
        style(COLOR_CLASS) applyTo arrayOf("type.class")
        style(COLOR_ENUM) applyTo arrayOf("type.enum")
        style(COLOR_TYPE_PARAMETER) applyTo arrayOf("type.parameter")
        style(COLOR_FUNCTION) applyTo arrayOf("function", "function.builtin")
        style(COLOR_METHOD) applyTo arrayOf("function.method")
        style(COLOR_MACRO) applyTo arrayOf("function.macro", "function.special")
        style(COLOR_VARIABLE) applyTo arrayOf("variable", "variable.builtin")
        style(COLOR_PARAMETER) applyTo arrayOf("variable.parameter")
        style(COLOR_PROPERTY) applyTo arrayOf("property", "variable.field", "field", "string.special.key")
        style(COLOR_NAMESPACE) applyTo arrayOf("namespace", "module")
        style(COLOR_OPERATOR) applyTo arrayOf("label")
    }

    private fun style(color_id: Int): Long {
        // Tree-sitter 只负责颜色，不改变粗体、斜体或删除线。
        return TextStyle.makeStyle(color_id, 0, false, false, false)
    }
}

private data class tree_sitter_language_config(
    val asset_name: String,
    val use_tab: Boolean,
    val language: () -> TSLanguage
)

private class BasicTsLanguageSpec(
    language: TSLanguage,
    highlight_scm_source: String,
    code_blocks_scm_source: String,
    brackets_scm_source: String,
    locals_scm_source: String
) : TsLanguageSpec(
    language = language,
    highlightScmSource = highlight_scm_source,
    codeBlocksScmSource = code_blocks_scm_source,
    bracketsScmSource = brackets_scm_source,
    localsScmSource = locals_scm_source
)
