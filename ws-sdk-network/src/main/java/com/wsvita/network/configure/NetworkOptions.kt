package com.wsvita.network

import okhttp3.Interceptor
import java.nio.charset.Charset
import java.util.*

/**
 * 全局网络配置选项
 */
class NetworkOptions private constructor(builder: Builder) {
    // 配置属性保持 val 不变
    val tokenUrl: String = builder.tokenUrl
    val appId: String? = builder.appId
    val successCode: Int = builder.successCode
    val loginErrorCode: Int = builder.loginErrorCode
    val emptyCode: Int = builder.emptyCode
    val networkErrorCode: Int = builder.networkErrorCode
    val serviceErrorCode: Int = builder.serviceErrorCode
    val charset: String = builder.charset
    val httpCharset: Charset = Charset.forName(builder.charset)
    val connectTimeout: Long = builder.connectTimeout
    val maxCacheAge: Int = builder.maxCacheAge
    val isNeedUrlDecode: Boolean = builder.isNeedUrlDecode
    val isNeedBase64: Boolean = builder.isNeedBase64
    val isEncryptParams: Boolean = builder.isEncryptParams
    val decryptType: Int = builder.decryptType
    val mediaType: String = builder.mediaType
    val interceptors: List<Interceptor> = builder.interceptors.toList()
    val headers: Map<String, String> = builder.headers

    /**
     * 将所有默认值通过 companion object 定义
     */
    companion object {
        const val DEFAULT_TOKEN_URL = ""
        const val DEFAULT_SUCCESS_CODE = 200
        const val DEFAULT_LOGIN_ERROR_CODE = 401
        const val DEFAULT_NETWORK_ERROR_CODE = -100
        const val DEFAULT_EMPTY_CODE = 204
        const val DEFAULT_SERVICE_ERROR_CODE = 500

        const val DEFAULT_CHARSET = "UTF-8"
        const val DEFAULT_TIMEOUT = 15L
        const val DEFAULT_MAX_CACHE_AGE = 60
        const val DEFAULT_MEDIA_TYPE = "application/json; charset=UTF-8"

        /**
         * 本地数据解析错误标识
         */
        const val RESPONSE_DECRYPT_ERROR = -9527;
    }

    class Builder(val appId: String? = null) {
        // 使用 Companion Object 中定义的默认值
        internal var tokenUrl = DEFAULT_TOKEN_URL
        internal var successCode = DEFAULT_SUCCESS_CODE
        internal var loginErrorCode = DEFAULT_LOGIN_ERROR_CODE
        internal var networkErrorCode = DEFAULT_NETWORK_ERROR_CODE
        internal var emptyCode = DEFAULT_EMPTY_CODE
        internal var serviceErrorCode = DEFAULT_SERVICE_ERROR_CODE

        internal var charset = DEFAULT_CHARSET
        internal var connectTimeout = DEFAULT_TIMEOUT
        internal var maxCacheAge = DEFAULT_MAX_CACHE_AGE

        internal var isNeedUrlDecode = false
        internal var isNeedBase64 = false
        internal var isEncryptParams = false
        internal var decryptType = 0
        internal var mediaType = DEFAULT_MEDIA_TYPE

        internal val interceptors = mutableListOf<Interceptor>()
        internal val headers = TreeMap<String, String>()

        fun tokenUrl(url: String) = apply { this.tokenUrl = url }
        fun successCode(code: Int) = apply { this.successCode = code }
        fun loginErrorCode(code: Int) = apply { this.loginErrorCode = code }
        fun timeout(seconds: Long) = apply { this.connectTimeout = seconds }
        fun addHeader(key: String, value: String) = apply { this.headers[key] = value }

        fun dataProcessing(urlDecode: Boolean, base64: Boolean) = apply {
            this.isNeedUrlDecode = urlDecode
            this.isNeedBase64 = base64
        }

        fun needUrlDecode() = apply{
            this.isNeedUrlDecode = true;
        }

        fun security(encrypt: Boolean, decryptType: Int = 0) = apply {
            this.isEncryptParams = encrypt
            this.decryptType = decryptType
        }

        fun addInterceptor(interceptor: Interceptor) = apply {
            this.interceptors.add(interceptor)
        }

        fun build() = NetworkOptions(this)
    }
}
