package com.wsvita.framework.router.contract.full

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.google.gson.Gson
import com.wsvita.framework.router.contract.FullRouterContract
import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import java.io.Serializable

/**
 * -------------------------------------------------------------------------------------
 * 【JSON 自动解析复杂路由契约 (Json Complex Router Contract)】
 * -------------------------------------------------------------------------------------
 * [1. 设计定位]
 * - 支持多参数链式输入。
 * - 针对返回数据为 JSON 字符串的场景，实现自动“护理”：将 Json 映射为指定的 [T] 对象。
 * -------------------------------------------------------------------------------------
 */
open class JsonComplexRouterContract<T : Any> : FullRouterContract<T> {

    companion object {
        private const val TAG = "WSF_Router_JsonComplexContract=>"
        private val mGson: Gson = JsonUtil.getInstance().getGson();
    }

    /** 内部维护的参数包 */
    protected val mParams: Bundle = Bundle()

    /** 目标对象的类类型 */
    private var mTargetClass: Class<T>? = null

    constructor(action: String, resultKey: String, clazz: Class<T>) : super(action, resultKey) {
        this.mTargetClass = clazz
    }

    /**
     * ---------------------------------------------------------------------------------
     * 【输入端：链式添加 (Fluent API)】
     * ---------------------------------------------------------------------------------
     */

    open fun addInput(key: String, value: String?): JsonComplexRouterContract<T> {
        mParams.putString(key, value)
        return this
    }

    open fun addInput(key: String, value: Int): JsonComplexRouterContract<T> {
        mParams.putInt(key, value)
        return this
    }

    open fun addInput(key: String, value: Boolean): JsonComplexRouterContract<T> {
        mParams.putBoolean(key, value)
        return this
    }

    open fun addInput(key: String, value: Parcelable?): JsonComplexRouterContract<T> {
        mParams.putParcelable(key, value)
        return this
    }

    /**
     * ---------------------------------------------------------------------------------
     * 【逻辑护理：数据装箱与 JSON 脱壳】
     * ---------------------------------------------------------------------------------
     */

    override fun createInput(input: Bundle): Bundle? {
        val baseBundle = super.createInput(input) ?: Bundle()
        if (!mParams.isEmpty) {
            baseBundle.putAll(mParams)
        }
        return baseBundle
    }

    /**
     * 核心护理逻辑：将 String 类型的 Json 转为实体类
     */
    override fun convertToOutput(data: Intent): T? {
        // 1. 获取约定好的 JSON 字符串
        val json = data.getStringExtra(resultKey ?: "")
        if (json.isNullOrEmpty()) {
            SLog.e(TAG, "convertToOutput: 获取到的 JSON 为空, key=$resultKey")
            return null
        }

        return try {
            // 2. 使用 Gson 进行数据护理（类型转换）
            SLog.d(TAG, "convertToOutput: 开始解析 JSON 数据")
            mGson.fromJson(json, mTargetClass)
        } catch (e: Exception) {
            SLog.e(TAG, "convertToOutput: JSON 解析失败, error=${e.message}")
            null
        }
    }
}
