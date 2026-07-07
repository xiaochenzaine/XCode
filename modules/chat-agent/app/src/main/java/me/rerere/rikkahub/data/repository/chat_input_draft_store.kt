package me.rerere.rikkahub.data.repository

import me.rerere.ai.ui.UIMessagePart
import kotlin.uuid.Uuid

/**
 * 聊天输入框草稿。
 *
 * 只保存运行期草稿，用于抵抗嵌入式页面、Fragment、ViewModel 重建导致的输入内容丢失。
 * 不做磁盘持久化，避免把临时附件 Uri、编辑中状态等不稳定数据长期落盘。
 */
data class ChatInputDraft(
    val text: String,
    val messageContent: List<UIMessagePart>,
    val editingMessage: Uuid?,
    val editingParts: List<UIMessagePart>?,
    val editingAttachmentUrls: Set<String>,
) {
    fun isBlank(): Boolean = text.isBlank() && messageContent.isEmpty() && editingMessage == null
}

/**
 * 聊天输入框运行期草稿缓存。
 *
 * key 需要带编辑器宿主作用域，例如：
 * - editor_agent:<project_root_path>:<conversation_id>
 *
 * 草稿只服务编辑器侧边栏 Agent，不为独立聊天页启用。
 */
object ChatInputDraftStore {
    private const val max_draft_count = 64
    private val drafts = object : LinkedHashMap<String, ChatInputDraft>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ChatInputDraft>?): Boolean {
            return size > max_draft_count
        }
    }

    @Synchronized
    fun save(key: String, draft: ChatInputDraft) {
        if (key.isBlank() || draft.isBlank()) {
            drafts.remove(key)
            return
        }
        drafts[key] = draft
    }

    @Synchronized
    fun restore(key: String): ChatInputDraft? {
        if (key.isBlank()) return null
        return drafts[key]
    }

    @Synchronized
    fun clear(key: String) {
        if (key.isBlank()) return
        drafts.remove(key)
    }

    @Synchronized
    fun clearConversation(conversationId: String) {
        drafts.keys
            .filter { it.startsWith("editor_agent:") && it.endsWith(":$conversationId") }
            .forEach { drafts.remove(it) }
    }
}
