package com.wsvita.core.configure

import android.graphics.Color

/**
 * 屏幕 UI 配置类。
 * 用于统一管理 Activity 或窗口的状态栏外观、全屏模式等视觉样式。
 *
 * 在组件化架构中，通过此类实现“数据驱动 UI”，避免子类直接操作复杂的 Window API。
 *
 * @author WSVita
 * @date 2026-01-07
 */
class ScreenConfig {
    /**
     * 是否全屏（隐藏状态栏）
     */
    var isFullScreen: Boolean = false;

    /**
     * 状态栏颜色
     */
    var statusBarColor: Int = Color.TRANSPARENT;

    /**
     * 状态栏图标是否为深色
     */
    var lightIcons: Boolean = true

    fun copy(): ScreenConfig {
        val newConfig = ScreenConfig()
        newConfig.isFullScreen = this.isFullScreen
        newConfig.statusBarColor = this.statusBarColor
        newConfig.lightIcons = this.lightIcons
        return newConfig
    }

    companion object{
        private const val TAG = "WSVita_App_ScreenConfig=>";

        fun build(): ScreenConfig {
            val c = ScreenConfig();
            c.isFullScreen = false;
            c.statusBarColor = Color.TRANSPARENT;
            c.lightIcons = false;
            return c;
        }

        fun build(isFullScreen: Boolean, statusBarColor: Int, lightIcons: Boolean): ScreenConfig {
            val config = ScreenConfig()
            config.isFullScreen = isFullScreen
            config.statusBarColor = statusBarColor
            config.lightIcons = lightIcons
            return config
        }

        fun buildFull(statusBarColor: Int): ScreenConfig {
            val config = ScreenConfig()
            config.isFullScreen = true
            config.statusBarColor = statusBarColor
            config.lightIcons = false;
            return config
        }
    }
}
