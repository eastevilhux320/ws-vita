package com.wsvita.app.local.startup

import com.wsvita.biz.core.startup.IStartupConfigProvider

/**
 * 责任链基础实现类
 * 封装了链式调用的逻辑，子类只需关注业务实现
 */
abstract class BaseAppStartupHandlerImpl : IAppStartupHandler {

    /**
     * 指向责任链中的下一个处理器
     */
    var nextHandler: IAppStartupHandler? = null

    /**
     * 模板方法：控制执行流程
     * 先执行当前节点的业务逻辑，再递归调用下一个节点
     */
    fun execute(provider: IStartupConfigProvider) {
        // 1. 执行当前组件的初始化
        onHandle(provider)

        // 2. 查找并执行下一个节点
        var next = nextHandler
        if (next != null) {
            // 如果下一个节点也是基础实现类，调用其 execute 触发链式反应
            if (next is BaseAppStartupHandlerImpl) {
                next.execute(provider)
            } else {
                // 如果是普通接口实现，则直接执行
                next.onHandle(provider)
            }
        }
    }
}
