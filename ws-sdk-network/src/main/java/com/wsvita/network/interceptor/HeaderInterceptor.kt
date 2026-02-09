package com.wsvita.network.interceptor

import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfigure
import com.wsvita.network.manager.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        SLog.d(TAG,"intercept start");
        val originalRequest = chain.request()
        SLog.i(TAG,"start build heade");
        // 构建新的请求，添加公共 Header
        val requestBuilder = originalRequest.newBuilder()
            .apply {
                // 示例：添加从 NetworkHelper 获取的公共 Header
                // NetworkHelper.instance().httpHeader().forEach { (k, v) -> addHeader(k, v) }

                addHeader("Content-Type", "application/json")
                addHeader("Accept", "application/json")
                addHeader("Platform", "android")
                addHeader("token",getToken()?:"")
                addHeader("versionCode",NetworkConfigure.instance.getConfig()?.version?.toString()?:"");
                addHeader("versionName",NetworkConfigure.instance.getConfig()?.versionName?:"")
                // 可以从本地存储获取 Token
                // addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
            }
        SLog.i(TAG,"build header success");

        return chain.proceed(requestBuilder.build())
    }

    private fun getToken(): String? {
        val token = TokenManager.instance.getToken();
        SLog.d(TAG,"token:${token}");
        return token;
    }

    companion object{
        private const val TAG = "WSV_NET_Interceptor_Header=>"
    }
}
