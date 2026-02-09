package com.wsvita.network.model

import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfig
import com.wsvita.network.configure.NetworkConfigure
import com.wsvita.network.request.SendOPTRequest
import com.wsvita.network.service.NetworkService
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 网络服务模型管理类
 * 采用普通类设计，支持实例化调用，符合业务组件化隔离原则
 */
class NetworkModel private constructor(){

    companion object {
        private const val TAG = "WSV_NET_NetworkModel=>"

        val instance : NetworkModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            NetworkModel();
        }

        /**
         * 基础服务实例依然通过单例获取，保证连接池和协议复用
         */
        private val netservice: NetworkService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(NetworkService::class.java, baseUrl)
        }

        private val appId : Long = NetworkConfigure.instance.appId();
    }

    private fun config(): NetworkConfig? {
        return NetworkConfigure.instance.getConfig();
    }

    /**
     * 业务方法定义在类层级，而不是 companion 中
     * 调用方式：NetworkModel().appConfig(appId)
     */
    suspend fun appBeforehand() = withContext(Dispatchers.IO) {
        return@withContext netservice.appBeforehand()
    }

    /**
     * 发送验证码
     * create by Administrator at 2026/1/11 17:52
     * @author Administrator
     * @param type - 验证码类型,1:账号登录,2:注册
     * @param mobile - 手机号码
     * @return
     */
    suspend fun sendOPT(type : Int,mobile : String) = withContext(Dispatchers.IO){
        val request = SendOPTRequest();
        request.mobile = mobile;
        request.type = type;

        request.channel = config()?.channelCode;
        request.appId = appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.w(TAG,"sendOPT request:${request.toJson()}");
        return@withContext netservice.sendOPT(request);
    }
}
