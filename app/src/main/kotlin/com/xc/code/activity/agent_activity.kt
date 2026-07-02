package com.xc.code.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import com.xc.code.agent.agent_screen
import com.xc.code.agent.dark_agent_colors
import com.xc.code.agent.light_agent_colors
import com.xc.code.agent.local_agent_colors
import com.xc.code.agent.agent_colors
import com.xc.code.ui.theme.app_theme_provider
import com.xc.code.ui.theme.theme_manager
import com.xc.code.ui.theme.app_theme_type

class agent_activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val is_dark = when (theme_manager.theme.value) {
                app_theme_type.DARK -> true
                app_theme_type.LIGHT -> false
                app_theme_type.SYSTEM -> isSystemInDarkTheme()
            }
            val agent_colors = if (is_dark) dark_agent_colors else light_agent_colors
            app_theme_provider {
                CompositionLocalProvider(local_agent_colors provides agent_colors) {
                    agent_screen()
                }
            }
        }
    }
}
