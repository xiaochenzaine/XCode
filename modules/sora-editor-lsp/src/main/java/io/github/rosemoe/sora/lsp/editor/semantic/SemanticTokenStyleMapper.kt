package io.github.rosemoe.sora.lsp.editor.semantic

import io.github.rosemoe.sora.lang.styling.TextStyle

/**
 * C/C++ semantic tokens 使用的自定义颜色 ID。
 * 这些颜色由 XCode 自定义 EditorColorScheme 从主题 JSON 的 syntax 节点读取。
 */
object SemanticTokenStyleMapper {
    const val COLOR_NAMESPACE = 300
    const val COLOR_TYPE = 301
    const val COLOR_FUNCTION = 302
    const val COLOR_PARAMETER = 303
    const val COLOR_VARIABLE = 304
    const val COLOR_PROPERTY = 305
    const val COLOR_MACRO = 306
    const val COLOR_ENUM_MEMBER = 307
    const val COLOR_METHOD = 308
    const val COLOR_KEYWORD = 309
    const val COLOR_OPERATOR = 310
    const val COLOR_COMMENT = 311
    const val COLOR_STRING = 312
    const val COLOR_NUMBER = 313
    const val COLOR_MODIFIER = 314
    const val COLOR_TYPE_PARAMETER = 315
    const val COLOR_CLASS = 316
    const val COLOR_ENUM = 317

    fun styleFor(token: SemanticToken): Long? {
        val colorId = when (token.tokenType) {
            "namespace" -> COLOR_NAMESPACE
            "type", "struct", "interface" -> COLOR_TYPE
            "class" -> COLOR_CLASS
            "enum" -> COLOR_ENUM
            "typeParameter" -> COLOR_TYPE_PARAMETER
            "function" -> COLOR_FUNCTION
            "method" -> COLOR_METHOD
            "parameter" -> COLOR_PARAMETER
            "property", "field" -> COLOR_PROPERTY
            "macro" -> COLOR_MACRO
            "enumMember" -> COLOR_ENUM_MEMBER
            "variable" -> COLOR_VARIABLE
            "keyword" -> COLOR_KEYWORD
            "operator" -> COLOR_OPERATOR
            "comment" -> COLOR_COMMENT
            "string" -> COLOR_STRING
            "number" -> COLOR_NUMBER
            "modifier" -> COLOR_MODIFIER
            else -> return null
        }
        // semantic tokens 只负责精确颜色，不改变字体粗细、斜体或删除线，避免编辑时文字形态跳动。
        return TextStyle.makeStyle(
            colorId,
            0,
            false,
            false,
            false
        )
    }
}
