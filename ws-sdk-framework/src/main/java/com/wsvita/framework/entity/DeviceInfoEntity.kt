package com.wsvita.framework.entity

import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.local.WsContext
import com.wsvita.framework.local.manager.device.AndroidIdManager
import com.wsvita.framework.local.manager.device.CpuManager
import com.wsvita.framework.local.manager.device.DeviceManager
import com.wsvita.framework.local.manager.device.ImeiManager
import com.wsvita.framework.local.manager.device.NetworkInfoManager
import com.wsvita.framework.local.manager.device.OAIDManager

class DeviceInfoEntity {

    /**
     * android_id
     */
    var androidId: String? = null

    /**
     * imei
     */
    var imei: String? = null

    /**
     * oaid
     */
    var oaid: String? = null

    /**
     * 登录ip
     */
    var ip: String? = null

    /**
     * 经度
     */
    var latitude: String? = null

    /**
     * 维度
     */
    var longitude: String? = null

    /**
     * 设备操作系统版本
     */
    var androidVersion: String? = null

    /**
     * 应用版本号
     */
    var versionCode: Int? = null

    /**
     * 应用版本名称
     */
    var versionName: String? = null

    /**
     * 登录方式，1-微信登录，2-账号密码登录，3-手机号码验证码登录
     */
    var loginType: Int? = null

    /**
     * 设备型号
     */
    var deviceModel: String? = null

    /**
     * 设备品牌
     */
    var deviceBrand: String? = null

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
     * 设备SIM卡状态，0-无，1-有
     */
    var simFlag: Int? = null

    /**
     * 设备是否root，0-未root，1-root
     */
    var rootFlag: Int? = null

    /**
     * 设备的开发者模式是否开启, 0-未开启，1-开启
     */
    var developerModeFlag: Int? = null

    companion object{

        fun gen(): DeviceInfoEntity {
            val d = DeviceInfoEntity();
            val app = WsContext.context;
            d.androidId = AndroidIdManager.instance.androidId(app);
            d.imei = ImeiManager.instance.imei(app);
            d.oaid = OAIDManager.instance.oaid();
            d.versionCode = FrameConfigure.instance.getConfig()?.version;
            d.versionName = FrameConfigure.instance.getConfig()?.versionName;
            d.deviceModel = DeviceManager.instance.modelName();
            d.deviceBrand = DeviceManager.instance.brandName();
            d.cpuCoreCount = CpuManager.instance.cupCoreCount();
            d.batteryCapacity = DeviceManager.instance.batteryTotalCapacity(app);
            d.sensorCount = DeviceManager.instance.sensorCount(app);
            d.simFlag = DeviceManager.instance.simStatus(app);
            val isRoot = DeviceManager.instance.isRooted();
            if(isRoot){
                d.rootFlag = 0;
            }else{
                d.rootFlag = 1;
            }
            d.developerModeFlag = if(DeviceManager.instance.isDeveloperOptionsEnabled(app)) 0 else 1;
            d.ip = NetworkInfoManager.instance.getLocalIpAddress();
            return d;
        }
    }
}
