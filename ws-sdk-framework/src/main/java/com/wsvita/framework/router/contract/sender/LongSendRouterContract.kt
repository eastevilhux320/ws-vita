package com.wsvita.framework.router.contract.sender

import android.content.Intent
import android.os.Bundle
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.contract.SendRouterContract

/**
 * [长整型数据定向发送契约]
 *
 * **1. 物理意义**
 * 专门用于“只发不收”的长整型（Long）数据传递场景。通常用于传递数据库 ID、
 * 时间戳或需要高精度的业务标识。
 *
 * **2. 工作流逻辑**
 * - **初始化**：在注册路由（prepareRouters）时，通过构造函数锁定特定的 Long 值。
 * - **数据装箱**：在路由跳转触发时，createInput 显式将该 Long 值注入 Bundle。
 * - **规范对齐**：使用全局统一的 EXTRA_ROUTER_LONG_KEY 确保组件间通信协议一致。
 *
 * create by Eastevil at 2026/1/8 15:38
 * @author Eastevil
 */
class LongSendRouterContract : SendRouterContract {

    /**
     * 内部持有的长整型业务值
     */
    private var longValue: Long

    /**
     * 显式次级构造函数
     * @param action 路由唯一标识（Intent Action）
     * @param value 注册路由时预设的长整型参数
     */
    constructor(action: String, value: Long) : super(action) {
        this.longValue = value
    }

    /**
     * 核心逻辑：处理跳转前的输入数据包
     * 显式拦截数据流，并注入构造时预设的 [longValue]。
     *
     * @param input 框架层传入的原始参数包
     * @return 包含预设长整型值的处理后参数包
     */
    override fun createInput(input: Bundle): Bundle? {
        // 1. 调用基类方法获取基础 Bundle 容器
        var bundle = super.createInput(input)

        // 2. 显式空判断：若基类返回 null（如 Empty 配置下），必须初始化新 Bundle
        if (bundle == null) {
            bundle = Bundle()
        }

        // 3. 显式注入：将业务值存入常量池定义的 Long 类型专用 Key 中
        bundle.putLong(ModelConstants.IntentExtra.EXTRA_ROUTER_LONG_KEY, longValue)

        // 4. 显式返回处理后的参数包，交给系统执行跳转
        return bundle
    }

    /**
     * 显式定义输出处理逻辑
     * 作为发送契约，忽略目标页面返回的 Intent，固定返回 Unit 实例。
     */
    override fun convertToOutput(data: Intent): Unit {
        return Unit
    }
}
