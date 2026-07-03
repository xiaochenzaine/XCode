package com.xc.code.ui.screens.editor

import androidx.annotation.StringRes
import com.xc.code.R
import com.xc.code.editor.model.editor_settings_state

internal data class editor_settings_switch_item(
    @StringRes val title_res: Int,
    @StringRes val description_res: Int,
    val checked: Boolean,
    val update: (Boolean) -> editor_settings_state
)

internal data class editor_settings_switch_group(
    @StringRes val title_res: Int,
    val items: List<editor_settings_switch_item>
)

internal fun editor_settings_switch_groups(settings: editor_settings_state): List<editor_settings_switch_group> {
    return listOf(
        editor_settings_switch_group(
            title_res = R.string.editor_group_line_number,
            items = listOf(
                editor_settings_switch_item(
                    title_res = R.string.editor_show_line_number,
                    description_res = R.string.editor_show_line_number_desc,
                    checked = settings.line_numbers,
                    update = { value -> settings.copy(line_numbers = value) }
                )
            )
        ),
        editor_settings_switch_group(
            title_res = R.string.editor_group_display,
            items = listOf(
                editor_settings_switch_item(
                    title_res = R.string.editor_current_line,
                    description_res = R.string.editor_current_line_desc,
                    checked = settings.current_line_highlight,
                    update = { value -> settings.copy(current_line_highlight = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_bracket_pair,
                    description_res = R.string.editor_bracket_pair_desc,
                    checked = settings.bracket_pair_highlight,
                    update = { value -> settings.copy(bracket_pair_highlight = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_block_lines,
                    description_res = R.string.editor_block_lines_desc,
                    checked = settings.block_lines,
                    update = { value -> settings.copy(block_lines = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_block_end_hints,
                    description_res = R.string.editor_block_end_hints_desc,
                    checked = settings.block_end_hints,
                    update = { value -> settings.copy(block_end_hints = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_sticky_scroll,
                    description_res = R.string.editor_sticky_scroll_desc,
                    checked = settings.sticky_scroll,
                    update = { value -> settings.copy(sticky_scroll = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_whitespace,
                    description_res = R.string.editor_whitespace_desc,
                    checked = settings.whitespace_symbols,
                    update = { value -> settings.copy(whitespace_symbols = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_line_separator,
                    description_res = R.string.editor_line_separator_desc,
                    checked = settings.line_separator,
                    update = { value -> settings.copy(line_separator = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_font_ligatures,
                    description_res = R.string.editor_font_ligatures_desc,
                    checked = settings.font_ligatures,
                    update = { value -> settings.copy(font_ligatures = value) }
                )
            )
        ),
        editor_settings_switch_group(
            title_res = R.string.editor_group_behavior,
            items = listOf(
                editor_settings_switch_item(
                    title_res = R.string.editor_word_wrap,
                    description_res = R.string.editor_word_wrap_desc,
                    checked = settings.word_wrap,
                    update = { value -> settings.copy(word_wrap = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_pinch_zoom,
                    description_res = R.string.editor_pinch_zoom_desc,
                    checked = settings.pinch_zoom,
                    update = { value -> settings.copy(pinch_zoom = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_cursor_blink,
                    description_res = R.string.editor_cursor_blink_desc,
                    checked = settings.cursor_blink,
                    update = { value -> settings.copy(cursor_blink = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_auto_indent,
                    description_res = R.string.editor_auto_indent_desc,
                    checked = settings.auto_indent,
                    update = { value -> settings.copy(auto_indent = value) }
                )
            )
        ),
        editor_settings_switch_group(
            title_res = R.string.editor_clangd_enabled,
            items = listOf(
                editor_settings_switch_item(
                    title_res = R.string.editor_clangd_enabled,
                    description_res = R.string.editor_clangd_enabled_desc,
                    checked = settings.clangd_enabled,
                    update = { value -> settings.copy(clangd_enabled = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_completion,
                    description_res = R.string.editor_completion_desc,
                    checked = settings.clangd_completion,
                    update = { value -> settings.copy(clangd_completion = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_signature_help,
                    description_res = R.string.editor_signature_help_desc,
                    checked = settings.clangd_signature_help,
                    update = { value -> settings.copy(clangd_signature_help = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_symbol_highlight,
                    description_res = R.string.editor_symbol_highlight_desc,
                    checked = settings.clangd_document_highlight,
                    update = { value -> settings.copy(clangd_document_highlight = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_formatting,
                    description_res = R.string.editor_formatting_desc,
                    checked = settings.clangd_formatting,
                    update = { value -> settings.copy(clangd_formatting = value) }
                ),
                editor_settings_switch_item(
                    title_res = R.string.editor_hover,
                    description_res = R.string.editor_hover_desc,
                    checked = settings.clangd_hover,
                    update = { value -> settings.copy(clangd_hover = value) }
                )
            )
        )
    )
}
