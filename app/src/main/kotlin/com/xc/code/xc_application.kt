package com.xc.code

import android.app.Application
import android.content.Intent
import android.os.Build
import com.xc.code.activity.crash.crash_activity
import com.xc.code.core.logging.logger_manager
import com.xc.code.editor.theme.editor_theme_manager
import com.xc.code.service.keep_alive_service
import com.xc.code.runtime.app_runtime_provider
import com.xc.code.ui.locale.app_locale_manager
import com.xc.code.ui.theme.app_theme_type
import com.xc.code.ui.theme.theme_manager
import com.xc.code.utils.app_lifecycle_observer
import me.rerere.rikkahub.RikkaHubInitializer
import me.rerere.rikkahub.ui.theme.ColorMode
import me.rerere.rikkahub.ui.theme.ThemeStateBridge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
class xc_application : Application() {

    companion object {
        lateinit var instance: xc_application
    }

    private val application_scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var keep_alive_service_: keep_alive_service? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        app_locale_manager.init(this)
        theme_manager.init(this)
        bind_rikkahub_theme_state()
        logger_manager.init(this)
        logger_manager.is_debug = false
        logger_manager.enable_file_log = true
        logger_manager.errors_only = true

        val create_dirs = listOf(
            File(android.os.Environment.getExternalStorageDirectory(), "XCodeProjects"),
            File(filesDir, "home"),
            File(filesDir, "home/xcode/proot-tmps"),
            File(filesDir, "home/xcode/ubuntu-base")
        )
        for (dir in create_dirs) {
            if (!dir.exists()) dir.mkdirs()
        }

        app_runtime_provider.init(
            context = this,
            xcode_dir = File(filesDir, "home/xcode"),
            home_dir = File(filesDir, "home"),
            ubuntu_base_dir = File(filesDir, "home/xcode/ubuntu-base"),
            proot_tmp_dir = File(filesDir, "home/xcode/proot-tmps")
        )

        editor_theme_manager.init(this)
        start_keep_alive_service()
        app_lifecycle_observer.init(this)
        RikkaHubInitializer.init(this)
        setup_uncaught_exception_handler()
    }

    override fun onTerminate() {
        RikkaHubInitializer.shutdown(this)
        super.onTerminate()
    }

    private fun bind_rikkahub_theme_state() {
        // XCode 负责接管 Agent 的明暗模式与应用缩放。
        // 但编辑器侧边栏里的 Agent 背景色只通过 RouteFragment 参数做局部覆盖，避免污染独立 Agent 页面。
        ThemeStateBridge.setColorMode(theme_manager.theme.value.to_rikkahub_color_mode())
        ThemeStateBridge.setScale(theme_manager.scale.value)
        application_scope.launch {
            theme_manager.theme.collect { theme ->
                ThemeStateBridge.setColorMode(theme.to_rikkahub_color_mode())
            }
        }
        application_scope.launch {
            theme_manager.scale.collect { scale ->
                ThemeStateBridge.setScale(scale)
            }
        }
    }

    private fun app_theme_type.to_rikkahub_color_mode(): ColorMode = when (this) {
        app_theme_type.DARK -> ColorMode.DARK
        app_theme_type.LIGHT -> ColorMode.LIGHT
        app_theme_type.SYSTEM -> ColorMode.SYSTEM
    }

    private fun setup_uncaught_exception_handler() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            handle_crash(throwable)
        }
    }

    private fun handle_crash(throwable: Throwable) {
        try {
            val log = throwable.javaClass.simpleName + ": " + (throwable.message ?: "Unknown error")
            val stack = get_stack_trace_string(throwable)
            val intent = Intent(this, crash_activity::class.java)
            intent.putExtra("crash_log", log)
            intent.putExtra("crash_stack", stack)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            logger_manager.e("xc_application", "Failed to show crash activity: ${e.message}")
        }
    }

    private fun get_stack_trace_string(throwable: Throwable): String {
        val sw = java.io.StringWriter()
        val pw = java.io.PrintWriter(sw)
        throwable.printStackTrace(pw)
        return sw.toString()
    }

    private fun start_keep_alive_service() {
        try {
            val intent = Intent(this, keep_alive_service::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            logger_manager.e("xc_application", "Failed to start service: ${e.message}")
        }
    }
}
