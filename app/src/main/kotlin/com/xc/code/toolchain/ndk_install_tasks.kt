package com.xc.code.toolchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun install_ndk_from_url(
    version: String,
    url: String,
    sha256: String = "",
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    val target_name = normalize_ndk_install_name(version)
    val archive_name = archive_name_from_url(url).ifBlank { "$target_name.tar.xz" }
    val archive_path = "$guest_cache_dir/$archive_name"
    return run_ndk_install_task(
        command = create_ndk_install_command(
            archive_path = archive_path,
            target_name = target_name,
            download_url = url,
            sha256 = sha256
        ),
        on_log = on_log,
        on_progress = on_progress
    )
}

suspend fun install_ndk_from_archive(
    archive_path: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    val trimmed_path = archive_path.trim()
    if (trimmed_path.isBlank()) {
        emit_log(on_log, "错误: 压缩包路径为空")
        return false
    }

    val host_file = File(trimmed_path)
    if (!trimmed_path.startsWith("$guest_home_dir/") && !host_file.isFile) {
        emit_log(on_log, "错误: 压缩包不存在: $trimmed_path")
        return false
    }

    val archive_name = if (trimmed_path.startsWith("$guest_home_dir/")) {
        trimmed_path.substringAfterLast('/')
    } else {
        host_file.name
    }
    val target_name = normalize_ndk_install_name(strip_archive_extension(archive_name.ifBlank { "custom-ndk" }))
    return run_ndk_install_task(
        command = create_ndk_install_command(
            archive_path = host_path_to_proot_path(trimmed_path),
            target_name = target_name,
            download_url = null
        ),
        on_log = on_log,
        on_progress = on_progress
    )
}

suspend fun uninstall_ndk_tool(
    version: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    val key = version.trim()
    if (key.isBlank()) {
        emit_log(on_log, "错误: NDK 版本为空")
        return false
    }

    val requested = normalize_version_key(key)
    val installed = withContext(Dispatchers.IO) {
        toolchain_manager.installed_ndks().firstOrNull { info ->
            info.aliases.any { alias -> normalize_version_key(alias) == requested } ||
                normalize_version_key(info.version) == requested ||
                normalize_version_key(info.host_dir.name) == requested
        }
    }
    val target = installed?.proot_dir ?: "$guest_ndk_root/${normalize_ndk_install_name(key)}"
    val command = """
        set -e
        TARGET=${shell_quote(target)}
        case "@D@TARGET" in ${guest_ndk_root}/*) ;; *) echo "错误: 拒绝删除非 NDK 目录: @D@TARGET"; exit 1 ;; esac
        if [ -d "@D@TARGET" ]; then
          echo "删除 NDK: @D@TARGET"
          rm -rf "@D@TARGET"
          echo "NDK 卸载完成"
        else
          echo "NDK 未安装: @D@TARGET"
        fi
    """.trimIndent().with_shell_dollar()

    emit_progress(on_progress, 0)
    emit_log(on_log, "开始卸载 NDK $key")
    emit_progress(on_progress, 20)
    val success = toolchain_command_runner.execute_command(command) { line -> on_log(line) }
    emit_progress(on_progress, if (success) 100 else 90)
    return success
}


private suspend fun run_ndk_install_task(
    command: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    emit_progress(on_progress, 0)
    emit_log(on_log, "开始安装 NDK")
    emit_progress(on_progress, 8)
    val success = toolchain_command_runner.execute_command(command) { line ->
        on_log(line)
        update_ndk_progress_from_log(line, on_progress)
    }
    emit_progress(on_progress, if (success) 100 else 90)
    emit_log(on_log, if (success) "NDK 安装完成" else "NDK 安装失败")
    return success
}


private fun create_ndk_install_command(
    archive_path: String,
    target_name: String,
    download_url: String?,
    sha256: String = ""
): String {
    val download_block = create_download_block(download_url ?: "", sha256)

    val script = """
        set -e
        set -o pipefail
        CACHE_DIR=${shell_quote(guest_cache_dir)}
        NDK_ROOT=${shell_quote(guest_ndk_root)}
        ARCHIVE=${shell_quote(archive_path)}
        TARGET_NAME=${shell_quote(target_name)}
        TEMP_DIR="@D@CACHE_DIR/ndk-install-@D@TARGET_NAME-@D@@D@"
        mkdir -p "@D@CACHE_DIR" "@D@NDK_ROOT"

        $download_block

        rm -rf "@D@TEMP_DIR"
        mkdir -p "@D@TEMP_DIR"
        echo "开始解压: @D@ARCHIVE"
        print_extract_entries() {
          "@D@@A@" | while IFS= read -r entry; do
            [ -n "@D@entry" ] && printf '解压: %s\r' "@D@entry"
          done
          status=@D@{PIPESTATUS[0]}
          printf '\n'
          return @D@status
        }
        case "@D@ARCHIVE" in
          *.tar.xz|*.txz) print_extract_entries tar -xJvf "@D@ARCHIVE" -C "@D@TEMP_DIR" ;;
          *.tar.gz|*.tgz) print_extract_entries tar -xzvf "@D@ARCHIVE" -C "@D@TEMP_DIR" ;;
          *.tar) print_extract_entries tar -xvf "@D@ARCHIVE" -C "@D@TEMP_DIR" ;;
          *.zip) command -v unzip >/dev/null 2>&1 || { echo "错误: Ubuntu 中未安装 unzip，请先安装 unzip"; exit 1; }; print_extract_entries unzip -o "@D@ARCHIVE" -d "@D@TEMP_DIR" ;;
          *) echo "错误: 不支持的压缩格式: @D@ARCHIVE"; exit 1 ;;
        esac

        FOUND=""
        while IFS= read -r props; do
          dir="@D@(dirname "@D@props")"
          if [ -d "@D@dir/toolchains/llvm/prebuilt" ] && [ -f "@D@dir/build/cmake/android.toolchain.cmake" ]; then
            FOUND="@D@dir"
            break
          fi
        done < <(find "@D@TEMP_DIR" -maxdepth 6 -type f -name source.properties)

        [ -n "@D@FOUND" ] || { echo "错误: 未找到有效 NDK 目录"; rm -rf "@D@TEMP_DIR"; exit 1; }
        REVISION="@D@(grep -E '^Pkg\.Revision[[:space:]]*=' "@D@FOUND/source.properties" | head -n 1 | cut -d= -f2- | tr -d '[:space:]')"
        [ -n "@D@REVISION" ] || { echo "错误: source.properties 缺少 Pkg.Revision"; rm -rf "@D@TEMP_DIR"; exit 1; }
        SAFE_REVISION="@D@(printf '%s' "@D@REVISION" | sed 's/[^A-Za-z0-9._-]/_/g; s/^[._-]*//; s/[._-]*@D@//')"
        [ -n "@D@SAFE_REVISION" ] || { echo "错误: Pkg.Revision 无效: @D@REVISION"; rm -rf "@D@TEMP_DIR"; exit 1; }
        TARGET="@D@NDK_ROOT/@D@SAFE_REVISION"
        echo "发现 NDK: @D@FOUND"
        echo "Pkg.Revision: @D@REVISION"
        echo "NDK 目标目录: @D@TARGET"
        if [ -f "@D@TARGET/source.properties" ] && [ -d "@D@TARGET/toolchains/llvm/prebuilt" ]; then
          echo "NDK 已安装: @D@TARGET"
          rm -rf "@D@TEMP_DIR"
          exit 0
        fi
        rm -rf "@D@TARGET"
        mkdir -p "@D@TARGET"
        cp -a "@D@FOUND"/. "@D@TARGET"/
        chmod -R u+rwX "@D@TARGET" || true
        rm -rf "@D@TEMP_DIR"
        echo "NDK 安装完成: @D@TARGET"
    """.trimIndent()
    return script.with_shell_dollar()
}


private fun update_ndk_progress_from_log(line: String, on_progress: (Int) -> Unit) {
    val percent = Regex("(\\d{1,3})%").find(line)?.groupValues?.getOrNull(1)?.toIntOrNull()
    if (percent != null) {
        on_progress((10 + percent.coerceIn(0, 100) * 55 / 100).coerceIn(10, 65))
        return
    }
    when {
        line.contains("使用缓存") -> on_progress(45)
        line.contains("开始下载") -> on_progress(12)
        line.contains("开始解压") -> on_progress(70)
        line.contains("解压:") -> on_progress(78)
        line.contains("发现 NDK") -> on_progress(84)
        line.contains("NDK 安装完成") -> on_progress(100)
    }
}


