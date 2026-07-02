package com.xc.code.agent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal class agent_state {
    // 会话列表
    val conversations = mutableStateListOf<agent_conversation_preview>()

    // 每个会话的消息
    private val _messages = mutableStateMapOf<Int, List<agent_message_preview>>()

    // 当前选中的会话
    var selected_conversation_id by mutableIntStateOf(1)
        private set

    // 置顶会话 id 集合
    var pinned_ids by mutableStateOf(setOf<Int>())
        private set

    val selected_conversation: agent_conversation_preview?
        get() = conversations.firstOrNull { it.id == selected_conversation_id }

    val messages_map: Map<Int, List<agent_message_preview>>
        get() = _messages

    // ── 会话操作 ──

    fun select_conversation(id: Int) {
        if (conversations.any { it.id == id }) selected_conversation_id = id
    }

    fun create_new_conversation() {
        val next_id = ((conversations.maxOfOrNull { it.id } ?: 0) + 1).coerceAtLeast(1)
        conversations.add(0, agent_conversation_preview(id = next_id, title = "新会话", subtitle = "暂无消息"))
        _messages[next_id] = emptyList()
        selected_conversation_id = next_id
    }

    fun rename_conversation(id: Int, title: String) {
        val next_title = title.trim()
        if (next_title.isEmpty()) return
        update_conversation(id) { it.copy(title = next_title) }
    }

    fun regenerate_title(id: Int) {
        val first_user_message = messages_for(id).firstOrNull { it.role == agent_message_role.User }?.text.orEmpty()
        val next_title = first_user_message.take(18).ifBlank { "新会话" }
        rename_conversation(id, next_title)
    }

    fun toggle_pin(id: Int) {
        pinned_ids = if (id in pinned_ids) pinned_ids - id else pinned_ids + id
    }

    fun delete_conversation(id: Int) {
        conversations.removeAll { it.id == id }
        _messages.remove(id)
        pinned_ids = pinned_ids - id
        if (conversations.isEmpty()) {
            conversations.add(agent_conversation_preview(id = 1, title = "新会话", subtitle = "暂无消息"))
            _messages[1] = emptyList()
            selected_conversation_id = 1
        } else if (selected_conversation_id == id) {
            selected_conversation_id = conversations.first().id
        }
    }

    // ── 消息 ──

    fun messages_for(id: Int): List<agent_message_preview> {
        return _messages[id].orEmpty()
    }

    fun send_user_message(text: String, time: String) {
        val content = text.trim()
        if (content.isEmpty()) return
        ensure_selected_conversation_exists()
        val user_message = agent_message_preview(role = agent_message_role.User, text = content, time = time)
        val assistant_message = agent_message_preview(
            role = agent_message_role.Assistant,
            text = "已收到：$content\n\n后续接入真实模型后，这里会返回助手回复。",
            time = time,
            tool_steps = listOf("接收用户输入", "等待模型接入")
        )
        _messages[selected_conversation_id] = messages_for(selected_conversation_id) + user_message + assistant_message
        if (selected_conversation?.title == "新会话") {
            rename_conversation(selected_conversation_id, content.take(18))
        }
        refresh_conversation_subtitle(selected_conversation_id)
    }

    fun delete_message(conversation_id: Int, message: agent_message_preview) {
        val current = messages_for(conversation_id)
        val index = current.indexOfFirst { it == message }
        if (index < 0) return
        _messages[conversation_id] = current.toMutableList().also { it.removeAt(index) }
        refresh_conversation_subtitle(conversation_id)
    }

    private fun ensure_selected_conversation_exists() {
        if (conversations.none { it.id == selected_conversation_id }) create_new_conversation()
    }

    private fun refresh_conversation_subtitle(id: Int) {
        val msgs = messages_for(id)
        val subtitle = if (msgs.isEmpty()) "暂无消息" else "${msgs.last().time} · ${msgs.size} 条消息"
        update_conversation(id) { it.copy(subtitle = subtitle) }
    }

    private fun update_conversation(id: Int, transform: (agent_conversation_preview) -> agent_conversation_preview) {
        val index = conversations.indexOfFirst { it.id == id }
        if (index >= 0) conversations[index] = transform(conversations[index])
    }

    // ── 初始化示例数据 ──

    fun load_sample_data() {
        _messages.clear()
        _messages.putAll(sample_conversation_messages)

        conversations.clear()
        conversations.addAll(buildList {
            for ((id, msgs) in sample_conversation_messages) {
                val count = msgs.size
                val last_time = msgs.lastOrNull()?.time ?: ""
                val subtitle = if (count == 0) "暂无消息" else "$last_time · $count 条消息"
                add(agent_conversation_preview(
                    id = id,
                    title = sample_conversation_titles[id] ?: "会话 $id",
                    subtitle = subtitle
                ))
            }
            if (isEmpty()) {
                add(agent_conversation_preview(id = 1, title = "新会话", subtitle = "暂无消息"))
            }
        })
        selected_conversation_id = conversations.firstOrNull()?.id ?: 1
    }
}
