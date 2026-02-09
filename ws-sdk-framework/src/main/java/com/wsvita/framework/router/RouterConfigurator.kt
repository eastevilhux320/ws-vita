package com.wsvita.framework.router

import com.wsvita.framework.router.contract.ContractResult
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime
import org.jetbrains.annotations.Contract

/**
 * -------------------------------------------------------------------------------------
 * 【路由配置辅助中心 (Router Configuration Hub)】
 * -------------------------------------------------------------------------------------
 * [1. 职责说明]
 * 本类作为组件化开发中的“契约收集器”，主要负责在 Activity 初始化阶段收集子类声明的路由协议
 * (Contract) 与对应的业务回调逻辑。它充当了路由跳转与结果处理之间的映射表。
 *
 * [2. 设计意图]
 * - **集中化管理**：将零散的路由跳转逻辑收拢到 prepareRouters 方法中统一声明。
 * - **类型安全分发**：利用 Kotlin 泛型 [I] 和 [O]，在编译期确保输入参数与回调结果的类型一致性。
 * - **逻辑解耦**：将 Activity Result API 的底层分发细节屏蔽，让开发者仅需关注业务回调。
 *
 * [3. 工作流程]
 * 1. 子类在 prepareRouters 中调用 [register]。
 * 2. 框架层（SDKActivity）遍历 [configs] 并注册系统级的 ActivityResultLauncher。
 * 3. 页面返回结果时，框架通过 [configs] 找到对应的 lambda 并执行。
 * -------------------------------------------------------------------------------------
 */
class RouterConfigurator {

    companion object{
        private const val TAG = "WSF_Router_RouterConfigurator=>";
    }

    // 内部存储：Key -> Pair(协议, 回调)
    internal val configs = mutableMapOf<String, Pair<BaseComponentResult<*, *>, (Any) -> Unit>>()

    /**
     * -------------------------------------------------------------------------------------
     * 【路由协议注册入口 (Route Contract Registration)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 将业务定义的“路由标识名”与特定的“通信协议”及“结果回调”进行绑定。
     * 它是建立发起方与目标方数据契约的核心步骤。
     *
     *
     * [3. 设计原理 - 泛型与类型安全]
     * - 输入 I：虽然当前大部分协议将 I 固定为 [Bundle]，但泛型设计保留了未来自定义非 Bundle 输入的可能性。
     * - 输出 O：通过泛型约束，确保 [contract] 解析出的类型与 [onSuccess] 接收的类型严格一致。
     * - 类型擦除：在 [configs] 存储时使用了 [Any] 转换，但在调用端（SDKActivity）会通过
     * ActivityResultLauncher 的契约机制重新找回类型安全性。
     *
     * [4. 注意事项]
     * - **非空安全**：如果使用的协议（如 StringRouterContract）允许输出为 null，请在
     * [onSuccess] 中做好非空判断。
     * - **覆盖风险**：严禁在循环中动态修改注册信息，同一个 [name] 只能指向一个确定的协议。
     * -------------------------------------------------------------------------------------
     *
     * create by Administrator at 2026/1/4 23:35
     * @author Administrator
     *
     * @param name     路由唯一标识名（建议使用常量）。调用 [router] 方法时需传入此名称。
     * @param contract 协议实现类（继承自 BaseComponentResult）。
     * 它定义了：
     * 1. 跳转的 Action 是什么；
     * 2. 如何包装输入参数 (createInput)；
     * 3. 如何解析返回结果 (convertToOutput)。
     * @param onSuccess 成功回调逻辑。
     * - 触发条件：目标页面通过 [com.wsvita.framework.commons.SDKActivity.finishWithResult] 返回且 ResultCode 为 OK。
     * - 自动脱壳：回调中的参数 [O] 已经是协议解析后的强类型对象，无需再次解析。
     * @return
     */
    fun <I, O> register(
        name: String,
        contract: BaseComponentResult<I, O>,
        onSuccess: (O) -> Unit
    ) {
        // 存储时抹除具体类型，以便统一管理
        configs[name] = Pair(contract, onSuccess as (Any) -> Unit)
        SLog.i(TAG,"register invoke success,time:${systemTime()},name:${name},action:${contract.getAction()}");
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由协议注册 - 契约结果包装模式 (Contract Result Mode)】
     * -------------------------------------------------------------------------------------
     * [1. 职责说明]
     * 此方法用于在组件化开发中建立“高内聚”的回调契约。它不仅传递目标页面返回的业务数据，
     * 还会通过“闭包捕获”机制，将发起跳转时的元数据（Action、Name）重新封装后回传。
     *
     * [2. 设计意图]
     * - **消除硬编码**：业务层通过 [ContractResult] 即可直接获取路由 Action，无需手动维护字符串常量。
     * - **结果溯源**：在 Activity 存在多个跳转逻辑时，通过返回的 [name] 或 [action] 快速识别回调来源。
     * - **类型安全**：内部通过 [@Suppress("UNCHECKED_CAST")] 屏蔽了由 configs 统一存储带来的类型擦除影响。
     *
     * [3. 核心机制 - 闭包捕获 (Closure Capture)]
     * 在调用此方法时，[action] 和 [name] 被“锁”在 [wrappedCallback] 内部。即使在异步的 Activity
     * 跳转返回后，这些变量依然有效，从而实现了 [ContractResult] 的自动填充。
     *
     * [4. MVVM 配合]
     * 建议在 ViewModel 中定义对应的回调逻辑，通过此方法注入，实现 View 层跳转与逻辑层回调的解耦。
     * -------------------------------------------------------------------------------------
     *
     * @param name      路由唯一标识名。需与发起跳转 [router] 时传入的名称一致。
     * @param contract  跳转协议实现类。定义了跳转指令 (Action) 及结果转换规则。
     * @param onSuccess 业务层回调逻辑。接收封装后的 [ContractResult] 对象。
     */
    /**
     * 注册组件协议回调
     * 遵循 MVVM 架构设计，用于组件间通信或 Activity Result 挂钩
     */
    fun <I, O> registerContract(
        name: String,
        contract: BaseComponentResult<I, O>,
        onSuccess: (action: String, name: String, data: O) -> Unit
    ) {
        // 1. 获取 contract 中的 action
        val action = contract.getAction()

        // 2. 修复点：显式声明为 (Any) -> Unit 以适配 configs 的存储类型，并内部强转 O
        val wrappedCallback: (Any) -> Unit = { result ->
            @Suppress("UNCHECKED_CAST")
            onSuccess.invoke(action, name, result as O)
        }

        // 3. 存储到配置表
        configs[name] = Pair(contract, wrappedCallback)

        // 4. 使用你原始的 systemTime() 打印日志
        SLog.i(TAG, "register(ContractResult) success | time:${systemTime()} | name:$name | action:$action")
    }

}
