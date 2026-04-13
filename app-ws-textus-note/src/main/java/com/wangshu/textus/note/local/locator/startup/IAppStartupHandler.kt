package com.wangshu.textus.note.local.locator.startup

import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.core.local.IBaseHandler

interface IAppStartupHandler : IBaseHandler{

    /**
     * 执行业务初始化
     * @param provider 启动配置访问器
     */
    fun onHandle(provider: IStartupConfigProvider)
}
