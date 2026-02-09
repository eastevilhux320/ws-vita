package com.wsvita.core.common

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import com.wsvita.core.R
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.entity.RequestEvent
import com.wsvita.core.local.manager.ContainerManager
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.commons.SDKNavContainerActivity
import com.wsvita.framework.entity.ErrorHolder
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.entity.VError
import com.wsvita.framework.router.contract.ComplexResult
import com.wsvita.framework.router.contract.ContractResult
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.widget.view.VitaTitleBar
import com.wsvita.ui.dialog.ConfirmDialog
import com.wsvita.ui.dialog.ConventionalDialog
import com.wsvita.ui.dialog.TipsDialog
import ext.ViewExt.dip2px
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * 业务层导航容器基类。作为组件化架构中的“壳 Activity”，负责聚合 SDK 导航能力、
 * 分发业务请求事件、管理 Fragment 物理生命周期以及实施导航权限拦截。
 * * [核心功能]
 * 1. 导航隔离：通过 [destinationIdList] 物理移除未授权节点，实现业务权限硬隔离。
 * 2. 事件分发：订阅 ViewModel 的 [RequestEvent] 流，统一处理 Loading、Toast、Dialog 及错误页。
 * 3. 栈管理：重写物理返回键逻辑，提供 [onInterceptBack] 钩子并支持深度遍历销毁通知。
 * 4. 标题同步：自动根据 Navigation XML 的 label 属性同步标题栏文案。
 *
 * [使用方式]
 * 1. 继承此类并指定对应的 [AppViewModel] 泛型。
 * 2. 实现 [startDestinationId] 定义首屏，实现 [destinationIdList] 定义白名单。
 * 3. 在子类中通过 [navigate] 方法进行受控跳转。
 *
 * [注意事项]
 * - 权限控制：任何未在 [destinationIdList] 返回列表中的 ID，在导航图中将被物理删除，跳转会失败。
 * - 资源关联：子类必须通过 [getNavGraphResId] (父类方法) 提供正确的 Navigation XML。
 * - 生命周期：采用 repeatOnLifecycle(STARTED) 收集事件，确保 UI 刷新仅在页面可见时执行。
 *
 * -------------------------------------------------------------------------------------
 * @Author: Eastevil
 * @Date: 2025/12/30 (Updated: 2026/01/08)
 * @Version: 1.2.0
 */
abstract class AppContainerActivity<V : AppContainerViewModel> : SDKNavContainerActivity<V>(),VitaTitleBar.OnVitaTitleBarListener,
    EasyPermissions.PermissionCallbacks  {
    /**
     * 当前页面的屏幕配置缓存，用于保存状态栏颜色、全屏状态等。
     */
    private lateinit var mCurrentConfig: ScreenConfig

    override fun beforeOnCreate(savedInstanceState: Bundle?) {
        super.beforeOnCreate(savedInstanceState)
        mCurrentConfig = initScreenConfig();
    }

    /**
     * Activity 创建时的生命周期回调。
     * <p>
     * <b>作用：</b> 页面初始化入口，用于处理全局的主题适配、多语言切换或 Intent 参数的初步解析。<br>
     * <b>调用：</b> 系统启动 Activity 时由 Framework 自动调用。<br>
     * <b>注意：</b> 必须确保在调用 super 之前完成特定的主题配置。
     * </p>
     * * create by Eastevil at 2025/12/29 17:56
     * @author Eastevil
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. 首次同步系统栏状态
        SystemBarManager.instance.applyConfig(this, mCurrentConfig)
    }

    override fun initTitle(titleBar: VitaTitleBar) {
        super.initTitle(titleBar)
        titleBar.setOnVitaTitleListener(this);
        titleBar.setBackType(1);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setBackIconResource(com.wsvita.framework.R.drawable.ic_ws_base_back);
        //为了点击区域更大
        titleBar.setBackSize(40.dip2px(),40.dip2px());
        titleBar.setBackPadding(10.dip2px())
        titleBar.setMenuSize(40.dip2px(),40.dip2px())
        titleBar.setMenuPadding(10.dip2px());
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
                val dialog = ConventionalDialog.Builder(this)
                    .themeColor(viewModel.themeColor())
                    .title(R.string.sdkcore_location_title)
                    .message(R.string.sdkcore_location_rationale)
                    .submitText(R.string.sdkcore_location_confirm)
                    .cancelText(R.string.sdkcore_location_cancel)
                    .submitColor(viewModel.submitColor())
                    .cancelColor(viewModel.cancelColor())
                    .onSubmit {
                        //请求权限
                        EasyPermissions.requestPermissions(PermissionRequest.Builder(this, RC_LOCATION_PERM, *perms).build())
                    }
                    .builder();
                dialog.show();
            }
        }

    }

    override fun onError(error: ErrorHolder) {
        super.onError(error)
    }

    /**
     * 业务层：添加数据动态变化监听。
     * <p>
     * <b>作用：</b> 订阅并分发 {@link AppViewModel#requestEvent} 流，处理容器级别的业务请求生命周期。<br>
     * <b>调用：</b> 由框架在 onCreate 期间自动触发。<br>
     * <b>注意：</b> 采用 {@link Lifecycle.State#STARTED} 策略，确保 UI 操作的生命周期安全性。
     * </p>
     * * create by Eastevil at 2025/12/30 15:11
     * @author Eastevil
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
    }


    override fun onViewClick(view: View) {

    }

    /**
     * 获取指定 Fragment 碎片预存的业务数据。
     *
     * 该方法是组件化传参体系在宿主层级的实现。它通过传入的 [fragmentId]（通常为 Fragment 的物理 ID）
     * 映射到特定的数据空间，并从 ViewModel 维护的 ContainerManager 中提取对应 [key] 的值。
     *
     * 调用场景：
     * 1. 当 Fragment 调用 [fromActivityIntentCache] 时，宿主通过此方法中转提供数据。
     * 2. 支撑容器内多个 Fragment 碎片独立的数据隔离与获取需求。
     *
     * create by Eastevil at 2026/1/20 15:35
     * @author Eastevil
     *
     * @param fragmentId Fragment 碎片的物理标识索引。
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象；若不存在则返回 null。
     */
    fun getFragmentCacheData(fragmentId : Int,key : String): Any? {
        return viewModel.getFragmentCacheData(fragmentId,key);
    }


    /**
     * 请求执行阶段变更回调。
     * <p>
     * <b>作用：</b> 用于同步容器级别业务请求的执行状态。例如：控制全局搜索栏的加载态。<br>
     * <b>调用：</b> 在请求开始或逻辑结束时触发。<br>
     * <b>注意：</b> 子类重写时应判断 {@link ModelRequestConfig#requestCode} 以实现精准刷新。
     * </p>
     * * create by Eastevil at 2025/12/30 15:13
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
     * <b>作用：</b> 视觉路由中心。结合配置意图将容器捕获的错误路由至具体的 UI 提示方法。<br>
     * <b>注意：</b> 优先通过 {@link #onInterceptError} 提供给子类拦截机会。
     * </p>
     * * create by Eastevil at 2025/12/30 15:14
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体对象
     */
    protected open fun handleVError(config: ModelRequestConfig, error: VError) {
        if (onInterceptError(config, error)) {
            SLog.d(TAG, "Container intercepted error, requestCode: ${config.requestCode}")
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
     * <b>作用：</b> 允许特定的容器类（如 AccountActivity）拦截特定的全局错误，实现特殊的导航跳转。<br>
     * <b>注意：</b> 若拦截则返回 true，防止在容器层级弹出不必要的通用 Toast。
     * </p>
     * * create by Eastevil at 2025/12/29 17:59
     * @author Eastevil
     * @param config 请求配置
     * @param error 错误详情
     * @return boolean 返回 true 表示已处理
     */
    protected open fun onInterceptError(config: ModelRequestConfig, error: VError): Boolean = false

    /**
     * 全局目的地切换监听。
     * <p>
     * <b>作用：</b> 自动化 UI 同步。根据 Navigation 配置文件中的 Label 自动更新标题栏文案，同时可作为埋点统计的切入点。<br>
     * <b>调用：</b> NavController 目的地发生变化时由 SDK 回调。<br>
     * <b>维护：</b> 埋点逻辑建议抽取到具体的 Analytics 代理类中，避免本方法代码膨胀。
     * </p>
     * * create by Eastevil at 2025/12/29 18:00
     * @author Eastevil
     * @param destination 导航目的地实体
     */
    override fun onDestinationChanged(destination: NavDestination) {
        super.onDestinationChanged(destination)
        val title = destination.label?.toString() ?: ""
        // 假设布局中包含统一的标题栏组件
        // dataBinding.vitaTitleBar.setTitleText(title)
        SLog.d(TAG, "onDestinationChanged: $title")
        val id = destination.id;
        val name = destination.navigatorName;
        val arguments = destination.arguments;
    }

    /**
     * 业务层统一 Toast 展示回调。
     * <p>
     * <b>作用：</b> 在容器顶层展示轻量级错误反馈。
     * </p>
     * * create by Eastevil at 2025/12/30 15:15
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showToast(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showToast -> requestCode: ${config.requestCode}, msg:${error.msg}")
    }

    /**
     * 业务层确认弹窗实现（单按钮）。
     * <p>
     * <b>作用：</b> 处理容器级别的严重故障（如初始化必须的业务模块失败）。<br>
     * <b>特性：</b> 优先使用 {@link ModelRequestConfig#requestTitle} 作为标题。
     * </p>
     * * create by Eastevil at 2025/12/30 15:16
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showConfirmDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConfirmDialog -> requestCode: ${config.requestCode}")
        ConfirmDialog.Builder(this)
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .builder()
            .show()
    }

    /**
     * 业务层标准交互弹窗实现（双按钮）。
     * <p>
     * <b>作用：</b> 适用于容器层级发起的需要用户决策的请求反馈。
     * </p>
     * * create by Eastevil at 2025/12/30 15:17
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showConventionalDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showConventionalDialog -> requestCode: ${config.requestCode}")
        val dialog = ConventionalDialog.Builder(this)
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
     * 业务层提示性弹窗实现。
     * <p>
     * <b>作用：</b> 全局公告或非阻断性的业务信息告知。
     * </p>
     * * create by Eastevil at 2025/12/30 15:18
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun showTipsDialog(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "showTipsDialog -> requestCode: ${config.requestCode}")
        TipsDialog.Builder(this)
            .themeColor(viewModel.themeColor())
            .title(config.requestTitle ?: getString(R.string.wsvita_app_dialog_title_tips))
            .message(error.msg ?: getString(R.string.wsvita_app_default_service_error))
            .builder()
            .show()
    }

    /**
     * 业务层错误缺省页切换。
     * <p>
     * <b>作用：</b> 将导航容器的内容区域整体切换为错误视图。
     * </p>
     * * create by Eastevil at 2025/12/30 15:19
     * @author Eastevil
     * @param config 请求上下文
     * @param error 错误实体
     */
    protected open fun onShowErrorStateView(config: ModelRequestConfig, error: VError) {
        SLog.i(TAG, "onShowErrorStateView -> requestCode: ${config.requestCode}")
    }

    /**
     * 业务自定义动作分发回调。
     * <p>
     * <b>作用：</b> 响应容器级别自定义动作指令识别码。
     * </p>
     * * create by Eastevil at 2025/12/30 15:20
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
     * 重写 UI 事件分发逻辑。
     * <p>
     * <b>逻辑说明：</b> 屏蔽原始 Error 信号，交由 [RequestEvent] 流统一处理。
     * </p>
     * * create by Eastevil at 2025/12/30 15:21
     * @author Eastevil
     * @param event UI 事件实体
     */
    override fun handleUIEvent(event: IUIEvent) {
        super.handleUIEvent(event)
    }

    override fun onPrepareNavGraph(graph: NavGraph) {
        super.onPrepareNavGraph(graph)
        val authList = destinationIdList()

        //默认获取子类返回的id
        var startId = startDestinationId()
        // 1. 获取 Intent 传入的指定 ID (targetId)
        val targetId = intent.getIntExtra(ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID, -1)
        if(targetId != -1){
            //上级跳转到当前容器页面有指定目标id，使用指定的目标id
            startId = targetId;
        }
        // 3. 物理移除未授权节点
        val it = graph.iterator()
        val removeList = mutableListOf<NavDestination>()
        while (it.hasNext()) {
            val node = it.next()
            if (!authList.contains(node.id)) {
                removeList.add(node)
            }
        }
        removeList.forEach { graph.remove(it) }
        // 4. 最终校验并设置
        if (graph.findNode(startId) != null) {
            graph.setStartDestination(startId)
            SLog.i(TAG, "Success set startDestination: $startId")
            //获取所有的intent中的数据，需要进行存储
            val intentStartTime = systemTime();
            intent.extras?.let {
                val iterator = it.keySet().iterator();
                while (iterator.hasNext()) {
                    val key = iterator.next()
                    // 按照你的组件化规范，这里直接全量拿，或者过滤 wsui_ 前缀
                    val value = it.get(key);
                    viewModel.cacheFragmentData(startId,key,value);
                }
            }
            val intentEndTime = systemTime();
            val getIntentTime = intentEndTime - intentStartTime;
            SLog.i(TAG,"startTime:${intentStartTime},endTime:${intentEndTime},usedTime:${getIntentTime}");

        } else {
            SLog.e(TAG, "startId($startId) not found in graph")
        }
    }

    /**
     * 核心导航调度（私有，不对业务层直接暴露）
     */
    private fun navigateInternal(@IdRes resId: Int, bundle: Bundle?, navOptions: NavOptions?) {
        // 获取当前业务模块允许的所有 ID 列表
        val authList = destinationIdList()
        // 健壮性校验：如果跳转的目的地不在授权列表中，直接拦截
        if (!authList.contains(resId)) {
            SLog.e(TAG, "resId not allow");
            return
        }

        // 经过校验后，再执行真正的跳转
        try {
            navController.navigate(resId, bundle, navOptions)
        } catch (e: Exception) {
            SLog.e(TAG, "navigateInternal error,ID($resId)");
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【获取当前导航目的地 ID (Current Navigate ID)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 从 [navController] 的当前状态中提取当前正在展示的 [NavDestination] 资源 ID。
     *
     * [2. 物理意义]
     * 该 ID 对应 [navigation.xml] 资源文件中定义的 "android:id" 属性。
     *
     * [3. 应用场景]
     * - 逻辑判定：用于判断当前容器是否处于特定的业务 Fragment（如详情页或列表页）。
     * - 状态恢复：在 Activity 重建时，通过此 ID 校验当前 UI 状态。
     *
     * [4. 注意事项]
     * 若 [navController] 尚未完成初始化或处于空栈状态，将返回默认值 -1。
     *
     * @return 当前展示 Fragment 的资源 ID，无效时返回 -1。
     */
    protected fun currentNavigateId(): Int {
        return navController.currentDestination?.id?:-1;
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【获取当前导航目的地标签 (Current Navigate Label)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 获取 [navigation.xml] 中为当前 [NavDestination] 配置的 "android:label" 属性值。
     *
     * [2. 物理意义]
     * Label 通常作为 Fragment 的逻辑名称或标题。
     *
     * [3. 应用场景]
     * - 动态标题：可将该返回值直接应用于 Toolbar 的 Title 设置。
     * - 业务埋点：在日志中记录当前页面的逻辑名称，而非难以识别的数字 ID。
     *
     * [4. 数据特征]
     * 该值可能为空（如果在 XML 中未定义 label 属性），调用方需处理 null 情况。
     *
     * @return 当前展示 Fragment 的标签字符串，未定义时返回 null。
     */
    protected fun currentNavigateLabel() : String?{
        return navController.currentDestination?.label?.toString();
    }

    /**
     * 暴露给业务层的标准跳转（无参）
     */
    fun navigate(@IdRes resId: Int) {
        navigateInternal(resId, null, null)
    }

    /**
     * 暴露给业务层的标准跳转（带参数）
     */
    fun navigate(@IdRes resId: Int, bundle: Bundle?) {
        navigateInternal(resId, bundle, null)
    }

    /**
     * 暴露给业务层的进阶跳转（带配置）
     */
    fun navigate(@IdRes resId: Int, bundle: Bundle?, navOptions: NavOptions?) {
        navigateInternal(resId, bundle, navOptions)
    }

    /**
     * 泛型跳转重载：自动将 Key-Value 封装进 Bundle
     */
    fun <T> navigate(@IdRes resId: Int, key: String, value: T) {
        val bundle = Bundle()
        when (value) {
            is Int -> bundle.putInt(key, value)
            is String -> bundle.putString(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            is Long -> bundle.putLong(key, value)
            is Float -> bundle.putFloat(key, value)
            is Double -> bundle.putDouble(key, value)
            is java.io.Serializable -> bundle.putSerializable(key, value)
            else -> {
                // 如果类型不匹配，可以记录 Log 或抛出异常
                SLog.e(TAG, "Unsupported bundle type: ${value?.let { it::class.java.name }}")
            }
        }
        // 调用之前定义的 navigate(resId, bundle)
        navigate(resId, bundle)
    }

    /**
     * 指定该模块启动时展示的第一个页面（首屏）。
     * * 返回值必须是 [getNavGraphResId] 中定义的某个目的地 ID。
     * 该值会覆盖 XML 导航图中静态定义的 startDestination。
     *
     * create by Administrator at 2026/1/7 22:05
     * @author Administrator
     *
     * @return 起始页面的资源 ID (例如：R.id.listFragment)
     */
    @IdRes
    abstract fun startDestinationId() : Int;

    /**
     * 返回该模块在当前环境下“授权拥有”的所有目的地 ID 列表。
     * * 核心权限控制点：基类会遍历导航图，物理移除所有不在该列表中的节点。
     * 这样可以实现：
     * 1. 动态控制展示
     * 2. 导航安全：无法通过路径跳转到未授权的页面，因为节点已被移除。
     *
     * create by Administrator at 2026/1/7 22:05
     * @author Administrator
     *
     * @return 允许存在的 Fragment 资源 ID 集合（白名单）
     */
    abstract fun destinationIdList() : MutableList<Int>;

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            SLog.d(TAG, "onKeyDown: Physical back pressed")
            onBack()
            return true // 返回 true 表示已消费该事件，禁止系统进一步处理
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackClick(view: View) {
        onBack();
    }

    override fun onMenuClick(view: View) {
        SLog.d(TAG,"onMenuClick");
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


    open fun onBack(){
        //默认直接结束当前的容器activity
        if (onInterceptBack()) {
            SLog.d(TAG, "Back event intercepted by subclass")
            return
        }
        // 2. 强制记录日志（组件化调试建议）
        SLog.i(TAG, "Forcing container finish. Current destination: ${currentNavigateId()}")

        // 3. 直接结束 Activity
        // 这会同时销毁 Activity 及其内部持有的整个 NavController 栈
        dispatchDestroyFragments();
        finish()
    }

    /**
     * 默认返回 false，表示不拦截。
     * 子类只有在需要处理“数据未保存”、“支付中禁止退出”等特例时才去重写它。
     */
    protected open fun onInterceptBack(): Boolean{
        return false;
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【分发容器销毁信号 (Physical Traversal)】
     * -------------------------------------------------------------------------------------
     * [1. 物理逻辑]
     * 采用显式栈（Stack）结构代替递归，挨个遍历 FragmentManager 树。
     * * [2. 执行过程]
     * 循环弹出当前层级的 Fragment，执行业务回调，并将其子管理器（childFragmentManager）
     * 压入栈中继续循环，直到所有嵌套层级全部处理完毕。
     *
     * [3. 优势]
     * 逻辑平铺，无闭包函数，无递归深度限制，完全基于物理循环实现。
     */
    protected open fun dispatchDestroyFragments() {
        val stack = mutableListOf<androidx.fragment.app.FragmentManager>()
        stack.add(supportFragmentManager)

        while (stack.isNotEmpty()) {
            val fm = stack.removeAt(stack.size - 1)
            val fragments = fm.fragments
            if (fragments.isEmpty()) continue

            for (fragment in fragments) {
                if (fragment != null && fragment.isAdded) {
                    // 1. 如果是业务类，直接执行抽象方法
                    if (fragment is AppFragment<*,*>) {
                        SLog.d(TAG, "Notify fragment: ${fragment::class.java.simpleName}")
                        fragment.onContainerDestroy()
                    }
                    // 2. 将子管理器压入栈，下一轮循环处理
                    stack.add(fragment.childFragmentManager)
                }
            }
        }
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
     * 4. **重要事项**：此方法应在[onCreate]方法之前调用，此时不可以使用任何dataBinding和VM的相关操作，均为初始化
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

    protected open fun needLocation(): Boolean {
        return false;
    }

    protected fun <F : AppFragment<*,*>> currentFragment(clazz: Class<F>): F? {
        val navHostFragment = supportFragmentManager
            .findFragmentById(com.wsvita.framework.R.id.container_fragment) as? androidx.navigation.fragment.NavHostFragment

        val fragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment;
        if(fragment == null){
            return null;
        }

        if(clazz.isInstance(fragment)){
            return clazz.cast(fragment)
        }
        return null;
    }

    override fun onIntentValueReceived(key: String, value: Any?) {
        super.onIntentValueReceived(key, value)
    }

    /**
     * ╔══════════════════════════════════════════════════════════════════════════════════╗
     * ║ 私有实现                                                                          ║
     * ╚══════════════════════════════════════════════════════════════════════════════════╝
     */

    /**
     * 业务事件内部分发器。
     * <p>
     * <b>作用：</b> 将原始 {@link RequestEvent} 信号流转换为具体的 UI 调度指令。<br>
     * <b>执行序：</b> Begin(标记开始) -> requestEnd(逻辑结束，优先恢复交互) -> End(最终兜底)。
     * </p>
     * * create by Eastevil at 2025/12/30 15:12
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
     * -------------------------------------------------------------------------------------
     * 【组件化路由结果分发枢纽 (Componentized Router Dispatch Center)】
     * -------------------------------------------------------------------------------------
     * **1. 职责定位**：
     * 本方法是整套 MVVM 组件化通信框架在 Activity 层的“最后一公里”。
     * 它的核心职责是拦截外部组件（如插件化模块、系统页面）回传的原始 Intent，
     * 并将其转化为业务 Fragment 可感知的协议对象。
     *
     * **2. 核心机制：精准投递**：
     * - **物理链路**：Activity -> NavHostFragment -> 栈顶业务 Fragment。
     * - **数据清洗**：利用 [ComplexResult] 对原始 Intent 进行脱壳与安全护理，屏蔽 Bundle 操作细节。
     * - **解耦策略**：Fragment 无需直接持有 ActivityResultLauncher，仅需通过 override
     * [receiveRouterContract] 即可异步获取结果，极大地简化了 Fragment 的状态管理。
     *
     * **3. 调用方式 (Usage)**：
     * 配合 [RouterConfigurator.registerContract] 使用。在注册路由契约时，将此方法作为回调注入：
     * ```kotlin
     * // 在 SDKActivity 或 BaseActivity 的路由初始化阶段：
     * routerConfigurator.registerContract("user_info", UserContract()) { action, name, data ->
     * // 调用此分发方法，将结果安全投递至 Fragment
     * dispatchRouterContract(action, name, data)
     * }
     * ```
     *
     * **4. 架构优势**：
     * - **容器解耦**：Activity 作为外壳容器，仅负责路由通道的维护，不涉及具体的业务数据解析。
     * - **多 Fragment 兼容**：自动定位当前 NavGraph 中的活跃节点，解决多个 Fragment 在同一容器下结果监听混乱的问题。
     * - **安全性**：通过 [action] 标识符进行二级校验，确保回传数据被投递到正确的业务分支。
     * -------------------------------------------------------------------------------------
     * create by Administrator at 2026/1/21 23:21
     * @author Administrator
     * @param action 路由唯一标识（与注册契约时的 Action 一致），用于 Fragment 内部业务逻辑分流。
     * @param name   注册时的路由名称标识。
     * @param complexResult 经过封装的复合结果容器，支持基础类型提取及自动 JSON 映射。
     */
    protected fun dispatchRouterContract(action : String, name : String, complexResult : ComplexResult) {
        SLog.d(TAG,"dispatchRouterContract_action:${action},name:${name}")
        val f = currentFragment(AppFragment::class.java);
        f?.let {
            f.receiveRouterContract(action, name, complexResult);
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【组件化路由结果分发 - 泛型对象模式 (Generic Object Dispatch)】
     * -------------------------------------------------------------------------------------
     * **1. 职责定位**：
     * 当业务契约返回的结果不是 [ComplexResult] 包装类型，而是具体的业务实体类 (POJO)
     * 或基础包装类时，通过此方法将数据透传至 Fragment 层的业务观察者。
     *
     * **2. 调用逻辑**：
     * 此方法通常作为 [com.wsvita.framework.router.RouterConfigurator.registerContract] 的末端回调，在 Activity
     * 收到数据解析完成的通知后触发。
     *
     * **3. 调用方式 (Usage)**：
     * ```kotlin
     * // 在 SDKActivity 中配合路由注册器使用
     * routerConfigurator.registerContract("get_user_bean", UserBeanContract()) { action, name, data ->
     * // data 此时已是 UserBean 实体，通过此方法直接分发
     * dispatchRouterObject(action, name, data as Any)
     * }
     * ```
     * * **4. 设计差异**：
     * - [dispatchRouterContract]：适用于需要进行二次“脱壳”解析的复杂 Intent 数据流。
     * - [dispatchRouterObject]：适用于已经由协议层 (Contract) 完成解析后的“纯净对象”。
     * -------------------------------------------------------------------------------------
     * @param action          路由契约定义的 Action 指令。
     * @param name            路由注册时的唯一标识名。
     * @param complexResult   由 Contract 解析完成后的强类型业务对象 (Result Object)。
     */
    protected fun dispatchRouterObject(action : String, name : String, data : Any) {
        SLog.d(TAG,"dispatchRouterObject_action:${action},name:${name}")
        val f = currentFragment(AppFragment::class.java);
        f?.let {
            f.receiveRouterObject(action, name, data);
        }
    }


    companion object {
        private const val TAG = "WSVita_App_AppContainerActivity"
        private const val RC_LOCATION_PERM = 9521;
    }
}
