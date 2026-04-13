package com.wsvita.biz.core.configure

import com.wsvita.biz.core.startup.AppStartupConfigReceiver
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.framework.utils.SLog
import java.util.concurrent.CopyOnWriteArrayList

class StartupConfigLocator private constructor() {

    companion object {
        private const val TAG = "WSV_Core_StartupConfigLocator=>"
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StartupConfigLocator() }
    }

    private val receivers = CopyOnWriteArrayList<AppStartupConfigReceiver>()

    // 这里的 Map 存储全局公共配置
    private var globalData: MutableMap<String, Any> = HashMap()

    fun register(receiver: AppStartupConfigReceiver) {
        if (!receivers.contains(receiver)) receivers.add(receiver)
    }

    /**
     * Splash 模块存入数据
     */
    fun put(key: String, value: Any) {
        globalData[key] = value
    }

    /**
     * 核心触发方法：只发信号和数据快照，不关心谁来处理
     */
    fun dispatchAction(actionTag: String = "ALL") {
        SLog.d(TAG,"dispatchAction,actionTag:${actionTag}")
        val snapshot = ConfigSnapshot(HashMap(globalData))
        receivers.forEach { it.onStartupConfigReady(actionTag,snapshot) }
    }

    /**
     * 内部快照类：仅供本次回调使用
     */
    private class ConfigSnapshot(private var data: Map<String, Any>) : IStartupConfigProvider {
        override fun getString(key: String): String? {
            var v = data[key]
            return if (v is String) v else null
        }

        override fun getInt(key: String, defaultValue: Int): Int {
            var v = data[key]
            return if (v is Int) v else defaultValue
        }

        override fun getLong(key: String, defaultValue: Long): Long {
            var v = data[key]
            return if (v is Long) v else defaultValue
        }
    }
}
