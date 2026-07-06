package com.xc.code.activity

import android.content.Context
import com.xc.code.ui.locale.app_locale_manager
import me.rerere.rikkahub.RouteActivity

class agent_activity : RouteActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(app_locale_manager.wrap_context(newBase))
    }
}
