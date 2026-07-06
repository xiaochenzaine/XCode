/*******************************************************************************
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2023  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 ******************************************************************************/

package io.github.rosemoe.sora.lsp.editor

import android.os.Bundle
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException
import io.github.rosemoe.sora.lang.completion.CompletionHelper
import io.github.rosemoe.sora.lang.completion.CompletionItem
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.completion.createCompletionItemComparator
import io.github.rosemoe.sora.lang.completion.filterCompletionItems
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler
import io.github.rosemoe.sora.lsp.editor.completion.CompletionItemProvider
import io.github.rosemoe.sora.lsp.editor.completion.LspCompletionItem
import io.github.rosemoe.sora.lsp.editor.format.LspFormatter
import io.github.rosemoe.sora.lsp.editor.semantic.SemanticToken
import io.github.rosemoe.sora.lsp.editor.semantic.SemanticTokensAnalyzeManager
import io.github.rosemoe.sora.lsp.events.EventType
import io.github.rosemoe.sora.lsp.events.completion.completion
import io.github.rosemoe.sora.lsp.events.document.DocumentChangeEvent
import io.github.rosemoe.sora.lsp.requests.Timeout
import io.github.rosemoe.sora.lsp.requests.Timeouts
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import io.github.rosemoe.sora.util.MyCharacter
import io.github.rosemoe.sora.widget.SymbolPairMatch
import kotlinx.coroutines.future.future
import java.util.concurrent.TimeUnit
import kotlin.math.min


class LspLanguage(var editor: LspEditor) : Language {

    private var _formatter: Formatter? = null

    var wrapperLanguage: Language? = null
    var completionItemProvider: CompletionItemProvider<*>
    private var semanticAnalyzeManager: SemanticTokensAnalyzeManager? = null
    private var semanticBaseAnalyzeManager: AnalyzeManager? = null
    private var pendingSemanticTokens: List<SemanticToken> = emptyList()

    init {
        _formatter = LspFormatter(this)
        completionItemProvider =
            CompletionItemProvider { completionItem, eventManager, prefixLength ->
                LspCompletionItem(
                    completionItem,
                    eventManager,
                    prefixLength
                )
            }
    }

    override fun getAnalyzeManager(): AnalyzeManager {
        val base = wrapperLanguage?.analyzeManager ?: EmptyLanguage.EmptyAnalyzeManager.INSTANCE
        if (base === EmptyLanguage.EmptyAnalyzeManager.INSTANCE) {
            return base
        }
        val current = semanticAnalyzeManager
        if (current != null && semanticBaseAnalyzeManager === base) {
            return current
        }
        semanticBaseAnalyzeManager = base
        return SemanticTokensAnalyzeManager(
            base = base,
            editorProvider = { editor.editor },
            onBaseStylesReady = {
                // 用户现在通过“编辑一下内容”才能触发 clangd 语义颜色，根因是 didOpen 后请求太早。
                // 等 TextMate 首次基础高亮真正进入编辑器后，再主动请求 clangd semantic tokens。
                editor.requestSemanticTokensWithRetry()
            }
        ).also { manager ->
            semanticAnalyzeManager = manager
            // semanticTokens 可能比 TextMate AnalyzeManager 更早返回。
            // 这里把之前收到的 token 补给新 manager，避免必须手动刷新 clangd 才有语义颜色。
            if (pendingSemanticTokens.isNotEmpty()) {
                manager.updateSemanticTokens(pendingSemanticTokens)
            }
        }
    }

    fun updateSemanticTokens(tokens: List<SemanticToken>) {
        // clangd 刚打开文件或重建索引时，semanticTokens/full 可能短暂返回空列表。
        // 如果此时直接清空 overlay，用户会看到语义颜色闪一下或必须手动刷新。
        // 已经有语义颜色时，忽略短暂空结果；真正的新颜色会由后续自动重试覆盖。
        if (tokens.isEmpty() && pendingSemanticTokens.isNotEmpty()) {
            return
        }
        pendingSemanticTokens = tokens
        semanticAnalyzeManager?.updateSemanticTokens(tokens)
    }

    override fun getInterruptionLevel(): Int {
        return wrapperLanguage?.interruptionLevel ?: 0
    }

    @Throws(CompletionCancelledException::class)
    override fun requireAutoComplete(
        content: ContentReference,
        position: CharPosition,
        publisher: CompletionPublisher,
        extraArguments: Bundle
    ) {

        /* if (getEditor().hitTrigger(line)) {
            publisher.cancel();
            return;
        }*/

        if (!editor.isConnected) {
            return
        }

        val prefix = computePrefix(content, position)

        val prefixLength = prefix.length

        val documentChangeEvent =
            editor.eventManager.getEventListener<DocumentChangeEvent>() ?: return

        val documentChangeFuture =
            documentChangeEvent.future

        if (documentChangeFuture?.isDone == false || documentChangeFuture?.isCompletedExceptionally == false || documentChangeFuture?.isCancelled == false) {
            runCatching {
                documentChangeFuture.get(Timeout[Timeouts.WILLSAVE].toLong(), TimeUnit.MILLISECONDS)
            }
        }

        val completionList = ArrayList<CompletionItem>()

        val serverResultCompletionItems =
            editor.coroutineScope.future {
                editor.eventManager.emitAsync(EventType.completion, position)
                    .getOrNull<List<org.eclipse.lsp4j.CompletionItem>>("completion-items")
                    ?: emptyList()
            }

        try {
            serverResultCompletionItems
                .thenAccept { completions ->
                    completions.forEach { completionItem: org.eclipse.lsp4j.CompletionItem ->
                        completionList.add(
                            completionItemProvider.createCompletionItem(
                                completionItem,
                                editor.eventManager,
                                prefixLength
                            )
                        )
                    }
                }.exceptionally { throwable: Throwable ->
                    publisher.cancel()
                    throw CompletionCancelledException(throwable.message)
                }.get(Timeout[Timeouts.COMPLETION].toLong(), TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            return
        }

        filterCompletionItems(content, position, completionList).let { filteredList ->
            publisher.setComparator(createCompletionItemComparator(filteredList))
            publisher.addItems(filteredList)
        }

        publisher.updateList()
    }

    private fun computePrefix(text: ContentReference, position: CharPosition): String {
        val triggers = editor.completionTriggers.filterNot { trigger ->
            trigger.length == 1 && trigger[0].isLetterOrDigit()
        }
        if (triggers.isEmpty()) {
            return CompletionHelper.computePrefix(text, position) { key: Char ->
                MyCharacter.isJavaIdentifierPart(key)
            }
        }

        val delimiters = triggers.toMutableList().apply {
            addAll(listOf(" ", "\t", "\n", "\r"))
        }

        val s = StringBuilder()

        val line = text.getLine(position.line)
        for (i in min(line.lastIndex, position.column - 1) downTo 0) {
            val char = line[i]
            if (delimiters.contains(char.toString())) {
                return s.reverse().toString()
            }
            s.append(char)
        }
        return s.toString()
    }

    override fun getIndentAdvance(content: ContentReference, line: Int, column: Int): Int {
        return wrapperLanguage?.interruptionLevel ?: 0
    }

    override fun useTab(): Boolean {
        return wrapperLanguage?.useTab() == true
    }

    override fun getFormatter(): Formatter {
        return _formatter ?: wrapperLanguage?.formatter ?: EmptyLanguage.EmptyFormatter.INSTANCE
    }

    fun setFormatter(formatter: Formatter) {
        this._formatter = formatter
    }

    override fun getSymbolPairs(): SymbolPairMatch {
        return wrapperLanguage?.symbolPairs ?: EmptyLanguage.EMPTY_SYMBOL_PAIRS
    }

    override fun getNewlineHandlers(): Array<NewlineHandler?> {
        return wrapperLanguage?.newlineHandlers ?: emptyArray()
    }

    override fun destroy() {
        formatter.destroy()
        semanticAnalyzeManager = null
        semanticBaseAnalyzeManager = null
        pendingSemanticTokens = emptyList()
        wrapperLanguage?.destroy()
    }


}

