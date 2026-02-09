package com.wsvita.biz.core.network

import com.wsvita.network.entity.BaseRequest

/**
 * 业务核心请求构建器
 * 用于替代创建子类继承 BaseRequest 的模式
 */
class BizcoreRequestBuilder {

    // 存储动态业务参数
    private val params = mutableMapOf<String, Any?>()

    /**
     * 显式构造方法 [cite: 2026-01-16]
     */
    constructor()

    /**
     * 添加业务参数
     */
    fun addParam(key: String, value: Any?): BizcoreRequestBuilder {
        params[key] = value
        return this
    }

    /**
     * 构建最终发送给 Retrofit 的 Map
     * 自动合并 BaseRequest 中的全局参数
     */
    fun build(): Map<String, Any?> {
        val finalMap = mutableMapOf<String, Any?>()

        // 创建一个临时对象以获取 BaseRequest 的自动填充字段
        val base = object : BaseRequest() {
            // 显式构造
            init { }
        }

        // 注入基础参数
        finalMap["appId"] = base.appId
        finalMap["version"] = base.version
        finalMap["versionName"] = base.versionName
        finalMap["channel"] = base.channel

        // 注入业务自定义参数
        finalMap.putAll(params)

        return finalMap
    }
}
