package com.vita.xuantong.demo.ext

import android.app.Application
import android.os.Build

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

    /**
     * 获取版本名称 (如 "1.2.0")
     */
    fun Application.versionName(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取版本号 (Long 类型)
     * 兼容 Android P (API 28) 及以上版本的 longVersionCode
     */
    fun Application.versionCode(): Long {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            -1L
        }
    }
}
