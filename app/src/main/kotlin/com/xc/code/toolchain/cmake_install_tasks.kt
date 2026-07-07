package com.xc.code.toolchain

import com.xc.code.runtime.app_runtime_provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun install_cmake_tool(
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    return install_cmake_from_url(
        version = "4.3.0",
        url = "https://github.com/Kitware/CMake/releases/download/v4.3.0/cmake-4.3.0-linux-aarch64.tar.gz",
        sha256 = "26fe3011f497eb9398115dcabcc094685e634b1841f7c01dc01c5a89b8b0ea0d",
        on_log = on_log,
        on_progress = on_progress
    )
}

suspend fun install_cmake_from_url(
    version: String,
    url: String,
    sha256: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    val trimmed_url = url.trim()
    if (trimmed_url.isBlank()) {
        emit_log(on_log, "错误: CMake 下载地址为空")
        return false
    }

    val target_name = normalize_cmake_install_name(version).ifBlank { strip_archive_extension(archive_name_from_url(trimmed_url)) }
    val archive_name = archive_name_from_url(trimmed_url).ifBlank { "cmake-$target_name-linux-aarch64.tar.gz" }
    val archive_path = "$guest_cache_dir/$archive_name"
    return run_cmake_install_task(
        command = create_cmake_install_command(
            archive_path = archive_path,
            download_url = trimmed_url,
            sha256 = sha256
        ),
        on_log = on_log,
        on_progress = on_progress
    )
}

suspend fun install_cmake_from_archive(
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

    return run_cmake_install_task(
        command = create_cmake_install_command(
            archive_path = host_path_to_proot_path(trimmed_path),
            download_url = null
        ),
        on_log = on_log,
        on_progress = on_progress
    )
}

suspend fun uninstall_cmake_tool(
    version: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    val key = version.trim()
    val target = if (key.isBlank()) guest_cmake_root else "$guest_cmake_root/${normalize_cmake_install_name(key)}"
    val command = """
        set -e
        TARGET=${shell_quote(target)}
        case "@D@TARGET" in ${guest_cmake_root}|${guest_cmake_root}/*) ;; *) echo "错误: 拒绝删除非 CMake 目录: @D@TARGET"; exit 1 ;; esac
        if [ ! -d "@D@TARGET" ]; then
          echo "CMake 未安装: @D@TARGET"
          exit 0
        fi
        if [ "@D@TARGET" = "${guest_cmake_root}" ]; then
          FOUND=0
          while IFS= read -r candidate; do
            case "@D@candidate" in ${guest_cmake_root}/*) ;; *) echo "错误: 拒绝删除非 CMake 目录: @D@candidate"; exit 1 ;; esac
            if [ -f "@D@candidate/bin/cmake" ]; then
              echo "删除 CMake: @D@candidate"
              rm -rf "@D@candidate"
              FOUND=1
            fi
          done < <(find "@D@TARGET" -mindepth 1 -maxdepth 1 -type d)
          rmdir "@D@TARGET" 2>/dev/null || true
          if [ "@D@FOUND" = "1" ]; then echo "CMake 卸载完成"; else echo "CMake 未安装: @D@TARGET"; fi
        else
          if [ -f "@D@TARGET/bin/cmake" ]; then
            echo "删除 CMake: @D@TARGET"
            rm -rf "@D@TARGET"
            rmdir "${guest_cmake_root}" 2>/dev/null || true
            echo "CMake 卸载完成"
          else
            echo "CMake 目录结构无效: @D@TARGET"
            exit 1
          fi
        fi
    """.trimIndent().with_shell_dollar()

    return run_proot_toolchain_task(
        start_log = if (key.isBlank()) "开始卸载 CMake" else "开始卸载 CMake $key",
        command = command,
        success_log = "CMake 卸载完成",
        on_log = on_log,
        on_progress = on_progress
    )
}


private fun create_cmake_install_command(
    archive_path: String,
    download_url: String?,
    sha256: String = ""
): String {
    val download_block = create_download_block(download_url ?: "", sha256)

    val script = """
        set -e
        set -o pipefail
        CACHE_DIR=${shell_quote(guest_cache_dir)}
        CMAKE_ROOT=${shell_quote(guest_cmake_root)}
        ARCHIVE=${shell_quote(archive_path)}
        TEMP_DIR="@D@CACHE_DIR/cmake-install-@D@@D@"
        mkdir -p "@D@CACHE_DIR" "@D@CMAKE_ROOT"

        $download_block

        rm -rf "@D@TEMP_DIR"
        mkdir -p "@D@TEMP_DIR"
        echo "开始解压: @D@ARCHIVE"
        case "@D@ARCHIVE" in
          *.tar.xz|*.txz) tar -xJvf "@D@ARCHIVE" -C "@D@TEMP_DIR" >/dev/null ;;
          *.tar.gz|*.tgz) tar -xzvf "@D@ARCHIVE" -C "@D@TEMP_DIR" >/dev/null ;;
          *.tar) tar -xvf "@D@ARCHIVE" -C "@D@TEMP_DIR" >/dev/null ;;
          *.zip) command -v unzip >/dev/null 2>&1 || { echo "错误: Ubuntu 中未安装 unzip，请先安装 unzip"; exit 1; }; unzip -o "@D@ARCHIVE" -d "@D@TEMP_DIR" >/dev/null ;;
          *) echo "错误: 不支持的压缩格式: @D@ARCHIVE"; exit 1 ;;
        esac

        FOUND=""
        while IFS= read -r cmake_bin; do
          dir="@D@(dirname "@D@(dirname "@D@cmake_bin")")"
          if [ -d "@D@dir/bin" ]; then
            FOUND="@D@dir"
            break
          fi
        done < <(find "@D@TEMP_DIR" -maxdepth 5 -type f -path '*/bin/cmake')

        [ -n "@D@FOUND" ] || { echo "错误: 未找到有效 CMake 目录"; rm -rf "@D@TEMP_DIR"; exit 1; }
        CMAKE_VERSION="@D@("@D@FOUND/bin/cmake" --version | head -n 1 | awk '{print @D@3}')"
        [ -n "@D@CMAKE_VERSION" ] || { echo "错误: 无法读取 CMake 版本"; rm -rf "@D@TEMP_DIR"; exit 1; }
        printf '%s' "@D@CMAKE_VERSION" | grep -Eq '^[0-9]+(\.[0-9]+)+([A-Za-z0-9._-]*)?@D@' || { echo "错误: CMake 版本无效: @D@CMAKE_VERSION"; rm -rf "@D@TEMP_DIR"; exit 1; }
        SAFE_VERSION="@D@(printf '%s' "@D@CMAKE_VERSION" | sed 's/[^A-Za-z0-9._-]/_/g; s/^[._-]*//; s/[._-]*@D@//')"
        [ -n "@D@SAFE_VERSION" ] || { echo "错误: CMake 版本无效: @D@CMAKE_VERSION"; rm -rf "@D@TEMP_DIR"; exit 1; }
        TARGET="@D@CMAKE_ROOT/@D@SAFE_VERSION"
        echo "发现 CMake: @D@FOUND"
        echo "CMake 版本: @D@CMAKE_VERSION"
        echo "CMake 目标目录: @D@TARGET"
        if [ -f "@D@TARGET/bin/cmake" ]; then
          echo "CMake 已安装: @D@TARGET"
          rm -rf "@D@TEMP_DIR"
          echo "CMAKE_TARGET=@D@TARGET"
          exit 0
        fi
        rm -rf "@D@TARGET"
        mkdir -p "@D@TARGET"
        cp -a "@D@FOUND"/. "@D@TARGET"/
        chmod -R u+rwX "@D@TARGET" || true
        rm -rf "@D@TEMP_DIR"
        echo "CMake 安装完成: @D@TARGET"
        echo "CMAKE_TARGET=@D@TARGET"
    """.trimIndent()
    return script.with_shell_dollar()
}

private suspend fun run_cmake_install_task(
    command: String,
    on_log: (String) -> Unit,
    on_progress: (Int) -> Unit
): Boolean {
    emit_progress(on_progress, 0)
    emit_log(on_log, "开始安装 CMake 构建工具")
    emit_progress(on_progress, 10)
    var cmake_target = ""
    val success = toolchain_command_runner.execute_command(command) { line ->
        on_log(line)
        val marker = "CMAKE_TARGET="
        val index = line.indexOf(marker)
        if (index >= 0) cmake_target = line.substring(index + marker.length).trim()
    }
    if (!success) {
        emit_progress(on_progress, 90)
        emit_log(on_log, "命令执行失败")
        return false
    }
    val copied = install_bundled_ninja(cmake_target, on_log)
    emit_progress(on_progress, if (copied) 100 else 90)
    emit_log(on_log, if (copied) "CMake 构建工具安装完成" else "Ninja 安装失败")
    return copied
}

private suspend fun install_bundled_ninja(
    cmake_target: String,
    on_log: (String) -> Unit
): Boolean = withContext(Dispatchers.IO) {
    val source = File(app_runtime_provider.paths().native_library_dir, "libninja_exec.so")
    if (!source.isFile) {
        emit_log(on_log, "错误: 未找到内置 Ninja: ${source.absolutePath}")
        return@withContext false
    }
    if (!cmake_target.startsWith("$guest_cmake_root/")) {
        emit_log(on_log, "错误: CMake 目标目录无效: $cmake_target")
        return@withContext false
    }
    val relative_path = cmake_target.removePrefix(guest_tool_dir).trimStart('/')
    if (relative_path.isBlank()) {
        emit_log(on_log, "错误: CMake 目标目录无效: $cmake_target")
        return@withContext false
    }
    val target_dir = File(app_runtime_provider.paths().xcode_dir, relative_path)
    val bin_dir = File(target_dir, "bin")
    if (!File(bin_dir, "cmake").isFile) {
        emit_log(on_log, "错误: CMake 目标目录缺少 bin/cmake: ${target_dir.absolutePath}")
        return@withContext false
    }
    val target = File(bin_dir, "ninja")
    runCatching {
        source.copyTo(target, overwrite = true)
        target.setExecutable(true, false)
        target.setReadable(true, false)
        target.setWritable(true, true)
    }.onFailure { error ->
        emit_log(on_log, "错误: 安装 Ninja 失败: ${error.message}")
        return@withContext false
    }
    emit_log(on_log, "Ninja 安装完成: ${target.absolutePath}")
    true
}


