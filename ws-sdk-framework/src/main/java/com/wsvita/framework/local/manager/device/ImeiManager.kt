package com.wsvita.framework.local.manager.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * ### IMEI 硬件标识管理组件
 * * **1. 主要职责**
 * - 负责 Android 10 (API 29) 以下版本的设备 IMEI/MEID 获取与管理。
 * - 提供多层降级策略（系统 API -> 反射 Slot 0 -> 反射 Slot 1）。
 * - 整合 [StorageManager] 实现硬件标识的持久化存储，减少敏感系统 API 调用频率。
 *
 * **2. 设计原则**
 * - **内存安全**：禁止成员变量持有任何 [Context] 引用，防止在组件化长生命周期单例中发生内存泄漏。
 * - **合规避险**：通过“内存缓存 + 磁盘缓存”优先策略，规避国内应用市场对隐私数据高频采集的检测。
 * - **频率拦截**：内置 [MIN_CALL_INTERVAL] 机制，物理读取操作间隔必须大于 10 秒。
 *
 * **3. 使用方式**
 * - **初始化**：在 Application 且用户同意隐私协议后调用 `init(context)`。
 * - **获取数据**：调用 `imei(context)`，建议传入当前 Activity 或 ApplicationContext。
 *
 * **4. 注意事项**
 * - **系统限制**：Android 10 及以上版本由于系统安全策略，本组件将返回 null，需配合其他标识（如 AndroidID）使用。
 * - **混淆配置**：若开启混淆，需在 proguard-rules 中 keep 住 TelephonyManager 的 getImei/getDeviceId 方法。
 * - **依赖顺序**：必须在 [StorageManager] 初始化之后再执行本组件的 init。
 *
 * @author Administrator
 * @createTime 2026/1/5
 */
class ImeiManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_ImeiManager=>"

        // 存储在 StorageManager 中的 Key
        private const val KEY_PERSIST_IMEI = "persist_device_imei"

        // 最小物理调用间隔 (ms)，应对合规检测 (10秒)
        private const val MIN_CALL_INTERVAL = 10000L

        val instance: ImeiManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ImeiManager() }

        /**
         * 运行时内存缓存
         */
        @Volatile
        private var cacheImei: String? = null

        /**
         * 上一次物理调用的时间戳
         */
        private var lastImeiTime: Long = -1
    }

    /**
     * 初始化方法
     * @param context 外部传入的上下文，仅用于初始化逻辑，不被成员变量持有
     */
    fun init(context: Context) {
        super.init()
        SLog.i(TAG, "init: ImeiManager starting initialization...")

        // 1. 首先尝试从持久化层（SP/磁盘）恢复缓存
        restoreCacheFromStorage()

        // 2. 逻辑优化：如果持久化层无数据，立即利用当前 context 触发一次硬件采集
        if (cacheImei.isNullOrEmpty()) {
            SLog.i(TAG, "init: No persistent data found, triggering hardware fetch...")
            fetchImeiFromHardware(context.applicationContext)
        } else {
            SLog.d(TAG, "init: Cache warmed from StorageManager.")
        }
    }

    override fun onInit() {
        // 基类回调逻辑，由于 init 已经处理了核心逻辑，此处可保持默认
    }

    /**
     * 获取 IMEI
     * @param context 局部参数，用于在内存缓存失效时重新尝试获取
     */
    fun imei(context: Context): String? {
        checkInit()

        // 1. 优先内存缓存（最高效、最合规）
        if (!cacheImei.isNullOrEmpty()) {
            SLog.d(TAG, "imei: [MemoryCache] hit -> $cacheImei")
            return cacheImei
        }

        // 2. 内存无数据，尝试物理获取
        return fetchImeiFromHardware(context.applicationContext)
    }

    /**
     * 尝试从持久化组件 StorageManager 中恢复数据
     */
    private fun restoreCacheFromStorage() {
        try {
            val persistImei = StorageManager.instance.getString(KEY_PERSIST_IMEI, "")
            if (persistImei.isNotEmpty()) {
                cacheImei = persistImei
                SLog.d(TAG, "restoreCacheFromStorage: Success -> $cacheImei")
            }
        } catch (e: Exception) {
            SLog.e(TAG, "restoreCacheFromStorage Error: ${e.message}")
        }
    }

    /**
     * 核心硬件获取逻辑
     */
    private fun fetchImeiFromHardware(appContext: Context): String? {
        // A. 版本拦截 (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            SLog.w(TAG, "fetchImeiFromHardware: OS Version 10+, access denied.")
            return null
        }

        // B. 频率限制
        val now = systemTime()
        if (lastImeiTime > 0 && now - lastImeiTime < MIN_CALL_INTERVAL) {
            SLog.w(TAG, "fetchImeiFromHardware: Call too frequent. Interval: ${now - lastImeiTime}ms")
            return null
        }
        lastImeiTime = now

        var result: String? = null

        // Step 1: 标准 API
        SLog.d(TAG, "Step 1: Attempting System API...")
        result = getImeiFromSystem(appContext)

        // Step 2: 反射 Slot 0
        if (result.isNullOrEmpty()) {
            SLog.d(TAG, "Step 2: System API failed, attempting reflection for Slot 0...")
            result = getImeiByReflection(appContext, 0)
        }

        // Step 3: 反射 Slot 1
        if (result.isNullOrEmpty()) {
            SLog.d(TAG, "Step 3: Slot 0 failed, attempting reflection for Slot 1...")
            result = getImeiByReflection(appContext, 1)
        }

        // 结果持久化与内存同步
        if (!result.isNullOrEmpty()) {
            SLog.i(TAG, "fetchImeiFromHardware: Successfully obtained -> $result")
            cacheImei = result
            StorageManager.instance.put(KEY_PERSIST_IMEI, result)
        } else {
            SLog.e(TAG, "fetchImeiFromHardware: All attempts failed.")
        }

        return result
    }

    /**
     * 调用官方 TelephonyManager API
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getImeiFromSystem(appContext: Context): String? {
        val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.imei ?: tm.meid
            } else {
                @Suppress("DEPRECATION")
                tm.deviceId
            }
        } catch (e: Exception) {
            SLog.e(TAG, "getImeiFromSystem Exception: ${e.message}")
            null
        }
    }

    /**
     * 反射调用隐藏的 getImei/getDeviceId(int slotId)
     */
    private fun getImeiByReflection(appContext: Context, slotId: Int): String? {
        val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return null
        return try {
            val methodName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "getImei" else "getDeviceId"
            val method = tm.javaClass.getMethod(methodName, Int::class.javaPrimitiveType)
            val res = method.invoke(tm, slotId) as? String
            if (!res.isNullOrEmpty()) SLog.d(TAG, "getImeiByReflection Success [Slot $slotId] -> $res")
            res
        } catch (e: Exception) {
            SLog.w(TAG, "getImeiByReflection failed for Slot $slotId")
            null
        }
    }
}
