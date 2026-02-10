package com.wsvita.app.local.startup

import com.wsvita.biz.core.startup.IStartupConfigProvider

/**
 * 责任链节点包装器
 */
class StartupChainNode {
    // 真正的业务逻辑实现
    var handler: IAppStartupHandler? = null
    // 指向下一个节点
    var next: StartupChainNode? = null

    fun execute(provider: IStartupConfigProvider) {
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
