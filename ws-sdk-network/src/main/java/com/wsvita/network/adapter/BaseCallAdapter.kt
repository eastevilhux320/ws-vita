package com.wsvita.network.adapter

import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import com.wsvita.network.entity.Result
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BaseCallAdapter<R>(private val type: Type) : CallAdapter<R, R> {

    companion object {
        private const val TAG = "WSV_NET_BaseCallAdapter=>"
    }

    override fun responseType(): Type = type

    override fun adapt(call: Call<R>): R {
        val options = NetworkClient.instance.getOptions()
        return try {
            val response = call.execute()
            SLog.d(TAG,"response:${response.code()}")
            if (response.isSuccessful) {
                SLog.d(TAG,"response isSuccessful")
                val body = response.body() ?: return createEmptyResponse()

                // 增加类型安全检查
                if (body is Result<*>) {
                    when (body.code) {
                        options.loginErrorCode -> return handleLoginError()
                        options.emptyCode -> return createEmptyResponse()
                    }
                }
                body
            } else {
                handleErrorResponse(response)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            handleException(e)
        }
    }

    /**
     * 处理登录失效
     */
    private fun handleLoginError(): R {
        val options = NetworkClient.instance.getOptions()
        SLog.e(TAG, "Login Error triggered")
        return createResult(options.loginErrorCode, "Login expired or error")
    }

    /**
     * 处理 Http 错误（如 404, 500）
     */
    private fun handleErrorResponse(response: Response<R>): R {
        SLog.e(TAG, "Http Error: ${response.code()}")
        return createResult(response.code(), response.message())
    }

    /**
     * 处理网络异常
     */
    private fun handleException(e: Exception): R {
        val options = NetworkClient.instance.getOptions()
        val (code, msg) = when (e) {
            is SocketTimeoutException -> options.networkErrorCode to "Network timeout"
            is ConnectException, is UnknownHostException -> options.networkErrorCode to "Network unreachable"
            is IOException -> options.networkErrorCode to "IO Exception"
            else -> options.serviceErrorCode to (e.message ?: "Unknown error")
        }
        SLog.e(TAG, "Exception: $msg")
        return createResult(code, msg)
    }

    private fun createEmptyResponse(): R {
        val options = NetworkClient.instance.getOptions()
        return createResult(options.emptyCode, "Empty data")
    }

    /**
     * 统一构造 Result 响应并强转为 R
     */
    @Suppress("UNCHECKED_CAST")
    private fun createResult(code: Int, msg: String?): R {
        val result = Result<Any>()
        result.code = code
        result.msg = msg ?: ""
        return result as R
    }
}
