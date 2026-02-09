package com.wsvita.framework.commons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.utils.VToast
import com.wsvita.framework.widget.dialog.LoadingDialog
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * SDK 级业务 Fragment 基类
 * 集成了 DataBinding 自动绑定、ViewModel 生命周期感知、UI事件分发、自动化参数解析及组件化路由能力。
 */
abstract class SDKFragment<D : ViewDataBinding, V : SDKViewModel> : BaseFragment() {

    private var _binding: D? = null
    protected val dataBinding: D get() = _binding!!

    protected lateinit var viewModel: V

    private var mIsInstanceInitialized = false

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SLog.i(TAG, "onCreateView start, time: ${systemTime()}")
        _binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SLog.i(TAG, "onViewCreated start")

        // 1. 实例化 ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[getVMClass()]

        // 2. 自动绑定 ViewModel 到 DataBinding (反射方式)
        if (isBoundViewModel()) {
            bindViewModelByReflection()
        }
        // 3.解析静态参数，为 initView 的 UI 分支逻辑提供数据。
        if(savedInstanceState != null){
            handleArguments(savedInstanceState);
        }
        if(arguments != null){
            handleArguments(arguments)
        }
        // 4. 建立监听管道
        observeUIEvents() // 基础 UI 事件 (Loading/Toast)
        addObserve()      // 业务自定义事件
        // 5. 触发业务初始化
        initView(savedInstanceState)
        // 6. 初始化视图与参数解析
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        if (shouldInitializeModel(savedInstanceState)) {
            SLog.i(TAG, "Executing business init: initModel and handleArguments")
            viewModel.initModel()
        }
        SLog.i(TAG, "onViewCreated end")
    }

    /**
     * 通过反射将 viewModel 实例注入到 XML 的 "viewModel" 变量中
     */
    private fun bindViewModelByReflection() {
        try {
            // 1. 获取生成的 Binding 类（如 FragmentUserBinding）
            val bindingClass = dataBinding.javaClass
            // 注意：viewModel 的类型可能是具体的类，所以用 getMethods 遍历更稳妥
            val setterMethod = bindingClass.methods.find {
                it.name == "setViewModel" && it.parameterTypes.size == 1
            }

            if (setterMethod != null) {
                setterMethod.invoke(dataBinding, viewModel)
            } else {
                SLog.w(TAG, "Method setViewModel not found in ${bindingClass.simpleName}")
            }
        } catch (e: Exception) {
            SLog.e(TAG, "Reflection injection failed: ${e.message}")
        }
    }

    /**
     * 解析 Fragment 的 Arguments 参数 (对应 Activity 的 Intent 解析)
     */
    private fun handleArguments(args : Bundle?) {
        val keys = autoArgumentValue()
        if (keys != null && args != null) {
            keys.forEach { key ->
                if (args.containsKey(key)) {
                    val value = args.get(key)
                    onArgumentValueReceived(key, value)
                    dispatchTypeArgument(key, value)
                }
            }
        }
    }

    /**
     * 自动化参数注入钩子：子类返回需要解析的 Key 列表
     */
    protected open fun autoArgumentValue(): MutableList<String>? = null

    private fun dispatchTypeArgument(key: String, value: Any?) {
        when (value) {
            is Int -> onArgumentReceivedInt(key, value)
            is String -> onArgumentReceivedString(key, value)
            is Boolean -> onArgumentReceivedBoolean(key, value)
            is Long -> onArgumentReceivedLong(key, value)
            is Serializable -> onArgumentReceivedSerializable(key, value)
            else -> argumentTypeError(key)
        }
    }

    /**
     * 判定是否需要执行业务初始化（initModel）
     * 合理逻辑：如果 savedInstanceState 为空，说明不是系统内存回收导致的重建，
     * 那么只需判断当前 Fragment 实例是否是第一次执行 onViewCreated。
     */
    private fun shouldInitializeModel(savedInstanceState: Bundle?): Boolean {
        // 方案：在 Fragment 实例中维护一个简单的私有变量。
        // 因为 Navigation 场景下，从 B 返回 A，Fragment A 实例没动，变量值会保留。
        if (savedInstanceState != null) return false // 系统重建交给 SavedStateHandle 处理

        // 如果你不想在基类加变量，可以利用 ViewModel 的公开 LiveData 是否有值来判定
        // 但最物理的做法是记录实例的初始化状态（此变量随 Fragment 实例销毁而销毁）
        return if (mIsInstanceInitialized) false else {
            mIsInstanceInitialized = true
            true
        }
    }

    // --- 参数回调钩子 (同 Activity) ---
    protected open fun onArgumentValueReceived(key: String, value: Any?) {

    }
    protected open fun onArgumentReceivedInt(key: String, value: Int) {

    }
    protected open fun onArgumentReceivedString(key: String, value: String) {

    }
    protected open fun onArgumentReceivedBoolean(key: String, value: Boolean) {

    }
    protected open fun onArgumentReceivedLong(key: String, value: Long) {

    }
    protected open fun onArgumentReceivedSerializable(key: String, value: Serializable) {

    }
    protected open fun argumentTypeError(key: String) { SLog.e(TAG, "Type error: $key") }

    /**
     * 接收来自宿主容器（SDKNavContainerActivity）透传的数据
     * * [核心用途]：
     * 1. 解决 Navigation 模式下，Activity 接收到新 Intent 或 Result 时，无法直接触达当前 Fragment 的问题。
     * 2. 在组件化开发中，作为容器与 Fragment 通信的标准入口。
     * * [工作流]：
     * 1. SDKNavContainerActivity 通过 onIntentValueReceived 捕获外部数据。
     * 2. Activity 自动寻找当前显示的 primaryNavigationFragment。
     * 3. 跨层级调用本方法。
     * 4. 内部自动触发 [dispatchTypeArgument] 实现数据的类型化分发。
     * * [注意事项]：
     * - 子类通常无需重写此方法，只需重写具体的 onArgumentReceivedXxx 回调即可。
     * - 若子类有特殊的拦截逻辑（如根据 Key 决定是否刷新 UI），可重写此方法。
     *
     * @param key 数据的标识 Key，对应 Intent 的 Key 或自定义业务 Key。
     * @param value 传递的数据对象，支持基础类型及 Serializable。
     */
    fun onReceiveContainerData(key: String,value : Any?){
        SLog.d(TAG,"onReceiveContainerData,key:${key},value:${value}");
        dispatchTypeArgument(key,value);
    }

    public abstract fun onViewClick(view : View);

    // --- 基础 UI 处理 ---
    private fun observeUIEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event -> handleUIEvent(event) }
            }
        }
    }

    protected open fun handleUIEvent(event: IUIEvent) {
        when (event) {
            is IUIEvent.ShowLoading -> showLoading(event.msg)
            is IUIEvent.HideLoading -> hideLoading()
            is IUIEvent.ShowToast -> toast(event.message)
            is IUIEvent.ShowResToast-> toast(event.resId);
            else -> SLog.d(TAG, "Unhandled event: $event")
        }
    }

    protected abstract fun layoutId(): Int
    protected abstract fun getVMClass(): Class<V>
    protected open fun initView(savedInstanceState: Bundle?) {}


    protected open fun addObserve() {
        viewModel.success.observe(viewLifecycleOwner, Observer {
            onSuccess(it);
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            onError(it);
        })
    }

    private fun showLoading(msg: String?) {
        if (isAdded && !requireActivity().isFinishing) loadingDialog.show()
    }

    private fun hideLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    override fun onDestroyView() {
        hideLoading()
        _binding = null
        super.onDestroyView()
    }

    protected fun toast(msg: CharSequence?){
        VToast.show(msg)
    }

    protected fun toast(resId : Int){
        VToast.show(resId);
    }

    protected fun toast(@StringRes resId: Int, vararg formatArgs: String){
        val text = getString(resId,formatArgs);
        toast(text);
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
        toast(error.msg);
    }


    companion object {
        private const val TAG = "WSVita_Framework_SDKFragment==>"
    }
}
