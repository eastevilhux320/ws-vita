package com.wsvita.framework.commons

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.wsvita.framework.utils.SLog

abstract class BaseFragment : Fragment(){

    override fun onAttach(context: Context) {
        super.onAttach(context)
        SLog.d(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SLog.d(TAG, "onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SLog.d(TAG, "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SLog.d(TAG, "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        SLog.d(TAG, "onDetach")
    }

    /**
     * 获取系统当前时间（与 BaseActivity 保持一致）
     */
    fun systemTime(): Long {
        return System.currentTimeMillis()
    }

    fun <A : BaseActivity> getCurrentActivity(clazz: Class<A>): A? {
        val currentActivity = activity
        // 使用 clazz.isInstance 检查运行时的具体类型
        return if (clazz.isInstance(currentActivity)) {
            clazz.cast(currentActivity)
        } else {
            null
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【单参数容器路由跳转 (Single-Parameter Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 公开的容器跳转入口。允许 Activity 或其所属 Fragment 跨模块启动目标容器，并指定首屏目的地。
     *
     * [2. 跨组件访问规范]
     * - Activity 调用：直接调用即可。
     * - Fragment 调用：通过 (requireActivity() as SDKActivity).routerContainer(...) 触发。
     *
     * [3. 设计原理]
     * 内部通过 [putValue] 将业务参数与 [ModelConstants.IntentExtra.EXTRA_TARGET_FRAGMENT_ID]
     * 封装进同一 Bundle，随后委托给底层 router(name, bundle) 方法执行 launch 操作。
     *
     * [4. 使用示例]
     * routerContainer("hitch_router", R.id.f_hitch_detail, "order_id", "SN123")
     *
     * create by Administrator at 2026/1/7 23:55
     * @param name       路由唯一标识名（需在 prepareRouters 中注册）。
     * @param fragmentId 目标容器 Activity 启动后默认显示的 Fragment ID。
     * @param key        传递给目标 Fragment 的业务参数键名。
     * @param value      传递给目标 Fragment 的业务参数值。
     */
    protected fun <I> routerContainer(name: String, @IdRes fragmentId: Int, key: String, value: I) {
        (requireActivity() as? SDKActivity<*, *>)?.routerContainer(name, fragmentId, key, value)
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【无参数容器路由跳转 (No-Argument Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 公开的简化版容器跳转入口。仅执行页面切换逻辑，不携带初始业务参数。
     *
     * [2. 适用场景]
     * 常用于从当前模块跳转到另一模块的首页或列表页等不需要初始化 ID 的场景。
     *
     * [3. 物理流向]
     * 该方法生成的 Bundle 仅包含导航协议 ID，由 [AppContainerActivity] 拦截并重置 NavGraph 起点。
     *
     * [4. 使用示例]
     * routerContainer("hitch_router", R.id.f_hitch_list)
     *
     * create by Administrator at 2026/1/7 23:55
     * @param name       路由唯一标识名。
     * @param fragmentId 目标容器中的 Fragment 资源 ID。
     */
    protected fun routerContainer(name: String, @IdRes fragmentId: Int) {
        (requireActivity() as? SDKActivity<*, *>)?.routerContainer(name, fragmentId)
    }

    protected fun routerContainer(name : String){
        (requireActivity() as? SDKActivity<*, *>)?.routerContainer(name)
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【多参数容器路由跳转 (Multi-Parameter Container Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 公开的多参数容器跳转入口。支持通过变长参数（vararg）一次性传递复杂的业务数据。
     *
     * [2. 跨模块通信]
     * 本方法是 Fragment 跨模块协作的核心通道。它通过宿主 Activity 的能力，将多对键值对与
     * 导航协议 ID 统一打包，确保了组件间数据传递的原子性。
     *
     * [3. 注意事项]
     * 传入的 [params] 最终会平铺在 Intent 的 extras 中，建议使用
     * [ModelConstants.IntentExtra] 中定义的公共 Key 以保证键名的一致性。
     *
     * [4. 使用示例]
     * routerContainer("hitch_router", R.id.f_hitch_detail, "id" to 1, "type" to "vip")
     *
     * create by Administrator at 2026/1/7 23:55
     * @param name       路由唯一标识名。
     * @param fragmentId 目标容器中的 Fragment 资源 ID。
     * @param params     变长参数列表，使用 "Key" to Value 结构。
     */
    protected fun routerContainer(name: String, @IdRes fragmentId: Int, vararg params: Pair<String, Any>) {
        (requireActivity() as? SDKActivity<*, *>)?.routerContainer(name, fragmentId, *params)
    }

    companion object{
        private const val TAG = "WSVita_Framework_BaseFragment==>";
    }
}
