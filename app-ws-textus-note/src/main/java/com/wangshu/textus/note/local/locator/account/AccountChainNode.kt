package com.wangshu.textus.note.local.locator.account

import com.wsvita.account.local.locator.IAccountConfigProvider

class AccountChainNode {

    // 真正的业务逻辑实现
    var handler: IAccountHandler? = null
    // 指向下一个节点
    var next: AccountChainNode? = null

    fun execute(provider: IAccountConfigProvider) {
        // 执行当前逻辑
        var h = handler
        if (h != null) {
            h.onHandle(provider)
        }
        // 传递给下一个节点
        var n = next
        if (n != null) {
            n.execute(provider)
        }
    }
}
