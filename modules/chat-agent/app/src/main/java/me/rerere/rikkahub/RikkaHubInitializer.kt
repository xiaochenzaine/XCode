package me.rerere.rikkahub

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.whl.quickjs.android.QuickJSLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.rerere.common.android.appTempFolder
import me.rerere.rikkahub.data.datastore.SettingsStore
import me.rerere.rikkahub.data.files.FileFolders
import me.rerere.rikkahub.data.files.FilesManager
import me.rerere.rikkahub.data.repository.WorkspaceRepository
import me.rerere.rikkahub.di.appModule
import me.rerere.rikkahub.di.dataSourceModule
import me.rerere.rikkahub.di.repositoryModule
import me.rerere.rikkahub.di.viewModelModule
import me.rerere.rikkahub.service.WebServerService
import me.rerere.rikkahub.utils.CrashHandler
import me.rerere.rikkahub.utils.DatabaseUtil
import me.rerere.workspace.WorkspaceManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import java.io.File

private const val RIKKAHUB_INITIALIZER_TAG = "RikkaHubInitializer"

object RikkaHubInitializer {
    private var initialized = false

    fun init(application: Application) {
        if (initialized) return
        initialized = true

        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidLogger()
                androidContext(application)
                workManagerFactory()
                modules(appModule, viewModelModule, dataSourceModule, repositoryModule)
            }
        }

        createNotificationChannel(application)
        DatabaseUtil.setCursorWindowSize(32 * 1024 * 1024)
        CrashHandler.install(application)
        QuickJSLoader.init()

        deleteTempFiles(application)
        cleanupToolOutputs(application)
        cleanupWorkspaceTempDirs()
        checkWorkspaceIntegrity()
        syncManagedFiles()
        startWebServerIfEnabled(application)
        incrementLaunchCount()
    }

    fun shutdown(application: Application) {
        runCatching { koinGet<AppScope>().cancel() }
        runCatching { application.stopService(Intent(application, WebServerService::class.java)) }
    }

    private fun incrementLaunchCount() {
        koinGet<AppScope>().launch {
            runCatching {
                val store = koinGet<SettingsStore>()
                val current = store.settingsFlowRaw.first()
                store.update(current.copy(launchCount = current.launchCount + 1))
            }.onFailure {
                Log.e(RIKKAHUB_INITIALIZER_TAG, "incrementLaunchCount failed", it)
            }
        }
    }

    private fun cleanupWorkspaceTempDirs() {
        koinGet<AppScope>().launch(Dispatchers.IO) {
            runCatching {
                koinGet<WorkspaceManager>().cleanupAllTempDirs()
            }.onFailure {
                Log.e(RIKKAHUB_INITIALIZER_TAG, "cleanupWorkspaceTempDirs failed", it)
            }
        }
    }

    private fun checkWorkspaceIntegrity() {
        koinGet<AppScope>().launch(Dispatchers.IO) {
            runCatching {
                koinGet<WorkspaceRepository>().checkIntegrity()
            }.onFailure {
                Log.e(RIKKAHUB_INITIALIZER_TAG, "checkWorkspaceIntegrity failed", it)
            }
        }
    }

    private fun deleteTempFiles(application: Application) {
        koinGet<AppScope>().launch(Dispatchers.IO) {
            val dir = application.appTempFolder
            if (dir.exists()) dir.deleteRecursively()
        }
    }

    private fun cleanupToolOutputs(application: Application) {
        koinGet<AppScope>().launch(Dispatchers.IO) {
            runCatching {
                val dir = File(application.filesDir, FileFolders.TOOL_OUTPUTS)
                if (dir.exists()) dir.deleteRecursively()
            }
        }
    }

    private fun syncManagedFiles() {
        koinGet<AppScope>().launch(Dispatchers.IO) {
            runCatching {
                koinGet<FilesManager>().syncFolder()
            }.onFailure {
                Log.e(RIKKAHUB_INITIALIZER_TAG, "syncManagedFiles failed", it)
            }
        }
    }

    private fun startWebServerIfEnabled(application: Application) {
        koinGet<AppScope>().launch {
            runCatching {
                delay(500)
                val settings = koinGet<SettingsStore>().settingsFlowRaw.first()
                if (settings.webServerEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            application,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.w(RIKKAHUB_INITIALIZER_TAG, "notification permission not granted, skipping web server")
                        return@launch
                    }
                    if (Build.VERSION.SDK_INT >= 37 &&
                        !settings.webServerLocalhostOnly &&
                        ContextCompat.checkSelfPermission(
                            application,
                            Manifest.permission.ACCESS_LOCAL_NETWORK
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.w(RIKKAHUB_INITIALIZER_TAG, "local network permission not granted, skipping web server")
                        return@launch
                    }
                    val intent = Intent(application, WebServerService::class.java).apply {
                        action = WebServerService.ACTION_START
                        putExtra(WebServerService.EXTRA_PORT, settings.webServerPort)
                        putExtra(WebServerService.EXTRA_LOCALHOST_ONLY, settings.webServerLocalhostOnly)
                    }
                    application.startForegroundService(intent)
                }
            }.onFailure {
                Log.e(RIKKAHUB_INITIALIZER_TAG, "startWebServerIfEnabled failed", it)
            }
        }
    }

    private fun createNotificationChannel(application: Application) {
        val notificationManager = NotificationManagerCompat.from(application)
        val chatCompletedChannel = NotificationChannelCompat
            .Builder(CHAT_COMPLETED_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName(application.getString(R.string.notification_channel_chat_completed))
            .setVibrationEnabled(true)
            .build()
        notificationManager.createNotificationChannel(chatCompletedChannel)

        val chatLiveUpdateChannel = NotificationChannelCompat
            .Builder(CHAT_LIVE_UPDATE_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(application.getString(R.string.notification_channel_chat_live_update))
            .setVibrationEnabled(false)
            .build()
        notificationManager.createNotificationChannel(chatLiveUpdateChannel)

        val webServerChannel = NotificationChannelCompat
            .Builder(WEB_SERVER_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(application.getString(R.string.notification_channel_web_server))
            .setVibrationEnabled(false)
            .setShowBadge(false)
            .build()
        notificationManager.createNotificationChannel(webServerChannel)
    }

    private inline fun <reified T> koinGet(): T = GlobalContext.get().get()
}
