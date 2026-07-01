package com.xc.code.agent

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.time.LocalTime
import kotlinx.coroutines.launch
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.Add01
import me.rerere.hugeicons.stroke.AiSearch02
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.hugeicons.stroke.ArrowUp02
import me.rerere.hugeicons.stroke.Idea01
import me.rerere.hugeicons.stroke.Menu03
import me.rerere.hugeicons.stroke.MessageAdd01
import me.rerere.hugeicons.stroke.PencilEdit01
import me.rerere.hugeicons.stroke.Pin
import me.rerere.hugeicons.stroke.Refresh01
import me.rerere.hugeicons.stroke.Refresh03
import me.rerere.hugeicons.stroke.Delete01
import me.rerere.hugeicons.stroke.Search01
import me.rerere.hugeicons.stroke.Cancel01
import me.rerere.hugeicons.stroke.Copy01
import me.rerere.hugeicons.stroke.MoreVertical
import me.rerere.hugeicons.stroke.ChartColumn
import me.rerere.hugeicons.stroke.BubbleChatQuestion
import me.rerere.hugeicons.stroke.ServerStack01
import me.rerere.hugeicons.stroke.Settings03
import me.rerere.hugeicons.stroke.Sparkles

private data class agent_message_preview(
    val role: agent_message_role,
    val text: String,
    val time: String,
    val tool_steps: List<String> = emptyList()
)

private enum class agent_message_role {
    User,
    Assistant
}

private data class agent_conversation_preview(
    val title: String,
    val subtitle: String,
    val selected: Boolean = false
)

private enum class agent_search_mode {
    Chat,
    Conversation
}

private enum class agent_main_page {
    Chat,
    Assistant,
    Stats,
    Settings
}

private const val user_prefs_name = "xcode_agent_user_profile"
private const val user_name_key = "user_name"
private const val user_avatar_uri_key = "user_avatar_uri"
private const val default_user_name = "小陈在肝码"
private const val default_assistant_name = "默认助手"

private fun load_user_name(context: Context): String {
    return context.getSharedPreferences(user_prefs_name, Context.MODE_PRIVATE)
        .getString(user_name_key, default_user_name)
        ?: default_user_name
}

private fun load_user_avatar_uri(context: Context): String {
    return context.getSharedPreferences(user_prefs_name, Context.MODE_PRIVATE)
        .getString(user_avatar_uri_key, "")
        .orEmpty()
}

private fun save_user_profile(context: Context, name: String, avatar_uri: String) {
    context.getSharedPreferences(user_prefs_name, Context.MODE_PRIVATE)
        .edit()
        .putString(user_name_key, name)
        .putString(user_avatar_uri_key, avatar_uri)
        .apply()
}

private fun copy_user_avatar_to_private_file(context: Context, source_uri: Uri): String? {
    val avatar_dir = File(context.filesDir, "user_avatar").apply { mkdirs() }
    val avatar_file = File(avatar_dir, "avatar_${System.currentTimeMillis()}.jpg")
    return runCatching {
        context.contentResolver.openInputStream(source_uri)?.use { input ->
            avatar_file.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        Uri.fromFile(avatar_file).toString()
    }.getOrNull()
}

private val sample_messages = listOf(
    agent_message_preview(
        role = agent_message_role.Assistant,
        text = "我可以读取当前项目结构、解释构建输出、整理 CMake 配置，并帮你规划下一步修改。",
        time = "14:18"
    ),
    agent_message_preview(
        role = agent_message_role.User,
        text = "先分析当前打开的 C++ 项目。",
        time = "14:20"
    ),
    agent_message_preview(
        role = agent_message_role.Assistant,
        text = "可以。后续接入工具后，这里会展示文件读取、命令执行、代码修改等操作结果。",
        time = "14:20",
        tool_steps = listOf("读取项目结构", "检查 Gradle 模块", "等待工具权限")
    )
)

private val sample_conversations = listOf(
    agent_conversation_preview("新会话", "刚刚 · 0 条消息", selected = true),
    agent_conversation_preview("分析 CMake 配置", "今天 14:20 · 2 条消息"),
    agent_conversation_preview("解释构建错误", "昨天 22:10 · 5 条消息"),
    agent_conversation_preview("整理项目结构", "6 月 30 日 · 8 条消息")
)

private fun agent_greeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..10 -> "早上好👋"
        in 11..13 -> "中午好👋"
        in 14..17 -> "下午好👋"
        else -> "晚上好👋"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun agent_screen() {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val drawer_state = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var user_name by remember { mutableStateOf(load_user_name(context)) }
    var user_avatar_uri by remember { mutableStateOf(load_user_avatar_uri(context)) }
    var selected_assistant_name by remember { mutableStateOf(default_assistant_name) }
    var main_page by remember { mutableStateOf(agent_main_page.Chat) }

    BackHandler(enabled = main_page != agent_main_page.Chat) {
        main_page = agent_main_page.Chat
    }

    BackHandler(enabled = main_page == agent_main_page.Chat && drawer_state.isOpen) {
        scope.launch { drawer_state.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawer_state,
        gesturesEnabled = main_page == agent_main_page.Chat,
        drawerContent = {
            agent_drawer(
                user_name = user_name,
                user_avatar_uri = user_avatar_uri,
                selected_assistant_name = selected_assistant_name,
                on_user_profile_change = { next_name, next_avatar_uri ->
                    user_name = next_name
                    user_avatar_uri = next_avatar_uri
                    save_user_profile(context, next_name, next_avatar_uri)
                },
                on_assistant_click = {
                    main_page = agent_main_page.Assistant
                    scope.launch { drawer_state.close() }
                },
                on_stats_click = {
                    main_page = agent_main_page.Stats
                    scope.launch { drawer_state.close() }
                },
                on_settings_click = {
                    main_page = agent_main_page.Settings
                    scope.launch { drawer_state.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (main_page == agent_main_page.Chat) {
                            IconButton(onClick = { scope.launch { drawer_state.open() } }) {
                                Icon(HugeIcons.Menu03, contentDescription = "侧边栏", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                            }
                        } else {
                            agent_back_button(on_click = { main_page = agent_main_page.Chat })
                        }
                    },
                    title = {
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            Text(
                                when (main_page) {
                                    agent_main_page.Chat -> "新会话"
                                    agent_main_page.Assistant -> selected_assistant_name
                                    agent_main_page.Stats -> "统计"
                                    agent_main_page.Settings -> "设置"
                                },
                                color = colors.onBackground,
                                fontSize = 16.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                if (main_page == agent_main_page.Chat) "未选择供应商" else "占位页面",
                                color = colors.onSurfaceVariant,
                                fontSize = 10.sp,
                                lineHeight = 12.sp
                            )
                        }
                    },
                    actions = {
                        if (main_page == agent_main_page.Chat) {
                            IconButton(onClick = {}) {
                                Icon(HugeIcons.Search01, contentDescription = "搜索聊天", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                            }
                            IconButton(onClick = {}) {
                                Icon(HugeIcons.MessageAdd01, contentDescription = "新建会话", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                if (main_page == agent_main_page.Chat) {
                    agent_input_bar(
                        value = input,
                        on_value_change = { input = it },
                        on_send = { input = "" }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding_values ->
            AnimatedContent(
                targetState = main_page,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it }.togetherWith(
                            slideOutHorizontally { -it / 2 } + scaleOut(targetScale = 0.7f) + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { -it / 2 } + scaleIn(initialScale = 0.7f) + fadeIn()).togetherWith(
                            slideOutHorizontally { it }
                        )
                    }.using(SizeTransform(clip = false))
                },
                label = "agent_main_page"
            ) { page ->
                when (page) {
                    agent_main_page.Chat -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding_values)
                                .padding(horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }
                            items(sample_messages) { message ->
                                agent_message_bubble(
                                    message = message,
                                    user_name = user_name,
                                    user_avatar_uri = user_avatar_uri,
                                    assistant_name = selected_assistant_name
                                )
                            }
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }
                    }
                    agent_main_page.Assistant -> agent_assistant_page(
                        assistant_name = selected_assistant_name,
                        modifier = Modifier.padding(padding_values)
                    )
                    agent_main_page.Stats -> agent_stats_page(modifier = Modifier.padding(padding_values))
                    agent_main_page.Settings -> agent_settings_page(modifier = Modifier.padding(padding_values))
                }
            }
        }
    }
}

@Composable
private fun agent_drawer(
    user_name: String,
    user_avatar_uri: String,
    selected_assistant_name: String,
    on_user_profile_change: (String, String) -> Unit,
    on_assistant_click: () -> Unit,
    on_stats_click: () -> Unit,
    on_settings_click: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    var editing_profile by remember { mutableStateOf(false) }
    var draft_name by remember { mutableStateOf(user_name) }
    var search_mode by remember { mutableStateOf<agent_search_mode?>(null) }
    var conversation_query by remember { mutableStateOf("") }
    var chat_query by remember { mutableStateOf("") }
    val avatar_picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selected_uri ->
            copy_user_avatar_to_private_file(context, selected_uri)?.let { local_uri ->
                on_user_profile_change(user_name, local_uri)
            }
        }
    }
    val visible_conversations = remember(conversation_query) {
        if (conversation_query.isBlank()) {
            sample_conversations
        } else {
            sample_conversations.filter { item ->
                item.title.contains(conversation_query, ignoreCase = true) ||
                    item.subtitle.contains(conversation_query, ignoreCase = true)
            }
        }
    }

    if (editing_profile) {
        AlertDialog(
            onDismissRequest = { editing_profile = false },
            title = { Text("修改用户资料") },
            text = {
                agent_profile_text_field(
                    value = draft_name,
                    on_value_change = { draft_name = it },
                    placeholder = "输入名称"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val next_name = draft_name.trim()
                        if (next_name.isNotEmpty()) {
                            on_user_profile_change(next_name, user_avatar_uri)
                        }
                        editing_profile = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { editing_profile = false }) {
                    Text("取消")
                }
            }
        )
    }

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = colors.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                agent_avatar(
                    name = user_name,
                    avatar_uri = user_avatar_uri,
                    modifier = Modifier.size(46.dp),
                    on_click = { avatar_picker.launch("image/*") }
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(user_name, color = colors.onSurface, fontSize = 15.sp, lineHeight = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Icon(
                            HugeIcons.PencilEdit01,
                            contentDescription = "编辑",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier
                                .size(13.dp)
                                .clip(CircleShape)
                                .clickable {
                                    draft_name = user_name
                                    editing_profile = true
                                }
                        )
                    }
                    Text(agent_greeting(), color = colors.onSurfaceVariant, fontSize = 11.sp, lineHeight = 13.sp)
                }
            }

            AnimatedContent(
                targetState = search_mode,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(clip = false))
                },
                label = "agent_search_mode"
            ) { mode ->
                if (mode != null) {
                    val is_chat_search = mode == agent_search_mode.Chat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        agent_search_field(
                            value = if (is_chat_search) chat_query else conversation_query,
                            on_value_change = {
                                if (is_chat_search) {
                                    chat_query = it
                                } else {
                                    conversation_query = it
                                }
                            },
                            placeholder = if (is_chat_search) "搜索聊天..." else "搜索会话...",
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            modifier = Modifier.size(35.dp),
                            shape = CircleShape,
                            color = colors.primary.copy(alpha = 0.16f),
                            onClick = {
                                if (is_chat_search) {
                                    chat_query = ""
                                } else {
                                    conversation_query = ""
                                }
                                search_mode = null
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    HugeIcons.Cancel01,
                                    contentDescription = "取消搜索",
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        agent_drawer_action(
                            icon = HugeIcons.AiSearch02,
                            label = "搜索聊天",
                            modifier = Modifier.weight(1f),
                            on_click = { search_mode = agent_search_mode.Chat }
                        )
                        agent_drawer_action(
                            icon = HugeIcons.Search01,
                            label = "搜索会话",
                            modifier = Modifier.weight(1f),
                            on_click = { search_mode = agent_search_mode.Conversation }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(visible_conversations) { item ->
                    agent_conversation_item(item)
                }
            }

            agent_drawer_footer(
                selected_assistant_name = selected_assistant_name,
                on_assistant_click = on_assistant_click,
                on_stats_click = on_stats_click,
                on_settings_click = on_settings_click
            )
        }
    }
}

@Composable
private fun agent_back_button(on_click: () -> Unit) {
    FilledTonalIconButton(onClick = on_click) {
        Icon(HugeIcons.ArrowLeft01, contentDescription = "返回")
    }
}

@Composable
private fun agent_avatar(
    name: String,
    avatar_uri: String,
    modifier: Modifier = Modifier,
    on_click: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = colors.primaryContainer,
        onClick = on_click
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (avatar_uri.isNotBlank()) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            setImageURI(Uri.parse(avatar_uri))
                        }
                    },
                    update = { image_view -> image_view.setImageURI(Uri.parse(avatar_uri)) }
                )
            } else {
                Icon(
                    HugeIcons.Sparkles,
                    contentDescription = null,
                    tint = colors.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun agent_profile_text_field(
    value: String,
    on_value_change: (String) -> Unit,
    placeholder: String
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceVariant.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.55f))
    ) {
        TextField(
            value = value,
            onValueChange = on_value_change,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = colors.onSurfaceVariant) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun agent_search_field(
    value: String,
    on_value_change: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, colors.onSurfaceVariant.copy(alpha = 0.5f))
    ) {
        BasicTextField(
            value = value,
            onValueChange = on_value_change,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface, fontSize = 12.sp),
            cursorBrush = SolidColor(colors.primary),
            decorationBox = { inner_text_field ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = colors.onSurfaceVariant, fontSize = 12.sp)
                    }
                    inner_text_field()
                }
            }
        )
    }
}

@Composable
private fun agent_drawer_action(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    on_click: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceVariant.copy(alpha = 0.7f),
        onClick = on_click
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(17.dp))
            Text(label, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun agent_conversation_item(item: agent_conversation_preview) {
    val colors = MaterialTheme.colorScheme
    var menu_expanded by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (item.selected) colors.primaryContainer else Color.Transparent
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { menu_expanded = true }
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    item.title,
                    color = if (item.selected) colors.onPrimaryContainer else colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = if (item.selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.subtitle,
                    color = if (item.selected) colors.onPrimaryContainer.copy(alpha = 0.72f) else colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            DropdownMenu(
                expanded = menu_expanded,
                onDismissRequest = { menu_expanded = false }
            ) {
                agent_conversation_menu_item(icon = HugeIcons.Pin, text = "置顶会话") { menu_expanded = false }
                agent_conversation_menu_item(icon = HugeIcons.Refresh01, text = "生成标题") { menu_expanded = false }
                agent_conversation_menu_item(icon = HugeIcons.Delete01, text = "删除会话") { menu_expanded = false }
            }
        }
    }
}

@Composable
private fun agent_conversation_menu_item(icon: ImageVector, text: String, on_click: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text) },
        leadingIcon = {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        },
        onClick = on_click
    )
}

@Composable
private fun agent_drawer_assistant_footer(
    selected_assistant_name: String,
    modifier: Modifier = Modifier,
    on_click: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceVariant.copy(alpha = 0.7f),
        onClick = on_click
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(selected_assistant_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(HugeIcons.BubbleChatQuestion, contentDescription = null, tint = colors.primary, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
private fun agent_drawer_footer_icon(
    icon: ImageVector,
    content_description: String,
    on_click: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(38.dp),
        shape = CircleShape,
        color = colors.surfaceVariant.copy(alpha = 0.7f),
        onClick = on_click
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = content_description, tint = colors.onSurfaceVariant, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
private fun agent_drawer_footer(
    selected_assistant_name: String,
    on_assistant_click: () -> Unit,
    on_stats_click: () -> Unit,
    on_settings_click: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        agent_drawer_assistant_footer(
            selected_assistant_name = selected_assistant_name,
            modifier = Modifier.weight(1f),
            on_click = on_assistant_click
        )
        agent_drawer_footer_icon(icon = HugeIcons.ChartColumn, content_description = "统计", on_click = on_stats_click)
        agent_drawer_footer_icon(icon = HugeIcons.Settings03, content_description = "设置", on_click = on_settings_click)
    }
}

@Composable
private fun agent_message_bubble(
    message: agent_message_preview,
    user_name: String,
    user_avatar_uri: String,
    assistant_name: String
) {
    val is_user = message.role == agent_message_role.User
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (is_user) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        agent_message_header(
            message = message,
            user_name = user_name,
            user_avatar_uri = user_avatar_uri,
            assistant_name = assistant_name
        )
        if (message.tool_steps.isNotEmpty()) {
            agent_tool_steps_card(message.tool_steps)
        }
        agent_message_content(message)
        agent_message_actions(message)
    }
}

@Composable
private fun agent_message_header(
    message: agent_message_preview,
    user_name: String,
    user_avatar_uri: String,
    assistant_name: String
) {
    val is_user = message.role == agent_message_role.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        agent_assistant_identity(message = message, assistant_name = assistant_name, modifier = Modifier.weight(1f))
        agent_user_identity(
            message = message,
            user_name = user_name,
            user_avatar_uri = user_avatar_uri,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun agent_assistant_identity(
    message: agent_message_preview,
    assistant_name: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    if (message.role != agent_message_role.Assistant) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = colors.primaryContainer) {
            Box(contentAlignment = Alignment.Center) {
                Icon(HugeIcons.Sparkles, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(16.dp))
            }
        }
        Text(assistant_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun agent_user_identity(
    message: agent_message_preview,
    user_name: String,
    user_avatar_uri: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    if (message.role != agent_message_role.User) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        Text(user_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        agent_avatar(
            name = user_name,
            avatar_uri = user_avatar_uri,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun agent_message_content(message: agent_message_preview) {
    val colors = MaterialTheme.colorScheme
    val is_user = message.role == agent_message_role.User
    Surface(
        modifier = Modifier.widthIn(max = 330.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (is_user) colors.primaryContainer.copy(alpha = 0.78f) else colors.surfaceVariant.copy(alpha = 0.58f)
    ) {
        SelectionContainer {
            Text(
                text = message.text,
                modifier = Modifier.padding(10.dp),
                color = if (is_user) colors.onPrimaryContainer else colors.onSurface,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun agent_message_actions(message: agent_message_preview) {
    val colors = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        agent_message_action_icon(HugeIcons.Copy01, "复制")
        agent_message_action_icon(HugeIcons.Refresh03, "重新生成")
        agent_message_action_icon(HugeIcons.MoreVertical, "更多")
        Text(message.time, color = colors.onSurfaceVariant.copy(alpha = 0.62f), fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun agent_message_action_icon(icon: ImageVector, content_description: String) {
    val colors = MaterialTheme.colorScheme
    Icon(
        icon,
        contentDescription = content_description,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {}
            .padding(7.dp)
            .size(15.dp),
        tint = colors.onSurfaceVariant
    )
}

@Composable
private fun agent_tool_steps_card(steps: List<String>) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surface.copy(alpha = 0.78f),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.38f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("执行步骤", color = colors.onSurface, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            steps.forEach { step ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(colors.primary.copy(alpha = 0.72f)))
                    Text(step, color = colors.onSurfaceVariant, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun agent_input_icon(icon: ImageVector, content_description: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(30.dp),
        shape = CircleShape,
        color = Color.Transparent,
        onClick = {}
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = content_description, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun agent_round_icon_button(icon: ImageVector, content_description: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(30.dp),
        shape = CircleShape,
        color = Color.Transparent,
        onClick = {}
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = content_description, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun agent_input_bar(
    value: String,
    on_value_change: (String) -> Unit,
    on_send: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                tonalElevation = 0.dp,
                color = colors.surfaceVariant.copy(alpha = 0.72f),
                border = BorderStroke(
                    1.dp,
                    colors.outlineVariant.copy(alpha = 0.55f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    TextField(
                        value = value,
                        onValueChange = on_value_change,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 1,
                        maxLines = 5,
                        placeholder = { Text("询问、修改或解释当前项目...", fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            agent_input_icon(icon = HugeIcons.ServerStack01, content_description = "供应商")
                            agent_input_icon(icon = HugeIcons.AiSearch02, content_description = "搜索")
                            agent_input_icon(icon = HugeIcons.Idea01, content_description = "思考")
                        }
                        agent_round_icon_button(icon = HugeIcons.Add01, content_description = "更多选项")
                        Surface(
                            modifier = Modifier.size(30.dp),
                            shape = CircleShape,
                            color = if (value.isBlank()) colors.surface else colors.primary,
                            onClick = on_send
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = HugeIcons.ArrowUp02,
                                    contentDescription = "发送",
                                    tint = if (value.isBlank()) colors.onSurface.copy(alpha = 0.38f) else colors.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
