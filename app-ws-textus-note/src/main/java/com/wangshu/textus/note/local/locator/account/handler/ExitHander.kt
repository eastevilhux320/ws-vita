package com.wangshu.textus.note.local.locator.account.handler

import com.wangshu.textus.note.local.locator.account.BaseAccountHandlerImpl
import com.wangshu.textus.note.local.locator.account.IAccountHandler
import com.wangshu.textus.note.local.locator.startup.BaseAppStartupHandlerImpl
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.locator.IAccountConfigProvider

class ExitHander : BaseAccountHandlerImpl() {

    override fun onHandle(provider: IAccountConfigProvider) {

    }

    override fun getScope(): String {
        return AccountScope.AC_SCOPE_LOGOUT;
    }

    companion object{
        private const val TAG = "Note_Account_Handler_ExitHander=>";
    }
}
