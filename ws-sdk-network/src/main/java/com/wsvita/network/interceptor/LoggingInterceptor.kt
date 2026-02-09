package com.wsvita.network.interceptor

import android.util.Log
import com.wsvita.framework.ext.MathExt.randomNumber
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

/**
 * 修正版日志拦截器
 * 核心改进：彻底移除对 NetworkClient 的强依赖，防止初始化死锁。
 */
class LoggingInterceptor : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. 生成局部 ID，确保日志不串联
        val currentHttpId = randum()
        val request = chain.request()
        val startTime = System.nanoTime()

        // --- 打印请求详情 (用 try-catch 保护，确保日志逻辑不影响主流程) ---
        try {
            Log.d(TAG, "[ID:$currentHttpId] --> request: ${request.url}, ${request.method}")

            val headers = request.headers
            for (i in 0 until headers.size) {
                Log.d(TAG, "[ID:$currentHttpId] Header: ${headers.name(i)} = ${headers.value(i)}")
            }

            request.body?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                val charset = body.contentType()?.charset(UTF8) ?: UTF8
                Log.d(TAG, "[ID:$currentHttpId] Params: ${buffer.readString(charset)}")
            }
        } catch (e: Exception) {
            // 日志报错不抛出，确保请求能继续走
            Log.e(TAG, "[ID:$currentHttpId] Log request error: ${e.message}")
        }

        // 2. 【最关键一步】执行网络请求，将控制权交给 HeaderInterceptor
        val response: Response
        try {
            // 只要这行执行，后面的 HeaderInterceptor 就会被调用
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "[ID:$currentHttpId] <-- HTTP FAILED: $e")
            throw e
        }

        val endTime = System.nanoTime()
        val duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

        // --- 打印响应详情 ---
        try {
            Log.d(TAG, "[ID:$currentHttpId] <-- response [${response.code}] (${duration}ms): ${request.url}")

            val responseBody = response.body
            if (responseBody != null) {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                val charset = responseBody.contentType()?.charset(UTF8) ?: UTF8

                val content = buffer.clone().readString(charset)
                logInSegments(currentHttpId, "Result: $content")
            }
        } catch (e: Exception) {
            Log.e(TAG, "[ID:$currentHttpId] Log response error: ${e.message}")
        }

        return response
    }

    private fun randum(): Int = randomNumber(10000, 99999)

    private fun logInSegments(id: Int, message: String) {
        val maxLogSize = 3000
        var i = 0
        while (i < message.length) {
            val end = (i + maxLogSize).coerceAtMost(message.length)
            Log.d(TAG, "[ID:$id] ${message.substring(i, end)}")
            i += maxLogSize
        }
    }

    companion object {
        private const val TAG = "WSV_NET_HTTP_Interceptor_Log=>"
        private val UTF8 = Charset.forName("UTF-8")
    }
}
