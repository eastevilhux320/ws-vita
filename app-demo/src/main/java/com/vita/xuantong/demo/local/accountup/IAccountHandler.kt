package com.wsvita.app.local.accountup

import com.wsvita.account.accountup.IAccountConfigProvider

/**
 * 账号登录成功后的处理回调
 */
interface IAccountHandler {
    /**
     * 执行账号相关的业务初始化逻辑
     * @param provider 账号配置访问器
     */
    fun onHandle(provider: IAccountConfigProvider)
}
