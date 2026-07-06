package com.xc.code.ui.screens.editor

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.xc.code.R
import com.xc.code.ui.theme.app_theme_provider
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

internal enum class editor_output_tab(val title_res: Int) {
    Output(R.string.output_tab_build),
    Log(R.string.output_tab_log),
    Terminal(R.string.output_tab_terminal)
}

internal enum class editor_output_line_level {
    NORMAL,
    INFO,
    WARNING,
    ERROR,
    SUCCESS
}

internal data class editor_output_line(
    val text: String,
    val level: editor_output_line_level = editor_output_line_level.NORMAL
)

private const val editor_output_max_lines = 5000
private const val editor_output_refresh_delay_ms = 80L
private val editor_log_time_formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

internal class editor_output_panel_state {
    var selected_tab by mutableStateOf(editor_output_tab.Output)
    var task_title by mutableStateOf("")
    var task_subtitle by mutableStateOf("")
    var task_running by mutableStateOf(false)
    var task_stopping by mutableStateOf(false)
    var output_revision by mutableStateOf(0)
    val output_lines = mutableStateListOf<editor_output_line>()
    val log_lines = mutableStateListOf<editor_output_line>()

    fun append_output(text: String, level: editor_output_line_level = editor_output_line_level.NORMAL) {
        append_lines(output_lines, text, level) { line ->
            update_task_subtitle_from_output(line)
        }
    }

    fun append_log(text: String, level: editor_output_line_level = editor_output_line_level.INFO) {
        val time = LocalTime.now().format(editor_log_time_formatter)
        append_lines(log_lines, "[$time] $text", level)
    }

    fun clear_output() {
        output_lines.clear()
        output_revision++
    }

    fun clear_selected_tab() {
        when (selected_tab) {
            editor_output_tab.Output -> {
                output_lines.clear()
                output_revision++
            }
            editor_output_tab.Log -> {
                log_lines.clear()
                output_revision++
            }
            editor_output_tab.Terminal -> Unit
        }
    }

    fun selected_tab_text(): String {
        val lines = when (selected_tab) {
            editor_output_tab.Output -> output_lines
            editor_output_tab.Log -> log_lines
            editor_output_tab.Terminal -> emptyList()
        }
        return lines.joinToString("\n") { line -> line.text }
    }

    private fun update_task_subtitle_from_output(line: String): Boolean {
        if (line.isBlank()) return false
        task_subtitle = line.trimStart()
            .removePrefix("> TaskOut ")
            .removePrefix("> TaskErr ")
            .removePrefix("-- ")
        return true
    }

    private fun append_lines(
        target: MutableList<editor_output_line>,
        text: String,
        level: editor_output_line_level,
        on_line: ((String) -> Unit)? = null
    ) {
        text.lineSequence()
            .map { line -> line.trimEnd() }
            .filter { line -> line.isNotBlank() }
            .forEach { line ->
                target.add(editor_output_line(line, level))
                on_line?.invoke(line)
            }
        if (target.size > editor_output_max_lines) {
            target.subList(0, target.size - editor_output_max_lines).clear()
        }
        output_revision++
    }
}

@Composable
internal fun remember_editor_output_panel_state(): editor_output_panel_state {
    return remember { editor_output_panel_state() }
}

@Composable
internal fun editor_output_bottom_sheet_scaffold(
    state: editor_output_panel_state,
    terminal_state: editor_terminal_state,
    terminal_cwd: String,
    terminal_extra_environment: Map<String, String>,
    show_symbol_bar: Boolean = false,
    symbol_bar: @Composable () -> Unit = {},
    editor_settings: com.xc.code.editor.model.editor_settings_state? = null,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues, Float) -> Unit
) {
    val colors = app_theme_provider.colors
    BoxWithConstraints(
        modifier = modifier.background(colors.editor_bg)
    ) {
        val density = LocalDensity.current
        val coroutine_scope = rememberCoroutineScope()
        val bottom_safe_padding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
        val handle_gap = 5.dp
        val half_sheet_height = maxHeight * 0.50f
        val full_sheet_height = maxHeight * 0.82f
        val half_sheet_height_px = with(density) { half_sheet_height.toPx() }
        val full_sheet_height_px = with(density) { full_sheet_height.toPx() }
        val bottom_safe_padding_px = with(density) { bottom_safe_padding.toPx() }
        val handle_gap_px = with(density) { handle_gap.toPx() }
        val collapsed_offset_px = (full_sheet_height_px - bottom_safe_padding_px).coerceAtLeast(0f)
        val half_offset_px = full_sheet_height_px - half_sheet_height_px
        val expanded_offset_px = 0f
        val sheet_offset = remember { Animatable(collapsed_offset_px) }

        LaunchedEffect(full_sheet_height_px) {
            if (!sheet_offset.isRunning) {
                sheet_offset.snapTo(sheet_offset.value.coerceIn(expanded_offset_px, collapsed_offset_px))
            }
        }

        LaunchedEffect(show_symbol_bar, collapsed_offset_px) {
            if (show_symbol_bar) {
                sheet_offset.animateTo(collapsed_offset_px, tween(280))
            }
        }

        val sheet_visible_height_px = full_sheet_height_px - sheet_offset.value
        val sheet_visible_height = with(density) { sheet_visible_height_px.toDp() }
        val sheet_progress = if (full_sheet_height_px <= bottom_safe_padding_px) {
            0f
        } else {
            ((sheet_visible_height_px - bottom_safe_padding_px) / (full_sheet_height_px - bottom_safe_padding_px)).coerceIn(0f, 1f)
        }
        val panel_content_fade_height_px = (half_sheet_height_px - bottom_safe_padding_px).coerceAtLeast(1f)
        val panel_content_alpha = ((sheet_visible_height_px - bottom_safe_padding_px) / panel_content_fade_height_px).coerceIn(0f, 1f)
        val panel_button_alpha = panel_content_alpha
        val handle_bottom_px = if (sheet_visible_height_px <= bottom_safe_padding_px + 0.5f) {
            bottom_safe_padding_px + handle_gap_px
        } else {
            sheet_visible_height_px + handle_gap_px
        }

        Box(modifier = Modifier.fillMaxSize()) {
            content(PaddingValues(), sheet_progress)

            if (show_symbol_bar) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    symbol_bar()
                }
            } else {
                editor_output_dock_panel(
                    state = state,
                    terminal_state = terminal_state,
                    terminal_cwd = terminal_cwd,
                    terminal_extra_environment = terminal_extra_environment,
                    content_alpha = panel_content_alpha,
                    button_alpha = panel_button_alpha,
                    bottom_safe_padding = bottom_safe_padding,
                    editor_settings = editor_settings,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(sheet_visible_height)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 18.dp)
                        .offset { IntOffset(0, -handle_bottom_px.roundToInt()) }
                        .clearAndSetSemantics {}
                ) {
                    editor_output_status_card(
                        title = if (state.task_stopping) stringResource(R.string.editor_stopping) else if (state.task_running) stringResource(R.string.output_building) else stringResource(state.selected_tab.title_res),
                        subtitle = state.task_subtitle.ifBlank { stringResource(R.string.output_subtitle) },
                        running = state.task_running,
                        sheet_progress = sheet_progress,
                        on_click = {
                            coroutine_scope.launch {
                                val target = if (sheet_progress >= 0.5f) collapsed_offset_px else half_offset_px
                                sheet_offset.animateTo(target, tween(320))
                            }
                        },
                        on_drag_start = {
                            coroutine_scope.launch { sheet_offset.stop() }
                        },
                        on_drag = { drag_y ->
                            coroutine_scope.launch {
                                sheet_offset.snapTo((sheet_offset.value + drag_y).coerceIn(expanded_offset_px, collapsed_offset_px))
                            }
                        },
                        on_drag_end = {
                            coroutine_scope.launch {
                                val anchors = listOf(collapsed_offset_px, half_offset_px, expanded_offset_px)
                                val target = anchors.minBy { anchor -> abs(anchor - sheet_offset.value) }
                                sheet_offset.animateTo(target, tween(280))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun editor_output_dock_panel(
    state: editor_output_panel_state,
    terminal_state: editor_terminal_state,
    terminal_cwd: String,
    terminal_extra_environment: Map<String, String>,
    content_alpha: Float,
    button_alpha: Float,
    bottom_safe_padding: Dp,
    modifier: Modifier = Modifier,
    editor_settings: com.xc.code.editor.model.editor_settings_state? = null
) {
    val colors = app_theme_provider.colors
    val context = LocalContext.current
    Box(
        modifier = modifier
            .background(colors.editor_output_panel_bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = content_alpha }
        ) {
            editor_output_tabs(
                selected_tab = state.selected_tab,
                on_select = { state.selected_tab = it }
            )
            val content_modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .background(colors.editor_output_panel_bg)
                .then(
                    if (state.selected_tab == editor_output_tab.Terminal) {
                        Modifier
                    } else {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures(onTap = {})
                        }
                    }
                )
            Box(modifier = content_modifier) {
                editor_output_content(
                    state = state,
                    terminal_state = terminal_state,
                    terminal_cwd = terminal_cwd,
                    terminal_extra_environment = terminal_extra_environment,
                    editor_settings = editor_settings,
                    modifier = Modifier.fillMaxSize()
                )

                if (state.selected_tab != editor_output_tab.Terminal) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .graphicsLayer { alpha = button_alpha }
                            .padding(end = 18.dp, bottom = bottom_safe_padding + 18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val selected_tab_title = stringResource(state.selected_tab.title_res)
                        editor_output_floating_button(
                            icon = Icons.Default.Share,
                            content_description = stringResource(R.string.output_share),
                            background = colors.card_icon_bg.copy(alpha = 0.82f),
                            tint = colors.card_text_title,
                            on_click = {
                                share_editor_output_text(
                                    context = context,
                                    title = selected_tab_title,
                                    text = state.selected_tab_text()
                                )
                            }
                        )
                        editor_output_floating_button(
                            icon = Icons.Default.Delete,
                            content_description = stringResource(R.string.output_clear),
                            background = colors.editor_icon.copy(alpha = 0.22f),
                            tint = colors.editor_icon,
                            on_click = { state.clear_selected_tab() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun editor_output_status_card(
    title: String,
    subtitle: String,
    running: Boolean,
    sheet_progress: Float,
    on_click: () -> Unit,
    on_drag_start: () -> Unit,
    on_drag: (Float) -> Unit,
    on_drag_end: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = app_theme_provider.colors
    val shape = RoundedCornerShape(18.dp)
    val card_alpha = 0.95f
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .graphicsLayer {
                alpha = card_alpha
            }
            .clip(shape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { on_drag_start() },
                    onDragEnd = { on_drag_end() },
                    onDragCancel = { on_drag_end() }
                ) { change, drag_amount ->
                    change.consume()
                    on_drag(drag_amount.y)
                }
            }
            .clickable(onClick = on_click),
        shape = shape,
        color = colors.editor_output_capsule_bg
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            editor_output_status_indicator(
                running = running,
                modifier = Modifier.size(30.dp)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(1.dp)
                    .height(20.dp)
                    .background(colors.editor_capsule_divider)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    color = colors.editor_capsule_icon,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = colors.editor_hint,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun editor_output_status_indicator(
    running: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = app_theme_provider.colors
    val transition = rememberInfiniteTransition(label = "editor_output_build_indicator")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "editor_output_build_progress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (running) {
            Canvas(modifier = Modifier.size(24.dp)) {
                val stroke_width = 2.5.dp.toPx()
                val inset = stroke_width / 2f
                val arc_size = Size(
                    width = size.width - stroke_width,
                    height = size.height - stroke_width
                )
                val grow_end = 0.56f
                val release_end = 0.83f
                val min_sweep = 54f
                val max_sweep = 330f
                val sweep: Float
                val tail_offset: Float

                if (progress < grow_end) {
                    val t = progress / grow_end
                    val eased = editor_output_smoothstep(t)
                    sweep = min_sweep + (max_sweep - min_sweep) * eased
                    tail_offset = 0f
                } else if (progress < release_end) {
                    val t = (progress - grow_end) / (release_end - grow_end)
                    val eased = editor_output_smoothstep(t)
                    sweep = max_sweep - (max_sweep - min_sweep) * eased
                    tail_offset = eased * 240f
                } else {
                    val t = (progress - release_end) / (1f - release_end)
                    val eased = editor_output_smoothstep(t)
                    sweep = min_sweep
                    tail_offset = 240f + eased * 120f
                }

                val start_angle = progress * 360f - 90f + tail_offset

                drawArc(
                    color = colors.editor_icon,
                    startAngle = start_angle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arc_size,
                    style = Stroke(width = stroke_width, cap = StrokeCap.Round)
                )
            }
        } else {
            Surface(
                modifier = Modifier.size(26.dp),
                shape = CircleShape,
                color = colors.editor_hint.copy(alpha = 0.16f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_code_tag),
                        contentDescription = null,
                        tint = colors.editor_capsule_icon,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

private fun editor_output_smoothstep(t: Float): Float {
    val x = t.coerceIn(0f, 1f)
    return x * x * (3f - 2f * x)
}

@Composable
private fun editor_output_tabs(
    selected_tab: editor_output_tab,
    on_select: (editor_output_tab) -> Unit
) {
    val colors = app_theme_provider.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.editor_output_panel_bg)
            .padding(horizontal = 18.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(16.dp),
            color = colors.editor_output_capsule_bg,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                editor_output_tab.entries.forEach { tab ->
                    val selected = tab == selected_tab
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selected) colors.editor_sidebar_selected_bg
                                else Color.Transparent
                            )
                            .clickable {
                                if (!selected) on_select(tab)
                            }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(tab.title_res),
                            color = if (selected) colors.editor_icon else colors.editor_hint,
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun editor_output_content(
    state: editor_output_panel_state,
    terminal_state: editor_terminal_state,
    terminal_cwd: String,
    terminal_extra_environment: Map<String, String>,
    modifier: Modifier = Modifier,
    editor_settings: com.xc.code.editor.model.editor_settings_state? = null
) {
    if (state.selected_tab == editor_output_tab.Terminal) {
        editor_terminal_panel(
            state = terminal_state,
            cwd = terminal_cwd,
            extra_environment = terminal_extra_environment,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val lines = when (state.selected_tab) {
        editor_output_tab.Output -> state.output_lines
        editor_output_tab.Log -> state.log_lines
        editor_output_tab.Terminal -> emptyList()
    }
    if (lines.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            editor_output_empty_state(tab = state.selected_tab)
        }
    } else {
        editor_output_line_list(
            lines = lines,
            revision = state.output_revision,
            editor_settings = editor_settings,
            modifier = modifier
        )
    }
}

@Composable
private fun editor_output_line_list(
    lines: List<editor_output_line>,
    revision: Int,
    modifier: Modifier = Modifier,
    editor_settings: com.xc.code.editor.model.editor_settings_state? = null
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val colors = app_theme_provider.colors
    val settings = editor_settings ?: com.xc.code.editor.model.editor_settings_state()
    var rendered_line_count by remember { mutableStateOf(0) }
    var rendered_revision by remember { mutableStateOf(-1) }
    var rendered_source by remember { mutableStateOf<Any?>(null) }
    val output_editor = remember {
        CodeEditor(context).apply {
            isEditable = false
            setUndoEnabled(false)
            setLineNumberEnabled(true)
            setWordwrap(false, false)
            setScrollBarEnabled(false)
            setVerticalScrollBarEnabled(false)
            setHorizontalScrollBarEnabled(false)
            setTextSize(11f)
            setLineInfoTextSize(10f)
            setLineSpacing(2f, 1.08f)
            replaceComponent(
                EditorTextActionWindow::class.java,
                editor_text_action_window(editor = this, show_edit_actions = false)
            )
            setHighlightCurrentLine(false)
            setHighlightCurrentBlock(false)
            setHighlightBracketPair(false)
            setBlockLineEnabled(false)
            props.stickyScroll = false
            setTypefaceText(com.xc.code.editor.settings.load_editor_typeface(context, settings))
        }
    }

    LaunchedEffect(colors) {
        val background = colors.editor_bg.toArgb()
        val line_number = colors.editor_hint.toArgb()
        output_editor.setBackgroundColor(background)
        output_editor.colorScheme.setColor(EditorColorScheme.WHOLE_BACKGROUND, background)
        output_editor.colorScheme.setColor(EditorColorScheme.TEXT_NORMAL, colors.editor_text.toArgb())
        output_editor.colorScheme.setColor(EditorColorScheme.CURRENT_LINE, Color.Transparent.toArgb())
        output_editor.colorScheme.setColor(EditorColorScheme.LINE_DIVIDER, Color.Transparent.toArgb())
        output_editor.colorScheme.setColor(EditorColorScheme.LINE_NUMBER_BACKGROUND, background)
        output_editor.colorScheme.setColor(EditorColorScheme.LINE_NUMBER, line_number)
        output_editor.colorScheme.setColor(EditorColorScheme.LINE_NUMBER_CURRENT, line_number)
        output_editor.invalidate()
    }

    LaunchedEffect(settings) {
        output_editor.setTypefaceText(com.xc.code.editor.settings.load_editor_typeface(context, settings))
    }

    LaunchedEffect(revision, lines.size) {
        if (rendered_source !== lines) {
            output_editor.setText("")
            rendered_line_count = 0
            rendered_revision = -1
            rendered_source = lines
        }
        if (rendered_revision == revision && rendered_line_count == lines.size) return@LaunchedEffect
        val follow_tail_threshold = with(density) { 56.dp.toPx() }.toInt()
        val should_follow_tail = rendered_line_count == 0 ||
            output_editor.getScrollMaxY() - output_editor.offsetY <= follow_tail_threshold
        if (lines.size < rendered_line_count || rendered_revision < 0) {
            output_editor.setText("")
            rendered_line_count = 0
        }
        delay(editor_output_refresh_delay_ms)
        if (lines.size > rendered_line_count) {
            val append_text = lines.drop(rendered_line_count)
                .joinToString("\n", postfix = "\n") { line -> line.text }
            if (rendered_line_count == 0 || output_editor.text.length == 0) {
                output_editor.setText(append_text)
            } else {
                val last_line = output_editor.text.lineCount.coerceAtLeast(1) - 1
                val last_column = output_editor.text.getColumnCount(last_line)
                runCatching {
                    output_editor.text.insert(last_line, last_column, append_text)
                }.onFailure {
                    val full_text = lines.joinToString("\n", postfix = "\n") { line -> line.text }
                    output_editor.setText(full_text)
                }
            }
            rendered_line_count = lines.size
        }
        rendered_revision = revision
        if (should_follow_tail) {
            val last_line = output_editor.text.lineCount.coerceAtLeast(1) - 1
            val last_column = output_editor.text.getColumnCount(last_line)
            delay(16)
            output_editor.post {
                output_editor.setSelection(last_line, last_column, true)
            }
        }
    }

    DisposableEffect(output_editor) {
        onDispose { output_editor.release() }
    }

    AndroidView(
        factory = {
            output_editor.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun editor_output_empty_state(
    tab: editor_output_tab,
    modifier: Modifier = Modifier
) {
    val colors = app_theme_provider.colors
    val title = when (tab) {
        editor_output_tab.Output -> stringResource(R.string.output_empty_build)
        editor_output_tab.Log -> stringResource(R.string.output_empty_log)
        editor_output_tab.Terminal -> stringResource(R.string.output_empty_terminal)
    }
    val subtitle = when (tab) {
        editor_output_tab.Output -> stringResource(R.string.output_empty_build_desc)
        editor_output_tab.Log -> stringResource(R.string.output_empty_log_desc)
        editor_output_tab.Terminal -> stringResource(R.string.output_empty_terminal_desc)
    }
    val icon = when (tab) {
        editor_output_tab.Output -> Icons.Default.Terminal
        editor_output_tab.Log -> Icons.AutoMirrored.Filled.ListAlt
        editor_output_tab.Terminal -> Icons.Default.Terminal
    }
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.editor_icon,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            color = colors.editor_text,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = subtitle,
            color = colors.editor_hint,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun share_editor_output_text(
    context: Context,
    title: String,
    text: String
) {
    if (text.isBlank()) return
    val send_intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    runCatching {
        context.startActivity(Intent.createChooser(send_intent, context.getString(R.string.output_share_title, title)))
    }
}

@Composable
private fun editor_output_floating_button(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content_description: String,
    background: Color,
    tint: Color,
    on_click: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .clickable(onClick = on_click),
        shape = CircleShape,
        color = background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = content_description,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun Modifier.editor_running_border(
    running: Boolean,
    color: Color,
    rotation: Float
): Modifier {
    if (!running) return this
    return drawWithContent {
        drawContent()
        val stroke_width = 1.5.dp.toPx()
        val inset = stroke_width / 2f
        val radius = (size.height - stroke_width) / 2f
        val border_size = Size(size.width - stroke_width, size.height - stroke_width)
        val straight_width = (border_size.width - border_size.height).coerceAtLeast(0f)
        val perimeter = straight_width * 2f + Math.PI.toFloat() * radius * 2f
        val runner_length = (perimeter * 0.45f).coerceIn(140.dp.toPx(), 220.dp.toPx())
        val gap_length = perimeter - runner_length
        val phase = -perimeter * ((rotation % 360f) / 360f)

        drawRoundRect(
            color = color.copy(alpha = 0.16f),
            topLeft = Offset(inset, inset),
            size = border_size,
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(width = stroke_width, cap = StrokeCap.Round)
        )

        drawRoundRect(
            color = color.copy(alpha = 0.95f),
            topLeft = Offset(inset, inset),
            size = border_size,
            cornerRadius = CornerRadius(radius, radius),
            style = Stroke(
                width = stroke_width,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(runner_length, gap_length), phase)
            )
        )
    }
}
