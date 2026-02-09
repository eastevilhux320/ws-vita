package com.wsvita.biz.core.configure

import com.wsvita.biz.core.startup.AppStartupConfigReceiver
import com.wsvita.biz.core.startup.IStartupConfigProvider

class StartupConfigLocator private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StartupConfigLocator() }
    }

    @Volatile
    private var receiver: AppStartupConfigReceiver? = null

    // 这里的 Map 存储全局公共配置
    private var globalData: MutableMap<String, Any> = HashMap()

    fun register(receiver: AppStartupConfigReceiver?) {
        this.receiver = receiver
    }

    /**
     * Splash 模块存入数据
     */
    fun put(key: String, value: Any) {
        globalData[key] = value
    }

    /**
     * 关键修正：分发时创建快照
     */
    fun dispatchReady() {
        var callback = this.receiver
        if (callback != null) {
            // 创建当前数据的快照，防止回调执行过程中的数据污染
            var snapshot = ConfigSnapshot(HashMap(globalData))
            callback.onStartupConfigReady(snapshot)
        }
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
