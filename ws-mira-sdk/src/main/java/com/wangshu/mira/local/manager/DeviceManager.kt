package com.wangshu.mira.local.manager

import android.app.Application
import android.content.Context
import com.fendasz.moku.MoguID
import com.wangshu.mira.entity.DeviceInfoEntity
import com.wsvita.framework.local.BaseManager

class DeviceManager : BaseManager {

    private constructor(){

    }

    companion object{
        private const val TAG = "WSVita_F_M_DeviceManager=>"

        val instance: DeviceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DeviceManager() }

        /**
         * 后端记录的用户设备关系唯一标识id
         */
        private var userDeviceId : Long? = null;
    }


    override fun onInit() {

    }

    fun init(app : Application){
        //val device = DeviceInfoEntity.build(app);
    }

    fun device(context: Context): DeviceInfoEntity {
        return DeviceInfoEntity.build(context);
    }

    fun setUserDeviceId(userDeviceId: Long){
        DeviceManager.userDeviceId = userDeviceId;
    }

    /**
     * 获取后端生成的用户和设备关联的唯一标识 ID
     *
     * create by Eastevil at 2026/3/3 14:39
     * @author Eastevil
     * @return
     *      后端生成的用户和设备关联的唯一标识 ID
     */ 
    fun getUserDeviceId(): Long? {
        return userDeviceId;
    }
}
