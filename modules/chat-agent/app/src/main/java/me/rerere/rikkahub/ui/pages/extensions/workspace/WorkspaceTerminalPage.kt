package me.rerere.rikkahub.ui.pages.extensions.workspace

import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.termux.terminal.TerminalSession
import com.termux.view.TerminalView
import androidx.compose.ui.res.stringResource
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.components.nav.BackButton
import me.rerere.rikkahub.ui.theme.ColorMode
import me.rerere.rikkahub.ui.theme.RikkahubTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorkspaceTerminalPage(id: String) {
    val vm: WorkspaceDetailVM = koinViewModel(parameters = { parametersOf(id) })
    val state by vm.state.collectAsStateWithLifecycle()

    RikkahubTheme(colorMode = ColorMode.DARK, useHostColorMode = false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = state.workspace?.name?.let { stringResource(R.string.workspace_terminal_title_with_name, it) } ?: stringResource(R.string.workspace_terminal_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = { BackButton() },
                )
            },
        ) { innerPadding ->
            WorkspaceTerminalContent(
                root = state.workspace?.root,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun WorkspaceTerminalContent(
    root: String?,
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current
    val terminalTextSizePx = with(LocalDensity.current) { 12.sp.roundToPx() }
    val terminalTypeface = remember(context) {
        ResourcesCompat.getFont(context, R.font.jetbrains_mono) ?: Typeface.MONOSPACE
    }
    var finished by remember(root) { mutableStateOf(false) }
    var controlDown by remember(root) { mutableStateOf(false) }
    var altDown by remember(root) { mutableStateOf(false) }
    val sessionClient = remember(root) {
        WorkspaceTerminalSessionClient(context.applicationContext) {
            finished = true
        }
    }
    val viewClient = remember(root) {
        WorkspaceTerminalViewClient(context)
    }
    viewClient.controlDown = controlDown
    viewClient.altDown = altDown

    val sessionState by produceState<TerminalSessionUiState>(
        initialValue = TerminalSessionUiState.Loading,
        root,
        sessionClient,
    ) {
        val current = root
        value = if (current == null) {
            TerminalSessionUiState.Loading
        } else {
            // rootfs stat 与 RootfsPatcher().patch()/DNS 查询都是阻塞 I/O, 放到 IO 线程执行;
            // TerminalSession 构造内部会创建 Handler, 必须回到主线程执行
            val prepared = withContext(Dispatchers.IO) {
                if (!workspaceRootfsReady(context, current)) {
                    false
                } else {
                    prepareWorkspaceTerminalSession(context, current)
                    true
                }
            }
            if (!prepared) {
                TerminalSessionUiState.NotInstalled
            } else {
                if (!isActive) return@produceState
                val created = createWorkspaceTerminalSession(context, current, sessionClient)
                // 创建后若组合已离开, 主动回收以免泄漏 proot 进程, 且不再把已 finish 的 session 暴露为 Ready
                if (!isActive) {
                    created.finishIfRunning()
                    return@produceState
                }
                TerminalSessionUiState.Ready(created)
            }
        }
    }

    val currentState = sessionState
    if (currentState !is TerminalSessionUiState.Ready) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (currentState is TerminalSessionUiState.NotInstalled) {
                    stringResource(R.string.workspace_terminal_not_installed)
                } else {
                    stringResource(R.string.workspace_terminal_loading)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        }
        return
    }
    val session = currentState.session

    DisposableEffect(session) {
        onDispose {
            sessionClient.terminalView = null
            viewClient.terminalView = null
            session.finishIfRunning()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        color = Color.Black,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { viewContext ->
                        TerminalView(viewContext, null).apply {
                            isFocusable = true
                            isFocusableInTouchMode = true
                            setTextSize(terminalTextSizePx)
                            setTypeface(terminalTypeface)
                            setTerminalViewClient(viewClient)
                            attachSession(session)
                            applyWorkspaceTerminalColors()
                            sessionClient.terminalView = this
                            viewClient.terminalView = this
                            setOnTouchListener { _, event ->
                                if (event.action == MotionEvent.ACTION_UP) {
                                    viewClient.focusAndShowKeyboard()
                                }
                                false
                            }
                            post {
                                viewClient.focusAndShowKeyboard()
                            }
                        }
                    },
                    update = { terminalView ->
                        terminalView.isFocusable = true
                        terminalView.isFocusableInTouchMode = true
                        terminalView.setTextSize(terminalTextSizePx)
                        terminalView.setTypeface(terminalTypeface)
                        terminalView.setTerminalViewClient(viewClient)
                        sessionClient.terminalView = terminalView
                        viewClient.terminalView = terminalView
                        terminalView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_UP) {
                                viewClient.focusAndShowKeyboard()
                            }
                            false
                        }
                        terminalView.attachSession(session)
                        terminalView.applyWorkspaceTerminalColors()
                        terminalView.onScreenUpdated()
                    },
                )
                if (finished) {
                    Text(
                        text = stringResource(R.string.workspace_terminal_exited),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                        .padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )
                }
            }
            TerminalExtraKeysBar(
                controlDown = controlDown,
                altDown = altDown,
                onControlToggle = { controlDown = !controlDown },
                onAltToggle = { altDown = !altDown },
                onSendText = { session.writeText(it) },
            )
        }
    }
}

@Composable
private fun TerminalExtraKeysBar(
    controlDown: Boolean,
    altDown: Boolean,
    onControlToggle: () -> Unit,
    onAltToggle: () -> Unit,
    onSendText: (String) -> Unit,
) {
    val rowPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    val itemPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)

    Column(modifier = Modifier.fillMaxWidth().imePadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(rowPadding)
                .imePadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TerminalExtraKey("ESC", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText("\u001B")
            }
            TerminalExtraKey("/", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText("/") }) {
                onSendText("/")
            }
            TerminalExtraKey("-", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText("-") }) {
                onSendText("-")
            }
            TerminalExtraKey("HOME", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText(terminalKeySequence(controlDown, altDown, "H"))
            }
            TerminalExtraKey("↑", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText(terminalKeySequence(controlDown, altDown, "A")) }) {
                onSendText(terminalKeySequence(controlDown, altDown, "A"))
            }
            TerminalExtraKey("END", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText(terminalKeySequence(controlDown, altDown, "F"))
            }
            TerminalExtraKey("PGUP", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText(terminalPageKeySequence(controlDown, altDown, pageUp = true))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(rowPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TerminalExtraKey("TAB", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText("\t")
            }
            TerminalExtraKey("CTRL", selected = controlDown, modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onControlToggle()
            }
            TerminalExtraKey("ALT", selected = altDown, modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onAltToggle()
            }
            TerminalExtraKey("←", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText(terminalKeySequence(controlDown, altDown, "D")) }) {
                onSendText(terminalKeySequence(controlDown, altDown, "D"))
            }
            TerminalExtraKey("↓", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText(terminalKeySequence(controlDown, altDown, "B")) }) {
                onSendText(terminalKeySequence(controlDown, altDown, "B"))
            }
            TerminalExtraKey("→", modifier = Modifier.weight(1f), contentPadding = itemPadding, onLongClick = { onSendText(terminalKeySequence(controlDown, altDown, "C")) }) {
                onSendText(terminalKeySequence(controlDown, altDown, "C"))
            }
            TerminalExtraKey("PGDN", modifier = Modifier.weight(1f), contentPadding = itemPadding) {
                onSendText(terminalPageKeySequence(controlDown, altDown, pageUp = false))
            }
        }
    }
}

@Composable
private fun TerminalExtraKey(
    label: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 14.sp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var longPressJob by remember { mutableStateOf<Job?>(null) }
    val backgroundColor = if (pressed) TerminalExtraKeyPressedBackground else Color.Transparent
    val textColor = when {
        pressed -> TerminalExtraKeyPressedContent
        selected -> TerminalExtraKeySelectedContent
        else -> TerminalExtraKeyContent
    }

    Box(
        modifier = modifier
            .padding(1.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(contentPadding)
            .pointerInput(onLongClick, onClick) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        if (onLongClick != null) {
                            longPressJob = scope.launch {
                                delay(500)
                                while (true) {
                                    onLongClick()
                                    delay(100)
                                }
                            }
                        }
                        tryAwaitRelease()
                        longPressJob?.cancel()
                        longPressJob = null
                        pressed = false
                    },
                    onTap = { onClick() },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = textSize,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private fun terminalKeySequence(controlDown: Boolean, altDown: Boolean, code: String): String =
    when {
        controlDown && altDown -> "\u001bO8$code"
        controlDown -> "\u001bO5$code"
        altDown -> "\u001bO3$code"
        else -> "\u001bO$code"
    }

private fun terminalPageKeySequence(controlDown: Boolean, altDown: Boolean, pageUp: Boolean): String {
    val base = if (pageUp) "5" else "6"
    return when {
        controlDown && altDown -> "\u001b[$base;8~"
        controlDown -> "\u001b[$base;5~"
        altDown -> "\u001b[$base;3~"
        else -> "\u001b[$base~"
    }
}

private val TerminalExtraKeysBarBackground = Color(0xFF0B0D10)
private val TerminalExtraKeyContent = Color(0xFFE5E7EB)
private val TerminalExtraKeySelectedContent = Color(0xFF93C5FD)
private val TerminalExtraKeyPressedBackground = Color(0xFF1F2937)
private val TerminalExtraKeyPressedContent = Color.White

private fun TerminalSession.writeText(text: String) {
    val bytes = text.toByteArray()
    write(bytes, 0, bytes.size)
}

private sealed interface TerminalSessionUiState {
    data object Loading : TerminalSessionUiState
    data object NotInstalled : TerminalSessionUiState
    data class Ready(val session: TerminalSession) : TerminalSessionUiState
}
