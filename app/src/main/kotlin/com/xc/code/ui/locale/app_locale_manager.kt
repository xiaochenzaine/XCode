package com.xc.code.ui.locale

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 应用语言设置。SYSTEM 代表跟随系统语言。
enum class app_language_type(val language_tag: String) {
    SYSTEM(""),
    ZH("zh"),
    EN("en")
}

object app_locale_manager {
    private const val PREFS = "app_settings"
    private const val LANGUAGE_KEY = "language_type"

    private val _language = MutableStateFlow(app_language_type.SYSTEM)
    val language: StateFlow<app_language_type> = _language.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ordinal = prefs.getInt(LANGUAGE_KEY, app_language_type.SYSTEM.ordinal)
        val type = app_language_type.entries.getOrElse(ordinal) { app_language_type.SYSTEM }
        _language.value = type
        apply_language(type)
    }

    fun set_language(context: Context, type: app_language_type) {
        _language.value = type
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(LANGUAGE_KEY, type.ordinal)
            .apply()
        apply_language(type)
    }

    fun wrap_context(base: Context): Context {
        val prefs = base.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ordinal = prefs.getInt(LANGUAGE_KEY, app_language_type.SYSTEM.ordinal)
        val type = app_language_type.entries.getOrElse(ordinal) { app_language_type.SYSTEM }
        if (type == app_language_type.SYSTEM) return base

        val locale = Locale.forLanguageTag(type.language_tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }

    private fun apply_language(type: app_language_type) {
        val locales = if (type == app_language_type.SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(type.language_tag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
