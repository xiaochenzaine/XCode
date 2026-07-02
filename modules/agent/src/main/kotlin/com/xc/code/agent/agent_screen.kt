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
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import me.rerere.hugeicons.stroke.Edit03
import me.rerere.hugeicons.stroke.Sparkles

// ── 数据模型 ────────────────────────────────────────────

internal data class agent_message_preview(
    val role: agent_message_role,
    val text: String,
    val time: String,
    val tool_steps: List<String> = emptyList()
)

internal enum class agent_message_role {
    User,
    Assistant
}

internal data class agent_conversation_preview(
    val id: Int,
    val title: String,
    val subtitle: String,
    val pinned: Boolean = false
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

private enum class agent_input_sheet {
    Provider,
    Search,
    Reasoning,
    More
}

// ── 示例数据 ────────────────────────────────────────────

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

internal val sample_conversation_messages = mapOf(
    1 to sample_messages,
    2 to listOf(
        agent_message_preview(role = agent_message_role.User, text = "帮我看看 CMakeLists.txt 有什么问题", time = "14:12"),
        agent_message_preview(role = agent_message_role.Assistant, text = "CMakeLists.txt 中缺少对 C++20 标准的设置，建议添加 set(CMAKE_CXX_STANDARD 20)。另外 target_link_libraries 里少了一个关键依赖。", time = "14:20", tool_steps = listOf("读取 CMakeLists.txt", "分析构建配置")),
    ),
    3 to listOf(
        agent_message_preview(role = agent_message_role.Assistant, text = "编译失败了，错误信息显示找不到头文件 <ranges>。这通常是因为编译器版本过低或未启用 C++20。建议升级到 GCC 12+ 或 Clang 16+。", time = "22:10", tool_steps = listOf("读取构建日志", "分析错误原因")),
        agent_message_preview(role = agent_message_role.User, text = "我已经升级了 Clang，但还是报同样的错。", time = "22:12"),
        agent_message_preview(role = agent_message_role.Assistant, text = "检查 CMakeLists.txt 中 CMAKE_CXX_FLAGS 是否包含 -std=c++20。另外确认 toolchain 文件指向的是新版本的 Clang。", time = "22:16"),
    ),
    4 to listOf(
        agent_message_preview(role = agent_message_role.User, text = "这个项目的文件太多了，有什么好的组织建议吗？", time = "06-30"),
        agent_message_preview(role = agent_message_role.Assistant, text = "建议按功能模块拆分：core/、util/、ui/、third_party/。每个模块内部再按 src/include/test 分层。顶层 CMakeLists.txt 用 add_subdirectory 管理。", time = "06-30", tool_steps = listOf("分析项目结构", "生成目录方案")),
    )
)

internal val sample_conversation_titles = mapOf(
    1 to "新会话",
    2 to "分析 CMake 配置",
    3 to "解释构建错误",
    4 to "整理项目结构"
)

// ── 用户配置 ────────────────────────────────────────────

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

private fun agent_greeting(): String {
    val hour = LocalTime.now().hour
    return when (hour) {
        in 5..10 -> "早上好👋"
        in 11..13 -> "中午好👋"
        in 14..17 -> "下午好👋"
        else -> "晚上好👋"
    }
}

// ── 主界面 ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun agent_screen() {
    val ac = local_agent_colors.current
    val agent_scheme = if (ac == dark_agent_colors) {
        darkColorScheme(
            primary = ac.accent, onPrimary = ac.bg,
            primaryContainer = ac.accent_container, onPrimaryContainer = ac.on_accent_container,
            secondary = ac.accent_dim,
            surface = ac.surface, onSurface = ac.text_primary,
            onSurfaceVariant = ac.text_secondary, surfaceVariant = ac.surface_variant,
            background = ac.bg, onBackground = ac.text_primary,
            outline = ac.border, outlineVariant = ac.input_border,
            error = ac.danger, errorContainer = ac.danger_bg
        )
    } else {
        lightColorScheme(
            primary = ac.accent, onPrimary = ac.bg,
            primaryContainer = ac.accent_container, onPrimaryContainer = ac.on_accent_container,
            secondary = ac.accent_dim,
            surface = ac.surface, onSurface = ac.text_primary,
            onSurfaceVariant = ac.text_secondary, surfaceVariant = ac.surface_variant,
            background = ac.bg, onBackground = ac.text_primary,
            outline = ac.border, outlineVariant = ac.input_border,
            error = ac.danger, errorContainer = ac.danger_bg
        )
    }
    MaterialTheme(colorScheme = agent_scheme) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val drawer_state = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var user_name by remember { mutableStateOf(load_user_name(context)) }
    var user_avatar_uri by remember { mutableStateOf(load_user_avatar_uri(context)) }
    var selected_assistant_name by remember { mutableStateOf(default_assistant_name) }
    var main_page by remember { mutableStateOf(agent_main_page.Chat) }
    var editing_conversation_title by remember { mutableStateOf(false) }
    var draft_conversation_title by remember { mutableStateOf("") }
    var input_sheet by remember { mutableStateOf<agent_input_sheet?>(null) }
    var message_more_sheet by remember { mutableStateOf<agent_message_preview?>(null) }
    var show_assistant_picker by remember { mutableStateOf(false) }
    var show_add_assistant_dialog by remember { mutableStateOf(false) }
    var draft_assistant_name by remember { mutableStateOf("") }
    var assistant_names by remember { mutableStateOf(listOf(default_assistant_name, "代码助手", "翻译助手")) }

    val state = remember { agent_state().apply { load_sample_data() } }
    val conversation_title = state.selected_conversation?.title ?: "新会话"
    val conversation_messages = state.messages_for(state.selected_conversation_id)

    // 会话重命名弹窗
    if (editing_conversation_title) {
        AlertDialog(
            onDismissRequest = { editing_conversation_title = false },
            title = { Text("修改会话名称") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    agent_profile_text_field(
                        value = draft_conversation_title,
                        on_value_change = { draft_conversation_title = it },
                        placeholder = "输入会话名称"
                    )
                    Text(
                        "显示在主页顶部和侧边栏会话列表",
                        color = colors.onSurfaceVariant.copy(alpha = 0.72f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = draft_conversation_title.trim()
                    if (trimmed.isNotEmpty()) state.rename_conversation(state.selected_conversation_id, trimmed)
                    editing_conversation_title = false
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { editing_conversation_title = false }) { Text("取消") }
            }
        )
    }

    BackHandler(enabled = main_page != agent_main_page.Chat) { main_page = agent_main_page.Chat }
    BackHandler(enabled = main_page == agent_main_page.Chat && drawer_state.isOpen) { scope.launch { drawer_state.close() } }

    Box(modifier = Modifier.fillMaxSize()) {
    ModalNavigationDrawer(
        drawerState = drawer_state,
        gesturesEnabled = true,
        drawerContent = {
            agent_drawer(
                user_name = user_name,
                user_avatar_uri = user_avatar_uri,
                selected_assistant_name = selected_assistant_name,
                selected_conversation_id = state.selected_conversation_id,
                on_conversation_select = { state.select_conversation(it); scope.launch { drawer_state.close() } },
                on_pin = { state.toggle_pin(it.id) },
                on_delete_conversation = { state.delete_conversation(it.id) },
                on_regenerate_title = { state.regenerate_title(it.id) },
                pinned_ids = state.pinned_ids,
                conversations = state.conversations,
                conversation_messages_map = state.messages_map,
                on_user_profile_change = { n, a ->
                    user_name = n
                    user_avatar_uri = a
                    save_user_profile(context, n, a)
                },
                on_assistant_click = { main_page = agent_main_page.Assistant },
                on_assistant_switch_click = { show_assistant_picker = true },
                on_stats_click = { main_page = agent_main_page.Stats },
                on_settings_click = { main_page = agent_main_page.Settings }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawer_state.open() } }) {
                            Icon(HugeIcons.Menu03, contentDescription = "侧边栏", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                        }
                    },
                    title = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            modifier = Modifier.clickable {
                                draft_conversation_title = conversation_title
                                editing_conversation_title = true
                            }
                        ) {
                            Text(conversation_title, color = colors.onBackground, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text("未选择供应商", color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 12.sp)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(HugeIcons.Search01, contentDescription = "搜索聊天", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                        }
                        IconButton(onClick = { state.create_new_conversation() }) {
                            Icon(HugeIcons.MessageAdd01, contentDescription = "新建会话", tint = colors.onBackground, modifier = Modifier.size(22.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                agent_input_bar(
                    value = input,
                    on_value_change = { input = it },
                    on_send = {
                        state.send_user_message(input, LocalTime.now().let { "%02d:%02d".format(it.hour, it.minute) })
                        input = ""
                    },
                    on_provider_click = { input_sheet = agent_input_sheet.Provider },
                    on_search_click = { input_sheet = agent_input_sheet.Search },
                    on_reasoning_click = { input_sheet = agent_input_sheet.Reasoning },
                    on_more_click = { input_sheet = agent_input_sheet.More }
                )
            },
            containerColor = Color.Transparent
        ) { padding_values ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding_values)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                items(conversation_messages) { message ->
                    agent_message_bubble(
                        message = message,
                        user_name = user_name,
                        user_avatar_uri = user_avatar_uri,
                        assistant_name = selected_assistant_name,
                        on_more_click = { message_more_sheet = message }
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    // Overlay 页面
    AnimatedContent(
        targetState = main_page,
        modifier = Modifier.fillMaxSize(),
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
        label = "agent_overlay_page"
    ) { page ->
        if (page != agent_main_page.Chat) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = { agent_back_button(on_click = { main_page = agent_main_page.Chat }) },
                        title = {
                            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                                Text(
                                    when (page) {
                                        agent_main_page.Assistant -> selected_assistant_name
                                        agent_main_page.Stats -> "统计"
                                        agent_main_page.Settings -> "设置"
                                        agent_main_page.Chat -> conversation_title
                                    },
                                    color = colors.onBackground, fontSize = 16.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold
                                )
                                Text("占位页面", color = colors.onSurfaceVariant, fontSize = 10.sp, lineHeight = 12.sp)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
                    )
                },
                containerColor = colors.background
            ) { padding_values ->
                when (page) {
                    agent_main_page.Assistant -> agent_assistant_page(assistant_name = selected_assistant_name, modifier = Modifier.padding(padding_values))
                    agent_main_page.Stats -> agent_stats_page(modifier = Modifier.padding(padding_values))
                    agent_main_page.Settings -> agent_settings_page(modifier = Modifier.padding(padding_values))
                    agent_main_page.Chat -> Unit
                }
            }
        }
    }

    // 输入栏底部弹窗
    if (input_sheet != null) {
        agent_bottom_sheet(on_dismiss = { input_sheet = null })
    }

    // 消息更多弹窗
    if (message_more_sheet != null) {
        agent_bottom_sheet(on_dismiss = { message_more_sheet = null }) {
            agent_sheet_action_item(HugeIcons.ArrowUp02, "引用回复") { message_more_sheet = null }
            agent_sheet_action_item(HugeIcons.Delete01, "删除消息") {
                message_more_sheet?.let { state.delete_message(state.selected_conversation_id, it) }
                message_more_sheet = null
            }
        }
    }

    // 助手选择弹窗
    if (show_assistant_picker) {
        agent_bottom_sheet(on_dismiss = { show_assistant_picker = false }) {
            assistant_names.forEach { assistant_name ->
                agent_assistant_picker_item(
                    name = assistant_name,
                    selected = assistant_name == selected_assistant_name,
                    on_select = {
                        selected_assistant_name = assistant_name
                        show_assistant_picker = false
                    },
                    on_settings = {
                        selected_assistant_name = assistant_name
                        show_assistant_picker = false
                        main_page = agent_main_page.Assistant
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            agent_add_assistant_item {
                draft_assistant_name = ""
                show_assistant_picker = false
                show_add_assistant_dialog = true
            }
        }
    }

    // 添加助手弹窗
    if (show_add_assistant_dialog) {
        AlertDialog(
            onDismissRequest = { show_add_assistant_dialog = false },
            title = { Text("添加助手") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    agent_profile_text_field(
                        value = draft_assistant_name,
                        on_value_change = { draft_assistant_name = it },
                        placeholder = "输入助手名称"
                    )
                    Text("添加后会自动切换到这个助手", color = colors.onSurfaceVariant.copy(alpha = 0.72f), fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val next_name = draft_assistant_name.trim()
                    if (next_name.isNotEmpty()) {
                        if (next_name !in assistant_names) assistant_names = assistant_names + next_name
                        selected_assistant_name = next_name
                        main_page = agent_main_page.Assistant
                    }
                    show_add_assistant_dialog = false
                }) { Text("添加") }
            },
            dismissButton = { TextButton(onClick = { show_add_assistant_dialog = false }) { Text("取消") } }
        )
    }
    } // Box
    } // MaterialTheme
}

// ── 侧边栏 ──────────────────────────────────────────────

@Composable
private fun agent_drawer(
    user_name: String,
    user_avatar_uri: String,
    selected_assistant_name: String,
    selected_conversation_id: Int,
    on_conversation_select: (Int) -> Unit,
    on_pin: (agent_conversation_preview) -> Unit,
    on_delete_conversation: (agent_conversation_preview) -> Unit,
    on_regenerate_title: (agent_conversation_preview) -> Unit,
    pinned_ids: Set<Int>,
    conversations: List<agent_conversation_preview>,
    conversation_messages_map: Map<Int, List<agent_message_preview>>,
    on_user_profile_change: (String, String) -> Unit,
    on_assistant_click: () -> Unit,
    on_assistant_switch_click: () -> Unit,
    on_stats_click: () -> Unit,
    on_settings_click: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    var editing_profile by remember { mutableStateOf(false) }
    var draft_name by remember { mutableStateOf(user_name) }
    var show_avatar_options by remember { mutableStateOf(false) }
    var search_mode by remember { mutableStateOf<agent_search_mode?>(null) }
    var conversation_query by remember { mutableStateOf("") }
    var chat_query by remember { mutableStateOf("") }
    val search_focus_requester = remember { FocusRequester() }
    var delete_conversation by remember { mutableStateOf<agent_conversation_preview?>(null) }
    var regenerate_title_target by remember { mutableStateOf<agent_conversation_preview?>(null) }
    var show_emoji_picker by remember { mutableStateOf(false) }
    var show_url_input by remember { mutableStateOf(false) }
    var url_input by remember { mutableStateOf("") }

    val avatar_picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selected_uri ->
            copy_user_avatar_to_private_file(context, selected_uri)?.let { local_uri ->
                on_user_profile_change(user_name, local_uri)
            }
        }
    }

    val visible_conversations = remember(conversation_query, chat_query, search_mode, pinned_ids, conversations) {
        val base = when {
            search_mode == null -> conversations
            search_mode == agent_search_mode.Conversation -> {
                if (conversation_query.isBlank()) conversations
                else conversations.filter { it.title.contains(conversation_query, ignoreCase = true) }
            }
            else -> { // Chat search
                if (chat_query.isBlank()) conversations
                else {
                    val query = chat_query.trim()
                    conversations.filter { conv ->
                        conversation_messages_map[conv.id].orEmpty().any { msg -> msg.text.contains(query, ignoreCase = true) }
                    }
                }
            }
        }
        base.map { it.copy(pinned = it.id in pinned_ids) }
            .sortedWith(compareByDescending<agent_conversation_preview> { it.pinned }.thenBy { it.id })
    }

    if (editing_profile) {
        AlertDialog(
            onDismissRequest = { editing_profile = false },
            title = { Text("修改昵称") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    agent_profile_text_field(
                        value = draft_name,
                        on_value_change = { draft_name = it },
                        placeholder = "输入新的昵称"
                    )
                    Text(
                        "显示在侧边栏和你的消息旁",
                        color = colors.onSurfaceVariant.copy(alpha = 0.72f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val next_name = draft_name.trim()
                    if (next_name.isNotEmpty()) on_user_profile_change(next_name, user_avatar_uri)
                    editing_profile = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { editing_profile = false }) { Text("取消") } }
        )
    }

    // 头像选择弹窗
    if (show_avatar_options) {
        agent_bottom_sheet(on_dismiss = { show_avatar_options = false }) {
            agent_sheet_action_item(HugeIcons.Edit03, "相册选择") {
                show_avatar_options = false
                avatar_picker.launch("image/*")
            }
            agent_sheet_action_item(HugeIcons.Sparkles, "选择表情") {
                show_avatar_options = false
                show_emoji_picker = true
            }
            agent_sheet_action_item(HugeIcons.Copy01, "输入链接") {
                show_avatar_options = false
                url_input = ""
                show_url_input = true
            }
            agent_sheet_action_item(HugeIcons.Refresh01, "恢复默认") {
                show_avatar_options = false
                on_user_profile_change(user_name, "")
            }
        }
    }

    // 删除会话确认弹窗
    if (delete_conversation != null) {
        AlertDialog(
            onDismissRequest = { delete_conversation = null },
            title = { Text("删除会话") },
            text = { Text("确定删除「${delete_conversation!!.title}」？") },
            confirmButton = {
                TextButton(onClick = {
                    val conv = delete_conversation!!
                    on_delete_conversation(conv)
                    delete_conversation = null
                }) { Text("删除", color = colors.error) }
            },
            dismissButton = { TextButton(onClick = { delete_conversation = null }) { Text("取消") } }
        )
    }

    // 生成标题确认弹窗
    if (regenerate_title_target != null) {
        AlertDialog(
            onDismissRequest = { regenerate_title_target = null },
            title = { Text("生成标题") },
            text = { Text("确定要为「${regenerate_title_target!!.title}」重新生成标题吗？") },
            confirmButton = { TextButton(onClick = { val t = regenerate_title_target; regenerate_title_target = null; if (t != null) on_regenerate_title(t) }) { Text("确定") } },
            dismissButton = { TextButton(onClick = { regenerate_title_target = null }) { Text("取消") } }
        )
    }

    // 表情选择弹窗
    if (show_emoji_picker) {
        agent_bottom_sheet(on_dismiss = { show_emoji_picker = false }) {
            val emoji_categories = remember {
                listOf(
                    "😀" to listOf("😀","😂","🤣","😊","😇","😍","🤩","😎","🥳","🥺","😢","😡","🤔","😴","😱","🤗","🤭","🫡","🥹","😶‍🌫️"),
                    "🐱" to listOf("🐱","🐶","🦊","🐼","🐨","🐸","🐵","🦁","🐮","🐷","🐰","🐻","🐔","🐧","🦄","🐙","🦋","🐞","🦜","🐢"),
                    "🍕" to listOf("🍕","🍔","🌮","🍣","🎂","🍩","☕","🍺","🍎","🍇","🥑","🧀","🍿","🍦","🍫","🥐","🍉","🍒","🥩","🧁"),
                    "🚀" to listOf("🚀","✈️","🚗","🚲","🏠","🏖️","🌍","⭐","🔥","💡","⏰","📱","💻","🎮","📚","🎵","🎬","🛒","🔑","🎁"),
                    "❤️" to listOf("❤️","💯","✅","❌","⚠️","🔒","💪","👍","👋","✨","🌟","💎","🎉","🎨","🔔","📌","♻️","🛑","💤","🏳️"),
                )
            }
            var selected_cat by remember { mutableIntStateOf(0) }
            var emoji_search by remember { mutableStateOf("") }

            // 搜索栏
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = colors.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(HugeIcons.Search01, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    BasicTextField(
                        value = emoji_search,
                        onValueChange = { emoji_search = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = colors.onSurface, fontSize = 14.sp),
                        cursorBrush = SolidColor(colors.primary),
                        decorationBox = { inner ->
                            Box { if (emoji_search.isEmpty()) Text("搜索表情...", color = colors.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp); inner() }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 分类标签 (搜索时不显示)
            if (emoji_search.isEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    items(emoji_categories.size) { index ->
                        val (icon, _) = emoji_categories[index]
                        Surface(
                            modifier = Modifier.clickable { selected_cat = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected_cat == index) colors.primary else colors.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(icon, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), fontSize = 16.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 表情网格
            val display_emojis = if (emoji_search.isNotEmpty()) {
                emoji_categories.flatMap { it.second }.filter { it.contains(emoji_search, ignoreCase = true) }
            } else {
                emoji_categories[selected_cat].second
            }
            if (display_emojis.isEmpty()) {
                Text("没有匹配的表情", color = colors.onSurfaceVariant, fontSize = 13.sp)
            } else {
                var i = 0
                while (i < display_emojis.size) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(5) { col ->
                            val idx = i + col
                            if (idx < display_emojis.size) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = colors.surfaceVariant.copy(alpha = 0.35f),
                                    onClick = {
                                        on_user_profile_change(user_name, display_emojis[idx])
                                        show_emoji_picker = false
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(display_emojis[idx], fontSize = 22.sp)
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    i += 5
                }
            }
        }
    }

    // URL 输入弹窗
    if (show_url_input) {
        AlertDialog(
            onDismissRequest = { show_url_input = false },
            title = { Text("输入图片链接") },
            text = {
                OutlinedTextField(
                    value = url_input,
                    onValueChange = { url_input = it },
                    label = { Text("图片 URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (url_input.isNotBlank()) {
                        on_user_profile_change(user_name, url_input.trim())
                        show_url_input = false
                    }
                }) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { show_url_input = false }) { Text("取消") }
            }
        )
    }

    ModalDrawerSheet(modifier = Modifier.width(300.dp), drawerContainerColor = colors.surface) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                agent_avatar(
                    name = user_name, avatar_uri = user_avatar_uri,
                    modifier = Modifier.size(46.dp), editable = true,
                    on_click = { show_avatar_options = true }
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(user_name,
                            color = colors.onSurface, fontSize = 15.sp, lineHeight = 17.sp,
                            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.clickable { draft_name = user_name; editing_profile = true }
                        )
                        Icon(HugeIcons.PencilEdit01, contentDescription = "编辑", tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(13.dp).clip(CircleShape).clickable { draft_name = user_name; editing_profile = true }
                        )
                    }
                    Text(agent_greeting(), color = colors.onSurfaceVariant, fontSize = 11.sp, lineHeight = 13.sp)
                }
            }

            AnimatedContent(targetState = search_mode,
                transitionSpec = { fadeIn().togetherWith(fadeOut()).using(SizeTransform(clip = false)) },
                label = "agent_search_mode"
            ) { mode ->
                if (mode != null) {
                    LaunchedEffect(mode) {
                        search_focus_requester.requestFocus()
                    }
                    val is_chat_search = mode == agent_search_mode.Chat
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        agent_search_field(
                            value = if (is_chat_search) chat_query else conversation_query,
                            on_value_change = { if (is_chat_search) chat_query = it else conversation_query = it },
                            placeholder = if (is_chat_search) "搜索聊天..." else "搜索会话...",
                            modifier = Modifier.weight(1f),
                            focus_requester = search_focus_requester
                        )
                        Surface(modifier = Modifier.size(35.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.16f),
                            onClick = {
                                if (is_chat_search) chat_query = "" else conversation_query = ""
                                search_mode = null
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(HugeIcons.Cancel01, contentDescription = "取消搜索", tint = colors.primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        agent_drawer_action(icon = HugeIcons.AiSearch02, label = "搜索聊天", modifier = Modifier.weight(1f), on_click = { search_mode = agent_search_mode.Chat })
                        agent_drawer_action(icon = HugeIcons.Search01, label = "搜索会话", modifier = Modifier.weight(1f), on_click = { search_mode = agent_search_mode.Conversation })
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val flat = buildList {
                    val pinned_items = visible_conversations.filter { it.pinned }
                    val unpinned_items = visible_conversations.filter { !it.pinned }
                    addAll(pinned_items)
                    if (pinned_items.isNotEmpty() && unpinned_items.isNotEmpty()) add(null)
                    addAll(unpinned_items)
                }
                items(flat.size) { index ->
                    val item = flat[index]
                    if (item == null) {
                        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp).height(1.dp).background(colors.outlineVariant))
                    } else {
                        agent_conversation_item(
                            item = item,
                            selected = item.id == selected_conversation_id,
                            on_click = { on_conversation_select(item.id) },
                            on_pin = { on_pin(item) },
                            on_delete = { delete_conversation = it },
                            on_regenerate_title = { regenerate_title_target = it }
                        )
                    }
                }
            }

            agent_drawer_footer(
                selected_assistant_name = selected_assistant_name,
                on_assistant_click = on_assistant_switch_click,
                on_assistant_icon_click = on_assistant_click,
                on_stats_click = on_stats_click,
                on_settings_click = on_settings_click
            )
        }
    }
}

// ── 会话列表项 ──────────────────────────────────────────

@Composable
private fun agent_conversation_item(
    item: agent_conversation_preview,
    selected: Boolean,
    on_click: () -> Unit,
    on_pin: (agent_conversation_preview) -> Unit,
    on_delete: (agent_conversation_preview) -> Unit,
    on_regenerate_title: (agent_conversation_preview) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var menu_expanded by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), color = if (selected) colors.primaryContainer else Color.Transparent) {
        Box {
            Column(
                modifier = Modifier.fillMaxWidth().combinedClickable(onClick = on_click, onLongClick = { menu_expanded = true }).padding(horizontal = 12.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(item.title, color = if (selected) colors.onPrimaryContainer else colors.onSurface, fontSize = 14.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (item.pinned) Icon(HugeIcons.Pin, contentDescription = "已置顶", tint = colors.primary.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                }
                Text(item.subtitle, color = if (selected) colors.onPrimaryContainer.copy(alpha = 0.72f) else colors.onSurfaceVariant, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            DropdownMenu(expanded = menu_expanded, onDismissRequest = { menu_expanded = false }) {
                agent_conversation_menu_item(icon = HugeIcons.Pin, text = if (item.pinned) "取消置顶" else "置顶会话") { menu_expanded = false; on_pin(item) }
                agent_conversation_menu_item(icon = HugeIcons.Refresh01, text = "生成标题") { menu_expanded = false; on_regenerate_title(item) }
                agent_conversation_menu_item(icon = HugeIcons.Delete01, text = "删除会话") { menu_expanded = false; on_delete(item) }
            }
        }
    }
}

@Composable
private fun agent_conversation_menu_item(icon: ImageVector, text: String, on_click: () -> Unit) {
    DropdownMenuItem(text = { Text(text) }, leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) }, onClick = on_click)
}

@Composable
private fun agent_assistant_picker_item(
    name: String,
    selected: Boolean,
    on_select: () -> Unit,
    on_settings: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) colors.primaryContainer else colors.surfaceVariant.copy(alpha = 0.55f),
        onClick = on_select
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = if (selected) colors.primary.copy(alpha = 0.18f) else colors.surface.copy(alpha = 0.7f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        HugeIcons.BubbleChatQuestion,
                        contentDescription = null,
                        tint = if (selected) colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(
                name,
                modifier = Modifier.weight(1f),
                color = if (selected) colors.onPrimaryContainer else colors.onSurface,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                HugeIcons.Settings03,
                contentDescription = "助手设置",
                tint = if (selected) colors.primary else colors.onSurfaceVariant,
                modifier = Modifier.size(32.dp).clip(CircleShape).clickable(onClick = on_settings).padding(7.dp)
            )
        }
    }
}

@Composable
private fun agent_add_assistant_item(on_click: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.primary.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.16f)),
        onClick = on_click
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(modifier = Modifier.size(34.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.14f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(HugeIcons.Add01, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Text(
                    "添加助手",
                    color = colors.primary,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "创建一个新的助手配置",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ── 底部栏 ──────────────────────────────────────────────

@Composable
private fun agent_drawer_assistant_footer(selected_assistant_name: String, modifier: Modifier = Modifier, on_card_click: () -> Unit = {}, on_icon_click: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = colors.surfaceVariant.copy(alpha = 0.7f)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = on_card_click).padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(selected_assistant_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Icon(HugeIcons.Settings03, contentDescription = "助手设置", tint = colors.primary, modifier = Modifier.size(28.dp).clip(CircleShape).clickable(onClick = on_icon_click).padding(5.dp))
        }
    }
}

@Composable
private fun agent_drawer_footer_icon(icon: ImageVector, content_description: String, on_click: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = Modifier.size(38.dp), shape = CircleShape, color = colors.surfaceVariant.copy(alpha = 0.7f), onClick = on_click) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = content_description, tint = colors.onSurfaceVariant, modifier = Modifier.size(19.dp)) }
    }
}

@Composable
private fun agent_drawer_footer(selected_assistant_name: String, on_assistant_click: () -> Unit, on_assistant_icon_click: () -> Unit, on_stats_click: () -> Unit, on_settings_click: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        agent_drawer_assistant_footer(selected_assistant_name = selected_assistant_name, modifier = Modifier.weight(1f), on_card_click = on_assistant_click, on_icon_click = on_assistant_icon_click)
        agent_drawer_footer_icon(icon = HugeIcons.ChartColumn, content_description = "统计", on_click = on_stats_click)
        agent_drawer_footer_icon(icon = HugeIcons.Settings03, content_description = "设置", on_click = on_settings_click)
    }
}

// ── 通用组件 ────────────────────────────────────────────

@Composable
private fun agent_back_button(on_click: () -> Unit) {
    FilledTonalIconButton(onClick = on_click) { Icon(HugeIcons.ArrowLeft01, contentDescription = "返回") }
}

@Composable
private fun agent_avatar(name: String, avatar_uri: String, modifier: Modifier = Modifier, editable: Boolean = false, on_click: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    val is_emoji = avatar_uri.isNotBlank() && !avatar_uri.startsWith("/") && !avatar_uri.startsWith("content:") && !avatar_uri.startsWith("file:") && !avatar_uri.startsWith("http") && avatar_uri.length <= 4
    Box(modifier = modifier) {
        Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = colors.primaryContainer, onClick = on_click) {
            Box(contentAlignment = Alignment.Center) {
                if (avatar_uri.isNotBlank() && !is_emoji) {
                    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context -> ImageView(context).apply { scaleType = ImageView.ScaleType.CENTER_CROP; setImageURI(Uri.parse(avatar_uri)) } }, update = { it.setImageURI(Uri.parse(avatar_uri)) })
                } else if (is_emoji) {
                    Text(avatar_uri, fontSize = 20.sp)
                } else {
                    Text(name.take(1).ifBlank { "你" }, color = colors.onPrimaryContainer, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (editable) {
            Surface(modifier = Modifier.align(Alignment.BottomEnd).size(18.dp), shape = CircleShape, color = colors.tertiaryContainer) {
                Box(contentAlignment = Alignment.Center) { Icon(HugeIcons.Edit03, contentDescription = "编辑头像", tint = colors.onTertiaryContainer, modifier = Modifier.size(10.dp)) }
            }
        }
    }
}

@Composable
private fun agent_profile_text_field(value: String, on_value_change: (String) -> Unit, placeholder: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceVariant.copy(alpha = 0.42f),
        border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = colors.primary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        HugeIcons.Edit03,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            BasicTextField(
                value = value,
                onValueChange = on_value_change,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(colors.primary),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                placeholder,
                                color = colors.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                        inner()
                    }
                }
            )
        }
    }
}

@Composable
private fun agent_search_field(value: String, on_value_change: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier, focus_requester: FocusRequester? = null) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = modifier.height(35.dp), shape = RoundedCornerShape(10.dp), color = colors.surfaceVariant.copy(alpha = 0.5f)) {
        BasicTextField(value = value, onValueChange = on_value_change, modifier = Modifier.then(if (focus_requester != null) Modifier.focusRequester(focus_requester) else Modifier).padding(horizontal = 10.dp, vertical = 8.dp), singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = colors.onSurface, fontSize = 13.sp), cursorBrush = SolidColor(colors.primary),
            decorationBox = { inner -> Box { if (value.isEmpty()) Text(placeholder, color = colors.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 13.sp); inner() } }
        )
    }
}

@Composable
private fun agent_drawer_action(icon: ImageVector, label: String, modifier: Modifier = Modifier, on_click: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = modifier.height(35.dp), shape = RoundedCornerShape(10.dp), color = colors.surfaceVariant.copy(alpha = 0.6f), onClick = on_click) {
        Row(modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Text(label, color = colors.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

// ── 消息 ────────────────────────────────────────────────

@Composable
private fun agent_message_bubble(message: agent_message_preview, user_name: String, user_avatar_uri: String, assistant_name: String, on_more_click: () -> Unit = {}) {
    val is_user = message.role == agent_message_role.User
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (is_user) Alignment.End else Alignment.Start, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        agent_message_header(message = message, user_name = user_name, user_avatar_uri = user_avatar_uri, assistant_name = assistant_name)
        if (message.tool_steps.isNotEmpty()) agent_tool_steps_card(message.tool_steps)
        agent_message_content(message)
        agent_message_actions(message, on_more_click = on_more_click)
    }
}

@Composable
private fun agent_message_header(message: agent_message_preview, user_name: String, user_avatar_uri: String, assistant_name: String) {
    val is_user = message.role == agent_message_role.User
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
        agent_assistant_identity(message = message, assistant_name = assistant_name, modifier = Modifier.weight(1f))
        agent_user_identity(message = message, user_name = user_name, user_avatar_uri = user_avatar_uri, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun agent_assistant_identity(message: agent_message_preview, assistant_name: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    if (message.role != agent_message_role.Assistant) return
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = colors.primaryContainer) {
            Box(contentAlignment = Alignment.Center) { Icon(HugeIcons.Sparkles, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(16.dp)) }
        }
        Text(assistant_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun agent_user_identity(message: agent_message_preview, user_name: String, user_avatar_uri: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    if (message.role != agent_message_role.User) return
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
        Text(user_name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        agent_avatar(name = user_name, avatar_uri = user_avatar_uri, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun agent_message_content(message: agent_message_preview) {
    val colors = MaterialTheme.colorScheme
    val is_user = message.role == agent_message_role.User
    Surface(modifier = Modifier.widthIn(max = 330.dp), shape = RoundedCornerShape(16.dp), color = if (is_user) colors.primaryContainer.copy(alpha = 0.78f) else colors.surfaceVariant.copy(alpha = 0.58f)) {
        SelectionContainer { Text(text = message.text, modifier = Modifier.padding(12.dp), color = colors.onSurface, fontSize = 13.sp, lineHeight = 18.sp) }
    }
}

@Composable
private fun agent_message_actions(message: agent_message_preview, on_more_click: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        agent_message_action_icon(HugeIcons.Copy01, "复制")
        agent_message_action_icon(HugeIcons.Refresh03, "重新生成")
        agent_message_action_icon(HugeIcons.MoreVertical, "更多", on_click = on_more_click)
        Text(message.time, color = colors.onSurfaceVariant.copy(alpha = 0.62f), fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun agent_message_action_icon(icon: ImageVector, content_description: String, on_click: () -> Unit = {}) {
    Icon(icon, contentDescription = content_description,
        modifier = Modifier.clip(CircleShape).clickable(onClick = on_click).padding(7.dp).size(15.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun agent_tool_steps_card(steps: List<String>) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), color = colors.surfaceVariant.copy(alpha = 0.4f), border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.38f))) {
        Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

// ── 输入栏 ──────────────────────────────────────────────

@Composable
private fun agent_input_bar(value: String, on_value_change: (String) -> Unit, on_send: () -> Unit, on_provider_click: () -> Unit, on_search_click: () -> Unit, on_reasoning_click: () -> Unit, on_more_click: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(color = Color.Transparent, tonalElevation = 0.dp) {
        Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(horizontal = 8.dp, vertical = 8.dp)) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), tonalElevation = 0.dp, color = colors.surfaceVariant.copy(alpha = 0.72f), border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.55f))) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    TextField(value = value, onValueChange = on_value_change, modifier = Modifier.fillMaxWidth(), minLines = 1, maxLines = 5,
                        placeholder = { Text("询问、修改或解释当前项目...", fontSize = 13.sp) },
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            agent_input_icon(icon = HugeIcons.ServerStack01, content_description = "供应商", on_click = on_provider_click)
                            agent_input_icon(icon = HugeIcons.AiSearch02, content_description = "搜索", on_click = on_search_click)
                            agent_input_icon(icon = HugeIcons.Idea01, content_description = "思考", on_click = on_reasoning_click)
                        }
                        agent_round_icon_button(icon = HugeIcons.Add01, content_description = "更多选项", on_click = on_more_click)
                        Surface(
                            modifier = Modifier.size(30.dp), shape = CircleShape,
                            color = if (value.isBlank()) colors.onSurface.copy(alpha = 0.12f) else colors.primary,
                            onClick = { if (value.isNotBlank()) on_send() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(HugeIcons.ArrowUp02, contentDescription = "发送",
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

@Composable
private fun agent_input_icon(icon: ImageVector, content_description: String, on_click: () -> Unit = {}) {
    Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = Color.Transparent, onClick = on_click) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = content_description, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
    }
}

@Composable
private fun agent_round_icon_button(icon: ImageVector, content_description: String, on_click: () -> Unit = {}) {
    Surface(modifier = Modifier.size(30.dp), shape = CircleShape, color = Color.Transparent, onClick = on_click) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = content_description, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
    }
}

// ── 底部弹窗 ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun agent_bottom_sheet(on_dismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit = {
    Text("此功能正在开发中，敬请期待", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
}) {
    val colors = MaterialTheme.colorScheme
    val sheet_state = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = on_dismiss, sheetState = sheet_state, containerColor = colors.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun agent_sheet_action_item(icon: ImageVector, text: String, on_click: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = colors.surfaceVariant.copy(alpha = 0.5f), onClick = on_click) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Text(text, color = colors.onSurface, fontSize = 14.sp)
        }
    }
}
