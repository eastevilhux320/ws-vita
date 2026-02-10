package com.wsvita.account.accountup

import com.wsvita.app.local.accountup.IAccountHandler
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * 账号责任链基础实现类
 * 后期所有扩展节点（如实名认证、等级刷新等）都继承此类
 */
abstract class BaseAccountHandlerImpl : IAccountHandler {

    companion object{
        private const val TAG = "Mirror_Accountup_BaseAccountHandlerImpl=>";
    }

    /**
     * 指向责任链中的下一个处理器
     */
    var nextHandler: IAccountHandler? = null

    /**
     * 模板方法：控制递归执行流程
     * 逻辑完全对标 BaseAppStartupHandlerImpl
     */
    fun execute(provider: IAccountConfigProvider) {
        SLog.d(TAG,"execute,time:${systemTime()}")
        // 1. 执行当前组件的业务逻辑
        onHandle(provider)

        // 2. 查找并递归执行下一个节点
        val next = nextHandler
        if (next != null) {
            SLog.d(TAG,"has next,do next handler")
            // 类型收敛：如果下一个也是基础实现类，调用其 execute 触发递归
            if (next is BaseAccountHandlerImpl) {
                next.execute(provider)
            } else {
                // 如果是直接实现的普通接口，则作为终点执行
                next.onHandle(provider)
            }
        }
    }
}
