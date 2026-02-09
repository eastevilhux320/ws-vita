package com.wsvita.account.local.initializer

import android.content.Context
import androidx.startup.Initializer
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.framework.utils.SLog

/**
 * Account 模块自动化初始化器
 */
class AccountInitializer : Initializer<AccountManager> {

    companion object {
        private const val TAG = "WS_AC_Init_AccountInitializer=>"
    }

    override fun create(context: Context): AccountManager {
        SLog.d(TAG,"create");
        val manager = AccountManager.instance;
        manager.init()
        return manager;
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        SLog.d(TAG,"dependencies");
        // 如果 AccountManager 依赖其他库（如 MMKV 或 ImeiManager），在此列出
        return emptyList()
    }
}
