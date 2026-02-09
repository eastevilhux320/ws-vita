package com.wsvita.framework.commons

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wsvita.framework.entity.ErrorHolder
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.entity.SuccessHolder
import com.wsvita.framework.router.BaseComponentResult
import com.wsvita.framework.router.ComponentResponse
import com.wsvita.framework.router.FinishParam
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.utils.VToast
import com.wsvita.framework.widget.dialog.LoadingDialog
import kotlinx.coroutines.launch
import java.io.Serializable

abstract class SDKActivity<D : ViewDataBinding, V : SDKViewModel> : BaseActivity() {
    /**
     * DataBinding 对象
     */
    protected lateinit var dataBinding: D

    /**
     * ViewModel 对象
     */
    protected lateinit var viewModel: V

    // SDKActivity.kt
    private val routerCache = HashMap<String, ActivityResultLauncher<*>>()

    /**
     * 延迟初始化 LoadingDialog
     */
    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. 路由自动化注册：必须在 super.onCreate 之前执行，确保符合系统生命周期约束
        val configurator = RouterConfigurator()
        // 2. 调用子类重写的钩子方法，填充配置器
        prepareRouters(configurator)
        // 3. 统一遍历注册
        configurator.configs.forEach { (name, pair) ->
            val (contract, callback) = pair
            // 动态注册路由，并处理 ComponentResponse 结果分发
            routerCache[name] = registerForActivityResult(contract) { response ->
                if (response is ComponentResponse.Success) {
                    response.data?.let { callback.invoke(it) }
                } else {
                    SLog.i(TAG, "Router action '$name' not success or canceled.")
                }
            }
        }

        super.onCreate(savedInstanceState)
        // 4. 初始化 DataBinding 容器
        SLog.d(TAG,"onCreate invoke,time:${systemTime()}");
        dataBinding = DataBindingUtil.setContentView(this, layoutId())
        dataBinding.lifecycleOwner = this

        // 5. 实例化 ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        viewModel = ViewModelProvider(this, factory)[getVMClass()]
        SLog.i(TAG, "view model init success")

        // 6. 建立数据管道 (Data-to-View)
        // 在数据初始化之前，先通过反射将 VM 实例注入 XML，确保首帧渲染不为 null
        if (isBoundViewModel()) {
            SLog.i(TAG, "need bound viewModel by self,bindViewModelByReflection");
            bindViewModelByReflection()
        }

        // 7. 建立观察者模式 (Logic-to-UI)
        // 必须在 initModel 之前挂载监听，确保初始化过程中的所有 LiveData/Flow 变化都能被捕获
        observeUIEvents() // 基础 UI 事件
        addObserve()      // 业务自定义事件
        SLog.i(TAG, "ui success,add observe success");

        // 8. 触发业务初始化 (The Seed of Data)
        // 这是所有数据流的起点，在一切准备就绪后最后执行
        viewModel.initModel()
        SLog.i(TAG, "view model initModel invoke success")

        // 9. 生命周期感知与视图配置
        lifecycle.addObserver(viewModel)
        initView(savedInstanceState)
        SLog.d(TAG,"onCreate invoke ok,time:${systemTime()}");
    }

    /**
     * 业务数据观察点
     * 子类重写此方法以实现具体的业务逻辑监听
     */
    protected open fun addObserve() {

        viewModel.success.observe(this, Observer {
            onSuccess(it);
        })

        viewModel.error.observe(this, Observer {
            onError(it);
        })
    }

    /**
     *
     * 自动绑定开关：决定框架是否通过反射将 [viewModel] 注入到 [dataBinding] 中。
     * 若子类布局中未定义该变量或需要手动管理绑定逻辑，请重写此方法并返回 false。
     *
     * create by Administrator at 2026/1/2 0:54
     * @author Administrator
     * @return
     *      若返回 true，框架将寻找布局中名为 "viewModel" 的变量并赋值；
     */
    protected open fun isBoundViewModel(): Boolean {
        return true;
    }

    /**
     * 收集来自 SDKViewModel 的 UI 交互信号 (Loading, Toast等)
     * 私有方法，确保框架基础功能在所有子类中强制执行
     */
    private fun observeUIEvents() {
        lifecycleScope.launch {
            // 使用 repeatOnLifecycle 确保只在界面可见时处理 UI 逻辑
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    handleUIEvent(event)
                }
            }
        }
    }

    /**
     * 响应 UI 事件，如显示 Loading、弹出 Toast
     * 子类可以重写此方法以实现自定义的 UI 交互效果，但建议调用 super
     */
    protected open fun handleUIEvent(event: IUIEvent) {
        when (event) {
            is IUIEvent.ShowLoading -> {
                showLoadingDialog(event.msg)
            }
            is IUIEvent.HideLoading -> {
                hideLoadingDialog()
            }
            is IUIEvent.ShowToast -> {
                // 弹出提示,
                // 这里只是预留了，没有具体的实现，后期如果需要，可以进行扩展，目前并没有这里的逻辑
            }
            else -> {
                SLog.d(TAG, "Receive Event: $event")
            }
        }
    }

    protected abstract fun layoutId(): Int
    protected abstract fun getVMClass(): Class<V>
    public abstract fun onViewClick(view : View);

    /** 弹出短 Toast */
    protected fun toast(msg: CharSequence?) = VToast.show(msg)

    /** 弹出短 Toast (资源ID) */
    protected fun toast(@StringRes resId: Int) = VToast.show(resId)

    /** 弹出长 Toast */
    protected fun longToast(msg: CharSequence?) = VToast.showLong(msg)

    /** 弹出长 Toast (资源ID) */
    protected fun longToast(@StringRes resId: Int) = VToast.showLong(resId)

    protected open fun initView(savedInstanceState: Bundle?) {
        SLog.d(TAG, "initView start");
        val i = intent;
        i?.let {
            handleIntent(it);
        }
    }

    /**
     * 处理 Intent 数据
     */
    protected open fun handleIntent(i : Intent) {
        val keys = autoIntentValue()
        val extras = i.extras
        if (keys != null && extras != null) {
            keys.forEach { key ->
                if (extras.containsKey(key)) {
                    val value = extras.get(key)
                    // 1. 先进行基础的通用分发
                    onIntentValueReceived(key, value)

                    // 2. 根据类型进行精确分发
                    dispatchTypeIntent(key, value)
                }
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【自动化参数注入配置钩子 (Auto Intent Configuration Hook)】
     * -------------------------------------------------------------------------------------
     * 该方法是实现 Activity 自动解析 Intent 数据的“指挥中心”。通过返回一个 Key 列表，
     * 告知基类哪些数据需要被自动提取并分发到对应的 [onIntentReceivedXxx] 回调中。
     *
     * - 返回 null (默认)：框架认为当前 Activity 不需要自动化解析 Intent。
     * - 返回 有值的列表：框架将启动遍历逻辑，尝试从 Intent.extras 中提取匹配这些 Key 的值。
     *
     * 1. 子类重写此方法，返回需要解析的 Key 集合：mutableListOf("USER_ID", "TYPE")。
     * 2. 子类重写对应的类型钩子（如 [onIntentReceivedString]）来接收并保存这些值。
     *
     * - 拼写检查：返回的 Key 必须与发起跳转（startActivity）时放入 Intent 的 Key 完全一致。
     * - 性能：列表仅用于声明，建议使用 [mutableListOf] 或 [arrayListOf]。
     * - 局限性：仅支持基础数据类型及 Serializable。若需特殊处理，请在 [onIntentValueReceived] 中手动转换。
     *
     * @return 需要自动解析的 Intent Key 列表；若不需要自动解析则返回 null。
     * @author Eastevil
     * @date 2026/01/04
     */
    protected open fun autoIntentValue() : MutableList<String>? {
        return null;
    }


    /**
     * 类型分发器：根据 Value 类型调用对应的钩子方法
     */
    private fun dispatchTypeIntent(key: String, value: Any?) {
        when (value) {
            is Int -> onIntentReceivedInt(key, value)
            is String -> onIntentReceivedString(key, value)
            is Boolean -> onIntentReceivedBoolean(key, value)
            is Long -> onIntentReceivedLong(key, value)
            is Float -> onIntentReceivedFloat(key, value)
            is Serializable -> onIntentReceivedSerializable(key, value)
            // 可以根据需求继续扩展 Parcelable 等
            else-> intentTypeError(key);
        }
    }

    private fun showLoadingDialog(msg: String?) {
        if (!isFinishing && !isDestroyed) {
            loadingDialog.show()
        }
    }

    private fun hideLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    override fun onDestroy() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
        super.onDestroy()
    }

    private fun bindViewModelByReflection() {
        SLog.w(TAG, "bindViewModelByReflection invoke,time:${systemTime()}");
        try {
            // 使用 activity 的 packageName 寻找 BR 类
            val brClass = Class.forName("${packageName}.BR")
            val field = brClass.getField("viewModel")
            val variableId = field.getInt(null)
            dataBinding.setVariable(variableId, viewModel)
            // 强制 DataBinding 立即执行同步，不等待下一帧
            dataBinding.executePendingBindings()
        } catch (e: Exception) {
            SLog.w(TAG, "Binding variable 'viewModel' failed: ${e.message}")
        }
    }

    /**
     * 【组件化极简注册入口】
     * 只有成功才会触发 [onSuccess]，自动过滤取消和失败。
     */
    protected fun <I, O> registerComponent(
        contract: BaseComponentResult<I, O>,
        onSuccess: (action : String,O) -> Unit
    ): androidx.activity.result.ActivityResultLauncher<I> {
        return registerForActivityResult(contract) { response ->
            if (response is ComponentResponse.Success) {
                onSuccess(contract.getAction(),response.data)
            } else {
                // 可统一处理取消或失败日志
                SLog.i(TAG, "Component action not success or canceled.")
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【自动参数注入钩子 (Intent Argument Hooks)】
     * -------------------------------------------------------------------------------------
     * 作用说明：
     * 1. 自动化：配合 [autoIntentValue] 使用，自动解析 Intent 中的 Bundle 数据并按类型分发。
     * 2. 解耦：子类无需手动调用 intent.getXXXExtra()，只需重写对应类型的回调方法。
     * 3. 安全：框架内部已完成类型检查，确保回调入参的类型绝对可靠，避免 ClassCastException。
     * 4. 规范：统一参数获取入口，便于在组件化多模块开发中追踪数据来源。
     * * 执行时机：在 Activity 生命周期 [onCreate] 早期阶段触发。
     * -------------------------------------------------------------------------------------
     */

    protected open fun onIntentReceivedInt(key: String, value: Int) {}

    protected open fun onIntentReceivedString(key: String, value: String) {}

    protected open fun onIntentReceivedBoolean(key: String, value: Boolean) {}

    protected open fun onIntentReceivedLong(key: String, value: Long) {}

    protected open fun onIntentReceivedFloat(key: String, value: Float) {}

    protected open fun onIntentReceivedSerializable(key: String, value: Serializable) {}
    /*********************************--- 自动参数注入钩子 (Intent Argument Hooks) --- **/


    /**
     * -------------------------------------------------------------------------------------
     * 【通用参数接收回调 (General Intent Value Hook)】
     * -------------------------------------------------------------------------------------
     * * [1. 作用说明]
     * 作为所有自动注入参数的总入口。当 [autoIntentValue] 中定义的 Key 在 Intent 中匹配成功时，
     * 无论其数据类型如何，都会首先触发此方法。
     *
     * [2. 调用机制]
     * - 触发时机：在 [onCreate] 过程中，[handleIntent] 遍历 Key 列表时同步调用。
     * - 调用顺序：先执行此通用回调 [onIntentValueReceived]，随后由框架自动分发至对应的
     * 类型安全回调（如 [onIntentReceivedInt] 等）。
     *
     * [3. 如何使用]
     * - 场景 A：当你不需要关心具体类型，只需根据 Key 做统一日志记录或埋点时。
     * - 场景 B：当某个参数类型较为特殊（非基础类型），且没有对应的类型钩子时。
     * - 场景 C：当你希望在子类中通过 when(key) 统一处理所有业务逻辑时。
     *
     * [4. 注意事项]
     * - 类型安全：[value] 是 Any? 类型，直接使用前必须进行安全强制转换（as?）。
     * - 执行顺序：如果在子类中重写了此方法并处理了逻辑，请考虑是否还需要在类型钩子中重复处理。
     * - 性能建议：避免在此处执行耗时的初始化逻辑，此处仅建议做赋值或简单的 UI 状态切换。
     *
     * @param key   从 Intent 中解析出的原始键名（Key）。
     * @param value 解析出的原始对象值（Object/Any），可能为 null。
     * @author Eastevil
     * @date 2026/01/04
     */
    protected open fun onIntentValueReceived(key: String, value: Any?) {
        SLog.d(TAG, "onIntentValueReceived -> key: $key, value: $value")
    }

    /**
     * **[Intent 参数类型不匹配/未知回调]**
     * 作用：当 [autoIntentValue] 中声明的 Key 存在，但其类型不在框架预设的（Int/String/Boolean等）范围内，
     * 或者解析失败时触发。
     * 场景：常用于开发阶段调试，捕获组件间跳转时由于 Key 同名但类型不一致引发的隐性 Bug。
     * @param key 出现类型问题的键名。
     */
    protected open fun intentTypeError(key : String){
        SLog.e(TAG, "intentTypeError -> key: $key")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【组件化路由回传配置钩子 (Component Router Configuration Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 该方法是组件化架构下，处理“跨模块页面跳转并获取返回结果”的统一注册中心。
     * 通过封装 Activity Result API，将“跳转动作”与“结果处理”解耦，彻底告别传统的
     * onActivityResult 逻辑分发。
     *
     * [2. 使用步骤]
     * 第一步：在子类中重写此方法。
     * 第二步：调用 [configurator.register]：
     * - [name]：唯一常量名（如 RouterKey.LOGIN_PAGE）。
     * - [contract]：具体的路由协议（如 StringRouterContract(ACTION)）。
     * - [onSuccess]：定义成功后的回调逻辑（已自动脱壳为强类型 $O$）。
     * 第三步：业务触发点调用重载的 [router(name, ...)] 方法。
     *
     * [3. 注意事项]
     * - **生命周期约束**：根据 Android 系统要求，注册必须在 Activity 达到 STARTED 状态前完成。
     * 因此，所有路由契约必须在此钩子方法内预先声明，禁止在业务运行时动态注册。
     * - **Key 唯一性**：同一个 Activity 内，[name] 作为路由标识必须唯一。
     * - **UI 安全**：[onSuccess] 回调执行时，Activity 处于返回后的 Resume 状态，View 已可用。
     * 但建议结合具体业务逻辑，注意处理异步任务与数据绑定的同步问题。
     *
     * create by Administrator at 2026/1/4 23:33
     * @author Administrator
     * @param configurator 路由配置器，提供 register 接口用于绑定协议与回调。
     * @return
     *      void
     */
    protected open fun prepareRouters(configurator: RouterConfigurator){
        SLog.d(TAG,"prepareRouters invoke,time:${systemTime()}");
    }

    /**
     * 【单参数便捷路由跳转 (Single-Parameter Router Hook)】
     *
     * [1. 作用说明]
     * 由于底层 [com.wsvita.framework.router.contract.CommonRouterContract] 统一将输入类型 I 固定为 [Bundle] 以支持多参数，
     * 本方法作为“适配器”，负责将业务层的单对 Key-Value 自动包装成 Bundle，降低子类调用的复杂度。
     *
     * [2. 核心特性]
     * - 动态 Key 绑定：支持在调用时指定 [key]，使得同一个协议可以根据不同业务场景传递不同的参数键。
     * - 类型安全：利用泛型 [I] 配合内部 [putValue] 工具，自动处理 String, Int, Serializable 等类型。
     * - 生命周期安全：内部通过 ActivityResultLauncher 触发，符合系统对页面回传的生命周期管理要求。
     *
     * create by Administrator at 2026/1/4 23:27
     * @author Administrator
     *
     * @param name  路由唯一标识名。必须是在 [prepareRouters] 中通过 configurator 注册过的 Key。
     * @param key   跳转时存入 Intent 的键名（目标页面通过此 Key 取值）。
     * @param value 跳转时携带的具体数据对象（支持基础类型、Serializable、Parcelable）。
     * @return
     *      void
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <I> router(name: String, key: String, value: I) {
        // 由于 CommonRouterContract 重构后输入 I 固定为 Bundle
        SLog.d(TAG,"router_Single-Parameter,name:${name},key:${key},value_to_string:${value.toString()}");
        val launcher = routerCache[name] as? ActivityResultLauncher<Bundle>
        if (launcher != null) {
            val bundle = Bundle().apply {
                putValue(this, key, value)
            }
            SLog.d(TAG,"router_Single-Parameter,launch");
            launcher.launch(bundle)
        } else {
            SLog.e(TAG, "Route '$name' not found! Please check prepareRouters().")
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【多参数 Key-Value 自由传参路由 (Multi-Parameter Router Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 本方法是组件化路由跳转的核心入口。它解决了传统 Intent 传参时需要
     * 反复调用 putExtra 的臃肿逻辑，允许通过 Kotlin 变长参数（vararg）一次性传递多个键值对。
     *
     * [2. 设计原理]
     * - 容器转换：由于底层 [CommonRouterContract] 契约规定输入 I 为 [Bundle]，本方法会自动将传入的
     * 多个 [Pair] 包装进一个 [Bundle] 实例中。
     * - 类型擦除处理：由于缓存的 Launcher 采用了泛型通配符，内部通过安全强制转换（as?）确保
     * 最终发送给系统的是标准的 [Bundle] 容器。
     *
     * create by Administrator at 2026/1/4 23:27
     * @author Administrator
     *
     * @param name   路由唯一标识。必须是在 [prepareRouters] 中注册过的 Key。
     * @param params 变长参数列表。使用 Kotlin 的 "Key" to Value 语法糖传入。
     */
    protected fun router(name: String, vararg params: Pair<String, Any>) {
        SLog.d(TAG,"router_Multi-Parameter,name:${name}");
        val launcher = routerCache[name] as? ActivityResultLauncher<Bundle>
        if (launcher != null) {
            val bundle = Bundle().apply {
                params.forEach { (k, v) -> putValue(this, k, v) }
            }
            SLog.d(TAG,"router_Multi-Parameter,launch");
            launcher.launch(bundle)
        } else {
            SLog.e(TAG, "Route '$name' not found!")
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【无参数便捷路由跳转 (No-Argument Router Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 本方法是路由跳转的简化版本，专门用于处理不需要传递任何初始数据的页面跳转逻辑。
     *
     * [2. 设计原理 - 协议适配]
     * 由于重构后的 [CommonRouterContract] 统一要求输入类型 I 为 [Bundle]，本方法不再传递
     * 传统的 [Unit] 占位符，而是传递 [Bundle.EMPTY]。
     * 这样做确保了在底层系统调用 [ActivityResultLauncher.launch] 时，能够完美对齐契约要求的
     * 输入类型，避免类型转换异常。
     *
     * create by Administrator at 2026/1/4 23:27
     * @author Administrator
     *
     * @param name 路由唯一标识名。必须是在 [prepareRouters] 中通过 configurator 注册过的 Key。
     */
    protected fun router(name: String) {
        SLog.d(TAG,"router_No-Argument,name:${name}");
        val launcher = routerCache[name] as? ActivityResultLauncher<Bundle>
        if (launcher != null) {
            SLog.d(TAG,"router_No-Argument,launch");
            launcher.launch(Bundle.EMPTY)
        } else {
            SLog.e(TAG, "Route '$name' not found!")
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【万能 RawBundle 直接跳转 (Universal Bundle Router Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 本方法是路由体系中的“终极自定义通道”，允许调用者直接传入一个预先构建好的 [Bundle] 实例。
     * 它主要配合 [RawBundleContract] 使用，能够实现输入（传参）与输出（回调结果）的完全自定义。
     *
     * [2. 适用场景]
     * - 复杂参数结构：当需要传递嵌套 Bundle、大量 Parcelable 列表或非标准 Key-Value 时。
     * - 动态透传：当需要将上一个 Activity 收到的整个 Intent extras 直接转发给下一个 Activity 时。
     * - 逃生通道：当现有的 String/IntRouterContract 无法满足特殊的业务契约要求时。
     *
     * [3. 设计原理]
     * - 绕过封装：不同于其他 router 方法会进行自动装箱，本方法直接将 [bundle] 对象通过
     * [ActivityResultLauncher.launch] 发送出去，减少了中间转换层。
     *
     * create by Administrator at 2026/1/4 23:27
     * @author Administrator
     *
     * @param name   路由唯一标识名。必须是在 [prepareRouters] 中注册过的 Key。
     * @param bundle 已经填充好业务数据的 Bundle 实例。
     * @return
     *      void
     */
    protected fun router(name: String, bundle: Bundle) {
        val launcher = routerCache[name] as? ActivityResultLauncher<Bundle>
        launcher?.launch(bundle) ?: SLog.e(TAG, "Route '$name' not found!")
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【单参数容器路由跳转 (Single-Parameter Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 专门用于启动 [com.wsvita.framework.commons.AppContainerActivity] 及其子类。
     * 本方法在传递业务参数的同时，会自动注入导航协议所需的目的地 ID，实现“一步到位”的跨页面 Fragment 导航。
     *
     * [2. 设计原理]
     * - 协议中转：将 [fragmentId] 存入 [ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID]。
     * - 业务透传：将 [key] 与 [value] 平铺存入同一个 Bundle，由底层 router 方法统一发射。
     * - 自动对齐：目标容器在启动时会拦截此 ID 并自动切换 NavGraph 的起始目的地。
     *
     * [3. 使用方式]
     * routerContainer("hitch_router", R.id.f_hitch_detail, "order_id", "SN123456")
     *
     * [4. 注意事项]
     * - 确保 [name] 已在 prepareRouters 中注册且对应的是容器类 Activity。
     * - 业务参数将平铺在 Intent 根部，目标 Fragment 需通过 arguments 直接获取。
     *
     * create by Administrator at 2026/1/7 23:50
     * @param name       路由唯一标识名。
     * @param fragmentId 目标容器中预定义的 Fragment 资源 ID。
     * @param key        业务参数键名。
     * @param value      业务参数具体值。
     */
    fun <I> routerContainer(name: String, @IdRes fragmentId: Int, key: String, value: I) {
        val bundle = Bundle()
        // 注入业务参数
        putValue(bundle, key, value)
        // 注入目标 Fragment ID 协议
        putValue(bundle, ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID, fragmentId)

        SLog.d(TAG, "routerContainer_Single: name=$name, target=$fragmentId, key=$key")
        // 调用底层的万能 Bundle 跳转方法
        router(name, bundle)
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【无参数容器路由跳转 (No-Argument Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 简化版的容器跳转方法，适用于仅需要指定显示某个 Fragment，但不需要传递任何业务初始化数据的场景。
     *
     * [2. 设计原理]
     * 仅构建包含导航协议 ID 的 Bundle。由于不携带业务数据，该方法能有效降低简单跳转时的代码冗余。
     *
     * [3. 使用方式]
     * routerContainer("hitch_router", R.id.f_hitch_list)
     *
     * [4. 生命周期说明]
     * 内部依然通过 [ActivityResultLauncher] 触发，符合系统对页面跳转的生命周期管理要求。
     *
     * create by Administrator at 2026/1/7 23:50
     * @param name       路由唯一标识名。
     * @param fragmentId 目标容器中预定义的 Fragment 资源 ID。
     */
    fun routerContainer(name: String, @IdRes fragmentId: Int) {
        val bundle = Bundle()
        putValue(bundle, ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID, fragmentId)

        SLog.d(TAG, "routerContainer_NoArg: name=$name, target=$fragmentId")
        router(name, bundle)
    }

    fun routerContainer(name: String) {
        val bundle = Bundle()
        router(name, bundle)
    }


    /**
     * -------------------------------------------------------------------------------------
     * 【多参数容器路由跳转 (Multi-Parameter Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 容器跳转的高级入口，允许在指定目的地 Fragment 的同时，利用 Kotlin 变长参数（vararg）一次性传递多个业务键值对。
     *
     * [2. 设计原理]
     * - 容器集成：方法内部会自动将 [fragmentId] 与 [params] 中的所有 Pair 键值对合并到同一个 Bundle 实例中。
     * - 物理路径：调用此方法后，所有数据将通过 Intent extras 传递至目标 Activity，并最终注入目标 Fragment 的 arguments 中。
     *
     * [3. 使用方式]
     * routerContainer(
     * "hitch_router",
     * R.id.f_hitch_detail,
     * "order_id" to "SN9527",
     * "is_vip" to true
     * )
     *
     * [4. 注意事项]
     * 内部通过循环调用 [putValue] 工具方法处理每个参数，需确保所有参数类型均在 [putValue] 的支持范围内。
     *
     * create by Administrator at 2026/1/7 23:50
     * @param name       路由唯一标识名。
     * @param fragmentId 目标容器中预定义的 Fragment 资源 ID。
     * @param params     变长参数列表，使用 "Key" to Value 语法传入。
     */
    fun routerContainer(name: String, @IdRes fragmentId: Int, vararg params: Pair<String, Any>) {
        val bundle = Bundle()
        // 注入目标 Fragment ID 协议
        putValue(bundle, ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID, fragmentId)
        // 循环注入多个业务参数
        for (pair in params) {
            putValue(bundle, pair.first, pair.second)
        }

        SLog.d(TAG, "routerContainer_Multi: name=$name, target=$fragmentId")
        router(name, bundle)
    }

    /**
     * 内部工具方法：安全地将 Any 类型装入 Bundle
     */
    private fun putValue(bundle: Bundle, key: String, value: Any?) {
        SLog.d(TAG,"router_putValue,key:${key},value:${value}");
        when (value) {
            is String -> bundle.putString(key, value)
            is Int -> bundle.putInt(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            is Long -> bundle.putLong(key, value)
            is Float -> bundle.putFloat(key, value)
            is Double -> bundle.putDouble(key, value)
            is Serializable -> bundle.putSerializable(key, value)
            is android.os.Parcelable -> bundle.putParcelable(key, value)
            is Bundle -> bundle.putAll(value)
        }
    }

    /**
     * 【组件化返回封装】
     * * 作用：
     * 1. 规范化页面关闭时的结果回传，确保 ResultCode 始终为 RESULT_OK 以适配协议框架。
     * 2. 自动根据泛型 [T] 的类型选择合适的 Intent 存入方法。
     * 3. 彻底解耦硬编码的 Key，支持从各组件协议中传入定义的 [inputKey]。
     *
     * 使用场景：
     * 在被唤起的 Activity 中（例如登录页、协议授权页），当操作完成需要返回数据给上一个页面时调用。
     *
     * @param key   路由协议中约定的数据键名 (例如：SplashKey.PROTOCOL_RESULT_FLAG)
     * @param value 需要回传的具体数据内容
     * create by Administrator at 2026/1/4 20:28
     * @author Administrator
     * @return
     *      void
     */
    protected fun <T> finishWithResult(key: String, value: T) {
        val intent = Intent()
        when (value) {
            is String -> intent.putExtra(key, value)
            is Int -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            is Double -> intent.putExtra(key, value)
            is Long -> intent.putExtra(key, value)
            is Float -> intent.putExtra(key, value)
            is Serializable -> intent.putExtra(key, value)
            is android.os.Parcelable -> intent.putExtra(key, value)
            // 如果是 Bundle，通常建议作为整体存入 key，或者按需平铺
            is Bundle -> intent.putExtra(key, value)
            else -> {
                // 如果传入了不支持的类型，打印日志提醒，避免静默失败
                SLog.e("finishWithResult", "Unsupported data type: ${value?.let { it::class.java } ?: "null"}")
            }
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * 【组件化返回封装 - 多参数版】
     *
     * 作用：
     * 1. 规范化页面关闭时的结果回传，确保 ResultCode 始终为 [RESULT_OK] 以适配协议框架。
     * 2. 自动遍历 [FinishParam] 容器，根据内部存储的 [value] 类型自动装载至 [Intent]。
     * 3. 彻底解耦：调用方只需关注业务 Key (wsui 前缀) 与数据流，无需触碰 Intent 操作。
     *
     * 使用场景：
     * 在被唤起的 Activity 中（例如登录页、车牌识别页、协议授权页），当操作完成需要回传多个数据给上一个页面时调用。
     *
     * @param params 结果参数容器。使用 [FinishParam.create] 构建，支持链式调用存储多组键值对。
     * create by Administrator at 2026/1/21
     * @author Administrator
     */
    fun finishWithResult(params : FinishParam) {
        val intent = Intent()
        params.getMap().forEach { key, value ->
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
                is android.os.Parcelable -> intent.putExtra(key, value)
                // 如果是 Bundle，通常建议作为整体存入 key，或者按需平铺
                is Bundle -> intent.putExtra(key, value)
                else -> {
                    // 如果传入了不支持的类型，打印日志提醒，避免静默失败
                    SLog.e("finishWithResult", "Unsupported data type: ${value?.let { it::class.java } ?: "null"}")
                }
            }
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * 业务操作成功回调
     * * 核心用途：
     * 1. 作为 SDKViewModel 中 success 订阅事件的直接响应点。
     * 2. 当 Model 层通过调用 success() 发送信号后，此方法会被自动触发。
     * * 注意事项：
     * - 本方法是父类定义的公共钩子，主要用于接收订阅事件。
     * - 框架原有的网络请求或底层逻辑不会默认触发此回调，仅响应业务层的显式驱动。
     * - 子类重写此方法可实现：关闭当前页、刷新列表、弹出定制化 UI 提示等。
     *
     * create by Administrator at 2026/1/12 0:49
     * @author Administrator
     *
     * @param success 成功数据透传对象，包含业务状态码 [SuccessHolder.code] 和提示消息。
     * @return
     *      void
     */
    protected open fun onSuccess(success : SuccessHolder){
        SLog.d(TAG,"onSuccess,code:${success.code},msg:${success.msg}");
    }

    /**
     * 业务操作失败回调
     * * 核心用途：
     * 1. 作为 SDKViewModel 中 error 订阅事件的直接响应点。
     * 2. 当 Model 层校验失败或业务逻辑受阻，手动调用 error() 方法时，此回调被触发。
     * * 注意事项：
     * - 本方法仅响应父类定义的公共错误分发逻辑，不拦截框架底层的原生请求异常。
     * - 子类重写此方法可实现：根据 ErrorHolder.code 执行差异化 UI 处理。
     * - 默认不执行任何逻辑，确保业务级错误处理的灵活性。
     *
     * create by Administrator at 2026/1/12 0:49
     * @author Administrator
     *
     * @param error 失败数据透传对象，包含错误码 [ErrorHolder.code] 和异常描述 [ErrorHolder.msg]。
     * @return
     *      void
     */
    protected open fun onError(error : ErrorHolder){
        SLog.d(TAG,"onError,code:${error.code},msg:${error.msg}");
    }

    companion object {
        private const val TAG = "WSVita_Framework_SDKActivity==>";
    }
}
