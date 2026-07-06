package io.github.rosemoe.sora.lsp.editor.semantic

/** clangd 返回的语义 token。 */
data class SemanticToken(
    val line: Int,
    val start: Int,
    val length: Int,
    val tokenType: String,
    val tokenModifiers: Set<String>
) {
    val end: Int get() = start + length
}
