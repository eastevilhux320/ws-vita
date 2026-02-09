package com.wsvita.framework.router.contract

import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.wsvita.framework.utils.SLog

/**
 * -------------------------------------------------------------------------------------
 * 【路由结果中转管理类 (Router Result Mediator)】
 * -------------------------------------------------------------------------------------
 *
 * **1. 职责定位**
 * 本类是在组件化跳转流程中，衔接“系统原始数据”与“业务逻辑对象”的中转站。
 * 它负责管理从目标页面回传的 [Bundle] 容器，并提供一套标准化的“数据护理”工具方法。
 *
 * **2. 解决痛点**
 * - **解耦 Key 值**：UI 层不需要直接操作 Intent 或 Bundle，降低了由于 Key 字符串拼写错误导致的 Bug。
 * - **统一转换逻辑**：内置 JSON 自动化转换，将数据清洗和解析逻辑收拢在契约体系内，保证业务回调拿到的是干净的实体类。
 * - **安全性控制**：内部通过 try-catch 消化解析异常，防止非法的 JSON 字符串导致主进程崩溃。
 *
 * **3. 使用示例**
 * ```kotlin
 * // 在 RouterConfigurator 的 onSuccess 回调中使用
 * routerConfigurator.register("get_user_info", contract) { result ->
 * // 1. 提取普通类型
 * val traceId = result.getString("wsui_trace_id")
 * // 2. 提取并护理 JSON 对象
 * val user = result.getJsonObject("wsui_user_data", UserEntity::class.java)
 * // 3. 业务逻辑处理
 * if (user != null) { ... }
 * }
 * ```
 *
 * **4. 注意事项**
 * - **性能开销**：由于内部集成了 [Gson]，频繁地在大循环中调用 [getJsonObject] 可能会产生反射开销。
 * - **空安全**：所有 get 方法均可能返回 null，业务层必须进行非空或有效性校验。
 * - **规范建议**：建议 Key 值统一使用 [wsui_] 前缀，以符合项目自定义属性规范。
 *
 * @author Administrator
 * @version 1.0
 * @since 2026-01-19
 */
class ComplexResult {

    companion object {
        private const val TAG = "WSF_Router_ComplexResult=>"
    }

    /** 原始回传数据容器 */
    private val mData: Bundle

    /** 数据护理工具：Gson 实例 */
    private val mGson: Gson = Gson()

    /**
     * 显式次级构造函数。
     * 遵循框架禁止主构造函数的规范，明确初始化流程。
     *
     * @param bundle 目标 Activity 通过 setResult 返回的原始 Bundle 数据包。
     */
    constructor(bundle: Bundle) {
        this.mData = bundle
    }

    /**
     * 获取原始数据容器。
     * 仅在 [ComplexResult] 提供的工具方法无法满足需求时使用。
     *
     * @return 包含所有回传键值对的 [Bundle] 对象。
     */
    fun getRawBundle(): Bundle = mData

    /**
     * 获取字符串类型数据。
     *
     * @param key 约定好的键名，建议遵循 wsui 前缀规范。
     * @return 对应的字符串值，若不存在则返回 null。
     */
    fun getString(key: String): String? = mData.getString(key)

    /**
     * 获取整型类型数据。
     *
     * @param key 约定好的键名。
     * @param default 缺省值，当 Key 不存在或类型不匹配时返回。
     * @return 对应的整型值。
     */
    fun getInt(key: String, default: Int = 0): Int = mData.getInt(key, default)

    /**
     * 获取布尔类型数据。
     *
     * @param key 约定好的键名。
     * @param default 缺省值。
     * @return 对应的布尔值。
     */
    fun getBoolean(key: String, default: Boolean = false): Boolean = mData.getBoolean(key, default)

    /**
     * 核心护理方法：JSON 字符串自动化对象映射。
     *
     * 该方法从 [mData] 中提取指定 [key] 的 JSON 字符串，并尝试利用 [Gson] 映射为指定的 [clazz] 实例。
     * 这是实现“多 Key-Value 复杂回传”时最核心的脱壳工具。
     *
     * @param T 目标业务对象类型。
     * @param key JSON 字符串对应的键名。
     * @param clazz 目标业务对象的类类型定义。
     * @return 护理后的业务实体对象。若 JSON 为空、格式非法或转换失败，则返回 null 并记录错误日志。
     *
     * @throws JsonSyntaxException 当 JSON 字符串格式与 Class 结构严重不符时可能抛出，已内部捕获。
     */
    fun <T> getJsonObject(key: String, clazz: Class<T>): T? {
        val json = mData.getString(key)
        if (json.isNullOrEmpty()) {
            SLog.w(TAG, "getJsonObject: 指定的 Key [$key] 对应内容为空")
            return null
        }

        return try {
            SLog.d(TAG, "getJsonObject: 开始对 Key [$key] 进行数据护理...")
            mGson.fromJson(json, clazz)
        } catch (e: Exception) {
            // 此处捕获所有解析异常，确保组件化通讯的鲁棒性
            SLog.e(TAG, "getJsonObject: 数据护理失败! Key=$key, Error=${e.message}")
            null
        }
    }
}
