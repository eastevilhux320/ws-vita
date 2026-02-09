package com.wsvita.framework.local.manager.device

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager as AndroidBatteryManager
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.SLog

/**
 * ### 电池状态管理组件
 * * **1. 主要职责**
 * - 实时采集电池电量 (Level)、充电状态 (Status)、电池健康度 (Health) 及温度 (Temperature)。
 * - 封装系统电池广播的解析逻辑。
 *
 * **2. 设计原则**
 * - **动态获取**：通过粘性广播 (Sticky Intent) 实时读取，无需长期驻留监听。
 * - **Context 零持有**：仅在方法调用时传入局部 Context，执行完毕即释放。
 *
 * @author Administrator
 * @createTime 2026/1/11 23:20
 */
class BatteryManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_BatteryManager=>"
        val instance: BatteryManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BatteryManager() }
    }

    override fun init() {
        super.init()
        SLog.d(TAG, "init: BatteryManager ready.")
    }

    override fun onInit() {}

    /**
     * 获取电池实时意图 (Sticky Intent)
     */
    private fun getBatteryIntent(context: Context): Intent? {
        return context.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    /**
     * 获取当前电量百分比
     * @return 0-100
     */
    fun level(context: Context): Int {
        val intent = getBatteryIntent(context) ?: return -1
        val level = intent.getIntExtra(AndroidBatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(AndroidBatteryManager.EXTRA_SCALE, -1)
        return if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            -1
        }
    }

    /**
     * 获取充电状态描述
     */
    fun status(context: Context): String {
        val intent = getBatteryIntent(context) ?: return "unknown"
        return when (intent.getIntExtra(AndroidBatteryManager.EXTRA_STATUS, -1)) {
            AndroidBatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            AndroidBatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            AndroidBatteryManager.BATTERY_STATUS_FULL -> "Full"
            AndroidBatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            else -> "Unknown"
        }
    }

    /**
     * 获取电池温度 (单位：摄氏度)
     */
    fun temperature(context: Context): Float {
        val intent = getBatteryIntent(context) ?: return 0f
        // 系统返回的是整数（如 365 代表 36.5℃）
        val temp = intent.getIntExtra(AndroidBatteryManager.EXTRA_TEMPERATURE, 0)
        return temp / 10f
    }

    /**
     * 获取电池总容量 (Total Capacity)
     * * **1. 逻辑说明：**
     * - 通过反射系统内部类 [com.android.internal.os.PowerProfile] 获取。
     * - 读取系统预设的 `battery.capacity` 属性。
     * * * **2. 返回值可靠性：**
     * - 返回设备出厂时标称的典型容量值。
     * - **常见示例值**：`4500.0`, `5000.0`
     */
    fun totalCapacity(context: Context): Double {
        val powerProfileClass = "com.android.internal.os.PowerProfile"
        return try {
            val mPowerProfile = Class.forName(powerProfileClass)
                .getConstructor(Context::class.java)
                .newInstance(context.applicationContext)

            val batteryCapacity = Class.forName(powerProfileClass)
                .getMethod("getBatteryCapacity")
                .invoke(mPowerProfile) as Double

            batteryCapacity
        } catch (e: Exception) {
            SLog.e(TAG, "totalCapacity Error: ${e.message}")
            0.0
        }
    }
}
