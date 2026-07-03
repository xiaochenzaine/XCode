package me.rerere.rikkahub.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 宿主应用用于接管 RikkaHub 明暗状态的桥接器。
 *
 * 为 null 时沿用 RikkaHub 自己的颜色模式设置；非 null 时以宿主应用注入的状态为准。
 */
object ThemeStateBridge {
    private val _colorMode = MutableStateFlow<ColorMode?>(null)
    val colorMode: StateFlow<ColorMode?> = _colorMode.asStateFlow()

    private val _scale = MutableStateFlow(1f)
    val scale: StateFlow<Float> = _scale.asStateFlow()

    fun setColorMode(mode: ColorMode?) {
        _colorMode.value = mode
    }

    fun setScale(scale: Float) {
        _scale.value = scale.coerceIn(0.5f, 1.5f)
    }
}
