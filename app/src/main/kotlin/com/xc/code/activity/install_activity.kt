package com.xc.code.activity

import com.xc.code.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xc.code.ui.locale.app_locale_manager
import com.xc.code.ui.toast.app_toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.xc.code.toolchain.proot_manager
import com.xc.code.toolchain.runtime.format_rootfs_size
import com.xc.code.toolchain.runtime.format_rootfs_speed
import com.xc.code.toolchain.runtime.rootfs_install_event
import com.xc.code.toolchain.runtime.rootfs_installer
import com.xc.code.toolchain.runtime.toolchain_runtime_paths
import com.xc.code.ui.screens.install.install_screen
import com.xc.code.ui.theme.app_theme_provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class install_activity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(app_locale_manager.wrap_context(newBase))
    }
    private var logs by mutableStateOf<List<String>>(emptyList())
    private var is_downloading by mutableStateOf(false)
    private var is_extracting by mutableStateOf(false)
    private var is_configuring by mutableStateOf(false)
    private var current_progress by mutableFloatStateOf(0f)

    private val home_dir_path: File get() = File(filesDir, "home")
    private val xcode_dir_path: File get() = File(home_dir_path, "xcode")
    private val ubuntu_base_dir_path: File get() = File(xcode_dir_path, "ubuntu-base")

    private val ubuntu_version = "24.04.4"
    private val expected_md5 = "5acb2eb6fe98908f41bc4e9ac0014c91"
    private val ubuntu_filename = "ubuntu-base-${ubuntu_version}-base-arm64.tar.gz"
    private val mirrors = listOf(
        "https://mirrors.aliyun.com/ubuntu-cdimage/ubuntu-base/releases/${ubuntu_version}/release/$ubuntu_filename",
        "https://mirrors.ustc.edu.cn/ubuntu-cdimage/ubuntu-base/releases/${ubuntu_version}/release/$ubuntu_filename",
        "https://mirrors.tuna.tsinghua.edu.cn/ubuntu-cdimage/ubuntu-base/releases/${ubuntu_version}/release/$ubuntu_filename"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            app_theme_provider {
                install_screen(
                    logs = logs,
                    is_downloading = is_downloading,
                    is_extracting = is_extracting,
                    is_configuring = is_configuring,
                    current_progress = current_progress,
                    on_export_logs = ::export_logs
                )
            }
        }
        start_download()
    }

    private fun add_log(text: String) {
        logs = if (text.startsWith("\r") && logs.isNotEmpty()) {
            logs.dropLast(1) + text.removePrefix("\r")
        } else {
            logs + text
        }
    }

    private fun add_proot_log(line: String) {
        if (line.contains("[OUT]") || line.contains("[ERR]")) {
            val clean = line.replace("[OUT] ", "").replace("[ERR] ", "")
            if (clean.isNotBlank()) add_log(clean)
        } else {
            add_log(line)
        }
    }

    private fun start_download() {
        lifecycleScope.launch {
            is_downloading = true
            is_extracting = false
            val installer = rootfs_installer(
                toolchain_runtime_paths(
                    xcode_dir = xcode_dir_path,
                    home_dir = home_dir_path,
                    ubuntu_base_dir = ubuntu_base_dir_path,
                    proot_tmp_dir = File(xcode_dir_path, "proot-tmps"),
                    external_storage_dir = null,
                    native_library_dir = File(applicationInfo.nativeLibraryDir)
                )
            )
            val success = installer.install_ubuntu_base(
                mirrors = mirrors,
                file_name = ubuntu_filename,
                expected_md5 = expected_md5
            ) { event ->
                withContext(Dispatchers.Main) {
                    when (event) {
                        is rootfs_install_event.log -> add_log(event.message)
                        is rootfs_install_event.download -> {
                            is_extracting = false
                            current_progress = event.percent / 100f
                            if (event.percent > 0) {
                                add_log("\r" + getString(R.string.install_download_progress, "${event.percent}%", format_rootfs_size(event.downloaded_size), format_rootfs_size(event.total_size), format_rootfs_speed(event.speed)))
                            }
                        }
                        is rootfs_install_event.extract -> {
                            is_extracting = true
                            current_progress = event.percent / 100f
                            if (event.current_file.isNotBlank()) {
                                add_log("\r" + getString(R.string.install_extract_progress, event.current_file))
                            }
                        }
                    }
                }
            }
            is_downloading = false
            is_extracting = false
            if (success) configure_environment()
        }
    }

    private fun configure_environment() {
        lifecycleScope.launch {
            is_configuring = true
            add_log(getString(R.string.install_configure_ubuntu))

            try {
                val ubuntu_fs = ubuntu_base_dir_path
                val installed_flag = File(ubuntu_base_dir_path, ".xcode_installed")

                suspend fun run_required_command(status: String, command: String): Boolean {
                    add_log(status)
                    val success = proot_manager.execute_command(command, ::add_proot_log)
                    if (!success) add_log(getString(R.string.install_status_failed, status.removeSuffix("...")))
                    return success
                }

                val needs_initialization = withContext(Dispatchers.IO) {
                    if (installed_flag.exists()) return@withContext false

                    true
                }

                if (needs_initialization) {
                    if (!run_required_command(getString(R.string.install_apt_update), "apt-get update -y")) return@launch
                    
                    val required_packages = listOf("wget", "tar", "unzip", "xz-utils", "ca-certificates", "git")
                    for (package_name in required_packages) {
                        if (!run_required_command(
                                getString(R.string.install_package, package_name),
                                "DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends $package_name"
                            )
                        ) return@launch
                    }

                    run_required_command(
                        getString(R.string.install_command_not_found),
                        "DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends command-not-found"
                    )
                    
                    if (!run_required_command(getString(R.string.install_refresh_command_index), "apt-get update -qq -y")) return@launch

                    withContext(Dispatchers.IO) {
                        installed_flag.createNewFile()
                    }
                    add_log(getString(R.string.install_environment_done))
                } else {
                    add_log(getString(R.string.install_environment_already_configured))
                }

                add_log(getString(R.string.install_all_steps_done))
                delay(2000)
                navigate_to_main()
            } catch (e: Exception) {
                add_log(getString(R.string.install_environment_failed, e.message ?: e.javaClass.simpleName))
                add_log("请重试")
            } finally {
                is_configuring = false
            }
        }
    }

    private fun navigate_to_main() {
        val intent = Intent(this, main_activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun export_logs() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(xcode_dir_path, "cache/install_log_$timestamp.txt")
            file.parentFile?.mkdirs()
            file.writeText(logs.joinToString("\n"))
            app_toast.show(this, "已导出到: ${file.absolutePath}", app_toast.LENGTH_LONG)
        } catch (e: Exception) {
            app_toast.show(this, getString(R.string.install_export_failed, e.message), app_toast.LENGTH_SHORT)
        }
    }
}