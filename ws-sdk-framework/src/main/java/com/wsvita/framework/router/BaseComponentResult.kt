package com.wsvita.framework.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * -------------------------------------------------------------------------------------
 * 【组件化路由协议最底层基类 (Base Component Router Contract)】
 * -------------------------------------------------------------------------------------
 * [1. 作用说明]
 * 本类是所有路由协议的终极基类，继承自 Android Jetpack 的 [ActivityResultContract]。
 * 它定义了跨组件页面跳转时的“通讯契约”，即：跳转时需要传什么（Input），返回时能拿到什么（Output）。
 *
 * [2. 设计意图]
 * - **标准化跳转**：通过构造函数传入 [action]，强制所有页面跳转基于 Action 机制，实现模块间物理隔离。
 * - **解耦解析逻辑**：将解析 Intent 数据的繁琐逻辑从 Activity 中抽离到具体的契约类中，实现职责单一化。
 * - **类型安全**：利用泛型 [I] 和 [O]，在编译阶段就确定了输入和输出的数据类型，避免运行时类型转换异常。
 *
 * [3. 泛型说明]
 * @param I 输入类型 (Input)：跳转到目标页面时携带的数据类型（在 CommonRouterContract 中被固定为 Bundle）。
 * @param O 输出类型 (Output)：目标页面关闭后，回传给调用方的“脱壳”后的业务数据类型。
 *
 * [4. 核心流程]
 * 1. [createIntent]：系统调用，负责将 Action 和 [createInput] 生成的数据包装成最终的 Intent。
 * 2. [parseResult]：系统调用，当目标页关闭时触发，负责将返回的 Intent 转化为业务对象 [O]。
 * -------------------------------------------------------------------------------------
 */
abstract class BaseComponentResult<I, O> : ActivityResultContract<I, ComponentResponse<O>>, IComponentContract<I, O> {

    companion object{
        private const val TAG = "WSF_Router_BaseComponentResult=>";
    }

    private var action : String;

    constructor(action: String){
        this.action = action;
    }

    override fun createIntent(context: Context, input: I): Intent {
        SLog.d(TAG,"createIntent start,time:${systemTime()}")
        // 1. 显式创建隐式 Intent
        val intent = Intent(action)
        SLog.i(TAG,"createIntent_create intent,action:${action}");
        // 2. 调用 IComponentContract 接口定义的实现方法获取 Bundle
        val bundle = createInput(input)
        // 3. 基础判空逻辑，替代 ?.let 链式调用
        if (bundle != null) {
            intent.putExtras(bundle)
            SLog.i(TAG,"createIntent_putExtras");
        }
        // 4. 返回构造完成的 Intent
        SLog.i(TAG,"createIntent_return intent");
        SLog.d(TAG,"createIntent success,time:${systemTime()}")
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ComponentResponse<O> {
        SLog.d(TAG,"parseResult invoke,time:${systemTime()},resultCode:${resultCode}");
        return when (resultCode) {
            Activity.RESULT_OK -> {
                val out = intent?.let { convertToOutput(it) }
                if (out != null) {
                    SLog.d(TAG,"parseResult,ComponentResponse Success")
                    ComponentResponse.Success(action,out)
                }else {
                    SLog.d(TAG,"parseResult,ComponentResponse Failure,data is null");
                    ComponentResponse.Failure(action,resultCode, "Data null")
                }
            }
            Activity.RESULT_CANCELED -> {
                SLog.d(TAG,"parseResult,RESULT_CANCELED");
                ComponentResponse.Canceled
            }
            else -> {
                SLog.d(TAG,"parseResult,unknow resultCode:${resultCode}");
                ComponentResponse.Failure(action,resultCode)
            }
        }
    }

    fun getAction(): String {
        return action;
    }
}
