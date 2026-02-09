package com.wsvita.core.local.manager

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.local.BaseManager

class SystemBarManager private constructor() : BaseManager() {

    companion object {

        val instance: SystemBarManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SystemBarManager()
        }
    }

    override fun onInit() {

    }


    /**
     * 将屏幕配置应用到指定的 Activity 窗口。
     *
     * 该方法会根据 [ScreenConfig] 的设定，动态调整状态栏的可见性、背景颜色以及图标颜色模式。
     * 支持在 Activity 运行期间多次调用以实现 UI 状态的平滑切换。
     *
     * * @throws IllegalStateException 如果在调用此方法前未执行 [init]，则会抛出初始化异常。
     * * 注意事项：
     * 1. 内部采用 [WindowInsetsControllerCompat] 实现，兼容 Android 5.0 (API 21) 及以上版本。
     * 2. 针对 Android 9.0 (API 28) 及以上版本的刘海屏设备，全屏模式下会自动处理 Cutout 适配，避免黑边。
     * 3. 开启全屏模式后，默认行为设置为 [WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE]，
     * 即用户可通过滑动操作临时呼出状态栏，随后系统会自动将其隐藏。
     * create by Administrator at 2026/1/7 0:41
     * @author Administrator
     *
     * @param activity 目标 Activity 实例。
     * @param config 屏幕样式配置对象。
     *
     * @return
     *      void
     */
    fun applyConfig(activity: Activity, config: ScreenConfig) {
        checkInit()
        if (activity == null || config == null) return

        val window = activity.window
        val decorView = window.decorView
        val controller = WindowCompat.getInsetsController(window, decorView)

        // 核心：设置内容是否适配系统窗口
        // 为 false 时，内容会延伸到状态栏和导航栏下方（全屏必备）
        WindowCompat.setDecorFitsSystemWindows(window, !config.isFullScreen)

        if (config.isFullScreen) {
            // --- 全屏模式 ---

            // 1. 隐藏状态栏
            controller.hide(WindowInsetsCompat.Type.statusBars())

            // 2. 设置沉浸式交互：滑动边缘显示临时栏，随后自动隐藏
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // 3. 处理刘海屏全屏黑边 (Android 9.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lp = window.attributes
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes = lp
            }
        } else {
            // --- 退出全屏 / 常规模式 ---

            // 1. 显示状态栏
            controller.show(WindowInsetsCompat.Type.statusBars())

            // 2. 恢复交互行为：点击或滑动均可交互
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH

            // 3. 颜色与绘制控制
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = config.statusBarColor

            // 4. 图标颜色（亮色/暗色模式）
            controller.isAppearanceLightStatusBars = config.lightIcons
        }
    }

}
