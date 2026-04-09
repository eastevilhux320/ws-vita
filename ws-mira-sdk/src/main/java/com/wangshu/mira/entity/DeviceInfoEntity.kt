package com.wangshu.mira.entity

import android.content.Context
import com.fendasz.moku.MoguID
import com.wsvita.framework.local.manager.device.AndroidIdManager
import com.wsvita.framework.local.manager.device.BatteryManager
import com.wsvita.framework.local.manager.device.CpuManager
import com.wsvita.framework.local.manager.device.DeviceManager
import com.wsvita.framework.local.manager.device.ImeiManager
import com.wsvita.framework.local.manager.device.OAIDManager

class DeviceInfoEntity {
    /**
     * 设备ID
     */
    var deviceId: String? = null

    /**
     * 设备型号
     */
    var deviceModel: String? = null

    /**
     * 设备品牌
     */
    var deviceBrand: String? = null

    /**
     * 设备操作系统版本
     */
    var androidVersion: Int? = null

    /**
     * 设备CPU核数
     */
    var cpuCoreCount: Int? = null

    /**
     * 设备电池容量 (单位mAh)
     */
    var batteryCapacity: Double? = null

    /**
     * 设备传感器数量
     */
    var sensorCount: Int? = null

    /**
     * 设备是否有ROOT权限
     */
    var isRooted: Boolean? = null

    /**
     * 设备SIM卡状态
     */
    var simStatus: Int? = null

    /**
     * 设备ANDROID_ID
     */
    var androidId: String? = null

    /**
     * 设备的开发者模式是否开启
     */
    var isDeveloperModeEnabled: Boolean? = null

    /**
     * 设备IMEI号
     */
    var imei: String? = null

    /**
     * 设备OAID号
     */
    var oaid: String? = null

    /**
     * 设备别名，例：我的华为手机
     */
    var aliasName: String? = null

    companion object{

        fun build(context: Context): DeviceInfoEntity {
            val d = DeviceInfoEntity();
            d.deviceId = MoguID.deviceId(context);
            d.deviceModel = DeviceManager.instance.modelName();
            d.deviceBrand = DeviceManager.instance.brandName();
            d.androidVersion = DeviceManager.instance.sdkVersion();
            d.cpuCoreCount = CpuManager.instance.cupCoreCount();
            d.batteryCapacity = BatteryManager.instance.totalCapacity(context);
            d.sensorCount = DeviceManager.instance.sensorCount(context);
            d.isRooted = DeviceManager.instance.isRooted();
            d.simStatus = DeviceManager.instance.simStatus(context);
            d.androidId = AndroidIdManager.instance.androidId(context);
            d.isDeveloperModeEnabled = DeviceManager.instance.isDeveloperOptionsEnabled(context);
            d.imei = ImeiManager.instance.imei(context);
            d.oaid = OAIDManager.instance.oaid();
            return d;
        }
    }
}
