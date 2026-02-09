package com.wsvita.framework.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Json 解析工具类
 * 采用传统的双重检查锁定（Double-Checked Locking）单例模式
 */
class JsonUtil private constructor() {

    // 内部持有的 Gson 实例，在构造函数中完成配置
    private val gson: Gson

    init {
        gson = GsonBuilder()
            // 启用宽容模式，允许 JSON 中存在不规范的字符
            .setLenient()
            // 支持复杂的 Map Key 序列化
            .enableComplexMapKeySerialization()
            // 防止 HTML 特殊字符（如 &、=）被转义为 Unicode
            .disableHtmlEscaping()
            .create()
    }

    companion object {
        // 使用 @Volatile 确保多线程环境下的内存可见性
        @Volatile
        private var instance: JsonUtil? = null

        /**
         * 获取 JsonUtil 单例的唯一入口
         * 严格遵循 getInstance() 命名规范
         */
        @JvmStatic
        fun getInstance(): JsonUtil {
            // 第一次检查：避免不必要的同步开销
            return instance ?: synchronized(this) {
                // 第二次检查：确保只创建一个实例
                instance ?: JsonUtil().also { instance = it }
            }
        }
    }

    /**
     * 获取配置好的 Gson 实例
     * @return Gson
     */
    fun getGson(): Gson {
        return gson
    }
}
