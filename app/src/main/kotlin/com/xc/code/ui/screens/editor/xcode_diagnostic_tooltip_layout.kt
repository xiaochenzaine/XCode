package com.xc.code.ui.screens.editor

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticDetail
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion
import io.github.rosemoe.sora.widget.component.DiagnosticTooltipLayout
import io.github.rosemoe.sora.widget.component.EditorDiagnosticTooltipWindow
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

class xcode_diagnostic_tooltip_layout : DiagnosticTooltipLayout {

    private lateinit var window: EditorDiagnosticTooltipWindow
    private lateinit var root: LinearLayout
    private lateinit var messageText: TextView
    private val background = GradientDrawable()

    private var pointer_over = false
    private var corner_radius_dp = 10f
    private var border_width_dp = 1f

    private val severity_colors = mapOf(
        DiagnosticRegion.SEVERITY_ERROR to Color.parseColor("#FFF87171"),
        DiagnosticRegion.SEVERITY_WARNING to Color.parseColor("#FFFACC15"),
        DiagnosticRegion.SEVERITY_TYPO to Color.parseColor("#FF38BDF8"),
        DiagnosticRegion.SEVERITY_NONE to Color.parseColor("#FF94A3B8")
    )

    override fun attach(w: EditorDiagnosticTooltipWindow) {
        window = w
    }

    override fun createView(inflater: LayoutInflater): View {
        val dp = window.editor.dpUnit

        root = LinearLayout(inflater.context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (10 * dp).toInt()
            setPadding(pad, (8 * dp).toInt(), pad, (8 * dp).toInt())
            minimumWidth = (120 * dp).toInt()
            clipToOutline = true
            setOnGenericMotionListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_HOVER_ENTER -> pointer_over = true
                    MotionEvent.ACTION_HOVER_EXIT -> pointer_over = false
                }
                false
            }
        }

        messageText = TextView(inflater.context).apply {
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(messageText)

        background.cornerRadius = dp * corner_radius_dp
        root.background = background

        return root
    }

    override fun applyColorScheme(scheme: EditorColorScheme) {
        val dp = window.editor.dpUnit
        val text_color = scheme.getColor(EditorColorScheme.TEXT_NORMAL)
        val bg_color = scheme.getColor(EditorColorScheme.WHOLE_BACKGROUND)

        messageText.setTextColor(text_color)
        messageText.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)

        background.setColor(darken(bg_color, 0.06f))
        background.cornerRadius = dp * corner_radius_dp
        background.setStroke(
            (dp * border_width_dp).toInt().coerceAtLeast(1),
            Color.TRANSPARENT
        )
    }

    override fun renderDiagnostic(diagnostic: DiagnosticDetail?) {
        renderDiagnostic(diagnostic, null)
    }

    override fun renderDiagnostic(diagnostic: DiagnosticDetail?, region: DiagnosticRegion?) {
        if (diagnostic == null) {
            messageText.visibility = View.GONE
            return
        }
        val msg = diagnostic.detailedMessage
        if (msg.isNullOrBlank()) {
            messageText.visibility = View.GONE
            return
        }
        messageText.visibility = View.VISIBLE
        messageText.text = msg

        val severity_color = severity_colors[region?.severity ?: DiagnosticRegion.SEVERITY_NONE]
            ?: severity_colors[DiagnosticRegion.SEVERITY_NONE]!!

        background.setStroke(
            (window.editor.dpUnit * border_width_dp).toInt().coerceAtLeast(1),
            severity_color
        )
    }

    override fun onTextSizeChanged(old: Float, new: Float) {}

    override fun measureContent(max_w: Int, max_h: Int): Pair<Int, Int> {
        root.measure(
            View.MeasureSpec.makeMeasureSpec(max_w, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(max_h, View.MeasureSpec.AT_MOST)
        )
        return root.measuredWidth.coerceAtMost(max_w) to root.measuredHeight.coerceAtMost(max_h)
    }

    override fun isPointerOverPopup() = pointer_over
    override fun isMenuShowing() = false
    override fun onWindowDismissed() { pointer_over = false }

    private fun darken(color: Int, factor: Float): Int {
        val r = ((color shr 16) and 0xFF) * (1f - factor)
        val g = ((color shr 8) and 0xFF) * (1f - factor)
        val b = (color and 0xFF) * (1f - factor)
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
}
