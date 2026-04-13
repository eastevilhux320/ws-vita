package com.wangshu.textus.note.local.locator.account

import com.wsvita.core.local.IBaseHandler
import com.wsvita.account.local.locator.IAccountConfigProvider

interface IAccountHandler : IBaseHandler {


    /**
     * 执行业务初始化
     * @param provider 启动配置访问器
     */
    fun onHandle(provider: IAccountConfigProvider)

}
