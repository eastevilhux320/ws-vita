package com.vita.xuantong.demo.ext

import android.app.Application

object AppExt {


    /**
     * 获取当前 App 名称
     */
    fun Application.appName(): String {
        return try {
            val packageManager = this.packageManager
            val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            this.resources.getString(labelRes)
        } catch (e: Exception) {
            "Unknown" // 兜底名称
        }
    }
}
