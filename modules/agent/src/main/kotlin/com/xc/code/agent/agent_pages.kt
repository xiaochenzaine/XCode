package com.xc.code.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.BubbleChatQuestion
import me.rerere.hugeicons.stroke.ChartColumn
import me.rerere.hugeicons.stroke.Settings03

@Composable
fun agent_assistant_page(
    assistant_name: String,
    modifier: Modifier = Modifier
) {
    agent_placeholder_page(
        title = assistant_name,
        subtitle = "助手页面占位",
        icon = HugeIcons.BubbleChatQuestion,
        modifier = modifier
    )
}

@Composable
fun agent_stats_page(modifier: Modifier = Modifier) {
    agent_placeholder_page(
        title = "统计",
        subtitle = "统计页面占位",
        icon = HugeIcons.ChartColumn,
        modifier = modifier
    )
}

@Composable
fun agent_settings_page(modifier: Modifier = Modifier) {
    agent_placeholder_page(
        title = "设置",
        subtitle = "设置页面占位",
        icon = HugeIcons.Settings03,
        modifier = modifier
    )
}

@Composable
private fun agent_placeholder_page(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(26.dp))
                }
            }
            Text(title, color = colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = colors.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}
