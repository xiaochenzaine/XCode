package com.xc.code.agent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.BubbleChatQuestion
import me.rerere.hugeicons.stroke.ChartColumn
import me.rerere.hugeicons.stroke.Settings03
import me.rerere.hugeicons.stroke.Sparkles

@Composable
fun agent_assistant_page(
    assistant_name: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            agent_assistant_header_card(assistant_name = assistant_name)
        }
        item {
            agent_settings_group(title = "基础配置") {
                agent_settings_row(label = "助手名称", value = assistant_name)
                agent_settings_row(label = "模型供应商", value = "未选择")
                agent_settings_row(label = "模型", value = "请选择模型")
            }
        }
        item {
            agent_settings_group(title = "提示词") {
                agent_prompt_card(
                    text = "你是 XCode 内置的 C/C++ 开发助手，擅长分析项目结构、解释构建错误、修改 CMake/Gradle 配置，并给出简洁可执行的建议。"
                )
            }
        }
        item {
            agent_settings_group(title = "生成参数") {
                agent_settings_row(label = "温度", value = "0.7")
                agent_settings_row(label = "上下文条数", value = "20")
                agent_settings_row(label = "流式输出", value = "开启")
                agent_settings_row(label = "工具调用", value = "开启")
            }
        }
        item {
            agent_settings_group(title = "能力开关") {
                agent_settings_row(label = "项目上下文", value = "开启")
                agent_settings_row(label = "联网搜索", value = "关闭")
                agent_settings_row(label = "深度思考", value = "自动")
            }
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
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
private fun agent_assistant_header_card(assistant_name: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.primaryContainer.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(modifier = Modifier.size(46.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.18f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(HugeIcons.Sparkles, contentDescription = null, tint = colors.primary, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(assistant_name, color = colors.onPrimaryContainer, fontSize = 17.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("助手配置 Mock，后续可对接真实模型参数", color = colors.onPrimaryContainer.copy(alpha = 0.70f), fontSize = 12.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Composable
private fun agent_settings_group(title: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = colors.onSurfaceVariant, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 4.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surfaceVariant.copy(alpha = 0.48f),
            border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.10f))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp), content = content)
        }
    }
}

@Composable
private fun agent_settings_row(label: String, value: String) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(label, color = colors.onSurface, fontSize = 14.sp, lineHeight = 16.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, color = colors.onSurfaceVariant, fontSize = 13.sp, lineHeight = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun agent_prompt_card(text: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface.copy(alpha = 0.48f),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.10f))
    ) {
        Text(text, color = colors.onSurface, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(12.dp))
    }
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
