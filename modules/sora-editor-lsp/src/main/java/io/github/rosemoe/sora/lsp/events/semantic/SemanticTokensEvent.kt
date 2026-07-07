package io.github.rosemoe.sora.lsp.events.semantic

import android.util.Log

import io.github.rosemoe.sora.lsp.editor.LspEditor
import io.github.rosemoe.sora.lsp.editor.semantic.SemanticToken
import io.github.rosemoe.sora.lsp.events.AsyncEventListener
import io.github.rosemoe.sora.lsp.events.EventContext
import io.github.rosemoe.sora.lsp.events.EventType
import io.github.rosemoe.sora.lsp.requests.Timeout
import io.github.rosemoe.sora.lsp.requests.Timeouts
import io.github.rosemoe.sora.lsp.utils.createTextDocumentIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.eclipse.lsp4j.SemanticTokens
import org.eclipse.lsp4j.SemanticTokensParams
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * 请求 textDocument/semanticTokens/full，并把 clangd 返回的 delta token 解码成普通区间。
 */
class SemanticTokensEvent : AsyncEventListener() {
    override val eventName: String = EventType.semanticTokens

    var future: CompletableFuture<SemanticTokens>? = null

    private val requestFlows = ConcurrentHashMap<String, MutableSharedFlow<LspEditor>>()

    override suspend fun doHandleAsync(context: EventContext) {
        val editor = context.get<LspEditor>("lsp-editor")
        val flow = requestFlows.getOrPut(editor.uri.toString()) {
            MutableSharedFlow<LspEditor>(
                replay = 0,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            ).also { created ->
                editor.coroutineScope.launch(Dispatchers.Main) {
                    @OptIn(kotlinx.coroutines.FlowPreview::class)
                    created.debounce(120).collect { requestEditor ->
                        processRequest(requestEditor, context)
                    }
                }
            }
        }
        flow.tryEmit(editor)
    }

    private suspend fun processRequest(editor: LspEditor, context: EventContext) = withContext(Dispatchers.IO) {
        val capabilities = editor.requestManager.capabilities ?: return@withContext
        if (capabilities.semanticTokensProvider == null) return@withContext

        val legend = readLegend(capabilities.semanticTokensProvider) ?: return@withContext
        val params = SemanticTokensParams(editor.uri.createTextDocumentIdentifier())
        val request = editor.requestManager.semanticTokensFull(params) ?: return@withContext
        future = request

        val semanticTokens = try {
            withTimeout(Timeout[Timeouts.COMPLETION].toLong()) {
                request.await()
            }
        } catch (e: Exception) {
            onException(context, e)
            return@withContext
        }

        val decodedTokens = decode(semanticTokens, legend)
        Log.d("SemanticTokensEvent", "semantic tokens: ${decodedTokens.size}, legendTypes=${legend.tokenTypes}")
        editor.showSemanticTokens(decodedTokens)
    }

    private data class Legend(
        val tokenTypes: List<String>,
        val tokenModifiers: List<String>
    )

    private fun readLegend(provider: Any): Legend? {
        val providerValue = when {
            provider.javaClass.methods.any { it.name == "isLeft" } && provider.javaClass.getMethod("isLeft").invoke(provider) == true ->
                provider.javaClass.getMethod("getLeft").invoke(provider)
            provider.javaClass.methods.any { it.name == "isRight" } && provider.javaClass.getMethod("isRight").invoke(provider) == true ->
                provider.javaClass.getMethod("getRight").invoke(provider)
            else -> provider
        } ?: return null

        val legend = providerValue.javaClass.methods.firstOrNull { it.name == "getLegend" }?.invoke(providerValue)
            ?: return null
        val tokenTypes = legend.javaClass.methods.firstOrNull { it.name == "getTokenTypes" }?.invoke(legend) as? List<*>
            ?: return null
        val tokenModifiers = legend.javaClass.methods.firstOrNull { it.name == "getTokenModifiers" }?.invoke(legend) as? List<*>
            ?: emptyList<Any>()
        return Legend(
            tokenTypes.map { it.toString() },
            tokenModifiers.map { it.toString() }
        )
    }

    private fun decode(tokens: SemanticTokens, legend: Legend): List<SemanticToken> {
        val data = tokens.data ?: return emptyList()
        val result = ArrayList<SemanticToken>(data.size / 5)
        var line = 0
        var start = 0
        var index = 0
        while (index + 4 < data.size) {
            val deltaLine = data[index]
            val deltaStart = data[index + 1]
            val length = data[index + 2]
            val tokenTypeIndex = data[index + 3]
            val modifierBits = data[index + 4]
            index += 5

            line += deltaLine
            start = if (deltaLine == 0) start + deltaStart else deltaStart

            val tokenType = legend.tokenTypes.getOrNull(tokenTypeIndex) ?: continue
            val modifiers = buildSet {
                legend.tokenModifiers.forEachIndexed { bit, modifier ->
                    if ((modifierBits and (1 shl bit)) != 0) add(modifier)
                }
            }
            result.add(SemanticToken(line, start, length, tokenType, modifiers))
        }
        return result
    }

    override fun dispose() {
        future?.cancel(true)
        future = null
        requestFlows.clear()
    }
}

val EventType.semanticTokens: String
    get() = "textDocument/semanticTokens/full"
