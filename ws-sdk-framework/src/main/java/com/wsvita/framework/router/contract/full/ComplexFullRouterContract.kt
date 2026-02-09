package com.wsvita.framework.router.contract.full

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.wsvita.framework.router.contract.FullRouterContract
import com.wsvita.framework.utils.SLog
import java.io.Serializable

/**
 * -------------------------------------------------------------------------------------
 * 【全能型复杂业务路由契约 (Open Complex Router Contract)】
 * -------------------------------------------------------------------------------------
 * [1. 设计定位]
 * 本类设计为 open 类，旨在作为所有复杂多参数跳转的通用基类或直接实现类。
 * 当业务方需要自行解析返回的多个数据字段时，可直接使用此类。
 *
 * [2. 核心功能]
 * - **Open 可继承**：支持子类扩展，如 PlateLicenseFullContract。
 * - **链式注入**：支持 addInput(...) 连续添加不同类型的跳转参数。
 * - **全量回传**：输出类型固定为 [Bundle]，确保目标页返回的所有数据都能被调用方获取。
 * -------------------------------------------------------------------------------------
 */
open class ComplexFullRouterContract : FullRouterContract<Bundle> {

    companion object {
        private const val TAG = "WSF_Router_ComplexContract=>"
    }

    /** 内部维护的参数包，用于链式添加 */
    protected val mParams: Bundle = Bundle()

    /**
     * 显式次级构造函数
     * @param action 路由目标标识
     */
    constructor(action: String) : super(action)

    /**
     * 显式次级构造函数（支持指定默认结果键）
     */
    constructor(action: String, resultKey: String) : super(action, resultKey)

    /**
     * ---------------------------------------------------------------------------------
     * 【链式参数添加方法组 (Fluent API)】
     * ---------------------------------------------------------------------------------
     */

    open fun addInput(key: String, value: String?): ComplexFullRouterContract {
        mParams.putString(key, value)
        return this
    }

    open fun addInput(key: String, value: Int): ComplexFullRouterContract {
        mParams.putInt(key, value)
        return this
    }

    open fun addInput(key: String, value: Boolean): ComplexFullRouterContract {
        mParams.putBoolean(key, value)
        return this
    }

    open fun addInput(key: String, value: Parcelable?): ComplexFullRouterContract {
        mParams.putParcelable(key, value)
        return this
    }

    open fun addInput(key: String, value: Serializable?): ComplexFullRouterContract {
        mParams.putSerializable(key, value)
        return this
    }

    /**
     * ---------------------------------------------------------------------------------
     * 【数据装箱逻辑】
     * ---------------------------------------------------------------------------------
     */
    override fun createInput(input: Bundle): Bundle? {
        // 调用 CommonRouterContract 的基础逻辑获取初始 Bundle
        val baseBundle = super.createInput(input) ?: Bundle()

        // 合并链式添加的参数
        if (!mParams.isEmpty) {
            baseBundle.putAll(mParams)
            SLog.i(TAG, "createInput: 链式参数合并完成, 当前总参数量=${baseBundle.size()}")
        }
        return baseBundle
    }

    /**
     * ---------------------------------------------------------------------------------
     * 【数据脱壳逻辑：全量提取】
     * ---------------------------------------------------------------------------------
     * [注意]：此处返回 Intent 的全量 Extras，由调用方在 onSuccess 回调中自行根据 Key 取值。
     */
    override fun convertToOutput(data: Intent): Bundle? {
        SLog.d(TAG, "convertToOutput: 提取全量返回数据 Bundle")
        return data.extras
    }
}
