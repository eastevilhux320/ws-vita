package com.wsvita.core.common

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import com.wsvita.core.R
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.entity.RequestEvent
import com.wsvita.core.local.manager.MediaPickerManager
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.core.media.OnMediaResultListener
import com.wsvita.framework.commons.SDKFragment
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.entity.VError
import com.wsvita.framework.router.contract.ComplexResult
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.utils.VToast
import com.wsvita.ui.dialog.ConfirmDialog
import com.wsvita.ui.dialog.ConventionalDialog
import com.wsvita.ui.dialog.TipsDialog
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * App 级业务 Fragment 基类。
 * <p>
 * 该类扩展了 {@link SDKFragment}，核心职责是承载业务层的 Fragment 级交互逻辑。
 * 重点实现了基于 {@link RequestEvent} 的全生命周期调度机制，支持与 Activity 保持一致的错误展示路由。
 * </p>
 * * create by Eastevil at 2025/12/30 14:40
 * @author Eastevil
 * @param D DataBinding 泛型类型
 * @param V AppViewModel 泛型类型
 */
abstract class AppFragment<D : ViewDataBinding, V : AppViewModel> : SDKFragment<D, V>(),EasyPermissions.PermissionCallbacks {

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        viewModel.setFragmentId(getId());
        if(needMediaPicker()){
            // 修正参数：
            MediaPickerManager.instance.register(requireActivity(), this, onMediaResultListener);
        }
    }

    /**
     * 业务层：添加数据动态变化监听。
     * <p>
     * <b>作用：</b> 订阅并分发 {@link AppViewModel#requestEvent} 流，实现请求过程的自动化处理。<br>
     * <b>调用：</b> 由框架在 onViewCreated 期间自动触发。<br>
     * <b>注意：</b> 必须绑定 {@code viewLifecycleOwner}，确保在视图销毁后自动取消订阅，防止内存泄漏。
     * </p>
     * * create by Eastevil at 2025/12/30 14:41
     * @author Eastevil
     */
    override fun addObserve() {
        super.addObserve()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestEvent.collect { event ->
                    dispatchBusinessEvent(event)
                }
            }
        }
    }


    override fun onViewClick(view: View) {
        SLog.w(TAG,"onViewClick");
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 1. 处理来自 NavController 的跳转参数
        // 这些参数是通过 AppContainerActivity.navigate(resId, bundle) 传进来的
        if (arguments != null) {
            initFromArguments(arguments)
        }
        // 2. 处理来自系统重建的保存状态
        // 当 Activity 被系统回收（如后台久置）再返回时，数据会从这里恢复
        if (savedInstanceState != null) {
            initFromArguments(savedInstanceState)
        }
    }

    /**
     * 业务事件内部分发器。
     * <p>
     * <b>作用：</b> 将原始请求信号转换为具体的 UI 调度指令。<br>
     * <b>执行序：</b> Begin(标记开始) -> requestEnd(逻辑结束，优先恢复交互) -> End(最终兜底)。
     * </p>
     * * create by Eastevil at 2025/12/30 14:42
     * @author Eastevil
     * @param event 接收到的请求事件实体
     */
    private fun dispatchBusinessEvent(event: RequestEvent) {
        when (event) {
            is RequestEvent.Begin -> {
                onRequestStageChanged(event.config, active = true)
            }
            is RequestEvent.requestEnd -> {
                onRequestStageChanged(event.config, active = false)
            }
            is RequestEvent.Error -> {
                handleVError(event.config, event.error)
            }
            is RequestEvent.End -> {
                onRequestStageChanged(event.config, active = false)
            }
            is RequestEvent.Action -> {
                onHandleAction(event.config, event.actionCode, event.extra)
            }
            is RequestEvent.Empty->{
                onRequestEmpty(event.config,event.code,event.msg);
            }
        }
    }

    /**
     * 请求执行阶段变更回调。
     * <p>
     * <b>作用：</b> 用于同步特定业务请求的执行状态。通常用于控制非全局 Loading 的局部 UI 交互。<br>
     * <b>调用：</b> 在请求开始或逻辑结束时触发。<br>
     * <b>注意：</b> 子类重写时应判断 {@link ModelRequestConfig#requestCode} 以实现精准刷新。
     * </p>
     * * create by Eastevil at 2025/12/30 14:43
     * @author Eastevil
     * @param config 本次请求的上下文配置
     * @param active true 表示业务执行中，false 表示逻辑结束
     */
    protected open fun onRequestStageChanged(config: ModelRequestConfig, active: Boolean) {
        SLog.d(TAG, "onRequestStageChanged -> Code: ${config.requestCode}, Active: $active")
    }

    /**
     * 统一处理业务错误的分流展示。
     * <p>
     * <b>作用：</b> 视觉路由中心。结合配置意图驱动最终的视觉反馈。<br>
     * <b>注意：</b> 分发前会先询问 {@link #onInterceptError}，确保特定业务逻辑具有最高优先级。
     * </p>
     * * create by Eastevil at 2025/12/30 14:44
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun handleVError(config: ModelRequestConfig, error: VError) {
        if (onInterceptError(config, error)) {
            SLog.d(TAG, "Fragment Error intercepted, requestCode: ${config.requestCode}")
            return
        }

        when (config.showType) {
            ModelRequestConfig.SHOW_TYPE_TOAST -> showToast(config, error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_COFMIRT -> showConfirmDialog(config, error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_CONVENTIONAL -> showConventionalDialog(config, error)
            ModelRequestConfig.SHOW_TYPE_VIEW -> onShowErrorStateView(config, error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_TIPS -> showTipsDialog(config, error)
        }
    }

    /**
     * 错误拦截器钩子方法。
     * <p>
     * <b>作用：</b> 允许子类针对特定接口或错误码执行非 UI 展示类操作（如：跳转、重试）。
     * </p>
     * * create by Eastevil at 2025/12/30 14:45
     * @author Eastevil
     * @param config 请求配置
     * @param error 错误详情
     * @return boolean 返回 true 表示拦截成功
     */
    protected open fun onInterceptError(config: ModelRequestConfig, error: VError): Boolean = false

    /**
     * 执行 Fragment 层的统一 Toast 展示。
     * <p>
     * <b>作用：</b> 在 Fragment 上下文内展示非阻断性的短消息。<br>
     * <b>注意：</b> 默认实现已通过日志记录请求溯源。
     * </p>
     * * create by Eastevil at 2025/12/30 14:46
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showToast(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showToast -> requestCode: ${config.requestCode}, code:${error.code}, msg:${error.msg}")
        toast(error.msg);
    }

    /**
     * 显示业务层的确认弹窗（单按钮）。
     * <p>
     * <b>特性：</b> 强交互弹窗，通常用于必须用户感知的阻断性错误。<br>
     * <b>配置依赖：</b> 优先使用 {@link ModelRequestConfig#requestTitle} 作为标题。
     * </p>
     * * create by Eastevil at 2025/12/30 14:47
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showConfirmDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConfirmDialog -> requestCode: ${config.requestCode}")
        ConfirmDialog.Builder(requireContext())
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .builder()
            .show()
    }

    /**
     * 显示业务层的提示弹窗。
     * <p>
     * <b>特性：</b> 轻交互弹窗，用户点击确定或外部即可消失。
     * </p>
     * * create by Eastevil at 2025/12/30 14:48
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showTipsDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showTipsDialog -> requestCode: ${config.requestCode}")
        TipsDialog.Builder(requireContext())
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .builder()
            .show()
    }

    /**
     * 通用业务弹窗逻辑（双按钮）。
     * <p>
     * <b>说明：</b> 用于处理带有双按钮交互的标准业务弹窗。
     * </p>
     * * create by Eastevil at 2025/12/30 14:49
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showConventionalDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConventionalDialog -> requestCode: ${config.requestCode}")
        val dialog = ConventionalDialog.Builder(requireContext())
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .submitText(config.submitText ?: "")
            .cancelText(config.cancelText ?: "")
            .submitColor(viewModel.submitColor())
            .cancelColor(viewModel.cancelColor())
            .builder()
        dialog.show()
    }

    /**
     * 框架兼容处理：拦截原始 UI 事件。
     * <p>
     * <b>逻辑说明：</b> 屏蔽任何可能从旧体系下发的错误信号，以确保全项目错误出口唯一。
     * </p>
     * * create by Eastevil at 2025/12/30 14:50
     * @author Eastevil
     * @param event 框架定义的 UI 事件
     */
    override fun handleUIEvent(event: IUIEvent) {
        super.handleUIEvent(event)
    }

    /**
     * 业务自定义动作分发回调。
     * <p>
     * <b>作用：</b> 响应开发者自定义的动作指令识别码。
     * </p>
     * * create by Eastevil at 2025/12/30 14:51
     * @author Eastevil
     * @param config 请求上下文
     * @param actionCode 指令识别码
     * @param extra 数据载体
     */
    protected open fun onHandleAction(config: ModelRequestConfig, actionCode: Int, extra: Any?) {
        SLog.i(TAG, "onHandleAction -> actionCode: $actionCode")
    }

    /**
     * ### 业务空数据调度处理器 (Hook)
     *
     * **核心职责：**
     * 当业务请求成功但返回内容为空（如：搜索无结果、列表无数据）时触发。
     * 旨在驱动 UI 层渲染“缺省页”或针对特定组件执行“隐藏/占位”逻辑。
     *
     * **交互建议：**
     * 1. **状态切换**：若 [config.showType] 为 [ModelRequestConfig.SHOW_TYPE_VIEW]，建议在此处通过 ViewStub 或自定义 Layout 切换至空状态视觉。
     * 2. **精准控制**：通过 [config.requestCode] 识别当前是哪个局部组件为空，从而避免“误伤”全屏 UI。
     * 3. **属性驱动**：可结合 `wsui` 前缀的自定义属性（如 `wsui:emptyIcon`），实现不同业务接口展示不同的缺省样式。
     *
     * **注意：**
     * - 默认不执行任何操作。子类若需统一处理空状态（如：弹出 Toast 提醒“暂无更多内容”），可在此重写。
     * - 该回调触发后，随后仍会触发 [RequestEvent.End] 信号，用于恢复通用的交互状态。
     *
     * @param config [ModelRequestConfig] - 请求上下文，包含业务标识及 UI 表现预设。
     * @param code [Int] - 细分的空状态码（由 ViewModel 层根据业务逻辑定义）。
     * @param msg [String?] - 缺省提示文案，可直接用于 UI 文本显示。
     */
    protected open fun onRequestEmpty(config: ModelRequestConfig,code:Int,msg : String?){
        SLog.i(TAG, "onRequestEmpty -> code,${code},msg:${msg}");
    }

    /**
     * 缺省状态页展示回调。
     * <p>
     * <b>说明：</b> 当页面或局部组件加载失败需渲染错误占位图时触发。
     * </p>
     * * create by Eastevil at 2025/12/30 14:52
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun onShowErrorStateView(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "onShowErrorStateView -> requestCode: ${config.requestCode}")
    }

    /**
     * **动态更新屏幕配置**
     *
     * **设计意图：**
     * 允许在 Activity 运行期间（生命周期内）随时变更系统栏状态。该方法会立即触发物理 Window
     * 的样式属性刷新，实现视觉上的动态切换。
     *
     * **使用场景：**
     * 1. **交互驱动**：例如用户点击“播放”按钮切换到全屏观看，点击“设置”动态修改主题色。
     * 2. **事件驱动**：基于 [dispatchBusinessEvent] 接收到的业务信号，在特定请求阶段调整状态栏。
     * 3. **内容驱动**：当页面滚动导致背景色变化时，动态切换状态栏图标的明暗反差 ([ScreenConfig.lightIcons])。
     *
     * **注意事项：**
     * - 调用后将同步更新所在[AppActivity]内部缓存 [mCurrentConfig]。
     * - 频繁调用可能会导致系统 UI 层的频繁重绘，请避免在 `onScroll` 等高频回调中无条件执行。
     *
     * create by Administrator at 2026/1/7 0:52
     * @author Administrator
     *
     * @param screenConfig 新的屏幕配置实体
     * @return
     *      void
     */
    protected open fun setScreenConfig(screenConfig: ScreenConfig){
        val let = getCurrentActivity(AppContainerActivity::class.java)?.let { a ->
            a.resetScreenConfig(screenConfig);
        }
    }

    protected open fun getScreenConfig(): ScreenConfig? {
        return getCurrentActivity(AppContainerActivity::class.java)?.getScreenConfig();
    }

    protected fun setTitleText(title : String){
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.setTitleText(title);
        }
    }

    protected fun setTitleText(resId: Int){
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.setTitleText(resId);
        }
    }

    protected open fun initFromArguments(argument : Bundle?){
        SLog.d(TAG,"initFromArguments invoke");
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【容器销毁回调 (Container Destroy Callback)】
     * -------------------------------------------------------------------------------------
     * [1. 触发时机]
     * 当宿主容器 [AppContainerActivity] 显式执行回退逻辑 [onBakck] 或 finish() 时触发。
     * 容器会物理遍历当前所有存活的 Fragment 树，并以此作为业务清理的最后信号。
     *
     * [2. 核心职责]
     * 专门用于处理“跨页面/跨组件”级别的资源清理。例如：
     * - 停止跨模块的长连接监听（如 WebSocket/MQTT 订阅）。
     * - 释放 Activity 级别的全局硬件句柄（如摄像头预览、高频传感器）。
     * - 触发业务埋点数据的最终上报。
     *
     * [3. 注意事项 (Critical)]
     * - **禁止重型计算**：由于该回调处于 finish() 调用链中，任何耗时操作（如大数据遍历、
     * 复杂图像处理等）都会直接阻塞 UI 线程，导致用户点击返回键后页面反馈迟钝或卡死。
     * - **异步建议**：若必须执行耗时任务（如将大量数据写入数据库），建议启动 lifecycleScope
     * 协程在 IO 线程处理，避免阻塞当前的销毁流。
     * - **状态逻辑**：此时 [isAdded] 仍为真，Fragment 的 View 尚未解绑。
     *
     * [4. 调用流向]
     * 用户点击回退 -> onBakck -> dispatchDestroyFragments(循环调用本方法) -> finish()。
     *
     * [5. 使用示例]
     * override fun onContainerDestroy() {
     * super.onContainerDestroy()
     * // 正确示例：轻量级清理
     * mSensorManager.unregisterListener(this)
     * // 正确示例：异步处理重型任务
     * viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { saveDraftToDisk() }
     * }
     *
     * create by Administrator at 2026/1/7 23:45
     * @author Administrator
     */
    open fun onContainerDestroy(){
        SLog.d(TAG,"onContainerDestroy")
    }


    /**
     * 暴露给业务层的标准跳转（无参）
     */
    protected fun navigate(@IdRes resId: Int) {
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.navigate(resId);
        }
    }

    /**
     * 暴露给业务层的标准跳转（带参数）
     */
    protected  fun navigate(@IdRes resId: Int, bundle: Bundle?) {
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.navigate(resId,bundle);
        }
    }

    /**
     * 暴露给业务层的进阶跳转（带配置）
     */
    protected  fun navigate(@IdRes resId: Int, bundle: Bundle?, navOptions: NavOptions?) {
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.navigate(resId,bundle,navOptions);
        }
    }

    protected fun showTitle(){
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.visibileTitle();
        }
    }

    protected fun goneTitle(){
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            it.goneTitle();
        }
    }

    /**
     * ### 鉴权失效重定向处理器 (Hook)
     *
     * **1. 核心说明：**
     * 当 Fragment 内部发起的业务请求触发了 401 或 Token 过期等鉴权异常时，
     * [MirrorViewModel] 会接收到信号并自动触发此回调。
     *
     * **2. 实现逻辑：**
     * 默认情况下，Fragment 会通过 [requireActivity] 将此逻辑透传给宿主 Activity 处理，
     * 从而保证整个 App 的登录跳转逻辑高度统一。
     *
     * **3. 重写场景：**
     * 只有当该 Fragment 需要特殊的登录处理（例如：在弹窗 Fragment 中登录而非跳转页面）时，才建议重写。
     *
     * @param code 鉴权失效原始错误码
     * @param msg  异常提示文案
     */
    protected open fun toLogin(code: Int, msg: String?) {
        val activity = requireActivity();
        if(activity is AppActivity<*,*>){
            activity.toLogin(code, msg)
        }else if(activity is AppContainerActivity<*>){
            activity.toLogin(code, msg)
        }else{
            SLog.e(TAG, "toLogin(code, msg) failed: Host Activity is not AppActivity")
        }
    }

    /**
     * ### 强制登录拦截器 (本地主动拦截)
     *
     * **1. 语义说明：**
     * 本方法代表 Fragment 内部触发了“主动登录校验”。
     * 例如：用户点击了 Fragment 布局中的某个敏感按钮，且本地判断 [isLogin] 为 false。
     *
     * **2. 默认实现：**
     * 内部通过调用 [toLogin] (code = -1) 转发至 Activity 层的跳转逻辑。
     *
     * **3. 调用示例：**
     * ```kotlin
     * btnCollect.setOnClickListener {
     * if (!isLogin()) {
     * toLogin()
     * return@setOnClickListener
     * }
     * // 执行收藏逻辑...
     * }
     * ```
     */
    protected open fun toLogin() {
        this.toLogin(-1, "")
    }

    /**
     * 获取由宿主容器预存的业务数据。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * @param fragmentId-
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentData(fragmentId : Int,key : String): Any? {
        return fromActivityIntentCache(fragmentId,key);
    }


    /**
     * 从宿主 Activity 的缓存池中物理检索数据。
     *
     * 内部逻辑：
     * 1. 获取关联的 [AppContainerActivity] 实例。
     * 2. 调用宿主的 getFragmentCacheData 方法，以当前 Fragment 的 [id] 为隔离标识进行检索。
     *
     * @param fragmentId - 目标 Fragment 的资源标识 ID
     * @param key 缓存数据的唯一标识键。
     * @return 业务数据对象；若宿主不存在或数据不存在则返回 null。
     */
    private fun fromActivityIntentCache(fragmentId : Int,key : String): Any? {
        val ac = getCurrentActivity(AppContainerActivity::class.java);
        ac?.let {
            SLog.d(TAG,"fromActivityIntentCache,id:${fragmentId},key:${key}");
            return ac.getFragmentCacheData(fragmentId,key);
        }
        return null;
    }

    fun getColor(colorResId :Int): Int {
        return requireActivity().getColor(colorResId);
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 契约复合模式 (Contract Complex Mode)】
     * -------------------------------------------------------------------------------------
     * **1. 职责说明**：
     * 本方法是当前 Fragment 响应跨组件回传数据的“标准协议”入口。它专门接收由宿主容器
     * [AppContainerActivity] 通过 [dispatchRouterContract] 分发的复合信号。
     *
     * **2. 核心机制：精准分流**：
     * - **自动脱壳**：传入的 [complexResult] 已完成对 Intent 原始 Bundle 的初步安全处理。
     * - **二级校验**：通过 [action] 识别具体的业务指令（如车牌识别 `ACTION_PLATE_LICENSE`）。
     * - **三级定位**：通过 [name] 识别发起请求的配置实例。
     *
     * **3. 调用方式与配合 (Usage)**：
     * - **触发源**：由 Activity 层注册的 `registerContract` 回调中调用 `dispatchRouterContract` 驱动。
     * - **处理方式**：子类 override 本方法，使用 [complexResult] 提供的工具方法提取数据。
     * ```kotlin
     * override fun receiveRouterContract(action: String, name: String, result: ComplexResult) {
     * if (action == RouterActions.PLATE_RECOGNITION) {
     * val plateNumber = result.getString("plate_num") // 提取车牌
     * viewModel.updatePlate(plateNumber) // 驱动 MVVM 数据流
     * }
     * }
     * ```
     *
     * @param action  路由协议定义的 Action 指令。
     * @param name    注册路由时定义的业务唯一标识名。
     * @param complexResult 复合结果对象，支持 Json 反序列化及多类型数据安全提取。
     */
    open fun receiveRouterContract(action: String, name: String, complexResult: ComplexResult) {
        SLog.d(TAG, "receiveRouterContract -> action:${action}, name:${name}")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 纯对象模式 (Pure Object Mode)】
     * -------------------------------------------------------------------------------------
     * **1. 职责说明**：
     * 用于接收已经由契约协议 (Contract) 直接解析完成的“纯业务对象”。适用于不需要
     * [ComplexResult] 进行二次护理的简单场景（如基础类型返回、POJO 实体返回）。
     *
     * **2. 架构设计意图**：
     * - **零碎提取**：当结果只需要一个具体的 Bean 或基本类型时，直接通过此方法透传，减少层级。
     * - **类型强转**：子类需根据 [action] 配合 `as` 操作符完成目标类型的最终还原。
     *
     * **3. 调用方式 (Usage)**：
     * - **触发源**：由 Activity 层 `registerContract` 闭包中调用 `dispatchRouterObject` 驱动。
     * ```kotlin
     * override fun receiveRouterObject(action: String, name: String, data: Any?) {
     * when(name) {
     * "USER_PICKER" -> {
     * val user = data as? UserEntity // 强转回实体
     * viewModel.postUser(user)
     * }
     * }
     * }
     * ```
     *
     * @param action 路由协议 Action。
     * @param name   路由注册标识名。
     * @param data   解析完成的业务实体对象 (POJO) 或基础类型。
     */
    open fun receiveRouterObject(action: String, name: String, data: Any?) {
        SLog.d(TAG, "receiveRouterObject -> action:${action}, name:${name}")
        if (data == null) {
            return
        }
        // 根据数据的物理类型进行分发，驱动对应的特化处理器
        when (data) {
            is Int -> receiveRouterInt(action, name, data)
            is Long -> receiveRouterLong(action, name, data)
            is String -> receiveRouterString(action, name, data)
            is Boolean -> receiveRouterBoolean(action, name, data)
            is Float -> receiveRouterFloat(action, name, data)
            is Double -> receiveRouterDouble(action, name, data)
            // 可以在此处继续扩展针对 ByteArray, Bundle 等类型的分发
            else -> {
                // 默认情况：如果是自定义 POJO 实体，子类可直接在 receiveRouterObject 中处理
                SLog.i(TAG, "receiveRouterObject: Generic object detected for action:$action")
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 整型特化模式 (Int Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:04
     * @author Eastevil
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 Int 数值。
     * @return
     *      void
     */
    protected open fun receiveRouterInt(action: String, name: String, value : Int){
        SLog.d(TAG,"receiveRouterInt_action:${action},name:${name},value:${value}");
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 整型特化模式 (Long Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:04
     * @author Eastevil
     *
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 Long 数值。
     * @return
     *      void
     */
    protected open fun receiveRouterLong(action: String, name: String, value : Long){
        SLog.d(TAG,"receiveRouterLong_action:${action},name:${name},value:${value}");
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 字符串特化模式 (String Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:08
     * @author Eastevil
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 String 文本。
     * @return
     * void
     */
    protected open fun receiveRouterString(action: String, name: String, value: String?) {
        SLog.d(TAG, "receiveRouterString -> action:${action}, name:${name}, value:${value}")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 布尔特化模式 (Boolean Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:08
     * @author Eastevil
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 Boolean 数值。
     * @return
     * void
     */
    protected open fun receiveRouterBoolean(action: String, name: String, value: Boolean) {
        SLog.d(TAG, "receiveRouterBoolean -> action:${action}, name:${name}, value:${value}")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 单精度浮点模式 (Float Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:08
     * @author Eastevil
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 Float 数值。
     * @return
     * void
     */
    protected open fun receiveRouterFloat(action: String, name: String, value: Float) {
        SLog.d(TAG, "receiveRouterFloat -> action:${action}, name:${name}, value:${value}")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【路由结果接收枢纽 - 双精度浮点模式 (Double Specialty Mode)】
     * -------------------------------------------------------------------------------------
     * create by Eastevil at 2026/1/22 11:08
     * @author Eastevil
     * @param action 路由协议指令 Action。
     * @param name   路由注册唯一标识名。
     * @param value  协议层解析后的原始 Double 数值。
     * @return
     * void
     */
    protected open fun receiveRouterDouble(action: String, name: String, value: Double) {
        SLog.d(TAG, "receiveRouterDouble -> action:${action}, name:${name}, value:${value}")
    }

    protected fun openGallery(){
        if(needMediaPicker()){
            MediaPickerManager.instance.openGallery(1);
        }else{
            SLog.d(TAG,"not need media picker");
        }
    }

    protected fun openCamera(authority : String,tag : Int){
        if(MediaPickerManager.instance.hasCameraPermission(requireContext())){
            MediaPickerManager.instance.openCamera(requireContext(),authority,tag);
        }else{
            //请求权限,这里只有继承了AppFragment的子类能收到请求权限返回的处理
            val dialog = ConventionalDialog.Builder(requireContext())
                .themeColor(viewModel.themeColor())
                .title(R.string.sdkcore_permission_camera_title)
                .message(R.string.sdkcore_permission_camear_message)
                .submitText(R.string.sdkcore_location_confirm)
                .cancelText(R.string.sdkcore_location_cancel)
                .submitColor(viewModel.submitColor())
                .cancelColor(viewModel.cancelColor())
                .onSubmit {
                    //请求权限
                    EasyPermissions.requestPermissions(PermissionRequest.Builder(this, RC_CAMERA_PERM, *PERMS_CAMERA).build())
                }
                .builder();
            dialog.show();
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【媒体选择能力开关 (Media Picker Hook)】
     * -------------------------------------------------------------------------------------
     * **1. 设计意图**：
     * 用于声明当前 Fragment 是否需要开启相册选择或拍照功能。这是一个“懒加载”式注册开关，
     * 只有返回 true 的子类才会触发 {@link MediaPickerManager} 的注册逻辑，节省资源。
     *
     * **2. 约束说明**：
     * - 若返回 true，子类必须同时重写 {@link #onMediaPicerResult} 以接收回调。
     * - 注册逻辑在 {@link #initView} 阶段自动完成，确保符合 Activity Result API 的生命周期要求。
     *
     * @return boolean 返回 true 表示激活媒体组件功能，默认返回 false。
     */
    protected open fun needMediaPicker(): Boolean {
        return false;
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【媒体选择结果回调枢纽 (Media Result Pivot)】
     * -------------------------------------------------------------------------------------
     * **1. 职责说明**：
     * 当用户完成相册选择或相机拍照后，数据会流转至此。它是 View 层处理媒体数据的唯一出口。
     *
     * **2. 参数解析**：
     * - **tag (Int)**: 对应发起请求时传入的 RequestCode（如头像、证件等），用于业务分流。
     * - **uri (Uri?)**: 选中的图片或拍照生成的物理路径 Uri；若用户取消或失败则为 null。
     *
     * **3. 架构建议 (MVVM)**：
     * - **非重型处理**：此回调属于 UI 线程，禁止在此处执行高耗时的图片压缩或位图转换。
     * - **状态驱动**：拿到有效 Uri 后，应立即转发给 ViewModel 对应的处理方法，驱动数据流更新。
     *
     * **4. 调用示例**：
     * ```kotlin
     * override fun onMediaPicerResult(tag: Int, uri: Uri?) {
     * super.onMediaPicerResult(tag, uri) // 保留基类日志记录
     * if (uri == null) return
     * when(tag) {
     * MediaPickerManager.REQUEST_CODE_AVATAR -> viewModel.uploadAvatar(uri)
     * }
     * }
     * ```
     *
     * @param tag 业务识别码（由调用方在 openCamera/openGallery 时指定）。
     * @param uri 媒体资源的统一资源标识符。
     */
    protected open fun onMediaPicerResult(tag : Int,uri : Uri?){
        if(!needMediaPicker()){
            return;
        }
        SLog.d(TAG,"onMediaPicerResult_tag:${tag}");
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_CAMERA_PERM) {
            //这种情况，属于请求相机后的返回，直接判断是否有权限，然后直接调起相机，后期再完善
            SLog.d(TAG,"request camera permission success");
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // 如果被永久拒绝（勾选了不再询问），引导去设置
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            //根据requestCode可以进行不同的处理
            when(requestCode){
                else->{
                    toast(R.string.sdkcore_permission_camear_denied);
                }
            }
        }
    }

    private val onMediaResultListener = object : OnMediaResultListener{

        override fun onMediaResult(tag: Int, uri: Uri?) {
            onMediaPicerResult(tag,uri);
        }
    }

    companion object {
        private const val TAG = "WSVita_App_AppFragment"
        private const val RC_CAMERA_PERM = 9522;
        private val PERMS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    }
}
