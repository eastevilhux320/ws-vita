package com.wsvita.core.common

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wsvita.core.R
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.entity.RequestEvent
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.framework.commons.SDKActivity
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.entity.VError
import com.wsvita.framework.utils.SLog
import com.wsvita.ui.dialog.ConfirmDialog
import com.wsvita.ui.dialog.ConventionalDialog
import com.wsvita.ui.dialog.TipsDialog
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * ### App 级业务 Activity 抽象基类
 *
 * **核心定位：**
 * 本类作为业务模块的控制器基类，集成了基于 [RequestEvent] 的全生命周期调度机制。
 * 它上承 [AppViewModel] 的业务意图，下接组件化 UI 的统一表现，是实现“配置驱动交互”的核心节点。
 *
 * **主要职责：**
 * 1. **事件调度**：监听并分发 [RequestEvent] 流，实现请求开始、逻辑结束、异常反馈的自动化处理。
 * 2. **状态感知**：通过 [onRequestStageChanged] 为子类提供精细化的业务执行阶段同步。
 * 3. **视觉路由**：基于 [ModelRequestConfig] 实现多维度的错误反馈映射（Toast/Dialog/View）。
 *
 * **重构注意事项：**
 * - 基础全局 Loading 仍由父类 [SDKActivity] 托管（通过 IUIEvent 响应）。
 * - 业务层错误处理已从 [IUIEvent] 剥离，本类及其子类是处理 [VError] 的唯一入口。
 *
 * @param D DataBinding 泛型，必须继承自 [ViewDataBinding]。
 * @param V ViewModel 泛型，必须继承自 [AppViewModel]。
 * @author Eastevil
 * @date 2025/12/30
 */
abstract class AppActivity<D : ViewDataBinding, V : AppViewModel> : SDKActivity<D, V>(),
    EasyPermissions.PermissionCallbacks {

    /**
     * 当前页面的屏幕配置缓存，用于保存状态栏颜色、全屏状态等。
     */
    private lateinit var mCurrentConfig: ScreenConfig

    override fun beforeOnCreate(savedInstanceState: Bundle?) {
        super.beforeOnCreate(savedInstanceState)
        mCurrentConfig = initScreenConfig();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. 首次同步系统栏状态
        SystemBarManager.instance.applyConfig(this, mCurrentConfig)
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
     * - 调用后将同步更新内部缓存 [mCurrentConfig]。
     * - 频繁调用可能会导致系统 UI 层的频繁重绘，请避免在 `onScroll` 等高频回调中无条件执行。
     *
     * create by Administrator at 2026/1/7 0:52
     * @author Administrator
     *
     * @param config 新的屏幕配置实体。建议基于 [getCurrentScreenConfig] 的副本进行增量修改。
     * @return
     *      void
     */
    protected fun setScreenConfig(config: ScreenConfig) {
        this.mCurrentConfig = config
        SystemBarManager.instance.applyConfig(this, mCurrentConfig)
    }

    /**
     * 重置屏幕配置。
     * * **注意：** 该方法设计为仅限 [androidx.fragment.app.Fragment] 调用。
     * 在组件化架构中，此方法用于 Fragment 根据自身业务需求通知宿主 Activity 改变屏幕状态（如旋转、全屏等）。
     * * @param config 屏幕配置参数，包含方向、亮度、是否隐藏状态栏等。
     * @throws IllegalStateException 如果检测到调用方不是 Fragment 实例，将抛出异常或终止执行。
     */
    fun resetScreenConfig(config: ScreenConfig) {
        val stackTrace = Thread.currentThread().stackTrace
        // 检查调用者是否为 Fragment (androidx 或原生)
        val isCalledFromFragment = stackTrace.any {
            it.className.contains("androidx.fragment.app.Fragment") ||
                    it.className.contains(".Fragment")
        }

        if (!isCalledFromFragment) {
            // 可以选择静默退出或者抛出异常提醒开发者
            SLog.e(TAG, "resetScreenConfig must be called from a Fragment")
            return
        }

        // 执行逻辑
        setScreenConfig(config)
    }

    fun getScreenConfig(): ScreenConfig {
        return mCurrentConfig;
    }

    /**
     * **初始化页面屏幕配置 (Hook)**
     *
     * **设计意图：**
     * 本方法作为子类介入初始视觉风格定义的“决策点”。在 [beforeOnCreate] 期间被触发，
     * 确保在 Window 首帧渲染前获取到最终配置，从而规避状态栏样式的后期跳变（闪烁）。
     *
     * **重写指导：**
     * 1. **默认行为**：若子类不重写，则沿用 [ScreenConfig.standard]（通常为沉浸式透明状态栏）。
     * 2. **定制场景**：如需实现全屏、修改状态栏颜色、或切换图标明暗，应在此处构建并返回自定义对象。
     * 3. **性能建议**：此方法应仅包含简单的对象构建逻辑，禁止执行耗时操作。
     *
     * **示例：**
     * ```kotlin
     * override fun initScreenConfig() = ScreenConfig.build(isFullScreen = true, ...)
     * ```
     *
     * @return 最终应用于当前 Activity 窗口的 [ScreenConfig] 实体。
     */
    protected open fun initScreenConfig(): ScreenConfig {
        return ScreenConfig.build()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        if(needLocation()){
            SLog.d(TAG,"initView_needLoacation")
            //需要定位，判断是否拥有位置权限
            val perms = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if (EasyPermissions.hasPermissions(this, *perms)) {
                // 已经有权限
                performLocationAction()
            } else {
                // 没有权限，发起请求，提示信息可根据情况设计子类返回，这里直接写死了
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(this, RC_LOCATION_PERM, *perms)
                        .setRationale(R.string.sdkcore_location_rationale)
                        .setPositiveButtonText(R.string.sdkcore_location_confirm)
                        .setNegativeButtonText(R.string.sdkcore_location_cancel)
                        .build()
                )
            }
        }
    }

    /**
     * **注册业务事件观察点**
     *
     * **调用时机：** 由框架在 `onCreate` 期间自动触发。
     * **实现逻辑：** 开启协程订阅 [AppViewModel.requestEvent]。采用 [Lifecycle.State.STARTED]
     * 策略，确保仅在页面可见时消费事件，并在配置变更或页面销毁时自动注销。
     */
    override fun addObserve() {
        super.addObserve()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestEvent.collect { event ->
                    dispatchBusinessEvent(event)
                }
            }
        }

        viewModel.authError.observe(this, Observer {
            if (it == null) {
                // 在 VM 中进行消耗的操作，会触发这里的逻辑
                SLog.d(TAG, "authError -> state reset/consumed")
            } else {
                // 发生账号异常错误
                if (it.isSessionExpired) {
                    // 登录事件已被消耗，不在进行其他逻辑
                    SLog.i(TAG, "authError -> event already processed, ignore")
                } else {
                    SLog.i(TAG,"authError -> to login")
                    toLogin(it.code, it.msg);
                    // 立即标记并通知 VM 消耗
                    it.isSessionExpired = true
                    viewModel.consumeAuthError(it.resultId)
                }
            }
        })
    }

    override fun onViewClick(view: View) {
        SLog.i(TAG,"onViewClick");
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == RC_LOCATION_PERM) {
            performLocationAction()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // 如果被永久拒绝（勾选了不再询问），引导去设置
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            onLocationPermissionDenied()
        }
    }

    /**
     * ### 鉴权失效重定向处理器 (Hook)
     *
     * **1. 核心说明：**
     * 本方法是全 App 业务拦截体系的“视觉终点”。当网络请求返回特定的鉴权失败错误码（如 401 Token 过期、
     * 5001 异地登录）时，由 [AppViewModel] 自动触发信号，并由基类在此处统一响应。
     *
     * **2. 逻辑保障：**
     * - **唯一性**：基于 `requestId` 消耗机制，确保单次请求导致的失效仅触发一次跳转。
     * - **防抖动**：内部已处理 `isSessionExpired` 状态判定，规避 LiveData 粘性事件导致的重复跳转。
     * - **优先级**：鉴权异常拥有最高优先级，一旦触发，将熔断后续的业务错误分发（如不再弹出 Toast）。
     *
     * **3. 调用方式：**
     * 本方法由基类的 [addObserve] 自动调度，业务层（子类）**无需手动调用**。
     * 子类仅需根据自身需求决定是否“重写”此方法以自定义跳转目标。
     *
     * **4. 子类如何使用（重写示例）：**
     * ```kotlin
     * override fun toLogin(code: Int, msg: String?) {
     * super.toLogin(code, msg) // 保留基类日志
     * // 执行具体的路由跳转，例如使用 ARouter
     * ARouter.getInstance().build("/account/login")
     * .withInt("errorCode", code)
     * .withString("errorMsg", msg)
     * .navigation()
     * * // 如果是个性化页面，可以在跳转后关闭当前页
     * // finish()
     * }
     * ```
     *
     * **5. 注意事项：**
     * - **状态清理**：重写时建议保留 `super.toLogin` 或手动调用 `hideLoading()`，防止 Loading 框挂死。
     * - **禁止耗时**：此方法运行在主线程，禁止在此处执行复杂的业务逻辑或 IO 操作。
     * - **出口唯一**：作为组件化规范，建议全项目仅在 `BaseActivity` 层级重写一次，除非特定页面有特殊拦截需求。
     *
     * @param code 鉴权失效原始错误码。
     * @param msg  异常提示文案（通常由后端返回或 VM 格式化）。
     */
    open fun toLogin(code : Int,msg : String?){
        SLog.e(TAG, "toLogin -> redirecting: code=$code")
    }

    /**
     * ### 强制登录拦截器 (本地主动拦截)
     *
     * **1. 语义说明：**
     * 本方法表示在**本地业务逻辑判定**中，当前用户处于“未登录态”或“访客态”，且当前操作需要登录权限。
     * 不同于有参的 [toLogin]，本方法通常不携带服务端错误码，属于**主动式拦截**。
     *
     * **2. 触发场景示例：**
     * - 用户在未登录状态下点击了“我的收藏”、“结算”或“发布评论”等受限按钮。
     * - 页面初始化检测：[isLogin] 返回 false 时。
     *
     * **3. 默认实现：**
     * 内部桥接至 [toLogin] (code = -1)，实现跳转逻辑的收口与复用。
     *
     * **4. 调用示例：**
     * ```kotlin
     * if (!isLogin()) {
     * toLogin()
     * return
     * }
     * ```
     */
    protected open fun toLogin(){
        toLogin(-1,"");
    }

    /**
     * **业务事件内部分发器**
     *
     * **作用：**
     * 将原始的 [RequestEvent] 信号流转换为具体的 UI 调度指令。
     *
     * **执行序说明：**
     * - [RequestEvent.Begin] -> 标记执行开始。
     * - [RequestEvent.requestEnd] -> **关键节点**。逻辑处理完成，优先于数据 Return 恢复 UI 交互态。
     * - [RequestEvent.Error] -> 业务失败反馈。
     * - [RequestEvent.End] -> 物理终点。确保在所有异常路径下都能恢复状态。
     *
     * @param event 接收到的请求事件实体。
     */
    private fun dispatchBusinessEvent(event: RequestEvent) {
        when (event) {
            is RequestEvent.Begin -> {
                onRequestStageChanged(event.config, active = true)
            }
            is RequestEvent.requestEnd -> {
                // 逻辑结束信号，用于在数据渲染前提前恢复 UI 响应，消除时序滞后感
                onRequestStageChanged(event.config, active = false)
            }
            is RequestEvent.Error -> {
                handleVError(event.config, event.error)
            }
            is RequestEvent.End -> {
                // 兜底信号，防止逻辑分支未覆盖导致的 UI 挂死
                onRequestStageChanged(event.config, active = false)
            }
            is RequestEvent.Action -> {
                onHandleAction(event.config, event.actionCode, event.extra)
            }
            is RequestEvent.Empty -> {
                onRequestEmpty(event.config,event.code,event.msg);
            }
        }
    }

    /**
     * ### 业务驱动：启动定位流程 (Hook)
     *
     * **核心职责：**
     * 当位置权限确认授予后触发。本方法作为 View 层的执行开关，负责初始化并开启百度地图定位 SDK。
     * 遵循 MVVM 职责分离原则：Activity 负责驱动引擎，结果回传给 ViewModel 处理业务。
     *
     * **执行逻辑：**
     * 1. 可以配置百度，google等一些具有定位功能的SDK进行定位参数设定（高精度模式、单次定位）。
     * 2. 注册监听器，捕获 [BDLocation] 实体。
     * 3. 成功后通过 [viewModel.onLocationResult] 转发数据并停止定位引擎。
     */
    protected open fun performLocationAction(){
        SLog.w(TAG, "Location onLocationPermissionGranted.")
        //开始位置定位
    }

    /**
     * ### 位置权限被拒/取消回调 (Hook)
     *
     * **1. 核心定位：**
     * 本方法是位置权限生命周期的“异常出口”。当用户在系统权限弹窗或 Rationale 解释弹窗中
     * 选择“拒绝”或点击“返回”取消时触发。
     *
     * **2. 业务逻辑建议：**
     * - **降级处理**：如果定位是核心功能（如考勤、周边检索），建议在此处触发缺省页展示。
     * - **静默处理**：如果定位仅作为辅助功能（如自动填写城市），可仅记录日志并允许用户手动输入。
     * - **视觉引导**：若权限被永久拒绝（勾选了不再询问），基类已通过 [AppSettingsDialog] 引导用户，
     * 此处只需处理 UI 层的状态切换。
     *
     * **3. 组件化集成示例（结合 VError 体系）：**
     * ```kotlin
     * override fun onLocationPermissionDenied() {
     * super.onLocationPermissionDenied()
     * // 1. 构建一个表示“无权限”的业务错误实体
     * val error = VError(code = 403, msg = "未获取到位置权限，部分功能无法使用")
     * * // 2. 结合 ModelRequestConfig，驱动 UI 展示错误占位图 (onShowErrorStateView)
     * val config = ModelRequestConfig.build {
     * showType = ModelRequestConfig.SHOW_TYPE_VIEW
     * requestCode = RC_LOCATION_PERM
     * }
     * handleVError(config, error)
     * }
     * ```
     *
     * **4. 注意事项：**
     * - **防抖处理**：避免在权限连续请求失败时弹出重叠的 Toast。
     * - **状态恢复**：若页面有局部 Loading 状态，应在此处通过 [onRequestStageChanged] 恢复 UI 交互。
     */
    protected open fun onLocationPermissionDenied() {
        SLog.w(TAG, "Location onLocationPermissionDenied")
    }

    /**
     * **请求执行阶段变更回调 (Hook)**
     *
     * **作用：**
     * 用于同步特定业务请求的执行状态。通常用于控制非全局 Loading 的局部 UI 交互。
     *
     * **使用场景：**
     * 1. 根据 [config.requestCode] 禁用/启用特定提交按钮。
     * 2. 开启/重置局部 ProgressBar 或下拉刷新组件的状态。
     *
     * **调用说明：**
     * 子类重写时应判断 [config.requestCode]，以实现精准的局部刷新。
     *
     * @param config 本次请求的上下文配置。
     * @param active true: 业务正在执行；false: 业务逻辑已结束，UI 可进入交互态。
     */
    protected open fun onRequestStageChanged(config: ModelRequestConfig, active: Boolean) {
        SLog.d(TAG, "RequestStageChanged -> Code: ${config.requestCode}, Active: $active")
    }

    protected open fun onRequestStageChanged(){

    }


    /**
     * **业务错误综合调度处理器**
     *
     * **作用：**
     * 结合业务意图（Config）与错误实体（Error），驱动最终的视觉反馈。
     *
     * **路由逻辑：**
     * 优先触发 [onInterceptError] 钩子。若未被拦截，则根据 [VError.showType]
     * 路由至对应的 UI 实现方法（如 [showConfirmDialog]）。
     *
     * @param config 请求上下文，提供 requestTitle 等业务描述。
     * @param error 错误详情，包含 code, msg 及展示类型。
     */
    protected open fun handleVError(config: ModelRequestConfig, error: VError) {
        if (onInterceptError(config, error)) {
            SLog.d(TAG, "Error Intercepted -> Code: ${config.requestCode}")
            return
        }

        when (config.showType) {
            ModelRequestConfig.SHOW_TYPE_TOAST -> showToast(config,error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_COFMIRT -> showConfirmDialog(config, error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_CONVENTIONAL -> showConventionalDialog(config, error)
            ModelRequestConfig.SHOW_TYPE_VIEW -> onShowErrorStateView(config, error)
            ModelRequestConfig.SHOW_TYPE_DIALOG_TIPS -> showTipsDialog(config, error)
        }
    }

    /**
     * **错误拦截器 (Hook)**
     *
     * **作用：**
     * 允许子类针对特定业务逻辑或错误码执行非 UI 展示类操作（如：跳转、埋点、静默重试）。
     *
     * @return true: 表示该错误已由子类消费，基类将不再执行后续的弹窗/Toast 展示。
     */
    protected open fun onInterceptError(config: ModelRequestConfig, error: VError): Boolean = false

    /**
     * **业务层统一 Toast 展示回调**
     *
     * **作用：**
     * 以轻量级反馈形式向用户通报非阻断性错误或业务提醒。
     *
     * **调用逻辑：**
     * 1. 当 [VError.showType] 为 [VError.SHOW_TYPE_TOAST] (100) 时由 [handleVError] 自动触发。
     * 2. 子类可重写此方法以改变 Toast 的视觉样式（如切换为 SnackBar 或自定义 UI）。
     *
     * **注意事项：**
     * - 默认实现已通过 [config.requestCode] 进行了日志追踪，方便排查是哪个接口触发的反馈。
     * - 建议优先使用 [error.msg] 作为展示内容，该内容通常已由后端或 [AppViewModel] 格式化。
     *
     * @param config 本次请求的上下文配置，包含请求标识及业务意图。
     * @param error 包含错误代码及具体提示信息的实体。
     */
    protected open fun showToast(config: ModelRequestConfig, error: VError){
        SLog.i(TAG, "showToast -> requestCode: ${config.requestCode},code:${error.code},msg:${error.msg}")
        toast(error.msg);
    }

    /**
     * **业务确认弹窗 (302)**
     *
     * **特性：** 强交互弹窗，通常用于必须用户感知的阻断性错误。
     * **配置依赖：** 优先使用 [config.requestTitle] 作为弹窗标题。
     *
     * create by Eastevil at 2025/12/30 13:34
     * @author Eastevil
     * @param config [ModelRequestConfig]-本次请求的上下文配置，包含请求标识及业务意图。
     * @param error [VError]-包含错误代码及具体提示信息的实体。
     * @return
     *      void
     */
    protected open fun showConfirmDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConfirmDialog -> requestCode: ${config.requestCode},code:${error.code},msg:${error.msg}")
        ConfirmDialog.Builder(this)
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .submitText(config.submitText?:"")
            .submitColor(viewModel.submitColor())
            .builder()
            .show()
    }

    /**
     * **业务提示弹窗 (301)**
     * **特性：** 轻交互弹窗，用户点击确定或外部即可消失。
     *
     * create by Eastevil at 2025/12/30 13:37
     * @author Eastevil
     * @param config [ModelRequestConfig]-本次请求的上下文配置，包含请求标识及业务意图。
     * @param error [VError]-包含错误代码及具体提示信息的实体。
     * @return
     *      void
     */
    protected open fun showTipsDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showTipsDialog -> requestCode: ${config.requestCode},code:${error.code},msg:${error.msg}")
        TipsDialog.Builder(this)
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .builder()
            .show()
    }

    /**
     * **通用业务弹窗逻辑 (303)**
     * **说明：** 预留接口，用于处理带有双按钮交互的标准业务弹窗。
     *
     * create by Eastevil at 2025/12/30 13:40
     * @author Eastevil
     * @param config [ModelRequestConfig]-本次请求的上下文配置，包含请求标识及业务意图。
     * @param error [VError]-包含错误代码及具体提示信息的实体。
     * @return
     *      void
     */
    protected open fun showConventionalDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConventionalDialog -> requestCode: ${config.requestCode},code:${error.code},msg:${error.msg}")
        val dialog = ConventionalDialog.Builder(this)
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .submitText(config.submitText?:"")
            .cancelText(config.cancelText?:"")
            .submitColor(viewModel.submitColor())
            .cancelColor(viewModel.cancelColor())
            .builder();
        dialog.show();
    }


    /**
     * **框架兼容处理：拦截原始 UI 事件**
     *
     * **逻辑说明：** * 由于 [IUIEvent] 中已不再定义 Error 对象，且 App 层已通过 [RequestEvent]
     * 实现了更完善的错误处理。本方法屏蔽任何可能从旧体系下发的错误信号，以确保全项目错误出口唯一。
     *
     * create by Eastevil at 2025/12/30 13:38
     * @author Eastevil
     * @param event [IUIEvent]
     * @return
     */
    override fun handleUIEvent(event: IUIEvent) {
        // 此处不再处理任何 Error 类信号，仅透传基础 Loading 信号给父类 SDKActivity
        super.handleUIEvent(event)
    }

    /**
     * **业务自定义动作分发回调**
     *
     * create by Eastevil at 2025/12/30 13:39
     * @author Eastevil
     * @param config [ModelRequestConfig]-本次请求的上下文配置，包含请求标识及业务意图。
     * @param actionCode 开发者自定义的动作指令识别码。
     * @param extra 随动作携带的数据载体
     * @return
     *      void
     */
    protected open fun onHandleAction(config: ModelRequestConfig, actionCode: Int, extra: Any?) {
        SLog.i(TAG, "onHandleAction -> actionCode: ${actionCode}")
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
     * **缺省状态页展示回调 (200)**
     * **说明：** 当页面或局部组件加载失败需渲染错误占位图时触发。需子类结合具体的布局 ID 实现。
     *
     * create by Eastevil at 2025/12/30 13:40
     * @author Eastevil
     * @param config [ModelRequestConfig]-本次请求的上下文配置，包含请求标识及业务意图。
     * @param error [VError]-包含错误代码及具体提示信息的实体。
     * @return
     *      void
     */
    protected open fun onShowErrorStateView(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "onShowErrorStateView -> requestCode: ${config.requestCode},code:${error.code},msg:${error.msg}")
    }

    protected open fun needLocation(): Boolean {
        return false;
    }

    /**
     * ╔════════════════════════════════════════════════════════════════════════════════════╗
     * ║ 私有实现                                             ║
     * ╚════════════════════════════════════════════════════════════════════════════════════╝
     */


    companion object {
        private const val TAG = "WSVita_App_AppActivity"

        private const val RC_LOCATION_PERM = 9521;
    }
}
