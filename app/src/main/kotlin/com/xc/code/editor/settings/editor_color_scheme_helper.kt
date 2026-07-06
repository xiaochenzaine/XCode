package com.xc.code.editor.settings

import android.content.Context
import com.xc.code.editor.theme.editor_theme_manager
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import org.json.JSONObject

internal fun apply_editor_colors(context: Context, editor: CodeEditor) {
    editor.colorScheme = xcode_editor_color_scheme(context)
}

private class xcode_editor_color_scheme(context: Context) : EditorColorScheme() {
    private val colors: JSONObject? = editor_theme_manager.load_color_object(context)
    private val syntax: JSONObject? = editor_theme_manager.load_syntax_object(context)

    init {
        applyDefault()
        apply_theme_colors()
    }

    override fun isDark(): Boolean = true

    private fun apply_theme_colors() {
        set_theme_color(WHOLE_BACKGROUND, "editor.background")
        set_theme_color(LINE_NUMBER_BACKGROUND, "editor.background")
        set_theme_color(TEXT_NORMAL, "editor.foreground")
        set_theme_color(SELECTION_INSERT, "editorCursor.foreground")
        set_theme_color(SELECTED_TEXT_BACKGROUND, "editor.selectionBackground")
        set_theme_color(TEXT_SELECTED, "editor.selectionForeground")
        set_theme_color(SELECTED_TEXT_BORDER, "editor.selectionBorder")
        set_theme_color(SELECTION_HANDLE, "editor.selectionHandle")
        set_theme_color(CURRENT_LINE, "editor.lineHighlightBackground")
        set_theme_color(CURRENT_ROW_BORDER, "editor.lineHighlightBorder")
        set_theme_color(NON_PRINTABLE_CHAR, "editorWhitespace.foreground")
        set_theme_color(LINE_NUMBER, "editorLineNumber.foreground")
        set_theme_color(LINE_NUMBER_CURRENT, "editorLineNumber.activeForeground")
        set_theme_color(LINE_NUMBER_PANEL, "editorLineNumber.panelBackground")
        set_theme_color(LINE_NUMBER_PANEL_TEXT, "editorLineNumber.panelForeground")
        set_theme_color(BLOCK_LINE, "editorIndentGuide.background")
        set_theme_color(BLOCK_LINE_CURRENT, "editorIndentGuide.activeBackground")
        set_theme_color(SIDE_BLOCK_LINE, "editorSideBlockLine.foreground", "editorIndentGuide.activeBackground")
        set_theme_color(HIGHLIGHTED_DELIMITERS_FOREGROUND, "highlightedDelimitersForeground")
        set_theme_color(HIGHLIGHTED_DELIMITERS_BACKGROUND, "highlightedDelimitersBackground")
        set_theme_color(HIGHLIGHTED_DELIMITERS_BORDER, "highlightedDelimitersBorder")
        set_theme_color(HIGHLIGHTED_DELIMITERS_UNDERLINE, "highlightedDelimitersUnderline")
        set_theme_color(MATCHED_TEXT_BACKGROUND, "editor.findMatchBackground")
        set_theme_color(MATCHED_TEXT_BORDER, "editor.findMatchBorder")
        set_theme_color(TEXT_HIGHLIGHT_BACKGROUND, "editor.wordHighlightBackground")
        set_theme_color(TEXT_HIGHLIGHT_STRONG_BACKGROUND, "editor.wordHighlightStrongBackground")
        set_theme_color(TEXT_HIGHLIGHT_BORDER, "editor.wordHighlightBorder")
        set_theme_color(TEXT_HIGHLIGHT_STRONG_BORDER, "editor.wordHighlightStrongBorder")
        set_theme_color(COMPLETION_WND_BACKGROUND, "editorSuggestWidget.background")
        set_theme_color(COMPLETION_WND_TEXT_PRIMARY, "editorSuggestWidget.foreground")
        set_theme_color(COMPLETION_WND_TEXT_MATCHED, "editorSuggestWidget.highlightForeground")
        set_theme_color(COMPLETION_WND_ITEM_CURRENT, "editorSuggestWidget.selectedBackground")
        set_theme_color(COMPLETION_WND_TEXT_SECONDARY, "editorSuggestWidget.secondaryForeground")
        set_theme_color(COMPLETION_WND_CORNER, "editorSuggestWidget.corner")
        set_theme_color(TEXT_INLAY_HINT_FOREGROUND, "editorInlayHint.foreground")
        set_theme_color(TEXT_INLAY_HINT_BACKGROUND, "editorInlayHint.background")
        set_theme_color(DIAGNOSTIC_TOOLTIP_BACKGROUND, "tooltipBackground")
        set_theme_color(DIAGNOSTIC_TOOLTIP_BRIEF_MSG, "tooltipBriefMessageColor")
        set_theme_color(DIAGNOSTIC_TOOLTIP_DETAILED_MSG, "tooltipDetailedMessageColor")
        set_theme_color(DIAGNOSTIC_TOOLTIP_ACTION, "tooltipActionColor")
        set_theme_color(PROBLEM_ERROR, "editorError.foreground")
        set_theme_color(PROBLEM_WARNING, "editorWarning.foreground")
        set_theme_color(PROBLEM_TYPO, "editorInfo.foreground")
        set_theme_color(HOVER_BACKGROUND, "editorHoverWidget.background")
        set_theme_color(HOVER_TEXT_NORMAL, "editorHoverWidget.foreground")
        set_theme_color(HOVER_TEXT_HIGHLIGHTED, "editorHoverWidget.highlightForeground")
        set_theme_color(HOVER_BORDER, "editorHoverWidget.border")
        set_theme_color(SCROLL_BAR_THUMB, "scrollbarSlider.background")
        set_theme_color(SCROLL_BAR_THUMB_PRESSED, "scrollbarSlider.activeBackground")
        set_theme_color(SCROLL_BAR_TRACK, "scrollbarSlider.track")
        set_theme_color(MINIMAP_BACKGROUND, "editorMinimap.background")
        set_theme_color(MINIMAP_VIEWPORT, "editorMinimap.viewportBackground")
        set_theme_color(MINIMAP_VIEWPORT_BORDER, "editorMinimap.viewportBorder")
        set_theme_color(SIGNATURE_BACKGROUND, "signatureHelp.background")
        set_theme_color(SIGNATURE_BORDER, "signatureHelp.border")
        set_theme_color(SIGNATURE_TEXT_NORMAL, "signatureHelp.foreground")
        set_theme_color(SIGNATURE_TEXT_HIGHLIGHTED_PARAMETER, "signatureHelp.activeParameterForeground")
        set_theme_color(TEXT_ACTION_WINDOW_BACKGROUND, "editorTextAction.background")
        set_theme_color(TEXT_ACTION_WINDOW_ICON_COLOR, "editorTextAction.iconForeground")
        set_theme_color(HARD_WRAP_MARKER, "editorHardWrapMarker.foreground")
        set_theme_color(SNIPPET_BACKGROUND_EDITING, "editorSnippet.activeBackground")
        set_theme_color(SNIPPET_BACKGROUND_RELATED, "editorSnippet.relatedBackground")
        set_theme_color(SNIPPET_BACKGROUND_INACTIVE, "editorSnippet.inactiveBackground")
        set_theme_color(UNDERLINE, "editorUnderline.foreground")
        set_theme_color(STRIKETHROUGH, "editorStrikethrough.foreground")
        set_theme_color(LINE_DIVIDER, "editorLineDivider.foreground")
        set_theme_color(STATIC_SPAN_BACKGROUND, "editorStaticSpan.background")
        set_theme_color(STATIC_SPAN_FOREGROUND, "editorStaticSpan.foreground")
        set_theme_color(FUNCTION_CHAR_BACKGROUND_STROKE, "editorFunctionChar.border")

        set_syntax_color(KEYWORD, "keyword")
        set_syntax_color(OPERATOR, "operator")
        set_syntax_color(COMMENT, "comment")
        set_syntax_color(LITERAL, "string")
        set_syntax_color(FUNCTION_NAME, "function")
        set_syntax_color(IDENTIFIER_NAME, "variable")
        set_syntax_color(IDENTIFIER_VAR, "variable")
        set_syntax_color(ATTRIBUTE_NAME, "property")
        set_syntax_color(ATTRIBUTE_VALUE, "string")
        set_syntax_color(ANNOTATION, "modifier")

        set_syntax_color(300, "namespace")
        set_syntax_color(301, "type")
        set_syntax_color(302, "function")
        set_syntax_color(303, "parameter")
        set_syntax_color(304, "variable")
        set_syntax_color(305, "property")
        set_syntax_color(306, "macro")
        set_syntax_color(307, "enumMember")
        set_syntax_color(308, "method")
        set_syntax_color(309, "keyword")
        set_syntax_color(310, "operator")
        set_syntax_color(311, "comment")
        set_syntax_color(312, "string")
        set_syntax_color(313, "number")
        set_syntax_color(314, "modifier")
        set_syntax_color(315, "typeParameter")
        set_syntax_color(316, "class")
        set_syntax_color(317, "enum")
        set_syntax_color(318, "punctuation")
    }

    private fun set_syntax_color(color_id: Int, key: String) {
        val value = syntax?.optString(key).orEmpty()
        val color = editor_theme_manager.parse_color_argb(value) ?: return
        setColor(color_id, color)
    }

    private fun set_theme_color(color_id: Int, key: String, fallback_key: String? = null) {
        val value = colors?.optString(key).orEmpty().ifBlank {
            fallback_key?.let { colors?.optString(it).orEmpty() }.orEmpty()
        }
        val color = editor_theme_manager.parse_color_argb(value) ?: return
        setColor(color_id, color)
    }
}
