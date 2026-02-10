package com.wsvita.app.local.startup

import com.wsvita.biz.core.startup.IStartupConfigProvider

interface IAppStartupHandler {

    /**
     * 执行业务初始化
     * @param provider 启动配置访问器
     */
    fun onHandle(provider: IStartupConfigProvider)
}
