package com.wangshu.textus.note.local.locator.account.handler

import com.wangshu.textus.note.local.locator.account.BaseAccountHandlerImpl
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.locator.IAccountConfigProvider
import com.wsvita.framework.utils.SLog

class AccountUpdateHandler : BaseAccountHandlerImpl() {

    override fun onHandle(provider: IAccountConfigProvider) {
        SLog.d(TAG,"onHandle");
    }

    override fun getScope(): String {
        return AccountScope.AC_SCOPE_UPDATE;
    }

    companion object{
        private const val TAG = "Note_Account_Handler_AccountUpdateHandler=>";
    }

}
