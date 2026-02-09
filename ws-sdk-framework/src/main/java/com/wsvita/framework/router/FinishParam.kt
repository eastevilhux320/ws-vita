package com.wsvita.framework.router

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * 【组件化】返回参数封装类
 * 作用：规范化多参数回传，提供明确的基础类型处理方法。
 * 设计：静态工厂模式，显式私有构造，支持链式调用。
 */
class FinishParam {

    private val dataMap: MutableMap<String, Any?>

    companion object {
        /**
         * 显式创建 FinishParam 实例
         */
        fun create(): FinishParam {
            val f = FinishParam()
            return f
        }
    }

    /**
     * 显式私有构造方法
     * 遵循：禁止使用主函数构造原则
     */
    private constructor() {
        dataMap = mutableMapOf()
    }

    // --- 基础数据类型处理方法 ---

    fun putString(key: String, value: String?): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putInt(key: String, value: Int): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putBoolean(key: String, value: Boolean): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putLong(key: String, value: Long): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putDouble(key: String, value: Double): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putFloat(key: String, value: Float): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putParcelable(key: String, value: Parcelable?): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putSerializable(key: String, value: Serializable?): FinishParam {
        dataMap[key] = value
        return this
    }

    fun putBundle(key: String, value: Bundle?): FinishParam {
        dataMap[key] = value
        return this
    }

    /**
     * 通用存入方法（作为兜底或快速调用）
     */
    fun put(key: String, value: Any?): FinishParam {
        dataMap[key] = value
        return this
    }

    /**
     * 提供给 BaseActivity 遍历读取
     */
    fun getMap(): Map<String, Any?> {
        return dataMap
    }
}
