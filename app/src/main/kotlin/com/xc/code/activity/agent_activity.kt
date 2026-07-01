package com.xc.code.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xc.code.agent.agent_screen
import com.xc.code.ui.theme.app_theme_provider

class agent_activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            app_theme_provider {
                agent_screen()
            }
        }
    }
}
