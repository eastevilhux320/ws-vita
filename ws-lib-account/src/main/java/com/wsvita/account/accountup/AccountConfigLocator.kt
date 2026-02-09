package com.wsvita.account.accountup

import java.util.concurrent.ConcurrentHashMap

class AccountConfigLocator private constructor() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AccountConfigLocator() }
    }

    @Volatile
    private var receiver: IAccountConfigReceiver? = null

    // 存储账号相关的动态配置数据
    private val accountData: MutableMap<String, Any> = ConcurrentHashMap()

    /**
     * 注册账号配置接收器
     */
    fun register(receiver: IAccountConfigReceiver) {
        this.receiver = receiver
    }

    /**
     * 存储账号数据
     */
    fun put(key: String, value: Any) {
        accountData[key] = value
    }

    /**
     * 移除特定数据
     */
    fun remove(key: String) {
        accountData.remove(key)
    }

    /**
     * 清空所有账号数据（通常用于注销场景）
     */
    fun clear() {
        accountData.clear()
    }

    /**
     * 分发登录就绪状态，创建当前数据的快照提供给接收方
     */
    fun dispatchLoginReady() {
        val callback = this.receiver
        if (callback != null) {
            // 创建快照，确保回调执行期间的数据一致性
            val snapshot = AccountConfigSnapshot(HashMap(accountData))
            callback.onAccountConfigReady(snapshot)
        }
    }

    /**
     * 内部快照实现
     */
    private class AccountConfigSnapshot(private val data: Map<String, Any>) : IAccountConfigProvider {
        override fun getString(key: String): String? {
            val v = data[key]
            return if (v is String) v else null
        }

        override fun getInt(key: String, defaultValue: Int): Int {
            val v = data[key]
            return if (v is Int) v else defaultValue
        }

        override fun getLong(key: String, defaultValue: Long): Long {
            val v = data[key]
            return if (v is Long) v else defaultValue
        }
    }
}
