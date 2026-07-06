package com.xc.code.editor.theme

import android.content.Context
import com.xc.code.core.logging.logger_manager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.io.File
import java.util.Locale

private enum class editor_theme_color_section {
    COLORS,
    SYNTAX
}

private data class editor_theme_color_definition(
    val section: editor_theme_color_section,
    val key: String,
    val title: String,
    val description: String
)

data class editor_theme_color_item(
    val key: String,
    val title: String,
    val description: String,
    val value: String
)

data class editor_theme_preview_palette(
    val background: String,
    val foreground: String,
    val cursor: String,
    val selection: String,
    val line_highlight: String,
    val indent_line: String,
    val block_line: String,
    val line_number: String,
    val active_line_number: String,
    val keyword: String,
    val keyword_directive: String,
    val type: String,
    val type_builtin: String,
    val namespace: String,
    val function: String,
    val method: String,
    val string: String,
    val string_special: String,
    val number: String,
    val comment: String,
    val constant: String,
    val constant_builtin: String,
    val variable_builtin: String,
    val property: String,
    val operator: String,
    val punctuation: String,
    val punctuation_bracket: String,
    val punctuation_delimiter: String
)

object editor_theme_manager {
    private const val DEFAULT_THEME_ASSET = "editor/themes/default.json"
    private const val USER_THEME_DIR = "editor/themes"
    private const val USER_THEME_FILE = "colors.json"

    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    private val color_definitions = listOf(
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.background", "背景", "编辑区背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.foreground", "文字", "普通文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorCursor.foreground", "光标", "输入光标"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.selectionBackground", "选中背景", "选中文本背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.selectionForeground", "选中文字", "选中文本前景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.selectionBorder", "选中边框", "选中文本边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.selectionHandle", "选区手柄", "拖动选择手柄"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.lineHighlightBackground", "当前行", "光标所在行背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.lineHighlightBorder", "当前行边框", "光标所在行边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorWhitespace.foreground", "空白符", "空格、制表符"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorLineNumber.foreground", "行号", "普通行号"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorLineNumber.activeForeground", "当前行号", "当前行号"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorLineNumber.panelBackground", "行号面板背景", "行号面板背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorLineNumber.panelForeground", "行号面板文字", "行号面板文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorIndentGuide.background", "缩进线", "普通缩进线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorIndentGuide.activeBackground", "活动缩进线", "当前缩进线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSideBlockLine.foreground", "代码块边线", "侧边代码块线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "highlightedDelimitersForeground", "括号匹配文字", "匹配括号文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "highlightedDelimitersBackground", "括号匹配背景", "匹配括号背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "highlightedDelimitersBorder", "括号匹配边框", "匹配括号边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "highlightedDelimitersUnderline", "括号匹配下划线", "匹配括号下划线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.findMatchBackground", "搜索命中", "当前搜索命中背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.findMatchHighlightBackground", "搜索其他命中", "其他搜索命中背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.findMatchBorder", "搜索命中边框", "当前搜索命中边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.wordHighlightBackground", "单词高亮", "相同单词背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.wordHighlightStrongBackground", "强单词高亮", "强单词背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.wordHighlightBorder", "单词高亮边框", "相同单词边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editor.wordHighlightStrongBorder", "强单词高亮边框", "强单词边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.background", "补全背景", "补全窗口背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.foreground", "补全文字", "补全普通文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.highlightForeground", "补全高亮", "补全匹配文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.selectedBackground", "补全选中", "补全选中项背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.secondaryForeground", "补全次要文字", "补全次要文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSuggestWidget.corner", "补全角标", "补全窗口角标/装饰"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorHoverWidget.background", "悬浮背景", "悬浮提示背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorHoverWidget.foreground", "悬浮文字", "悬浮提示文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorHoverWidget.highlightForeground", "悬浮高亮文字", "悬浮提示高亮文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorHoverWidget.border", "悬浮边框", "悬浮提示边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "scrollbarSlider.background", "滚动条", "滚动条滑块"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "scrollbarSlider.track", "滚动条轨道", "滚动条轨道"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "scrollbarSlider.activeBackground", "活动滚动条", "按下/活动滚动条滑块"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorMinimap.background", "小地图背景", "代码小地图背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorMinimap.viewportBackground", "小地图视口", "代码小地图当前视口"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorMinimap.viewportBorder", "小地图视口边框", "代码小地图当前视口边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorInlayHint.foreground", "内联提示文字", "内联提示文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorInlayHint.background", "内联提示背景", "内联提示背景"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "namespace", "命名空间", "Tree-sitter/语义高亮命名空间"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "type", "类型", "类型名称"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "function", "函数", "普通函数"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "parameter", "参数", "函数参数"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "variable", "变量", "普通变量"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "property", "属性/字段", "对象属性、字段、JSON/YAML key"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "macro", "宏", "宏与特殊函数"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "enumMember", "枚举成员/常量", "枚举成员、常量、true/false/null"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "method", "方法", "成员函数、方法"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "keyword", "关键字", "语言关键字"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "operator", "运算符", "运算符与特殊标点"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "comment", "注释", "代码注释"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "string", "字符串", "字符串字面量"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "number", "数字", "数字字面量"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "modifier", "修饰符", "修饰关键字"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "typeParameter", "类型参数", "模板/泛型类型参数"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "class", "类/结构体", "class/struct 类型"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "enum", "枚举类型", "enum 类型"),
        editor_theme_color_definition(editor_theme_color_section.SYNTAX, "punctuation", "标点", "括号、逗号、冒号等标点"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorError.foreground", "错误", "错误诊断颜色"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorWarning.foreground", "警告", "警告诊断颜色"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorInfo.foreground", "信息", "信息诊断颜色"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "tooltipBackground", "诊断提示背景", "错误、警告提示背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "tooltipBriefMessageColor", "诊断标题", "诊断主要文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "tooltipDetailedMessageColor", "诊断详情", "诊断详细说明文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "tooltipActionColor", "诊断操作", "诊断可点击操作文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "signatureHelp.background", "参数提示背景", "函数参数提示背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "signatureHelp.border", "参数提示边框", "函数参数提示边框"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "signatureHelp.foreground", "参数提示文字", "函数参数提示普通文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "signatureHelp.activeParameterForeground", "当前参数文字", "当前参数高亮文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorTextAction.background", "文本操作背景", "复制/粘贴操作栏背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorTextAction.iconForeground", "文本操作图标", "复制/粘贴操作栏图标"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorHardWrapMarker.foreground", "自动换行标记", "硬换行/自动换行标记"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSnippet.activeBackground", "片段编辑背景", "正在编辑的 snippet 背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSnippet.relatedBackground", "片段关联背景", "关联 snippet 占位背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorSnippet.inactiveBackground", "片段非活动背景", "非活动 snippet 占位背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorUnderline.foreground", "下划线", "普通下划线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorStrikethrough.foreground", "删除线", "删除线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorLineDivider.foreground", "行分隔线", "编辑器行分隔线"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorStaticSpan.background", "静态标记背景", "静态 Span 背景"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorStaticSpan.foreground", "静态标记文字", "静态 Span 文字"),
        editor_theme_color_definition(editor_theme_color_section.COLORS, "editorFunctionChar.border", "函数字符边框", "函数字符背景描边")
    )

    private val syntax_keys = color_definitions
        .filter { definition -> definition.section == editor_theme_color_section.SYNTAX }
        .map { definition -> definition.key }
        .toSet()

    fun init(context: Context) {
        set_current_theme(context)
    }

    fun set_current_theme(context: Context) {
        ensure_user_theme(context)
    }

    fun user_theme_file(context: Context): File {
        return File(File(context.filesDir, USER_THEME_DIR), USER_THEME_FILE)
    }

    fun load_color_items(context: Context): List<editor_theme_color_item> {
        val values = load_color_map(context)
        return color_definitions.map { definition ->
            editor_theme_color_item(
                key = definition.key,
                title = definition.title,
                description = definition.description,
                value = values[definition.key].orEmpty()
            )
        }
    }

    fun load_theme_object(context: Context): JSONObject? {
        return runCatching { read_theme_json(context, ensure_user_theme(context)) }.getOrNull()
    }

    fun load_color_object(context: Context): JSONObject? {
        return load_theme_object(context)?.optJSONObject("colors")
    }

    fun load_syntax_object(context: Context): JSONObject? {
        return runCatching {
            read_theme_json(context, ensure_user_theme(context)).optJSONObject("syntax")
        }.getOrNull()
    }

    fun load_preview_palette(context: Context): editor_theme_preview_palette {
        val values = load_color_map(context)

        fun editor_color(key: String, fallback: String): String {
            return values[key].orEmpty().ifBlank { fallback }
        }

        return editor_theme_preview_palette(
            background = editor_color("editor.background", "#1A242E"),
            foreground = editor_color("editor.foreground", "#E8EDF2"),
            cursor = editor_color("editorCursor.foreground", "#4A9EFF"),
            selection = editor_color("editor.selectionBackground", "#2A3A4A"),
            line_highlight = editor_color("editor.lineHighlightBackground", "#1E2A36"),
            indent_line = editor_color("editorIndentGuide.background", "#2A3A4A"),
            block_line = editor_color("editorIndentGuide.activeBackground", "#5A7A9A"),
            line_number = editor_color("editorLineNumber.foreground", "#5A7A9A"),
            active_line_number = editor_color("editorLineNumber.activeForeground", "#4A9EFF"),
            keyword = editor_color("keyword", "#FF8F73"),
            keyword_directive = editor_color("keyword", "#FF5370"),
            type = editor_color("type", "#4EC9B0"),
            type_builtin = editor_color("type", "#5DAEFF"),
            namespace = editor_color("namespace", "#B7A6FF"),
            function = editor_color("function", "#DCDCAA"),
            method = editor_color("method", "#82AAFF"),
            string = editor_color("string", "#A6E3A1"),
            string_special = editor_color("macro", "#F6C177"),
            number = editor_color("number", "#F6C177"),
            comment = editor_color("comment", "#6F8496"),
            constant = editor_color("enumMember", "#FFB86C"),
            constant_builtin = editor_color("enumMember", "#F78C6C"),
            variable_builtin = editor_color("variable", "#FFCB6B"),
            property = editor_color("property", "#9CDCFE"),
            operator = editor_color("operator", "#89DDFF"),
            punctuation = editor_color("punctuation", "#A9BED1"),
            punctuation_bracket = editor_color("punctuation", "#DDE7F0"),
            punctuation_delimiter = editor_color("punctuation", "#A9BED1")
        )
    }

    fun update_color(context: Context, key: String, value: String): Boolean {
        val normalized = normalize_color(value) ?: return false
        return runCatching {
            val theme_file = ensure_user_theme(context)
            val json = read_theme_json(context, theme_file)
            val section_name = if (key in syntax_keys) "syntax" else "colors"
            val section = json.optJSONObject(section_name) ?: JSONObject().also { json.put(section_name, it) }
            section.put(key, normalized)
            write_theme_json(theme_file, json)
            reload_theme(context)
            true
        }.getOrElse { error ->
            logger_manager.e("editor_theme_manager", "Failed to update editor theme color: ${error.message}", error)
            false
        }
    }

    fun reset_to_default(context: Context): Boolean {
        return runCatching {
            copy_default_theme(context, user_theme_file(context))
            reload_theme(context)
            true
        }.getOrElse { error ->
            logger_manager.e("editor_theme_manager", "Failed to reset editor theme: ${error.message}", error)
            false
        }
    }

    fun reload_theme(context: Context): Boolean {
        return runCatching {
            ensure_user_theme(context)
            _version.value = _version.value + 1
            true
        }.getOrElse { error ->
            logger_manager.e("editor_theme_manager", "Failed to reload editor theme: ${error.message}", error)
            false
        }
    }

    fun normalize_color(value: String): String? {
        val hex = value.trim().removePrefix("#")
        if (hex.length != 6 && hex.length != 8) return null
        if (!hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) return null
        return "#${hex.uppercase(Locale.US)}"
    }

    fun parse_color_argb(value: String): Int? {
        val normalized = normalize_color(value) ?: return null
        val hex = normalized.removePrefix("#")
        return runCatching {
            when (hex.length) {
                6 -> (0xFF000000L or hex.toLong(16)).toInt()
                8 -> {
                    val rgba = hex.toLong(16)
                    val red = (rgba shr 24) and 0xFF
                    val green = (rgba shr 16) and 0xFF
                    val blue = (rgba shr 8) and 0xFF
                    val alpha = rgba and 0xFF
                    ((alpha shl 24) or (red shl 16) or (green shl 8) or blue).toInt()
                }
                else -> null
            }
        }.getOrNull()
    }

    private fun ensure_user_theme(context: Context): File {
        val theme_file = user_theme_file(context)
        if (!theme_file.exists()) {
            copy_default_theme(context, theme_file)
        }
        merge_theme_defaults(context, theme_file)
        return theme_file
    }

    private fun copy_default_theme(context: Context, target: File) {
        target.parentFile?.mkdirs()
        context.assets.open(DEFAULT_THEME_ASSET).use { input ->
            target.outputStream().use { output -> input.copyTo(output) }
        }
    }

    private fun merge_theme_defaults(context: Context, theme_file: File) {
        val defaults = read_default_theme_json(context)
        val current = read_theme_json_or_default(context, theme_file)
        var changed = false

        if (!current.has("name")) {
            current.put("name", defaults.optString("name", "XCode"))
            changed = true
        }
        if (!current.has("type")) {
            current.put("type", defaults.optString("type", "dark"))
            changed = true
        }

        if (migrate_legacy_semantic_colors(current)) {
            changed = true
        }
        if (merge_section_defaults(current, defaults, "colors")) {
            changed = true
        }
        if (merge_section_defaults(current, defaults, "syntax")) {
            changed = true
        }

        if (changed) {
            write_theme_json(theme_file, current)
        }
    }

    private fun merge_section_defaults(current: JSONObject, defaults: JSONObject, section_name: String): Boolean {
        var changed = false
        val current_section = current.optJSONObject(section_name) ?: JSONObject().also {
            current.put(section_name, it)
            changed = true
        }
        val default_section = defaults.optJSONObject(section_name)
        if (default_section != null) {
            val keys = default_section.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (!current_section.has(key)) {
                    current_section.put(key, default_section.optString(key))
                    changed = true
                }
            }
        }
        return changed
    }

    private fun migrate_legacy_semantic_colors(theme: JSONObject): Boolean {
        val colors = theme.optJSONObject("colors") ?: return false
        val syntax = theme.optJSONObject("syntax") ?: JSONObject().also { theme.put("syntax", it) }
        var changed = false
        syntax_keys.forEach { key ->
            val legacy_key = "semantic.$key"
            if (colors.has(legacy_key)) {
                if (!syntax.has(key)) {
                    syntax.put(key, colors.optString(legacy_key))
                }
                colors.remove(legacy_key)
                changed = true
            }
        }
        return changed
    }

    private fun load_color_map(context: Context): Map<String, String> {
        val theme_file = ensure_user_theme(context)
        val current = read_theme_json(context, theme_file)
        val defaults = read_default_theme_json(context)
        return color_definitions.associate { definition ->
            val section_name = when (definition.section) {
                editor_theme_color_section.COLORS -> "colors"
                editor_theme_color_section.SYNTAX -> "syntax"
            }
            val value = current.optJSONObject(section_name)?.optString(definition.key).orEmpty()
                .ifBlank { defaults.optJSONObject(section_name)?.optString(definition.key).orEmpty() }
            definition.key to value
        }
    }

    private fun read_default_theme_json(context: Context): JSONObject {
        return JSONObject(
            context.assets.open(DEFAULT_THEME_ASSET)
                .bufferedReader()
                .use { it.readText() }
        )
    }

    private fun read_theme_json(context: Context, theme_file: File): JSONObject {
        return read_theme_json_or_default(context, theme_file)
    }

    private fun read_theme_json_or_default(context: Context, theme_file: File): JSONObject {
        val content = runCatching { theme_file.readText() }.getOrNull()
        if (content.isNullOrBlank()) {
            return read_default_theme_json(context)
        }
        return runCatching { JSONObject(content) }.getOrElse { read_default_theme_json(context) }
    }

    private fun write_theme_json(theme_file: File, json: JSONObject) {
        theme_file.parentFile?.mkdirs()
        theme_file.writeText(json.toString(2))
    }
}
