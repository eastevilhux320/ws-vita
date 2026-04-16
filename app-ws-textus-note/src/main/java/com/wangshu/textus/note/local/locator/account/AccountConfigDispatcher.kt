package com.wangshu.textus.note.local.locator.account

import com.wangshu.textus.note.local.locator.account.handler.AccountUpdateHandler
import com.wangshu.textus.note.local.locator.account.handler.ExitHander
import com.wangshu.textus.note.local.locator.account.handler.LoginHandler
import com.wangshu.textus.note.local.locator.account.handler.SecurityDateHandler
import com.wangshu.textus.note.local.locator.account.handler.TokenHandler
import com.wsvita.account.local.locator.AccountConfigReceiver
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.locator.IAccountConfigProvider
import kotlinx.coroutines.handleCoroutineException

class AccountConfigDispatcher : AccountConfigReceiver {

    // 动态存储所有 Handler，不需要 import 具体实现类
    private val handlers = mutableListOf<IAccountHandler>()

    /**
     * 单个handler注册
     * create by Eastevil at 2026/4/13 16:40
     * @author Eastevil
     * @param
     * @return
     */
    fun registerHandler(handler: IAccountHandler){
        handlers.add(handler)
    }

    /**
     * 优化后的组装方式：清晰、易扩展
     */
    fun initChain() {
        // 1. 自动添加已经定义好的handler，后续可扩展
        handlers.add(TokenHandler())
        handlers.add(LoginHandler())
        handlers.add(ExitHander())
        handlers.add(AccountUpdateHandler())
        handlers.add(SecurityDateHandler());
    }

    override fun onAccountConfigReady(actionTag: String, provider: IAccountConfigProvider) {
        // 逻辑过滤：如果发的是全量，或者 Tag 匹配，则执行
        if(AccountScope.AC_SCOPE_ALL.equals(actionTag)){
            //全执行
            handlers.forEach {
                it.onHandle(provider);
            }
        }else{
            handlers.find { actionTag.equals(it.getScope()) }?.onHandle(provider);
        }
    }

}
