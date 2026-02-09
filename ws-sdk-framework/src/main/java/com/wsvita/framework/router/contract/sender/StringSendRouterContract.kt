package com.wsvita.framework.router.contract.sender

import android.content.Intent
import android.os.Bundle
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.contract.SendRouterContract

/**
 * [字符串数据定向发送契约]
 *
 * **1. 物理意义**
 * 专门用于“只发不收”的字符串（String）数据传递场景。通常用于传递特定的业务指令、
 * 页面标题、用户 ID 或其他文本形式的硬编码参数。
 *
 * **2. 工作流逻辑**
 * - **初始化**：在注册路由（prepareRouters）时，通过构造函数锁定需要传递的 String 内容。
 * - **数据装箱**：在路由跳转触发时，createInput 显式将该字符串值注入 Bundle。
 * - **规范对齐**：使用全局统一的 EXTRA_ROUTER_STRING_KEY 确保组件间通信协议一致。
 *
 * **3. 编码规范**
 * - 必须使用显式构造函数 (constructor)。
 * - 必须显式处理 super.createInput 返回值为 null 的情况。
 * - 禁止使用任何形式的语法糖或隐式返回。
 */
class StringSendRouterContract : SendRouterContract {

    /**
     * 内部持有的字符串业务值
     */
    private var stringValue: String

    /**
     * 显式次级构造函数
     * @param action 路由唯一标识（Intent Action）
     * @param value 注册路由时预设的字符串参数
     */
    constructor(action: String, value: String) : super(action) {
        this.stringValue = value
    }

    /**
     * 核心逻辑：处理跳转前的输入数据包
     * 显式拦截数据流，并注入构造时预设的 [stringValue]。
     *
     * @param input 框架层传入的原始参数包
     * @return 包含预设字符串值的处理后参数包
     */
    override fun createInput(input: Bundle): Bundle? {
        // 1. 调用基类方法获取基础 Bundle 容器
        var bundle = super.createInput(input)

        // 2. 显式空判断：若基类返回 null，必须初始化新 Bundle 以承载数据
        if (bundle == null) {
            bundle = Bundle()
        }

        // 3. 显式注入：将业务值存入常量池定义的 String 类型专用 Key 中
        bundle.putString(ModelConstants.IntentExtra.EXTRA_ROUTER_STRING_KEY, stringValue)

        // 4. 显式返回处理后的参数包，交给系统执行跳转
        return bundle
    }

    /**
     * 显式定义输出处理逻辑
     * 作为发送契约，忽略目标页面返回的 Intent，固定返回 Unit。
     */
    override fun convertToOutput(data: Intent): Unit {
        return Unit
    }
}
