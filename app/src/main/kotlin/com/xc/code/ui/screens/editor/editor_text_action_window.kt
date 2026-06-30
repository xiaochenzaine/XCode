package com.xc.code.ui.screens.editor

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xc.code.R
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class editor_text_action_window(
    editor: CodeEditor
) : EditorTextActionWindow(editor) {

    private val action_buttons = mutableListOf<LinearLayout>()

    init {
        val root = LayoutInflater.from(editor.context).inflate(R.layout.xcode_text_action_window, null)
        setContentView(root)

        bind(R.id.panel_btn_select_all, root) { editor.selectAll() }
        bind(R.id.panel_btn_copy, root) { editor.copyText() }
        bind(R.id.panel_btn_paste, root) { editor.pasteText() }
        bind(R.id.panel_btn_cut, root) { editor.cutText() }
    }

    private fun bind(id: Int, root: View, action: () -> Unit) {
        val btn = root.findViewById<LinearLayout>(id) ?: return
        action_buttons += btn
        btn.setOnClickListener {
            action()
            dismiss()
        }
    }

    override fun applyColorScheme() {
        super.applyColorScheme()
        val color = editor.colorScheme.getColor(EditorColorScheme.TEXT_ACTION_WINDOW_ICON_COLOR)
        action_buttons.forEach { container ->
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
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
