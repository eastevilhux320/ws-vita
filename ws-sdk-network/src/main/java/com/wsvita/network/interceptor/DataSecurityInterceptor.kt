package com.wsvita.network.interceptor

import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import com.wsvita.network.configure.NetworkConfigure
import ext.TimeExt.systemTime
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.Charset

/**
 * 数据安全拦截器：负责响应数据的解密、解码及请求数据的加密预留
 */
class DataSecurityInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        SLog.d(TAG,"intercept start,time:${systemTime()}")
        SLog.i(TAG,"step 1,start");
        val request = chain.request()
        // 1. 请求阶段：可以在此对 RequestBody 进行加密 (预留)
        val response = chain.proceed(request)
        SLog.i(TAG,"step 2,proceed");
        //val option = NetworkClient.instance.getOptions();

        // 2. 响应阶段：在此对 ResponseBody 进行解密/解码
        if (response.isSuccessful && response.body != null) {
            SLog.i(TAG,"step 3,response isSuccessful");
            val body = response.body!!
            val source = body.source()
            source.request(Long.MAX_VALUE) // 读取全部内容
            val buffer = source.buffer
            val responseRawString = buffer.clone().readString(Charset.forName("UTF-8"))

            SLog.i(TAG,"step 4,read response raw string,responseRawString:\n${responseRawString}");
            val processedData = responseRawString

            // -----------------------

            val newBody = processedData.toResponseBody(body.contentType())
            return response.newBuilder().body(newBody).build()
        }

        return response
    }

    companion object{
        private const val TAG = "WSV_NET_Interceptor_DataSecurity=>"
    }
}
