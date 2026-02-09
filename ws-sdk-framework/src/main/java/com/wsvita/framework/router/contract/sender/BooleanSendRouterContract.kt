package com.wsvita.framework.router.contract.sender

import android.os.Bundle
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.contract.SendRouterContract

/**
 * [布尔值定向发送契约]
 *
 * **1. 物理意义**
 * 专门用于“只发不收”的路由场景。它在注册阶段即锁定一个布尔值，并在跳转发起时
 * 强制将该值注入到 Intent 参数包中。
 *
 * **2. 工作流逻辑**
 * - **初始化**：在 prepareRouters 阶段通过构造函数传入固定的 Action 和 Boolean 值。
 * - **注入**：当触发路由跳转时，createInput 会拦截并确保该布尔值存入 Bundle。
 * - **传递**：最终通过 Intent 传递给目标 Activity。
 *
 * create by Eastevil at 2026/1/8 15:37
 * @author Eastevil
 */
class BooleanSendRouterContract : SendRouterContract {
    private var booleanValue : Boolean;

    constructor(action : String,value : Boolean) : super(action) {
        this.booleanValue = value;
    }

    override fun createInput(input: Bundle): Bundle? {
        var bundle = super.createInput(input)

        // 如果基类返回 null，则需要初始化一个 Bundle 以防 NullPointerException
        if (bundle == null) {
            bundle = Bundle()
        }

        // 显式将布尔值存入，使用常量池定义的 Key
        bundle.putBoolean(ModelConstants.IntentExtra.EXTRA_ROUTER_BOOLEAN_KEY, booleanValue)

        // 显式返回处理后的结果
        return bundle
    }
}
