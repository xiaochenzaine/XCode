package com.xc.code.editor.core

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class workspace_file_change_event {
    data class changed(val path: String) : workspace_file_change_event()
    data class workspace_maybe_changed(val path: String = "/workspace") : workspace_file_change_event()
}

object workspace_file_change_notifier {
    private val _events = MutableSharedFlow<workspace_file_change_event>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val events: SharedFlow<workspace_file_change_event> = _events

    fun notify(event: workspace_file_change_event) {
        _events.tryEmit(event)
    }
}
