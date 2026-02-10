package com.wsvita.app.local.accountup

import com.wsvita.account.accountup.BaseAccountHandlerImpl
import com.wsvita.account.accountup.IAccountConfigProvider
import com.wsvita.account.accountup.IAccountConfigReceiver
import com.wsvita.app.local.accountup.handler.AccountChangedHandler
import com.wsvita.app.local.accountup.handler.AccountServiceKeyHandler
import com.wsvita.app.local.accountup.handler.AccountTokenHandler

class AccountConfigDispatcher : IAccountConfigReceiver {

    private var rootHandler: BaseAccountHandlerImpl? = null

    /**
     * 组装账号处理链路
     */
    fun initChain() {
        val handlers = mutableListOf<BaseAccountHandlerImpl>()

        // 按业务顺序添加节点
        handlers.add(AccountTokenHandler())
        handlers.add(AccountServiceKeyHandler())
        handlers.add(AccountChangedHandler())
        // 后期扩展：handlers.add(UserGradeHandler())

        // 自动串联链条指针
        if (handlers.isNotEmpty()) {
            this.rootHandler = handlers[0]
            for (i in 0 until handlers.size - 1) {
                handlers[i].nextHandler = handlers[i + 1]
            }
        }
    }

    override fun onAccountConfigReady(provider: IAccountConfigProvider) {
        // 从根节点开始执行递归
        rootHandler?.execute(provider)
    }
}
