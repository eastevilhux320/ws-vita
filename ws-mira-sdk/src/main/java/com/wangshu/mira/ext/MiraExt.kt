package com.wangshu.mira.ext

import android.content.Context
import android.content.Intent

/**
 * 24小时的毫秒数：24 * 60 * 60 * 1000
 */
const val DAY_IN_MILLIS = 86_400_000L

object MiraExt {

    fun Context.openApp(packageName: String) {
        if (packageName.isBlank()) return

        // 1. 获取目标 App 的启动 Intent
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            // 2. 增加标准启动标记，确保从非 Activity Context 启动时不会崩溃
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(launchIntent)
            } catch (e: Exception) {
                // 3. 异常捕获（如：权限限制或特定系统的安全拦截）
                e.printStackTrace()
            }
        } else {
            // 4. 容错处理：若未安装，可跳转应用市场或通过 ws-vita 下发提示
            onAppNotFound(packageName)
        }
    }

    private fun Context.onAppNotFound(packageName: String) {
        // 此处可接入玄同(ws-vita)的全局路由或提示系统

    }

    /**
     * 在原始时间戳基础上增加/减少指定天数。
     *
     * @receiver 原始毫秒时间戳。
     * @param days 需要偏移的天数。传入正数增加天数，传入负数减少天数。
     * @return 偏移后的毫秒时间戳。
     * * [参数说明]：
     * @param days 类型为 Int，内部会自动转换为 Long 进行安全计算，支持最大范围内的日期偏移。
     * * @example
     * // 增加 3 天
     * val future = currentTime.addDays(3)
     * // 减少 7 天
     * val past = currentTime.addDays(-7)
     */
    fun Long.addDays(days: Int): Long = this + (days * DAY_IN_MILLIS)
}
