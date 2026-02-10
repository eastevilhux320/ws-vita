package com.wsvita.framework.local

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * **WsContext**
 * 玄同框架全局上下文管理器。
 * 只要集成了框架库，在任何地方（工具类、Manager、Repository）均可直接通过 WsContext.app 获取。
 */
object WsContext {
    @SuppressLint("StaticFieldLeak")
    private var _application: Application? = null

    /**
     * Get global Application instance.
     */
    val app: Application
        get() = _application ?: throw IllegalStateException(
            "WsFramework is not initialized. Please ensure WsFrameInitializer is registered in your Library's AndroidManifest.xml."
        )

    /**
     * Get global ApplicationContext.
     */
    val context: Context get() = app.applicationContext

    @JvmStatic
    internal fun init(application: Application) {
        if (_application == null) {
            _application = application
        }
    }
}
