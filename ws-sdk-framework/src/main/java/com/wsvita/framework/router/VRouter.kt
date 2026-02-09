package com.wsvita.framework.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.AnimRes
import com.wsvita.framework.utils.SLog
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.LinkedHashMap

/**
 * =====================================================
 * VRouter - 组件化页面路由核心类
 * =====================================================
 *
 * 【设计目标】
 * - 为组件化 / 多模块架构提供统一、安全、解耦的页面跳转能力
 * - 避免组件之间直接依赖 Activity / Fragment 类
 * - 统一参数传递、跳转控制、防抖处理
 *
 * 【典型使用场景】
 * 1. 组件 A 跳转到组件 B 的页面
 * 2. 通过 Action / Uri 实现跨组件通信
 * 3. H5 / DeepLink / Native 页面统一跳转入口
 *
 * 【核心特性】
 * - 使用 WeakReference 持有 Context，防止 Activity 泄漏
 * - 支持显式 / 隐式 / Uri 三种跳转方式
 * - 支持 Bundle / 基础类型 / Parcelable / Serializable 参数
 * - 内置 500ms 跳转防抖，防止重复打开页面
 * - 自动适配 Activity / 非 Activity Context
 *
 * 【注意事项】
 * - 建议优先使用 Parcelable 传参，避免 Serializable 带来的性能问题
 * - 非 Activity Context 启动页面时会自动添加 NEW_TASK
 * - Router 实例不可复用（一次跳转一次 with）
 */
class VRouter private constructor(context: Context) {

    /**
     * 使用弱引用保存 Context，防止 Router 生命周期
     * 长于 Activity 时造成内存泄漏
     */
    private val contextRef: WeakReference<Context> = WeakReference(context)

    // =========================
    // 跳转目标配置
    // =========================

    /** 隐式跳转 Action */
    private var action: String? = null

    /** 完整 Url（优先级高于 scheme + host） */
    private var url: String? = null

    /** Uri scheme，如：wsvita、myapp */
    private var scheme: String? = null

    /** Uri host，如：user_detail */
    private var host: String? = null

    /** 限定 Intent 目标包名，默认当前应用 */
    private var packageName: String? = context.packageName

    /** 显式跳转目标 Activity */
    private var targetClass: Class<*>? = null

    // =========================
    // 参数 & 启动控制
    // =========================

    /** Intent 参数容器 */
    private var params: Bundle? = null

    /** startActivityForResult 请求码 */
    private var requestCode: Int = INVALID_VALUE

    /** Intent flag */
    private var flag: Int = INVALID_VALUE

    // =========================
    // 动画 & 页面控制
    // =========================

    /** 进入动画 */
    private var enterAnim: Int = 0

    /** 退出动画 */
    private var exitAnim: Int = 0

    /** 是否在跳转后关闭当前页面 */
    private var isFinish: Boolean = false

    // =====================================================
    // 链式配置 API（对外使用）
    // =====================================================

    /**
     * 设置隐式跳转 Action
     *
     * 【适用场景】
     * - 跨组件跳转
     * - 不希望依赖目标 Activity 类
     *
     * 【调用示例】
     * VRouter.with(context)
     *   .action("com.xxx.user.detail")
     *   .start()
     */
    fun action(action: String) = apply { this.action = action }

    /**
     * 设置跳转 Url（DeepLink / H5）
     *
     * @param url 完整 Url 字符串
     */
    fun url(url: String?) = apply { this.url = url }

    /**
     * 通过 Uri 对象设置跳转目标
     */
    fun url(uri: Uri?) = apply { this.url = uri?.toString() }

    /**
     * 设置自定义 Scheme
     *
     * 通常配合 host 使用
     */
    fun scheme(scheme: String) = apply { this.scheme = scheme }

    /**
     * 设置 Uri Host
     */
    fun host(host: String) = apply { this.host = host }

    /**
     * 设置显式跳转目标 Activity
     *
     * 【注意】
     * - 仅适用于当前模块或依赖模块
     * - 不建议在组件化强解耦场景下使用
     */
    fun target(cls: Class<*>) = apply { this.targetClass = cls }

    /**
     * 设置 Intent Flag
     *
     * 如：Intent.FLAG_ACTIVITY_CLEAR_TOP
     */
    fun flag(flag: Int) = apply { this.flag = flag }

    /**
     * 设置 startActivityForResult 请求码
     *
     * 仅在 Context 为 Activity 时生效
     */
    fun requestCode(code: Int) = apply { this.requestCode = code }

    /**
     * 批量设置参数 Bundle
     *
     * 已存在参数不会被清空
     */
    fun bundle(bundle: Bundle) = apply {
        getSafeParams().putAll(bundle)
    }

    /** 传递 String 参数 */
    fun addParam(key: String, value: String?) =
        apply { getSafeParams().putString(key, value) }

    /** 传递 Int 参数 */
    fun addParam(key: String, value: Int) =
        apply { getSafeParams().putInt(key, value) }

    /** 传递 Long 参数 */
    fun addParam(key: String, value: Long) =
        apply { getSafeParams().putLong(key, value) }

    /**
     * 传递 Serializable 参数
     *
     * ⚠️ 注意：性能较差，仅用于兼容旧模型
     */
    fun addParam(key: String, value: Serializable) =
        apply { getSafeParams().putSerializable(key, value) }

    /**
     * 传递 Parcelable 参数（推荐）
     *
     * 组件化场景下首选方式
     */
    fun addParam(key: String, value: Parcelable?) =
        apply { getSafeParams().putParcelable(key, value) }

    /**
     * 获取安全的 Bundle 参数容器
     *
     * 内部统一初始化入口
     */
    private fun getSafeParams(): Bundle {
        if (params == null) params = Bundle()
        return params!!
    }

    /**
     * 设置页面切换动画
     *
     * 仅在 Context 为 Activity 时生效
     */
    fun pendingTransition(
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) = apply {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
    }

    /**
     * 设置跳转完成后是否关闭当前页面
     */
    fun isFinish(isFinish: Boolean) =
        apply { this.isFinish = isFinish }

    // =====================================================
    // 跳转执行入口
    // =====================================================

    /**
     * 执行页面跳转
     *
     * @return true 跳转请求已成功发出
     * @return false 跳转被拦截或失败
     *
     * 【失败原因示例】
     * - Context 已被回收
     * - Activity 正在 finish
     * - 500ms 内重复跳转
     * - 未配置任何跳转目标
     */
    fun start(): Boolean {
        val context = contextRef.get() ?: return false

        // Activity 生命周期保护
        if (context is Activity && context.isFinishing) {
            Log.w(TAG, "VRouter: Activity is finishing, navigation aborted.")
            return false
        }

        // 根据优先级创建 Intent
        val intent = createIntent(context) ?: run {
            Log.e(TAG, "VRouter: No target destination found.")
            return false
        }

        // 防抖校验
        val routerKey = generateRouterKey(intent)
        if (isFastRouter(routerKey)) return false

        try {
            if (requestCode != INVALID_VALUE && context is Activity) {
                context.startActivityForResult(intent, requestCode)
            } else {
                if (context !is Activity && flag == INVALID_VALUE) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }

            handlePostNavigation(context)

        } catch (e: Exception) {
            SLog.e(TAG, "VRouter Navigation Error: ${e.message}")
            return false
        }
        return true
    }

    /**
     * 使用 ActivityResultLauncher 启动跳转
     * * 【适用场景】
     * 配合 BaseActivity 中的 registerLauncher 使用，替代过时的 requestCode 模式。
     * * @param launcher 预先在 Activity/Fragment 注册的启动器
     */
    fun launch(launcher: ActivityResultLauncher<Intent>): Boolean {
        val context = contextRef.get() ?: return false

        // 1. 构建 Intent
        val intent = createIntent(context) ?: run {
            Log.e(TAG, "VRouter: No target destination found.")
            return false
        }

        // 2. 防抖校验
        val routerKey = generateRouterKey(intent)
        if (isFastRouter(routerKey)) return false

        try {
            // 3. 执行启动
            launcher.launch(intent)
            // 4. 后置处理（动画/销毁）
            handlePostNavigation(context)
        } catch (e: Exception) {
            SLog.e(TAG, "VRouter Launch Error: ${e.message}")
            return false
        }
        return true
    }

    // =====================================================
    // 内部实现（不建议外部依赖）
    // =====================================================

    /**
     * 根据配置构建 Intent
     *
     * 优先级：
     * 1. 显式 targetClass
     * 2. Action
     * 3. Uri
     */
    private fun createIntent(context: Context): Intent? {
        val intent: Intent = when {
            targetClass != null -> Intent(context, targetClass)
            !action.isNullOrEmpty() -> Intent(action)
            else -> {
                val uri = makeUri()
                if (uri != null) Intent(Intent.ACTION_VIEW, uri) else null
            }
        } ?: return null

        if (flag != INVALID_VALUE) intent.flags = flag
        params?.let { intent.putExtras(it) }
        packageName?.let { intent.setPackage(it) }

        return intent
    }

    /**
     * 构建 Uri（url 优先）
     */
    private fun makeUri(): Uri? {
        if (!url.isNullOrEmpty()) return Uri.parse(url)
        if (!scheme.isNullOrEmpty() && !host.isNullOrEmpty()) {
            return Uri.Builder()
                .scheme(scheme)
                .authority(host)
                .build()
        }
        return null
    }

    /**
     * 跳转完成后的统一处理
     *
     * - 页面动画
     * - 当前页面关闭
     */
    private fun handlePostNavigation(context: Context) {
        if (context is Activity) {
            if (enterAnim != 0 || exitAnim != 0) {
                context.overridePendingTransition(enterAnim, exitAnim)
            }
            if (isFinish) {
                context.finish()
            }
        }
    }

    /**
     * 生成路由唯一标识
     *
     * 用于区分：
     * - 同一页面
     * - 不同参数
     */
    private fun generateRouterKey(intent: Intent): String {
        val target = intent.component?.className
            ?: intent.action
            ?: "view"
        val paramsId = params?.let {
            it.keySet().size + it.hashCode()
        } ?: 0
        return "$target|$paramsId"
    }

    /**
     * 跳转防抖判断
     *
     * 500ms 内相同目标 + 参数的跳转将被拦截
     */
    private fun isFastRouter(key: String): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = sRouteTime[key] ?: 0L
        if (now - lastTime < INTERVAL_TIME) {
            Log.d(TAG, "VRouter: Jump request too frequent, ignored.")
            return true
        }
        sRouteTime[key] = now
        return false
    }

    companion object {

        private const val TAG = "VRouter"
        private const val CACHE_SIZE = 15
        private const val INVALID_VALUE = -1
        private const val INTERVAL_TIME = 500L

        /**
         * 路由时间缓存
         *
         * 使用 LinkedHashMap 控制容量，避免无限增长
         */
        private val sRouteTime =
            LinkedHashMap<String, Long>(CACHE_SIZE)

        /**
         * Router 初始化入口
         *
         * 【标准用法】
         * VRouter.with(context)
         *   .target(UserActivity::class.java)
         *   .addParam("uid", "10001")
         *   .start()
         *
         * @throws IllegalStateException Context 为空时抛出
         */
        @JvmStatic
        fun with(context: Context?): VRouter {
            val ctx = context
                ?: throw IllegalStateException("VRouter: Context cannot be null")
            return VRouter(ctx)
        }
    }
}
