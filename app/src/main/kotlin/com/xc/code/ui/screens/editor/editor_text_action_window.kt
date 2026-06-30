package com.xc.code.ui.screens.editor

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class editor_text_action_window(
    editor: CodeEditor
) : EditorTextActionWindow(editor) {

    private val action_buttons by lazy { mutableListOf<LinearLayout>() }

    init {
        val dp = editor.dpUnit
        val root = LinearLayout(editor.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val scroll = HorizontalScrollView(editor.context).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val container = LinearLayout(editor.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        scroll.addView(container)
        root.addView(scroll)
        setContentView(root)

        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_select_all_20, "全选") { editor.selectAll() }
        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_copy_20, "复制") { editor.copyText() }
        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_paste_20, "粘贴") { editor.pasteText() }
        add_button(container, dp, io.github.rosemoe.sora.R.drawable.round_content_cut_20, "剪切") { editor.cutText() }
    }

    private fun add_button(
        container: LinearLayout,
        dp: Float,
        icon_res: Int,
        label: String,
        action: () -> Unit
    ) {
        val padding_h = (6 * dp).toInt()
        val padding_v = (4 * dp).toInt()
        val icon_size = (16 * dp).toInt()

        val btn = LinearLayout(editor.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(padding_h, padding_v, padding_h, padding_v)
            val bg = android.util.TypedValue()
            editor.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, bg, true)
            setBackgroundResource(bg.resourceId)
            isClickable = true
            isFocusable = true
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val icon = ImageView(editor.context).apply {
            setImageResource(icon_res)
            layoutParams = LinearLayout.LayoutParams(icon_size, icon_size)
        }

        val text = TextView(editor.context).apply {
            this.text = label
            textSize = 9f
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

        action_buttons += btn
        container.addView(btn)
    }

    override fun applyColorScheme() {
        if (action_buttons.isEmpty()) return
        val color = editor.colorScheme.getColor(EditorColorScheme.TEXT_ACTION_WINDOW_ICON_COLOR)
        action_buttons.forEach { btn ->
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
