package me.rerere.rikkahub.utils

class RikkaHubAnalytics {
    fun logEvent(name: String, params: Any?) {
        // 嵌入 XCode 后暂不接入 Firebase Analytics，这里保留空实现以兼容 RikkaHub 调用点。
    }
}
