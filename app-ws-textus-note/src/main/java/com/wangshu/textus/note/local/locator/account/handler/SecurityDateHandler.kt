package com.wangshu.textus.note.local.locator.account.handler

import com.wangshu.textus.note.local.locator.account.BaseAccountHandlerImpl
import com.wangshu.textus.note.local.manager.SecurityManager
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.locator.IAccountConfigProvider
import com.wsvita.framework.utils.SLog

class SecurityDateHandler : BaseAccountHandlerImpl() {
    override fun onHandle(provider: IAccountConfigProvider) {
        val keyType = provider.getInt(AccountConstants.AccountKeys.KEY_TYPE,1);
        val secretKey = provider.getString(AccountConstants.AccountKeys.SERVICE_KEY);
        SLog.d(TAG,"keyType:${keyType},secretKey:${secretKey}");
        SecurityManager.instance.resetSecret(keyType,secretKey?:"");
    }

    override fun getScope(): String {
        return AccountScope.AC_SCOPE_SECURITY_KEY
    }

    companion object{
        private const val TAG = "Note_Account_Handler_SecurityDateHandler=>";
    }
}
