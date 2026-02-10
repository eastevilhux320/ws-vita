package com.wsvita.app.local

import com.wsvita.biz.core.startup.IStartupConfigProvider

/**
 * 责任链基础处理器
 */
abstract class StartupHandler {

    // 指向下一个处理器
    var nextHandler: StartupHandler? = null

    /**
     * 处理逻辑
     * @return Boolean 是否拦截（true 表示处理完成不再向下传递，通常初始化逻辑建议传 false 让链路走完）
     */
    fun handle(provider: IStartupConfigProvider) {
        // 执行当前处理器的业务
        onHandle(provider)

        // 传递给下一个
        var next = nextHandler
        if (next != null) {
            next.handle(provider)
        }
    }

    /**
     * 子类实现具体的业务初始化逻辑
     */
    abstract fun onHandle(provider: IStartupConfigProvider)
}
