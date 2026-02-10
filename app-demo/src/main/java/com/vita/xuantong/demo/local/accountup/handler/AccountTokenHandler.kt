package com.wsvita.app.local.accountup.handler

import com.wsvita.account.accountup.BaseAccountHandlerImpl
import com.wsvita.account.accountup.IAccountConfigProvider
import com.wsvita.account.commons.AccountConstants
import com.wsvita.framework.utils.SLog
import com.wsvita.network.manager.TokenManager

/**
 * 专门处理 Token 修改的节点
 */
class AccountTokenHandler : BaseAccountHandlerImpl() {
    companion object{
        private const val TAG = "Mirror_Accountup_AccountToken=>";
        private const val SECRETKEY_PARAMS_NAME = "secretKey";
        private const val KEYTYPE_PARAMS_NAME = "keyType";
    }

    override fun onHandle(provider: IAccountConfigProvider) {
        SLog.d(TAG,"onHandle")
        val token = provider.getString(AccountConstants.AccountKeys.TOKEN);
        SLog.d(TAG,"onHandle_token:${token}，refresh token```");
        TokenManager.instance.resetToken(token)
    }
}


