package com.wsvita.account.local.locator

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class AccountConfigLocator private constructor() {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AccountConfigLocator() }
    }

    private val receivers = CopyOnWriteArrayList<AccountConfigReceiver>()
    private val accountData: MutableMap<String, Any> = ConcurrentHashMap()

    fun register(receiver: AccountConfigReceiver) {
        if (!receivers.contains(receiver)) receivers.add(receiver)
    }

    fun put(key: String, value: Any) { accountData[key] = value }

    /**
     * 核心触发方法：只发信号和数据快照，不关心谁来处理
     */
    fun dispatchAction(actionTag: String = "ALL") {
        val snapshot = AccountConfigSnapshot(HashMap(accountData))
        receivers.forEach { it.onAccountConfigReady(actionTag,snapshot) }
    }

    private class AccountConfigSnapshot(private val data: Map<String, Any>) : IAccountConfigProvider {
        override fun getString(key: String): String? = data[key] as? String
        override fun getInt(key: String, defaultValue: Int): Int = (data[key] as? Int) ?: defaultValue
        override fun getLong(key: String, defaultValue: Long): Long = (data[key] as? Long) ?: defaultValue
    }
}
