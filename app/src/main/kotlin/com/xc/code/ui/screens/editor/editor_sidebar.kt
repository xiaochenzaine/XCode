package com.xc.code.ui.screens.editor

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import com.xc.code.editor.model.editor_settings_state
import com.xc.code.editor.model.editor_file_node
import com.xc.code.project.project_ide_config

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.xc.code.project_file_tree.project_file_tree_colors
import com.xc.code.R
import com.xc.code.ui.theme.app_theme_provider
import com.xc.code.ui.theme.app_colors
import me.rerere.rikkahub.RouteFragment

private data class editor_tool_item(
    val icon: ImageVector,
    val label: String
)

@Composable
fun editor_sidebar(
    drawer_width: Dp,
    drawer_offset_px: Int,
    selected_tool: Int,
    project_root_path: String,
    agent_conversation_id: String,
    file_nodes: List<editor_file_node>,
    expanded_paths: Set<String>,
    file_tree_loading: Boolean,
    project_exists: Boolean,
    editor_settings: editor_settings_state,
    on_tool_selected: (Int) -> Unit,
    on_editor_settings_change: (editor_settings_state) -> Unit,
    on_project_config_apply: (project_ide_config, () -> Unit) -> Unit,
    on_import_editor_font: () -> Unit,
    on_open_editor_theme_settings: () -> Unit,
    on_new_file: (String) -> Unit,
    on_new_folder: (String) -> Unit,
    on_refresh: (String) -> Unit,
    on_rename_node: (String, String) -> Unit,
    on_delete_node: (String) -> Unit,
    on_directory_click: (String) -> Unit,
    on_file_click: (String) -> Unit,
    on_file_position_click: (String, Int, Int) -> Unit,
    on_drag: (Offset) -> Unit,
    on_close: () -> Unit
) {

    val colors = app_theme_provider.colors
    val tools = listOf(
        editor_tool_item(Icons.Default.Folder, stringResource(R.string.editor_tool_files)),
        editor_tool_item(Icons.Default.Tune, stringResource(R.string.editor_tool_project_config)),
        editor_tool_item(Icons.Outlined.AutoAwesome, stringResource(R.string.editor_tool_agent)),
        editor_tool_item(Icons.Default.Settings, stringResource(R.string.editor_tool_settings))
    )

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(drawer_width)
            .offset { IntOffset(drawer_offset_px, 0) },
        color = colors.editor_bg,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                sidebar_capsule_tool_bar(
                    tools = tools,
                    selected_tool = selected_tool,
                    colors = colors,
                    on_tool_selected = on_tool_selected,
                    on_close = on_close,
                    modifier = Modifier
                )

                when (selected_tool) {
                    0 -> file_tree_panel(
                        nodes = file_nodes,
                        project_root_path = project_root_path,
                        expanded_paths = expanded_paths,
                        loading = file_tree_loading,
                        project_exists = project_exists,
                        on_new_file = on_new_file,
                        on_new_folder = on_new_folder,
                        on_refresh = on_refresh,
                        on_rename_node = on_rename_node,
                        on_delete_node = on_delete_node,
                        on_directory_click = on_directory_click,
                        on_file_click = on_file_click,
                        on_file_position_click = on_file_position_click,
                        colors = project_file_tree_colors(
                            dialog_bg = colors.dialog_bg,
                            editor_button_bg = colors.editor_button_bg,
                            editor_capsule_bg = colors.editor_capsule_bg,
                            editor_divider = colors.editor_divider,
                            editor_hint = colors.editor_hint,
                            editor_icon = colors.editor_icon,
                            editor_file_tree_folder_icon = colors.editor_file_tree_folder_icon,
                            editor_file_tree_action_icon = colors.editor_file_tree_action_icon,
                            editor_text = colors.editor_text,
                            danger = colors.danger,
                            danger_bg = colors.danger_bg
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> editor_project_config_panel(
                        project_root_path = project_root_path,
                        on_apply = on_project_config_apply,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> editor_agent_panel(
                        project_root_path = project_root_path,
                        conversation_id = agent_conversation_id,
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> editor_settings_panel(
                        settings = editor_settings,
                        on_settings_change = on_editor_settings_change,
                        on_import_font = on_import_editor_font,
                        on_open_theme_settings = on_open_editor_theme_settings,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(28.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, drag_amount ->
                            change.consume()
                            on_drag(drag_amount)
                        }
                    },
                color = Color.Transparent,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.editor_adjust_width), tint = colors.editor_hint, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}


@Composable
private fun sidebar_capsule_tool_bar(
    tools: List<editor_tool_item>,
    selected_tool: Int,
    colors: app_colors,
    on_tool_selected: (Int) -> Unit,
    on_close: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        color = colors.editor_capsule_bg,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = colors.editor_capsule_icon,
                    modifier = Modifier.size(21.dp)
                )
            }

            sidebar_capsule_vertical_divider(colors)

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tools.forEachIndexed { index, tool ->
                    val selected = selected_tool == index
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 36.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (selected) colors.editor_sidebar_selected_bg
                                else Color.Transparent
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true),
                                onClick = {
                                    if (!selected) on_tool_selected(index)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = tool.label,
                            tint = if (selected) {
                                colors.editor_icon
                            } else {
                                colors.editor_hint.copy(alpha = 0.72f)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            sidebar_capsule_vertical_divider(colors)

            IconButton(
                onClick = on_close,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = colors.editor_capsule_icon,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun sidebar_capsule_vertical_divider(colors: app_colors) {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(18.dp)
            .background(colors.editor_capsule_divider)
    )
}


@Composable
private fun editor_agent_panel(project_root_path: String, conversation_id: String, modifier: Modifier = Modifier) {
    val embedded_background_color = app_theme_provider.colors.editor_bg.toArgb()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            androidx.fragment.app.FragmentContainerView(context).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { view ->
            val activity = view.context.find_fragment_activity()
            if (activity != null && activity.supportFragmentManager.findFragmentById(view.id) == null) {
                view.post {
                    if (activity.isFinishing || activity.isDestroyed) return@post
                    if (activity.supportFragmentManager.findFragmentById(view.id) != null) return@post
                    val fragment = RouteFragment().apply {
                        arguments = android.os.Bundle().apply {
                            putString("host_project_root_path", project_root_path)
                            putString("host_initial_workspace_cwd", "/workspace/XCodeProjects/${java.io.File(project_root_path).name}")
                            putString("host_conversation_id", conversation_id)
                            putInt("host_embedded_background_color", embedded_background_color)
                        }
                    }
                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(view.id, fragment)
                        .commitAllowingStateLoss()
                }
            }
        },
        onRelease = { view ->
            val activity = view.context.find_fragment_activity()
            val fragment = activity?.supportFragmentManager?.findFragmentById(view.id)
            if (activity != null && fragment != null) {
                activity.supportFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss()
            }
        }
    )
}

private tailrec fun Context.find_fragment_activity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.find_fragment_activity()
    else -> null
}

@Composable
private fun sidebar_placeholder(title: String) {
    val colors = app_theme_provider.colors
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.Description, contentDescription = null, tint = colors.editor_hint, modifier = Modifier.size(28.dp))
            Text(title, color = colors.editor_text, fontSize = 14.sp)
            Text(stringResource(R.string.editor_pending), color = colors.editor_hint, fontSize = 12.sp)
        }
    }
}
