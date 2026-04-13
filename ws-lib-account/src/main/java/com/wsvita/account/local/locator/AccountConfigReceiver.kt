package com.wsvita.account.local.locator

interface AccountConfigReceiver {
    /**
     * @param actionTag 纯字符串标识，由业务层定义（如 "LOGIN", "LOGOUT"）
     */
    fun onAccountConfigReady(actionTag: String,provider: IAccountConfigProvider)
}
