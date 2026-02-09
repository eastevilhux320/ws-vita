package com.wsvita.framework.commons

import android.app.Application
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.entity.ErrorHolder
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.entity.SuccessHolder
import com.wsvita.framework.entity.VError
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SDK 级业务 ViewModel 基类
 *
 * 集成了协程管理、UI事件分发（IUIEvent）以及自动化错误处理（VError）。
 */
abstract class SDKViewModel(application: Application) : BaseViewModel(application) {

    private val _uiEvent = MutableSharedFlow<IUIEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private val _baseConfig = MutableStateFlow<FrameConfig?>(null)
    val baseConfig: StateFlow<FrameConfig?> = _baseConfig.asStateFlow()

    /**
     * 成功数据透传处理
     */
    private val _success = MutableLiveData<SuccessHolder>();
    val success : LiveData<SuccessHolder>
        get() = _success;

    /**
     * 失败数据透传处理
     */
    private val _error = MutableLiveData<ErrorHolder>();
    val error : LiveData<ErrorHolder>
        get() = _error;

    /**
     * 初始化数据模型，子类可重写
     * 通常在 Activity/Fragment 的 onViewCreated 中调用
     */
    open fun initModel() {
        SLog.d(TAG, "initModel invoke")
        _baseConfig.value = FrameConfigure.instance.getConfig();
    }

    // --- 事件分发 ---

    protected fun emitEvent(event: IUIEvent) {
        _uiEvent.tryEmit(event)
    }

    // --- 快捷 UI 操作 ---

    /**
     * **分发显示加载框指令**
     * <p>
     * <b>作用：</b> 触发 UI 层展示全局阻塞式加载框。<br>
     * <b>机制：</b> 该方法通过 {@code emitEvent} 发送 {@link IUIEvent.ShowLoading} 信号，
     * 由绑定的生命周期组件（如 {@code SDKActivity}）进行拦截与弹窗渲染。
     * </p>
     * * create by Eastevil at 2025/12/30 14:12
     * @param msg 加载提示文案。传 null 则由 UI 层展示默认配置文案。
     */
    protected open fun showLoading(msg: String? = null) = emitEvent(IUIEvent.ShowLoading(msg))

    /**
     * **分发隐藏加载框指令**
     * <p>
     * <b>作用：</b> 通知 UI 层关闭当前正在展示的加载框。<br>
     * <b>注意：</b> 该方法发送 {@link IUIEvent.HideLoading} 信号，属于 UI 态重置指令。
     * </p>
     * * create by Eastevil at 2025/12/30 14:15
     */
    protected open fun hideLoading(){
        emitEvent(IUIEvent.HideLoading)
    }


    protected fun toast(msg: String) = emitEvent(IUIEvent.ShowToast(msg))

    protected fun toast(@StringRes resId: Int) = emitEvent(IUIEvent.ShowResToast(resId))

    protected fun getString(@StringRes resId: Int): String {
        return getApplication<Application>().getString(resId);
    }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return getApplication<Application>().getString(resId, *formatArgs)
    }

    /**
     * 获取当前业务配置中的主题色。
     * * <p>
     * 该方法是同步获取的快捷方式。如果 [baseConfig] 尚未初始化，
     * 将返回默认的白色 [Color.WHITE]。
     * </p>
     *
     * @return 颜色值（ARGB）
     * @author Eastevil (或 Administrator)
     * @date 2025/12/30
     */
    open fun themeColor(): Int {
        return baseConfig.value?.mainThemeColor?:Color.WHITE;
    }

    /**
     * 获取提交/确定按钮的颜色配置。
     * * **业务逻辑说明：**
     * 1. 优先从当前 [baseConfig] 实例中读取业务方配置的颜色。
     * 2. 若配置对象为空或未设置该颜色，则返回默认颜色 [Color.BLACK]。
     * * @return 颜色值（Int 类型），通常用于设置 Button 的 BackgroundTint 或文本颜色。
     */
    open fun submitColor(): Int {
        return baseConfig.value?.submitColor ?: Color.BLACK
    }

    /**
     * 获取取消/重置按钮的颜色配置。
     * * **业务逻辑说明：**
     * 1. 优先从当前 [baseConfig] 实例中读取业务方配置的颜色。
     * 2. 若配置对象为空或未设置该颜色，则返回默认颜色 [Color.WHITE]。
     * * @return 颜色值（Int 类型），通常用于设置次要操作按钮的 UI 样式。
     */
    open fun cancelColor(): Int {
        return baseConfig.value?.cancelColor ?: Color.WHITE
    }

    /**
    * **触发成功状态分发**
    * * **场景：**
    * 当业务逻辑顺利执行完毕，且不需要携带特定的业务数据（Data）返回给 View 时使用。
    * * **效果：**
    * View 层监听 [success] 的观察者将收到一个包含默认成功码 [SuccessHolder.DEFAULT_SUCCESS] 的实例。
    * * **注意：**
    * 此流由 [SDKViewModel] 定义，专门用于 Model 层在处理完特定业务逻辑后<b>手动发送订阅事件</b>。
    * 它不会被框架原有的底层请求方法自动触发，需开发者根据业务结果主动调用。
    */
    protected fun success(){
        val s = SuccessHolder.success()
        _success.value = s
    }

    /**
     * **触发带自定义信息的成功状态分发**
     * 允许 Model 层在操作成功时指定特定的业务码或文案，例如提示“配置已同步”。
     * @param code 自定义业务状态码
     * @param msg  成功提示文案
     */
    protected fun success(code: Int, msg: String? = null) {
        val s = SuccessHolder.success(code, msg)
        _success.value = s
    }

    /**
     * **触发默认错误状态分发**
     * 发送一个包含 [ErrorHolder.DEFAULT_ERROR] 的错误信号。
     * 适用于不确定具体原因或通用的操作失败场景。
     */
    protected fun error() {
        _error.value = ErrorHolder.error()
    }

    /**
     * **触发带自定义信息的错误分发**
     * * **场景：** 需要向用户展示具体的错误原因（如“余额不足”、“配置冲突”等）。
     * @param code 具体的错误业务码
     * @param msg  展示给用户的错误提示文案。若传 null，View 层通常展示默认错误提示。
     */
    protected fun error(code: Int, msg: String? = null) {
        _error.value = ErrorHolder.error(code, msg)
    }

    /**
     * **通过异常对象分发错误**
     * <p>
     * 方便在 catch 块中直接调用，将 Throwable 转换为 View 层可识别的 ErrorHolder。
     * </p>
     * @param e 捕获的异常实例
     */
    protected fun error(e: Throwable) {
        // 如果是协程取消异常，通常不作为错误分发给 UI
        if (e is CancellationException) return
        _error.value = ErrorHolder.error(ErrorHolder.DEFAULT_ERROR, e.message)
    }

    /**
     * [简易延时任务封装]
     * 作用：在 ViewModel 中快速启动一个随生命周期管理的延时协程。
     * @param timeMillis 延时的毫秒数（例如：1000 代表 1 秒）
     * @param dispatcher 指定协程运行的调度器，默认为 [Dispatchers.Main]（主线程），
     *          如果 block 中涉及大量耗时计算，建议传入 [Dispatchers.Default]。
     * @param block 延时结束后要执行的代码块，支持挂起操作。
     * @return 返回一个 [Job] 对象，
     * 调用者可以在需要时手动通过 job.cancel() 取消该任务。
     */
    fun delay(
        timeMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: suspend () -> Unit
    ): Job = viewModelScope.launch(dispatcher) {
        // 1. 协程挂起指定时间（非阻塞）
        delay(timeMillis)
        // 2. 时间到后执行业务逻辑
        block()
    }

    companion object {
        private const val TAG = "WSVita_SDKViewModel==>"
    }
}
