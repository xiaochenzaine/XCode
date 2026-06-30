package com.xc.code.utils

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.xc.code.xc_application

class app_lifecycle_observer(private val app: xc_application) : DefaultLifecycleObserver {

    companion object {
        fun init(app: xc_application) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(app_lifecycle_observer(app))
        }
    }
}
