package com.wsvita.account.network.model

import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.configure.AccountConfigure
import com.wsvita.account.network.request.PhoneLoginRequest
import com.wsvita.account.network.request.UsernameLoginRequest
import com.wsvita.account.network.service.AccountService
import com.wsvita.framework.entity.DeviceInfoEntity
import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfigure
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountModel private constructor(){

    companion object {
        private const val TAG = "WSV_NET_NetworkModel=>"

        val instance : AccountModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            AccountModel();
        }

        private val service: AccountService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(AccountService::class.java, baseUrl)
        }

        private val appId : Long = NetworkConfigure.instance.appId();
    }

    private fun config(): AccountConfig? {
        return AccountConfigure.instance.getConfig();
    }

    /**
     * 手机短信验证码登录
     * create by Administrator at 2026/1/11 22:38
     * @author Administrator
     * @param
     * @return
     */
    suspend fun phoneLogin(mobile : String,optCode : String) = withContext(Dispatchers.IO){
        val request = PhoneLoginRequest();
        request.deviceInfo = DeviceInfoEntity.gen();
        request.mobile = mobile;
        request.msgcode = optCode;

        request.channel = config()?.channelCode;
        request.appId = appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.w(TAG,"sendOPT request:${request.toJson()}");

        return@withContext service.phoneLogin(request);
    }

    /**
     * 账号密码登录
     * @author Eastevil
     * @createTime 2026/01/13 10:37
     * @param account
     *      账号
     * @param password
     *      登录密码
     * @since
     * @see
     * @return
     */
    suspend fun usernameLogin(account : String,password : String) = withContext(Dispatchers.IO){
        val request = UsernameLoginRequest();
        request.account = account;
        request.password = password;
        request.channel = AccountConfigure.instance.getConfig()?.channelCode;
        request.appId = appId;
        request.version = AccountConfigure.instance.getConfig()?.version;
        request.versionName = AccountConfigure.instance.getConfig()?.versionName;
        return@withContext service.usernameLogin(request);
    }

    /**
     * 用户信息查询
     * create by Eastevil at 2026/1/12 11:12
     * @author Eastevil
     * @param 
     * @return  
     */ 
    suspend fun memberInfo() = withContext(Dispatchers.IO){
        return@withContext service.memberInfo(appId);
    }
}
