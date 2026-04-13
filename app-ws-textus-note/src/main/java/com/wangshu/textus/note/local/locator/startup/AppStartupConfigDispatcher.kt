package com.wangshu.textus.note.local.locator.startup

import com.wangshu.textus.note.local.locator.account.IAccountHandler
import com.wangshu.textus.note.local.locator.account.handler.ExitHander
import com.wangshu.textus.note.local.locator.account.handler.LoginHandler
import com.wangshu.textus.note.local.locator.account.handler.TokenHandler
import com.wangshu.textus.note.local.locator.startup.handler.SecurityDataStartupHandler
import com.wangshu.textus.note.local.locator.startup.handler.SplashStartupHandler
import com.wangshu.textus.note.local.locator.startup.handler.TokenStartupHandler
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.biz.core.startup.AppStartupConfigReceiver
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.biz.core.startup.StartupScope

class AppStartupConfigDispatcher : AppStartupConfigReceiver {

    private var rootHandler: BaseAppStartupHandlerImpl? = null

    // 动态存储所有 Handler，不需要 import 具体实现类
    private val handlers = mutableListOf<IAppStartupHandler>()

    /**
     * 单个handler注册
     * create by Eastevil at 2026/4/13 16:40
     * @author Eastevil
     * @param
     * @return
     */
    fun registerHandler(handler: IAppStartupHandler){
        handlers.add(handler)
    }

    /**
     * 优化后的组装方式：清晰、易扩展
     */
    fun initChain() {
        // 1. 自动添加已经定义好的handler，后续可扩展
        handlers.add(SecurityDataStartupHandler())
        handlers.add(SplashStartupHandler())
        handlers.add(TokenStartupHandler())
    }

    override fun onStartupConfigReady(actionTag: String, provider: IStartupConfigProvider) {
        // 逻辑过滤：如果发的是全量，或者 Tag 匹配，则执行
        if(StartupScope.STARTUP_SCOPE_ALL.equals(actionTag)){
            //全执行
            handlers.forEach {
                it.onHandle(provider);
            }
        }else{
            handlers.find { actionTag.equals(it.getScope()) }?.onHandle(provider);
        }
    }
}
