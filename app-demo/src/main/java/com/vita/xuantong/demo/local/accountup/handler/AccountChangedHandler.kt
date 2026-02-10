package com.wsvita.app.local.accountup.handler

import com.wsvita.account.accountup.BaseAccountHandlerImpl
import com.wsvita.account.accountup.IAccountConfigProvider
import com.wsvita.account.commons.AccountConstants
import com.wsvita.framework.utils.SLog

/**
 * 账号发生变化的回调
 */
class AccountChangedHandler : BaseAccountHandlerImpl() {

    companion object{
        private const val TAG = "Mirror_Accountup_AccountChaned=>";
        private val ACCOUNT_KEY = AccountConstants.AccountKeys.ACCOUNT_KEY;
    }

    override fun onHandle(provider: IAccountConfigProvider) {
        val data = provider.getString(ACCOUNT_KEY);
        SLog.d(TAG,"onHandle,data:${data}");
    }

}
