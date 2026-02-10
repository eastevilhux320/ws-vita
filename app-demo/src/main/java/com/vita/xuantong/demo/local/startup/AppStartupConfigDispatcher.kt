package com.wsvita.app.local.startup

import com.wsvita.app.local.startup.handler.SecurityDataStartupHandler
import com.wsvita.app.local.startup.handler.SplashStartupHandler
import com.wsvita.app.local.startup.handler.TokenStartupHandler
import com.wsvita.biz.core.startup.AppStartupConfigReceiver
import com.wsvita.biz.core.startup.IStartupConfigProvider

class AppStartupConfigDispatcher : AppStartupConfigReceiver {

    private var rootHandler: BaseAppStartupHandlerImpl? = null

    /**
     * 优化后的组装方式：清晰、易扩展
     */
    fun initChain() {
        // 1. 定义有序列表（未来这里可以改为动态注入）
        var handlers: MutableList<BaseAppStartupHandlerImpl> = ArrayList()
        handlers.add(TokenStartupHandler())
        handlers.add(SecurityDataStartupHandler())
        handlers.add(SplashStartupHandler())
        // handlers.add(LogStartupHandler())        // 第三步：...

        // 2. 自动串联链条
        if (handlers.size > 0) {
            this.rootHandler = handlers[0]
            var i = 0
            while (i < handlers.size - 1) {
                handlers[i].nextHandler = handlers[i + 1]
                i++
            }
        }
    }

    override fun onStartupConfigReady(provider: IStartupConfigProvider) {
        var head = this.rootHandler
        if (head != null) {
            head.execute(provider)
        }
    }
}
