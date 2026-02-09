package com.wsvita.framework.local.manager.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * ### AndroidID 设备标识管理组件
 *
 * **1. 主要职责**
 * - 负责设备 AndroidID (Settings.Secure.ANDROID_ID) 的获取与持久化管理。
 * - 作为 IMEI/IMSI 在 Android 10+ 时代的官方推荐降级方案（除 OAID 外）。
 *
 * **2. 设计原则**
 * - **持久化优先**：一旦获取成功即写入 [StorageManager]，后续冷启动优先读取磁盘，规避对系统 Settings API 的高频轮询。
 * - **合规策略**：虽然获取 AndroidID 不需要运行时权限，但仍属于隐私数据，内置 [MIN_CALL_INTERVAL] 拦截。
 * - **零引用**：严格遵守组件规范，不持有 Context 成员变量。
 *
 * **3. 关键特性**
 * - **稳定性**：在设备不进行“恢复出厂设置”的情况下，该 ID 保持不变。
 * - **局限性**：某些低端机型或模拟器可能返回固定的错误值（如 "9774d56d682e549c"），代码中已做日志提醒。
 *
 * @author Administrator
 * @createTime 2026/01/11
 */
class AndroidIdManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_AndroidIdManager=>"

        // 存储在 StorageManager 中的 Key，使用 wsui 前缀符合项目规范
        private const val KEY_PERSIST_ANDROID_ID = "wsui_persist_device_android_id"

        // 最小物理调用间隔 (ms)
        private const val MIN_CALL_INTERVAL = 10000L

        // 模拟器常见的 bug AndroidID
        private const val BUGGY_ANDROID_ID = "9774d56d682e549c"

        val instance: AndroidIdManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AndroidIdManager() }

        @Volatile
        private var cacheAndroidId: String? = null

        private var lastCallTime: Long = -1
    }

    /**
     * 初始化：恢复缓存并在必要时预取
     */
    fun init(context: Context) {
        super.init()
        SLog.i(TAG, "init: Starting...")

        // 1. 尝试从持久化层恢复
        restoreFromStorage()

        // 2. 如果磁盘无数据，立即触发一次物理采集
        if (cacheAndroidId.isNullOrEmpty()) {
            fetchFromSystem(context.applicationContext)
        }
    }

    override fun onInit() {
        // 基类回调
    }

    /**
     * 获取 AndroidID
     */
    fun androidId(context: Context): String? {
        checkInit()

        // 1. 内存缓存命中
        if (!cacheAndroidId.isNullOrEmpty()) {
            return cacheAndroidId
        }

        // 2. 物理获取
        return fetchFromSystem(context.applicationContext)
    }

    private fun restoreFromStorage() {
        try {
            val savedId = StorageManager.instance.getString(KEY_PERSIST_ANDROID_ID, "")
            if (savedId.isNotEmpty()) {
                cacheAndroidId = savedId
                SLog.d(TAG, "restoreFromStorage: Hit -> $cacheAndroidId")
            }
        } catch (e: Exception) {
            SLog.e(TAG, "restoreFromStorage error: ${e.message}")
        }
    }

    /**
     * 核心获取逻辑：从系统设置数据库读取
     */
    @SuppressLint("HardwareIds")
    private fun fetchFromSystem(appContext: Context): String? {
        // 频率限制
        val now = systemTime()
        if (lastCallTime > 0 && now - lastCallTime < MIN_CALL_INTERVAL) {
            SLog.w(TAG, "fetchFromSystem: Too frequent, return null.")
            return null
        }
        lastCallTime = now

        return try {
            val aid = Settings.Secure.getString(
                appContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )

            if (!aid.isNullOrEmpty()) {
                // 针对特定 Bug ID 的警告
                if (aid == BUGGY_ANDROID_ID) {
                    SLog.w(TAG, "fetchFromSystem: Warning! Common buggy ID detected (9774d56d682e549c)")
                }

                cacheAndroidId = aid
                StorageManager.instance.put(KEY_PERSIST_ANDROID_ID, aid)
                SLog.i(TAG, "fetchFromSystem: Success -> $aid")
                aid
            } else {
                SLog.e(TAG, "fetchFromSystem: Result is empty")
                null
            }
        } catch (e: Exception) {
            SLog.e(TAG, "fetchFromSystem error: ${e.message}")
            null
        }
    }
}
