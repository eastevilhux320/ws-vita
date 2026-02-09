package com.wsvita.core.entity

import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.entity.VError

/**
 * ### 业务请求/指令生命周期事件流 (RequestEvent)
 *
 * **核心描述：**
 * 本类作为组件化架构中，Model 层与 View 层（或父子组件间）通信的强类型契约。
 * 旨在通过 [ModelRequestConfig] 透传完整的业务上下文，实现 UI 状态与业务执行周期的深度解耦与同步。
 *
 * **设计原则：**
 * 1. **上下文完备性**：所有事件分支必须持有 [ModelRequestConfig]，确保消费端能追溯请求来源、标题及 UI 表现预设。
 * 2. **执行序保障**：通过细化 [requestEnd] 与 [End] 状态，规避异步回调中常见的“数据到达早于加载框隐藏”的 UI 竞态问题。
 * 3. **精准响应**：消费端通过匹配 [ModelRequestConfig.requestCode] 实现单页面多组件的局部精确刷新。
 *
 * **注意事项：**
 * - 消费端在收集此流时，应针对不同的分支执行特定的 UI 幂等操作。
 * - [End] 是所有流程的终点，务必在此分支进行最后的资源释放或状态重置。
 *
 * @author Eastevil
 * @date 2025/12/30
 */
sealed class RequestEvent {

    /**
     * **事件：请求/任务开始 (Begin)**
     *
     * **业务职责：**
     * 标识一个异步原子任务正式进入执行栈。
     *
     * **消费建议：**
     * 1. 检查 [config.requestCode]，判断是否需要对特定组件加锁。
     * 2. 根据 [config.showType] 决定是否开启全局 Loading 或局部 ProgressBar。
     * 3. 建议此时禁用当前业务对应的交互按钮，防止并发点击。
     *
     * @property config 本次请求的完整配置上下文。
     */
    data class Begin(val config: ModelRequestConfig) : RequestEvent()

    /**
     * **事件：请求逻辑完成 (requestEnd)**
     *
     * **触发时机：**
     * 在数据层 [block] 调用结束、结果封装完成，但尚未 [return] 交付给调用者之前触发。
     *
     * **业务职责：**
     * 用于在 View 层接收到业务数据结果前，优先恢复 UI 交互状态。
     *
     * **注意：**
     * 这是解决 UI 刷新顺序问题的关键信号。收到此信号时，应优先执行隐藏 Loading 或恢复按钮点击态。
     *
     * @property config 本次请求的完整配置上下文。
     */
    data class requestEnd(val config: ModelRequestConfig) : RequestEvent()

    /**
     * **事件：生命周期总终点 (End)**
     *
     * **触发时机：**
     * 位于 ViewModel 内部请求模板的 `finally` 块中。
     *
     * **业务职责：**
     * 作为整个请求流的绝对终点，用于执行最后的兜底清理。
     *
     * **注意：**
     * 无论请求成功、失败、逻辑拦截或协程取消，[End] 信号必定会被触发。
     * 如果 UI 层在 [requestEnd] 遗漏了清理逻辑，应在此处强制恢复。
     *
     * @property config 本次请求的完整配置上下文。
     */
    data class End(val config: ModelRequestConfig) : RequestEvent()

    /**
     * **事件：业务/系统异常 (Error)**
     *
     * **业务职责：**
     * 标识请求流程未按预期成功执行。
     *
     * **消费建议：**
     * 1. 优先将 [error] 交付给通用的 UI 错误分发器。
     * 2. 利用 [config.showType] 判断是弹出 Toast、对话框，还是渲染缺省页。
     * 3. 处理 Error 后，消费端仍会收到 [End] 信号，建议将 UI 恢复逻辑保持在 End 中处理。
     *
     * @property config 本次请求的配置，用于辅助定位错误发生的业务语境。
     * @property error 封装后的错误实体，包含标准化错误码、提示文案及 UI 展示建议。
     */
    data class Error(val config: ModelRequestConfig, val error: VError) : RequestEvent()

    /**
     * **事件：空数据响应 (Empty)**
     *
     * **触发时机：**
     * 后端接口返回了约定的成功状态码（如 200），但业务数据体 [data] 为 null 或集合类型为空。
     *
     * **业务职责：**
     * 标识请求流程在通信层面已完成且逻辑合法，但当前业务语境下无可用内容。
     * * **消费建议：**
     * 1. 结合 [config.requestCode] 判断该空状态是属于整个页面还是局部组件。
     * 2. 建议根据此事件切换 UI 至“缺省页”或“空列表”状态。
     * 3. 仍会触发随后的 [requestEnd] 与 [End] 信号，UI 恢复逻辑可统一处理。
     *
     * @property config 本次请求的完整配置上下文。
     * @property code 业务层定义的具体空状态码（例如：特定筛选条件下无数据）。
     * @property msg 后端或本地封装的提示信息。
     */
    data class Empty(val config : ModelRequestConfig,val code : Int,val msg : String?) : RequestEvent();

    /**
     * **事件：业务扩展动作 (Action)**
     *
     * **业务职责：**
     * 用于处理标准请求生命周期之外的、具备特定业务语义的指令分发。
     *
     * **场景举例：**
     * - 请求成功后触发特定的页面导航（如：支付成功后跳转结果页）。
     * - 触发跨组件的通讯指令。
     *
     * @property config 本次请求的来源上下文。
     * @property actionCode 具体的动作指令识别码，建议在业务层统一定义。
     * @property extra 随指令携带的附属数据载体，建议按需转型。
     */
    data class Action(
        val config: ModelRequestConfig,
        val actionCode: Int,
        val extra: Any? = null
    ) : RequestEvent()

}
