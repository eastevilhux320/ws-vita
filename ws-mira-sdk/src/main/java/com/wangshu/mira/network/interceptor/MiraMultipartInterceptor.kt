package com.wangshu.mira.network.interceptor

import com.google.gson.JsonObject
import com.wangshu.mira.configure.MiraConfigure
import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException

class MiraMultipartInterceptor : Interceptor {

    private val gson = JsonUtil.getInstance().getGson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val body = request.body

        if (request.method.equals("POST", ignoreCase = true) && body is MultipartBody) {
            try {
                return processMultipartEncryption(chain, request, body)
            } catch (e: Exception) {
                SLog.e(TAG, "Multipart encryption failed", e)
            }
        }
        return chain.proceed(request)
    }

    private fun processMultipartEncryption(chain: Interceptor.Chain, request: Request, body: MultipartBody): Response {
        val newMultipartBuilder = MultipartBody.Builder().setType(body.type)
        val requestTime = System.currentTimeMillis().toString()

        for (part in body.parts) {
            // --- 修正点：从 headers() 中获取 Content-Disposition ---
            val headers = part.headers
            val contentDisposition = headers?.get("Content-Disposition") ?: ""

            if (contentDisposition.contains("name=\"params\"")) {
                val originalJson = extractPartContent(part.body)

                if (!originalJson.isNullOrBlank()) {
                    val signData = NetworkClient.instance.networkDataSecurity()?.signData(requestTime)
                    val entryData = NetworkClient.instance.networkDataSecurity()?.encrypt(originalJson)

                    val wrapper = JsonObject().apply {
                        addProperty("data", entryData)
                        addProperty("signData", signData)
                        addProperty("merchantNo", MiraConfigure.instance.getConfig()?.merchantNo)
                        addProperty("appId", MiraConfigure.instance.appId())
                        addProperty("timestamp", requestTime)
                    }

                    val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                    val newParamsBody = gson.toJson(wrapper).toRequestBody(jsonMediaType)

                    // 重新封装时保留原有的 Headers，但 Body 换成加密后的
                    newMultipartBuilder.addPart(headers, newParamsBody)
                }
            } else {
                newMultipartBuilder.addPart(part)
            }
        }

        val newRequest = request.newBuilder()
            .post(newMultipartBuilder.build())
            .build()

        return chain.proceed(newRequest)
    }

    private fun extractPartContent(body: RequestBody): String? {
        return try {
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            null
        }
    }

    companion object {
        private const val TAG = "MiraMultipartInterceptor"
    }
}
