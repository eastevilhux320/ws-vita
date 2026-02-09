package com.wsvita.framework.router.contract

import android.content.Intent
import android.os.Bundle
import com.wsvita.framework.router.BaseComponentResult
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * -------------------------------------------------------------------------------------
 * 【通用路由契约基类 (Common Router Contract Base)】
 * -------------------------------------------------------------------------------------
 * [1. 作用说明]
 * 本类是所有“标准业务路由”的基类。它将 [BaseComponentResult] 的输入类型 [I]
 * 固定为 [Bundle]，从而允许调用方通过 Key-Value 对的形式自由传递跳转参数。
 *
 * [2. 设计意图]
 * - **输入规范化**：通过固定输入为 [Bundle]，使得 [SDKActivity] 可以通过统一的
 * [router(name, key, value)] 方法进行装箱操作，无需为每个页面定义专门的输入模型类。
 * - **输出泛型化**：通过泛型 [O]，强制子类实现具体的解析逻辑，确保回调给 [onSuccess]
 * 的数据是已经过“脱壳”处理的强类型业务对象。
 *
 * [3. 泛型说明]
 * @param O 预期返回的结果类型（如 String, Int, Boolean 或自定义的 Parcelable 对象）。
 *
 * [4. 参数说明]
 * @property action 目标 Activity 的 Intent Action（在 AndroidManifest 中定义）。
 * @property resultKey 约定好的返回数据键名。用于从目标页返回的 Intent 中提取业务数据 [O]。
 * -------------------------------------------------------------------------------------
 */
abstract class CommonRouterContract<O : Any> : BaseComponentResult<Bundle, O> {

    companion object{
        private const val TAG = "WSF_Router_CommonRouterContract=>";
    }

    protected var resultKey : String? = null;

    constructor(action: String,resultKey : String) : super(action) {
        this.resultKey = resultKey;
    }

    constructor(action: String) : super(action) {

    }

    /**
     * 实现 IComponentContract
     * 直接透传 Bundle，不再封装成单值
     */
    override fun createInput(input: Bundle): Bundle? {
        SLog.d(TAG,"createInput invoke,time:${systemTime()}")
        return input
    }

    /**
     * 子类需实现具体的脱壳逻辑
     */
    abstract override fun convertToOutput(data: Intent): O?
}
