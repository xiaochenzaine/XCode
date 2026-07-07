package com.xc.code.toolchain

import com.xc.code.runtime.app_runtime_provider
import com.xc.code.toolchain.runtime.toolchain_guest_paths
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object toolchain_command_runner {
    suspend fun execute_command(
        command: String,
        on_log: (String) -> Unit
    ): Boolean {
        return execute_command_with_environment(
            command = command,
            working_dir = toolchain_guest_paths.home,
            extra_environment = emptyMap(),
            on_log = on_log
        )
    }

    suspend fun execute_command_with_environment(
        command: String,
        working_dir: String = toolchain_guest_paths.home,
        extra_environment: Map<String, String> = emptyMap(),
        on_log: (String) -> Unit
    ): Boolean {
        return try {
            app_runtime_provider.shell_runner().execute(
                command = command,
                working_dir = working_dir,
                extra_environment = extra_environment,
                on_log = on_log
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                on_log("执行错误: ${e.message}")
            }
            false
        }
    }
}
