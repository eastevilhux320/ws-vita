package com.wsvita.framework.local.manager.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * ### IMSI 与 SIM 卡硬件标识管理组件
 *
 * **1. 主要职责**
 * - 负责 IMSI 获取、多卡状态监控及运营商信息识别。
 * - 整合 [StorageManager] 实现 IMSI 的磁盘持久化。
 *
 * **2. 多卡适配**
 * - 兼容 Android 多卡架构，支持返回卡槽状态数组及运营商集合。
 *
 * @author Administrator
 * @createTime 2026/01/05
 */
class ImsiManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_ImsiManager=>"
        private const val KEY_PERSIST_IMSI = "persist_device_imsi"
        private const val MIN_CALL_INTERVAL = 10000L

        val instance: ImsiManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ImsiManager() }

        @Volatile
        private var cacheImsi: String? = null
        private var lastImsiTime: Long = -1
    }

    fun init(context: Context) {
        super.init()
        restoreCacheFromStorage()
        if (cacheImsi.isNullOrEmpty()) {
            fetchImsiFromHardware(context.applicationContext)
        }
    }

    override fun onInit() {}

    /**
     * 获取 IMSI (支持缓存策略)
     */
    fun imsi(context: Context): String? {
        checkInit()
        if (!cacheImsi.isNullOrEmpty()) return cacheImsi
        return fetchImsiFromHardware(context.applicationContext)
    }

    /**
     * 获取 SIM 卡插槽占用状态
     * * **逻辑说明：**
     * - 0: 一张卡都没有
     * - 1: 仅存在 SIM 1
     * - 2: 仅存在 SIM 2
     * - 12: 存在 SIM 1 和 SIM 2
     * - 以此类推 (如 123)
     *
     * @return 组合后的状态数字
     */
    @SuppressLint("MissingPermission")
    fun simStatus(context: Context): Int {
        var statusFlag = 0
        try {
            val appContext = context.applicationContext
            val sm = appContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
            val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            val activeList = sm?.activeSubscriptionInfoList
            if (!activeList.isNullOrEmpty()) {
                // 1. 获取所有活跃卡槽的状态 (过滤掉真正的无卡状态)
                val activeSlots = mutableListOf<Int>()
                activeList.forEach { info ->
                    val slotId = info.simSlotIndex // 0 代表卡槽1, 1 代表卡槽2
                    val state = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tm?.getSimState(slotId) ?: TelephonyManager.SIM_STATE_UNKNOWN
                    } else {
                        getSimStateByReflection(tm, slotId)
                    }

                    // 只有当状态为 READY (5) 时，才认为该位置有卡
                    if (state == TelephonyManager.SIM_STATE_READY) {
                        activeSlots.add(slotId + 1) // 转为从 1 开始的标识
                    }
                }

                // 2. 将卡槽列表转换为组合数字 (例如 [1, 2] -> 12)
                if (activeSlots.isNotEmpty()) {
                    activeSlots.sort() // 确保顺序
                    var multiplier = 1
                    var result = 0
                    // 从后往前计算：[1, 2] -> 2*1 + 1*10 = 12
                    for (i in activeSlots.indices.reversed()) {
                        result += activeSlots[i] * multiplier
                        multiplier *= 10
                    }
                    statusFlag = result
                }
            } else {
                // 兜底：检查单卡默认状态
                if (tm?.simState == TelephonyManager.SIM_STATE_READY) {
                    statusFlag = 1
                }
            }
        } catch (e: Exception) {
            SLog.e(TAG, "simStatus Error: ${e.message}")
        }
        return statusFlag
    }

    /**
     * Android 8.0 以下反射获取特定卡槽状态
     */
    private fun getSimStateByReflection(tm: TelephonyManager?, slotId: Int): Int {
        if (tm == null) return TelephonyManager.SIM_STATE_UNKNOWN
        return try {
            val method = tm.javaClass.getMethod("getSimState", Int::class.javaPrimitiveType)
            val state = method.invoke(tm, slotId) as Int
            state
        } catch (e: Exception) {
            // 如果反射失败，尝试返回默认状态
            tm.simState
        }
    }

    /**
     * 获取所有活跃卡的运营商名称集合
     */
    @SuppressLint("MissingPermission")
    fun operatorNames(context: Context): List<String> {
        val names = mutableListOf<String>()
        try {
            val sm = context.applicationContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
            val activeList = sm?.activeSubscriptionInfoList

            if (!activeList.isNullOrEmpty()) {
                activeList.sortedBy { it.simSlotIndex }.forEach { info ->
                    names.add(info.displayName?.toString() ?: "unknown")
                }
            } else {
                val tm = context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                val name = tm?.networkOperatorName
                if (!name.isNullOrEmpty()) names.add(name)
            }
        } catch (e: Exception) {
            SLog.e(TAG, "operatorNames Error: ${e.message}")
        }
        return if (names.isEmpty()) listOf("unknown") else names
    }

    /**
     * 获取主卡运营商名称
     */
    fun firstOperatorName(context: Context): String {
        return operatorNames(context).firstOrNull() ?: "unknown"
    }

    // --- 内部私有逻辑 ---

    private fun restoreCacheFromStorage() {
        try {
            val persistImsi = StorageManager.instance.getString(KEY_PERSIST_IMSI, "")
            if (persistImsi.isNotEmpty()) cacheImsi = persistImsi
        } catch (e: Exception) {
            SLog.e(TAG, "restoreCacheFromStorage Error: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchImsiFromHardware(appContext: Context): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

        val now = systemTime()
        if (lastImsiTime > 0 && now - lastImsiTime < MIN_CALL_INTERVAL) return null
        lastImsiTime = now

        var result = getImsiFromSystem(appContext)

        if (result.isNullOrEmpty()) result = getImsiByReflection(appContext, 0)
        if (result.isNullOrEmpty()) result = getImsiByReflection(appContext, 1)

        if (!result.isNullOrEmpty()) {
            cacheImsi = result
            StorageManager.instance.put(KEY_PERSIST_IMSI, result)
        }
        return result
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getImsiFromSystem(appContext: Context): String? {
        val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return try { tm?.subscriberId } catch (e: Exception) { null }
    }

    private fun getImsiByReflection(appContext: Context, slotId: Int): String? {
        val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return null
        return try {
            val method = tm.javaClass.getMethod("getSubscriberId", Int::class.javaPrimitiveType)
            method.invoke(tm, slotId) as? String
        } catch (e: Exception) { null }
    }
}
