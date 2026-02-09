package com.wsvita.biz.core.network.model

import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.network.request.MainTablistRequest
import com.wsvita.biz.core.network.service.AppService
import com.wsvita.biz.core.network.service.BizcoreService
import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfigure
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppModel private constructor(){

    companion object {
        private const val TAG = "WSVita_Bizcore_Network_AppModel=>"

        val instance : AppModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            AppModel();
        }

        private val service: AppService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(AppService::class.java, baseUrl)
        }

        val appId : Long = BizcoreConfigure.instance.appId();
    }

    private fun config(): BizcoreConfig? {
        return BizcoreConfigure.instance.getConfig();
    }

    suspend fun mainTabList(channelCode : String? = null) = withContext(Dispatchers.IO){
        val request = MainTablistRequest();
        request.channelCode = channelCode;
        request.appId = BizcoreModel.appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.d(TAG,"mainTabList_params:${request.toJson()}");
        return@withContext service.mainTabList(request);
    }

    suspend fun appHomeConfig() = withContext(Dispatchers.IO){
        return@withContext  service.appHomeConfig(appId);
    }
}
