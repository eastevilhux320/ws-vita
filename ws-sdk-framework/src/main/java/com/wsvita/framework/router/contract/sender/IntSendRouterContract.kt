package com.wsvita.framework.router.contract.sender

import android.content.Intent
import android.os.Bundle
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.contract.SendRouterContract

/**
 * [整型数据定向发送契约]
 *
 * **1. 物理意义**
 * 专门用于“只发不收”的整型数据传递场景。在注册路由时锁定一个特定的 Int 值，
 * 确保每次跳转均携带该业务标识（如：操作类型码、特定的 UI 状态码）。
 *
 * **2. 工作流逻辑**
 * - **初始化**：在 prepareRouters 阶段通过显式构造函数传入 Action 和预设的 Int 值。
 * - **装箱**：在跳转前，createInput 方法显式将该值存入 Bundle。
 * - **解耦**：目标页面只需通过常量池中的 EXTRA_ROUTER_INT_KEY 即可解析数据。
 *
 * create by Eastevil at 2026/1/8 15:37
 * @author Eastevil
 */
class IntSendRouterContract : SendRouterContract {

    /**
     * 内部持有的整型业务值
     */
    private var intValue: Int? = null;

    /**
     * 显式次级构造函数
     * @param action 路由唯一标识（Intent Action）
     * @param value 注册路由时预设的整型参数
     */
    constructor(action: String, value: Int) : super(action) {
        this.intValue = value
    }

    constructor(action: String) : super(action) {

    }

    /**
     * 核心逻辑：处理跳转前的输入数据包
     * 显式处理基类返回的 Bundle 对象，确保 intValue 被正确注入。
     * * @param input 框架层传入的原始参数包
     * @return 包含预设整型值的处理后参数包
     */
    override fun createInput(input: Bundle): Bundle? {
        // 1. 获取基类处理后的 Bundle（可能为 null）
        var bundle = super.createInput(input)

        // 2. 显式空判断：若为空则创建新对象，防止后续 putInt 操作异常
        if (bundle == null) {
            bundle = Bundle()
        }

        // 3. 显式注入：将构造时传入的 intValue 存入常量池定义的统一 Key 中
        intValue?.let { bundle.putInt(ModelConstants.IntentExtra.EXTRA_ROUTER_INT_KEY, it) }

        // 4. 显式返回处理后的参数包
        return bundle
    }

    /**
     * 显式定义输出处理逻辑
     * 由于是发送契约，不处理目标 Activity 返回的 Intent，固定返回 Unit。
     */
    override fun convertToOutput(data: Intent): Unit {
        return Unit
    }
}
