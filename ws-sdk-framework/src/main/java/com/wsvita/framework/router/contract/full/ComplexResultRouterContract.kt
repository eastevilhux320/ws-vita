package com.wsvita.framework.router.contract.full

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.wsvita.framework.router.contract.ComplexResult
import com.wsvita.framework.router.contract.FullRouterContract
import com.wsvita.framework.utils.SLog
import java.io.Serializable

/**
 * -------------------------------------------------------------------------------------
 * 【多参数-结果中转全双工契约 (Complex Result Router Contract)】
 * -------------------------------------------------------------------------------------
 *
 * **1. 设计定位**
 * 本类是组件化架构中处理“重业务跳转”的核心契约类。它支持在跳转前动态注入多个不同类型的参数，
 * 并在目标页面关闭后，通过 [ComplexResult] 中转对象统一管理回传的多个键值对。
 *
 * **2. 核心特性**
 * - **Open 可扩展性**：设计为 open 类，既可直接用于通用跳转，也可由具体业务契约（如 PlateLicenseContract）继承以进一步封装。
 * - **链式输入 (Fluent API)**：通过 [addInput] 方法实现参数的连贯添加，替代繁琐的 Bundle 手动操作。
 * - **结果中转护理**：输出类型固定为 [ComplexResult]，将原始 Intent 脱壳逻辑封装在管理对象中，确保业务层代码的整洁与安全。
 *
 * **3. 规范要求**
 * - **构造规范**：严格遵循框架指令，禁止使用主构造函数，必须通过显式次级构造函数 [constructor] 初始化 [cite: 2026-01-16]。
 * - **命名规范**：建议所有参数 Key 值遵循项目定义的前缀规范（如使用 `wsui_` 前缀） [cite: 2026-01-10]。
 *
 * **4. 使用示例**
 * ```kotlin
 * // 1. 创建并配置契约
 * val contract = ComplexResultRouterContract("ACTION_BIZ_USER_PICKER")
 * .addInput("wsui_limit_count", 5)
 * .addInput("wsui_title", "选择联系人")
 *
 * // 2. 注册并处理结果
 * routerConfigurator.register("user_picker", contract) { result ->
 * // 此时 result 为 ComplexResult 类型
 * val userJson = result.getString("wsui_picked_user")
 * val isBatch = result.getBoolean("wsui_is_batch")
 * }
 * ```
 *
 * @author Administrator
 * @version 1.0
 * @since 2026-01-19
 * @see ComplexResult
 * @see FullRouterContract
 */
open class ComplexResultRouterContract : FullRouterContract<ComplexResult> {
    companion object {
        private const val TAG = "WSF_Router_ComplexResultRouterContract=>"
    }

    /** 内部维护的输入参数容器，用于暂存链式添加的数据 */
    protected val mInputParams: Bundle = Bundle()

    /**
     * 显式次级构造函数。
     * 明确 Action 标识，不指定默认 resultKey（多 Key 场景下建议直接操作 ComplexResult）。
     *
     * @param action 目标 Activity 的 Intent Action 标识。
     */
    constructor(action: String) : super(action){

    }

    /**
     * 显式次级构造函数（带默认结果键）。
     * 兼容需要使用单个默认 Key 提取数据的场景。
     *
     * @param action 目标 Activity 的 Intent Action 标识。
     * @param resultKey 约定的主要返回数据键名。
     */
    constructor(action: String, resultKey: String) : super(action, resultKey)

    /**
     * ---------------------------------------------------------------------------------
     * 【链式输入配置 (Input Configuration)】
     * ---------------------------------------------------------------------------------
     */

    /**
     * 添加字符串类型输入参数。
     * @return 返回当前契约对象，支持链式调用。
     */
    open fun addInput(key: String, value: String?): ComplexResultRouterContract {
        mInputParams.putString(key, value)
        return this
    }

    /**
     * 添加整型类型输入参数。
     * @return 返回当前契约对象，支持链式调用。
     */
    open fun addInput(key: String, value: Int): ComplexResultRouterContract {
        mInputParams.putInt(key, value)
        return this
    }

    /**
     * 添加布尔类型输入参数。
     * @return 返回当前契约对象，支持链式调用。
     */
    open fun addInput(key: String, value: Boolean): ComplexResultRouterContract {
        mInputParams.putBoolean(key, value)
        return this
    }

    /**
     * 添加可序列化对象输入参数。
     * @return 返回当前契约对象，支持链式调用。
     */
    open fun addInput(key: String, value: Parcelable?): ComplexResultRouterContract {
        mInputParams.putParcelable(key, value)
        return this
    }

    /**
     * ---------------------------------------------------------------------------------
     * 【框架核心逻辑实现】
     * ---------------------------------------------------------------------------------
     */

    /**
     * 覆盖基类装箱逻辑。
     * 将通过 [addInput] 收集的所有业务参数合并到最终发送的参数包中。
     *
     * @param input 框架层（如 SDKActivity）传入的初始化 Bundle。
     * @return 最终合并后的 Bundle，若无参数则返回 Bundle.EMPTY。
     */
    override fun createInput(input: Bundle): Bundle? {
        // 调用 Common 层的基本处理逻辑
        val baseBundle = super.createInput(input) ?: Bundle()

        // 合并本类特有的链式参数
        if (!mInputParams.isEmpty) {
            baseBundle.putAll(mInputParams)
        }
        return baseBundle
    }

    /**
     * 覆盖基类脱壳逻辑。
     * 该方法不再尝试将 Intent 转换为单一对象，而是将其 Extras 全部提取并包装进
     * [ComplexResult] 中进行中转护理。
     *
     * @param data 目标页面关闭时回传的原始 Intent。
     * @return 封装后的 [ComplexResult] 管理对象。
     */
    override fun convertToOutput(data: Intent): ComplexResult? {
        val extras = data.extras ?: Bundle()
        val action = getAction();
        SLog.d(TAG,"convertToOutput_action:${action}");
        // 核心步骤：将系统原始容器转换为业务护理管理对象
        return ComplexResult(extras)
    }
}
