package com.xc.code.ui.screens.editor

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.xc.code.R
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import com.xc.code.ui.theme.resolve_app_colors
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow

class editor_text_action_window(
    editor: CodeEditor,
    private val show_edit_actions: Boolean = true
) : EditorTextActionWindow(editor) {

    private var root_view: LinearLayout? = null
    private var action_buttons: MutableList<LinearLayout>? = null
    private var dividers: MutableList<android.view.View>? = null

    init {
        action_buttons = mutableListOf()
        dividers = mutableListOf()
        val dp = editor.dpUnit
        val root = LinearLayout(editor.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val container = LinearLayout(editor.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        root.addView(container)
        root_view = root
        setContentView(root)
        setSize(0, (48 * dp).toInt())
        popup.animationStyle = io.github.rosemoe.sora.R.style.text_action_popup_animation

        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_copy_20, editor.context.getString(R.string.common_copy)) { editor.copyText() }
        add_divider(container, dp)
        if (show_edit_actions) {
            add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_cut_20, editor.context.getString(R.string.common_cut)) { editor.cutText() }
            add_divider(container, dp)
            add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_paste_20, editor.context.getString(R.string.common_paste)) { editor.pasteText() }
            add_divider(container, dp)
        }
        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_select_all_20, editor.context.getString(R.string.common_select_all)) { editor.selectAll() }
        applyColorScheme()
    }

    private fun add_divider(container: LinearLayout, dp: Float) {
        val divider = android.view.View(editor.context).apply {
            layoutParams = LinearLayout.LayoutParams((1 * dp).toInt().coerceAtLeast(1), ViewGroup.LayoutParams.MATCH_PARENT).apply {
                topMargin = (8 * dp).toInt()
                bottomMargin = (8 * dp).toInt()
            }
        }
        dividers?.add(divider)
        container.addView(divider)
    }

    private fun add_button(
        container: LinearLayout,
        dp: Float,
        icon_res: Int,
        label: String,
        action: () -> Unit
    ) {
        val padding_h = (4 * dp).toInt()
        val padding_v = (3 * dp).toInt()
        val icon_size = (18 * dp).toInt()
        val button_size = (45 * dp).toInt()

        val btn = LinearLayout(editor.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(padding_h, padding_v, padding_h, padding_v)
            val bg = android.util.TypedValue()
            editor.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, bg, true)
            setBackgroundResource(bg.resourceId)
            isClickable = true
            isFocusable = true
            layoutParams = ViewGroup.LayoutParams(button_size, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val icon = ImageView(editor.context).apply {
            setImageResource(icon_res)
            layoutParams = LinearLayout.LayoutParams(icon_size, icon_size)
        }

        val text = TextView(editor.context).apply {
            this.text = label
            textSize = 10f
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = (2 * dp).toInt()
            }
        }

        btn.addView(icon)
        btn.addView(text)
        btn.setOnClickListener {
            action()
            dismiss()
        }

        action_buttons?.add(btn)
        container.addView(btn)
    }

    override fun applyColorScheme() {
        val root = root_view ?: return
        val colors = resolve_app_colors(editor.context)
        val background = GradientDrawable().apply {
            cornerRadius = 5 * editor.dpUnit
            setColor(colors.editor_text_action_bg.toArgb())
        }
        root.background = background
        dividers?.forEach { divider ->
            divider.setBackgroundColor(colors.editor_text_action_divider.toArgb())
        }

        val buttons = action_buttons ?: return
        if (buttons.isEmpty()) return
        val color = colors.editor_text_action_content.toArgb()
        buttons.forEach { btn ->
            for (i in 0 until btn.childCount) {
                val child = btn.getChildAt(i)
                if (child is ImageView) {
                    child.setColorFilter(PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP))
                }
                if (child is TextView) {
                    child.setTextColor(color)
                }
            }
        }
    }

}
