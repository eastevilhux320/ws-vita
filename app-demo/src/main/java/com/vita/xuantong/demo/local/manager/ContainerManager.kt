package com.wsvita.app.local.manager

import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import java.util.concurrent.ConcurrentHashMap

class ContainerManager private constructor() : BaseManager() {

    // 内存缓存池：使用 ConcurrentHashMap 保证物理层面的线程安全
    private val mDataCache = ConcurrentHashMap<String, Any>()

    companion object {
        private const val TAG = "Mirror_M_ContainerManager=>"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ContainerManager() }
    }

    override fun onInit() {
        // 初始化逻辑
    }

    // ======================================================================================
    // 自定义对象缓存 (Object - 内存引用存储)
    // ======================================================================================

    /**
     * 存入对象（存储的是内存引用）
     */
    private fun putObject(data: Any) {
        val key = data.javaClass.name
        this.mDataCache[key] = data
    }

    /**
     * 获取对象
     */
    private fun <T : Any> getObject(clazz: Class<T>): T? {
        val key = clazz.name
        val data = this.mDataCache[key]
        if (clazz.isInstance(data)) {
            return clazz.cast(data)
        }
        return null
    }

    // ======================================================================================
    // 基础类型缓存 (Boolean, Int, Long, Float, Double, String)
    // ======================================================================================

    /**
     * Boolean 类型支持
     */
    fun putBoolean(key: String, value: Boolean) {
        this.mDataCache[key] = value
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val data = this.mDataCache[key]
        return if (data is Boolean) data else defaultValue
    }

    /**
     * Int 类型支持
     */
    fun putInt(key: String, value: Int) {
        this.mDataCache[key] = value
    }

    fun getInt(key: String, defaultValue: Int): Int {
        val data = this.mDataCache[key]
        return if (data is Int) data else defaultValue
    }

    /**
     * Long 类型支持
     */
    fun putLong(key: String, value: Long) {
        this.mDataCache[key] = value
    }

    fun getLong(key: String, defaultValue: Long): Long {
        val data = this.mDataCache[key]
        return if (data is Long) data else defaultValue
    }

    /**
     * Float 类型支持
     */
    fun putFloat(key: String, value: Float) {
        this.mDataCache[key] = value
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        val data = this.mDataCache[key]
        return if (data is Float) data else defaultValue
    }

    /**
     * Double 类型支持
     */
    fun putDouble(key: String, value: Double) {
        this.mDataCache[key] = value
    }

    fun getDouble(key: String, defaultValue: Double): Double {
        val data = this.mDataCache[key]
        return if (data is Double) data else defaultValue
    }

    /**
     * String 类型支持
     */
    fun putString(key: String, value: String) {
        this.mDataCache[key] = value
    }

    fun getString(key: String, defaultValue: String): String {
        val data = this.mDataCache[key]
        return if (data is String) data else defaultValue
    }

    fun getString(key: String): String? {
        val data = this.mDataCache[key]
        return if (data is String) data else null
    }

    // ======================================================================================
    // Json 实体缓存 (复杂对象 - 镜像存储)
    // ======================================================================================

    /**
     * 将对象转为 Json 字符串存储 (实现深拷贝效果)
     */
    fun putJson(key: String, data: Any) {
        val json = data.toJson()
        putString(key, json)
    }

    /**
     * 获取 Json 字符串并反序列化为实体
     */
    fun <T> getJsonEntity(key: String, clazz: Class<T>): T? {
        val json = getString(key)
        if (json != null && json.isNotEmpty()) {
            try {
                return JsonUtil.getInstance().getGson().fromJson<T>(json, clazz)
            } catch (e: Exception) {
                SLog.e(TAG, "getJsonEntity error: key=$key, cause=${e.message}")
            }
        }
        return null
    }

    // ======================================================================================
    // 管理操作
    // ======================================================================================

    /**
     * 移除指定缓存
     */
    fun remove(key: String) {
        this.mDataCache.remove(key)
    }

    /**
     * 清空所有缓存
     */
    fun clearAll() {
        this.mDataCache.clear()
        SLog.d(TAG, "ContainerManager Cache Cleared.")
    }
}
