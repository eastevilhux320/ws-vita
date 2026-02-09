package com.wsvita.network.interceptor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.util.*

/**
 * 最终优化版：适配 OkHttp 4.x+ 语法
 * 逻辑：将加密后的包装对象转为标准的 JSON 请求体，解决后端 MediaType 不支持的问题
 */
class ParamsInterceptor : Interceptor {

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
                    SLog.e(TAG, "加密流程崩溃，降级使用原请求", e)
                }
            }
        }

        return chain.proceed(request)
    }

    private fun processEncryption(chain: Interceptor.Chain, request: Request, body: RequestBody): Response {
        val originalDataJson = extractJsonContent(body)

        if (!originalDataJson.isNullOrBlank()) {
            val signData = generateSignature(originalDataJson)
            val entryData = NetworkClient.instance.networkDataSecurity()?.encrypt(originalDataJson)

            // 5. 组合包装对象
            val wrapper = JsonObject().apply {
                addProperty(SERVICE_DATA_KEY, entryData)
                addProperty(SIGN_DATA_KEY, signData)
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
                SLog.e(TAG, "AES 加密结果为空")
            }
        } else {
            SLog.e(TAG, "未获取到有效的 JSON 内容")
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

    private fun generateSignature(jsonData: String): String? {
        try {
            val jsonElement = JsonParser.parseString(jsonData)
            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                val sortedMap = TreeMap<String, String>()

                jsonObject.entrySet().forEach { entry ->
                    val value = entry.value
                    val valueStr = if (value.isJsonPrimitive) value.asString else value.toString()
                    sortedMap[entry.key] = valueStr
                }

                val sb = StringBuilder()
                sortedMap.forEach { (k, v) -> sb.append(k).append(v) }

                val security = NetworkClient.instance.networkDataSecurity()
                return security?.signData(sb.toString()) ?: ""
            }
        } catch (e: Exception) {
            SLog.e(TAG, "签名计算失败", e)
        }
        return ""
    }

    companion object {
        private const val TAG = "ParamsInterceptor"
        const val METHOD_POST = "post"
        const val SERVICE_DATA_KEY = "data"
        const val SIGN_DATA_KEY = "signData"
    }
}
