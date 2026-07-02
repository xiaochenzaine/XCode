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

    // ── 会话操作 ──

    fun select_conversation(id: Int) {
        selected_conversation_id = id
    }

    fun toggle_pin(id: Int) {
        pinned_ids = if (id in pinned_ids) pinned_ids - id else pinned_ids + id
    }

    fun delete_conversation(id: Int) {
        conversations.removeAll { it.id == id }
        _messages.remove(id)
        if (selected_conversation_id == id) {
            selected_conversation_id = 1 // 切到新会话
        }
    }

    // ── 消息 ──

    fun messages_for(id: Int): List<agent_message_preview> {
        return _messages[id].orEmpty()
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
    }
}
