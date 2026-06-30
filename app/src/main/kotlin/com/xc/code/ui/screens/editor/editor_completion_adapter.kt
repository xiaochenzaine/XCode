package com.xc.code.ui.screens.editor

import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.rosemoe.sora.graphics.Paint
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class editor_completion_adapter : EditorCompletionAdapter() {

    override fun getItemHeight(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 50f,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        is_current: Boolean
    ): View {
        val item = getItem(position)
        val dp = context.resources.displayMetrics.density
        val pad_h = (10 * dp).toInt()
        val pad_v = (6 * dp).toInt()
        val icon_size = (18 * dp).toInt()

        val root = (convertView as? LinearLayout) ?: LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(pad_h, pad_v, pad_h, pad_v)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.removeAllViews()

        val icon = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(icon_size, icon_size).apply {
                rightMargin = (8 * dp).toInt()
            }
            setImageDrawable(item.icon)
        }
        root.addView(icon)

        val text_col = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val label = TextView(context).apply {
            this.text = item.label
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = Typeface.DEFAULT_BOLD
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
            setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY))
        }
        text_col.addView(label)

        val detail_text = item.detail
        if (!detail_text.isNullOrBlank()) {
            val detail = TextView(context).apply {
                this.text = detail_text
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                maxLines = 2
                ellipsize = android.text.TextUtils.TruncateAt.END
                setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY))
            }
            text_col.addView(detail)
        }
        root.addView(text_col)

        val desc_text = item.desc
        if (!desc_text.isNullOrBlank()) {
            val desc = TextView(context).apply {
                this.text = desc_text
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                maxLines = 1
                gravity = Gravity.END
                ellipsize = android.text.TextUtils.TruncateAt.END
                setTextColor(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = (6 * dp).toInt()
                }
            }
            root.addView(desc)
        }

        if (item.deprecated) {
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)
                if (child is TextView) {
                    child.paintFlags = child.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }

        root.setBackgroundColor(
            if (is_current) getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT)
            else 0
        )

        return root
    }
}
