package com.wangshu.textus.note.local.locator.account.handler

import com.wangshu.textus.note.local.locator.account.BaseAccountHandlerImpl
import com.wangshu.textus.note.local.locator.account.IAccountHandler
import com.wangshu.textus.note.local.locator.startup.handler.TokenStartupHandler
import com.wangshu.textus.note.local.locator.startup.handler.TokenStartupHandler.Companion
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.locator.IAccountConfigProvider
import com.wsvita.framework.utils.SLog
import com.wsvita.network.manager.TokenManager

class TokenHandler : BaseAccountHandlerImpl() {

    override fun onHandle(provider: IAccountConfigProvider) {
        val token = provider.getString(TOKEN_PARAMS_NAME);
        SLog.d(TAG,"token:${token}");
        TokenManager.instance.resetToken(token);
    }

    override fun getScope(): String {
        return AccountScope.AC_SCOPE_TOKEN;
    }

    companion object{
        private const val TAG = "Note_Account_Handler_TokenHandler=>";
        private const val TOKEN_PARAMS_NAME = AccountConstants.AccountKeys.TOKEN;
    }
}
