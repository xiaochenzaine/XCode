package io.github.rosemoe.sora.lsp.editor.semantic

import android.os.Bundle
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.analysis.StyleReceiver
import io.github.rosemoe.sora.lang.brackets.BracketsProvider
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer
import io.github.rosemoe.sora.lang.styling.MappedSpans
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.Spans
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.inlayHint.InlayHintsContainer
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.widget.CodeEditor
import java.lang.ref.WeakReference
import java.util.TreeSet

/**
 * 在 TextMate 结果之上叠加 clangd semantic tokens。
 */
class SemanticTokensAnalyzeManager(
    private val base: AnalyzeManager,
    editorProvider: () -> CodeEditor?,
    private val onBaseStylesReady: (() -> Unit)? = null
) : AnalyzeManager {
    private val editorRefProvider = editorProvider
    private var receiver: StyleReceiver? = null
    private var baseReceiver: StyleReceiver? = null
    private var lastBaseStyles: Styles? = null
    private var currentTokens: List<SemanticToken> = emptyList()
    private var baseStylesReadyNotified = false

    override fun setReceiver(receiver: StyleReceiver?) {
        this.receiver = receiver
        if (receiver == null) {
            baseReceiver = null
            base.setReceiver(null)
            return
        }
        val wrapper = object : StyleReceiver {
            override fun setStyles(sourceManager: AnalyzeManager, styles: Styles?) {
                lastBaseStyles = styles
                notifyBaseStylesReady(styles)
                receiver.setStyles(this@SemanticTokensAnalyzeManager, merge(styles))
            }

            override fun setStyles(sourceManager: AnalyzeManager, styles: Styles?, action: Runnable?) {
                lastBaseStyles = styles
                notifyBaseStylesReady(styles)
                receiver.setStyles(this@SemanticTokensAnalyzeManager, merge(styles), action)
            }

            override fun updateStyles(sourceManager: AnalyzeManager, styles: Styles, range: io.github.rosemoe.sora.lang.analysis.StyleUpdateRange) {
                lastBaseStyles = styles
                notifyBaseStylesReady(styles)
                receiver.setStyles(this@SemanticTokensAnalyzeManager, merge(styles))
            }

            override fun setDiagnostics(sourceManager: AnalyzeManager, diagnostics: DiagnosticsContainer?) {
                receiver.setDiagnostics(this@SemanticTokensAnalyzeManager, diagnostics)
            }

            override fun setInlayHints(sourceManager: AnalyzeManager, inlayHints: InlayHintsContainer?) {
                receiver.setInlayHints(this@SemanticTokensAnalyzeManager, inlayHints)
            }

            override fun updateBracketProvider(sourceManager: AnalyzeManager, provider: BracketsProvider?) {
                receiver.updateBracketProvider(this@SemanticTokensAnalyzeManager, provider)
            }
        }
        baseReceiver = wrapper
        base.setReceiver(wrapper)
    }

    fun updateSemanticTokens(tokens: List<SemanticToken>) {
        currentTokens = tokens
        val styles = lastBaseStyles ?: return
        receiver?.setStyles(this, merge(styles))
    }

    override fun reset(content: ContentReference, extraArguments: Bundle) = base.reset(content, extraArguments)
    override fun insert(start: CharPosition, end: CharPosition, insertedContent: CharSequence) = base.insert(start, end, insertedContent)
    override fun delete(start: CharPosition, end: CharPosition, deletedContent: CharSequence) = base.delete(start, end, deletedContent)
    override fun rerun() = base.rerun()
    override fun destroy() = base.destroy()

    private fun notifyBaseStylesReady(styles: Styles?) {
        if (baseStylesReadyNotified || styles?.spans == null) return
        baseStylesReadyNotified = true
        onBaseStylesReady?.invoke()
    }

    private fun merge(styles: Styles?): Styles? {
        val baseSpans = styles?.spans ?: return styles
        val content = editorRefProvider()?.text ?: return styles
        val tokensByLine = currentTokens
            .filter { it.line >= 0 && it.line < content.lineCount && it.length > 0 }
            .groupBy { it.line }
        if (tokensByLine.isEmpty()) return styles

        val builder = MappedSpans.Builder(content.lineCount)
        val reader = baseSpans.read()
        val lastLine = content.lineCount - 1
        for (line in 0..lastLine) {
            val lineText = content.getLine(line)
            val lineLength = lineText.length
            val semanticTokens = tokensByLine[line].orEmpty()
            reader.moveToLine(line)
            val merged = if (semanticTokens.isEmpty()) {
                reader.getSpansOnLine(line).map { it.copy() }
            } else {
                mergeLine(reader.getSpansOnLine(line), semanticTokens, lineLength)
            }
            merged.forEach { builder.add(line, it) }
        }
        reader.moveToLine(-1)
        builder.determine(lastLine)

        return Styles(builder.build(), false).also { merged ->
            merged.blocks = styles.blocks
            merged.lineStyles = styles.lineStyles
            merged.styleTypeCount = styles.styleTypeCount
            merged.suppressSwitch = styles.suppressSwitch
            merged.indentCountMode = styles.indentCountMode
        }
    }

    private fun mergeLine(baseLine: List<Span>, semanticTokens: List<SemanticToken>, lineLength: Int): List<Span> {
        val cuts = TreeSet<Int>()
        cuts.add(0)
        cuts.add(lineLength)
        baseLine.forEach { cuts.add(it.column.coerceIn(0, lineLength)) }
        semanticTokens.forEach {
            cuts.add(it.start.coerceIn(0, lineLength))
            cuts.add(it.end.coerceIn(0, lineLength))
        }

        val result = ArrayList<Span>()
        var lastStyle: Long? = null
        val points = cuts.toList().filter { it < lineLength || it == 0 }
        for (point in points) {
            val semantic = semanticTokens.lastOrNull { point >= it.start && point < it.end }
            val style = semantic?.let { SemanticTokenStyleMapper.styleFor(it) } ?: styleAt(baseLine, point)
            if (lastStyle != style || result.isEmpty()) {
                result.add(Span.obtain(point, style))
                lastStyle = style
            }
        }
        if (result.isEmpty()) {
            result.add(baseLine.firstOrNull()?.copy() ?: Span.obtain(0, io.github.rosemoe.sora.widget.schemes.EditorColorScheme.TEXT_NORMAL.toLong()))
        }
        return result
    }

    private fun styleAt(spans: List<Span>, column: Int): Long {
        var current = spans.first().style
        for (span in spans) {
            if (span.column > column) break
            current = span.style
        }
        return current
    }
}
