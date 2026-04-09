package com.wangshu.mira.network.interceptor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wangshu.mira.configure.MiraConfigure
import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.util.*

class MiraReponseInterceptor : Interceptor {

    private val gson = JsonUtil.getInstance().getGson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method.lowercase()
        val body = request.body

        // 1. 判断是否需要拦截 (仅处理 POST 且有 Body 的非文件请求)
        if (method == METHOD_POST && body != null) {
            val contentType = body.contentType()
            val isMultipart = contentType?.type?.lowercase()?.contains("multipart") ?: false

            if (!isMultipart) {
                try {
                    return processEncryption(chain, request, body)
                } catch (e: Exception) {
                    SLog.e(TAG, "The encryption process crashed", e)
                }
            }
        }

        return chain.proceed(request)
    }

    private fun processEncryption(chain: Interceptor.Chain, request: Request, body: RequestBody): Response {
        val originalDataJson = extractJsonContent(body)
        val requestTime = System.currentTimeMillis();

        if (!originalDataJson.isNullOrBlank()) {
            val signData = NetworkClient.instance.networkDataSecurity()?.signData(requestTime.toString());
            val entryData = NetworkClient.instance.networkDataSecurity()?.encrypt(originalDataJson)

            // 5. 组合包装对象
            val wrapper = JsonObject().apply {
                addProperty(SERVICE_DATA_KEY, entryData)
                addProperty(SIGN_DATA_KEY, signData)
                addProperty(MERCHANT_NO,MiraConfigure.instance.getConfig()?.merchantNo)
                addProperty(APP_ID,MiraConfigure.instance.appId())
                addProperty(TIME_STAMP,requestTime.toString())
            }
            val finalWrapperString = gson.toJson(wrapper)

            if (!finalWrapperString.isNullOrEmpty()) {
                // --- 核心修改点：使用新版 RequestBody.create ---

                // 7. 使用 Kotlin 扩展函数构建 JSON 请求体 (替换过时的 FormBody)
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val newBody = finalWrapperString.toRequestBody(mediaType)

                val newRequest = request.newBuilder()
                    .post(newBody)
                    // 显式添加 Header 确保后端识别
                    .header("Content-Type", "application/json; charset=utf-8")
                    .build()

                return chain.proceed(newRequest)
            } else {
                SLog.e(TAG, "AES encrypt empty")
            }
        } else {
            SLog.e(TAG, "json empty")
        }

        return chain.proceed(request)
    }

    private fun extractJsonContent(body: RequestBody): String? {
        // 如果是表单，提取特定 key 的值；否则读取整个 Body
        if (body is FormBody) {
            for (i in 0 until body.size) {
                if (body.name(i) == SERVICE_DATA_KEY) {
                    return body.value(i)
                }
            }
        } else {
            try {
                val buffer = Buffer()
                body.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: Exception) {
                SLog.e(TAG, "读取请求体失败", e)
            }
        }
        return null
    }


    companion object {
        private const val TAG = "ParamsInterceptor"
        const val METHOD_POST = "post"
        const val SERVICE_DATA_KEY = "data"
        const val SIGN_DATA_KEY = "signData"
        const val MERCHANT_NO = "merchantNo"
        const val APP_ID = "appId";
        const val TIME_STAMP = "timestamp";
    }
}
