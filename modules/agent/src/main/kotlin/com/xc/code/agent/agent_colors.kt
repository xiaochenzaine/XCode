package com.xc.code.agent

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class agent_colors(
    val bg: Color,
    val surface: Color,
    val surface_variant: Color,
    val text_primary: Color,
    val text_secondary: Color,
    val accent: Color,
    val accent_container: Color,
    val on_accent_container: Color,
    val accent_dim: Color,
    val border: Color,
    val input_bg: Color,
    val input_border: Color,
    val danger: Color,
    val danger_bg: Color,
    val bubble_user: Color,
    val bubble_assistant: Color,
    val send_button_idle: Color,
    val icon_dim: Color,
)

val dark_agent_colors = agent_colors(
    bg = Color(0xFF1E1E2A),
    surface = Color(0xFF1F2230),
    surface_variant = Color(0xFF2A2A3A),
    text_primary = Color(0xFFE8E8F8),
    text_secondary = Color(0xFF707486),
    accent = Color(0xFFC0CCFF),
    accent_container = Color(0x29C0CCFF),
    on_accent_container = Color(0xFFC0CCFF),
    accent_dim = Color(0xFFC0CCFF),
    border = Color(0x4D787C8C),
    input_bg = Color(0x15FFFFFF),
    input_border = Color(0x55C0CCFF),
    danger = Color(0xFFFF5F57),
    danger_bg = Color(0x29FF5F57),
    bubble_user = Color(0x29C0CCFF),
    bubble_assistant = Color(0x0DFFFFFF),
    send_button_idle = Color(0x1AFFFFFF),
    icon_dim = Color(0xFF88889C),
)

val light_agent_colors = agent_colors(
    bg = Color(0xFFFFFFFF),
    surface = Color(0xFFF1F0FA),
    surface_variant = Color(0xFFE8E8F4),
    text_primary = Color(0xFF2D2D3F),
    text_secondary = Color(0xFF7A7A8C),
    accent = Color(0xFF1F54E8),
    accent_container = Color(0x1F1F54E8),
    on_accent_container = Color(0xFF1F54E8),
    accent_dim = Color(0xFF1F54E8),
    border = Color(0x4D888888),
    input_bg = Color(0x15000000),
    input_border = Color(0x551F54E8),
    danger = Color(0xFFE5484D),
    danger_bg = Color(0x1FE5484D),
    bubble_user = Color(0x1F1F54E8),
    bubble_assistant = Color(0x0D000000),
    send_button_idle = Color(0x1A000000),
    icon_dim = Color(0xFF666678),
)

val local_agent_colors = staticCompositionLocalOf { dark_agent_colors }
