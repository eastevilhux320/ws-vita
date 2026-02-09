package com.wsvita.core.network.model

import com.wsvita.core.configure.CoreConfig
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.network.request.PlateAlphabeticsRequest
import com.wsvita.core.network.service.SDKService
import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfig
import com.wsvita.network.configure.NetworkConfigure
import com.wsvita.network.model.NetworkModel
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SDKModel private constructor(){

    companion object {
        private const val TAG = "WSV_SDK_SDKModel=>"

        val instance : SDKModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            SDKModel();
        }

        /**
         * 基础服务实例依然通过单例获取，保证连接池和协议复用
         */
        private val service: SDKService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(SDKService::class.java, baseUrl)
        }

        val appId : Long = CoreConfigure.instance.appId();
    }


    private fun config(): CoreConfig? {
        return CoreConfigure.instance.getConfig();
    }

    /**
     * 获取所有的车牌城市
     * create by Eastevil at 2026/1/13 16:00
     * @author Eastevil
     * @param 
     * @return  
     */ 
    suspend fun allPlateList() = withContext(Dispatchers.IO){
        return@withContext service.allPlateList(appId);
    }

    /**
     * 获取车牌城市字母列表
     * create by Eastevil at 2026/1/13 16:00
     * @author Eastevil
     * @param 
     * @return  
     */ 
    suspend fun plateAlphabeticList(plateId : Long) = withContext(Dispatchers.IO){
        val request = PlateAlphabeticsRequest();
        request.plateId = plateId;

        request.channel = config()?.channelCode;
        request.appId = appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.w(TAG,"plateAlphabeticList request:${request.toJson()}");
        return@withContext service.plateAlphabeticList(request);
    }
}
