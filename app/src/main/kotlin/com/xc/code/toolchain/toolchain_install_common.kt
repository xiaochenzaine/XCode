package com.xc.code.toolchain

import android.os.Environment
import com.xc.code.runtime.app_runtime_provider
import com.xc.code.toolchain.runtime.toolchain_guest_paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal val guest_home_dir = toolchain_guest_paths.home
internal val guest_tool_dir = toolchain_guest_paths.tool_home
internal val guest_cache_dir = "${toolchain_guest_paths.tool_home}/cache"
internal val guest_cmake_root = "${toolchain_guest_paths.tool_home}/cmake"
internal val guest_ndk_root = "${toolchain_guest_paths.tool_home}/ndk"

internal suspend fun run_proot_toolchain_task(
    start_log: String,
    command: String,
    success_log: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    emit_progress(on_progress, 0)
    emit_log(on_log, start_log)
    emit_progress(on_progress, 10)
    val success = toolchain_command_runner.execute_command(command) { line -> on_log(line) }
    emit_progress(on_progress, if (success) 100 else 90)
    emit_log(on_log, if (success) success_log else "命令执行失败")
    return success
}


internal fun create_download_block(
    url: String,
    sha256: String
): String {
    if (url.isBlank()) {
        return """
            [ -f "@D@ARCHIVE" ] || { echo "错误: 压缩包不存在: @D@ARCHIVE"; exit 1; }
        """.trimIndent()
    }
    val checksum = sha256.takeIf { it.isNotBlank() }
    val sha256_block = if (checksum != null) {
        """
            echo "校验 SHA256..."
            ACTUAL_SHA256="@D@(sha256sum "@D@ARCHIVE" | awk '{print @D@1}')"
            if [ "@D@ACTUAL_SHA256" != "@D@EXPECTED_SHA256" ]; then
              echo "错误: SHA256 不匹配！"
              echo "  期望: @D@EXPECTED_SHA256"
              echo "  实际: @D@ACTUAL_SHA256"
              rm -f "@D@ARCHIVE"
              exit 1
            fi
            echo "SHA256 校验通过"
        """.trimIndent()
    } else ""
    return """
        URL=${shell_quote(url)}
        EXPECTED_SHA256=${shell_quote(sha256)}
        if [ -s "@D@ARCHIVE" ]; then
          echo "使用缓存: @D@ARCHIVE"
        else
          command -v wget >/dev/null 2>&1 || { echo "错误: 未安装 wget"; exit 1; }
          echo "开始下载: @D@URL"
          wget -c --show-progress --progress=bar:force:noscroll -O "@D@ARCHIVE" "@D@URL"
        fi
        $sha256_block
    """.trimIndent()
}


internal fun host_path_to_proot_path(path: String): String {
    if (path.startsWith("$guest_home_dir/")) return path

    val absolute_path = File(path).absolutePath
    val xcode_path = app_runtime_provider.paths().xcode_dir.absolutePath.trimEnd('/')
    val home_path = app_runtime_provider.paths().home_dir.absolutePath.trimEnd('/')
    val external_path = Environment.getExternalStorageDirectory().absolutePath.trimEnd('/')
    val sdcard_path = File("/sdcard").absolutePath.trimEnd('/')
    return when {
        absolute_path == xcode_path -> guest_tool_dir
        absolute_path.startsWith("$xcode_path/") -> "$guest_tool_dir/" + absolute_path.removePrefix("$xcode_path/")
        absolute_path == home_path -> guest_home_dir
        absolute_path.startsWith("$home_path/") -> "$guest_home_dir/" + absolute_path.removePrefix("$home_path/")
        absolute_path == sdcard_path -> external_path
        absolute_path.startsWith("$sdcard_path/") -> external_path + "/" + absolute_path.removePrefix("$sdcard_path/")
        else -> absolute_path
    }
}

internal fun archive_name_from_url(url: String): String {
    return url.substringBefore('?').substringAfterLast('/').trim()
}

internal fun strip_archive_extension(name: String): String {
    val lower_name = name.lowercase()
    val extensions = listOf(".tar.xz", ".tar.gz", ".tgz", ".zip", ".tar", ".xz", ".gz")
    val extension = extensions.firstOrNull { lower_name.endsWith(it) } ?: return name
    return name.dropLast(extension.length)
}

internal fun normalize_cmake_install_name(value: String): String {
    return value.trim()
        .replace(Regex("^cmake[-_ ]*", RegexOption.IGNORE_CASE), "")
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .trim('_', '.', '-')
}

internal fun normalize_ndk_install_name(value: String): String {
    val stripped = strip_archive_extension(value.trim())
    return stripped
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .trim('_', '.', '-')
        .ifBlank { "custom-ndk" }
}

internal fun normalize_version_key(value: String): String {
    return value.trim().lowercase().removePrefix("android-ndk-")
}

internal fun shell_quote(value: String): String {
    if (value.isEmpty()) return "''"
    return "'" + value.replace("'", "'\"'\"'") + "'"
}

internal fun String.with_shell_dollar(): String {
    return replace("@D@", "\$").replace("@A@", "@")
}

internal suspend fun emit_log(on_log: (String) -> Unit, message: String) {
    withContext(Dispatchers.Main) {
        on_log(message)
    }
}

internal suspend fun emit_progress(on_progress: (Int) -> Unit, progress: Int) {
    withContext(Dispatchers.Main) {
        on_progress(progress.coerceIn(0, 100))
    }
}
