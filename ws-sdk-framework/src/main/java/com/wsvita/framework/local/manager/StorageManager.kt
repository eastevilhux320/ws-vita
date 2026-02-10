package com.wsvita.framework.local.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache
import com.wsvita.framework.ext.JsonExt.parseGson
import com.wsvita.framework.local.WsContext
import com.wsvita.framework.utils.JsonUtil
import java.io.*
import java.security.MessageDigest

/**
 * **StorageManager**
 * 框架层公共存储管理组件。
 * 统一检索策略：内存 (LruCache) -> 轻量级 (SP) -> 磁盘文件 (Disk)。
 */
class StorageManager private constructor() {

    @PublishedApi
    internal lateinit var memoryCache: LruCache<String, Any?>
    @PublishedApi
    internal lateinit var diskCacheDir: File
    @PublishedApi
    internal lateinit var sp: SharedPreferences

    private val gson get() = JsonUtil.getInstance().getGson()

    companion object {
        private const val SP_NAME = "wsvita_framework_configs"
        private const val DISK_DIR_NAME = "wsvita_cache_data"
        @PublishedApi
        internal var isInit = false

        @JvmStatic
        val instance: StorageManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StorageManager() }
    }

    fun init(appId: Long) {
        //在这里需要
        val appContext = WsContext.context;
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        memoryCache = LruCache(maxMemory / 8)
        diskCacheDir = File(appContext.cacheDir, DISK_DIR_NAME).apply { if (!exists()) mkdirs() }
        sp = appContext.getSharedPreferences("${SP_NAME}_${appId}", Context.MODE_PRIVATE)
        isInit = true
    }

    // =================================================================================
    // 核心存储方法：put (同步三级)
    // =================================================================================

    /**
     * 存储对象或基础类型。
     * 对于基础类型，直接存入 SP；对于复杂对象，存入 SP 的 Json 字符串。
     */
    fun <T : Serializable> put(key: String, value: T) {
        checkInit()
        if (key.isBlank()) return

        // 1. 内存
        memoryCache.put(key, value)

        // 2. SP 处理
        val editor = sp.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            else -> editor.putString(key, gson.toJson(value)) // 复杂对象存 Json
        }
        editor.apply()

        // 3. 磁盘 (二进制持久化)
        saveToDisk(getHashedKey(key), value)
    }

    // =================================================================================
    // 核心获取方法：get (三级检索 + 回填)
    // =================================================================================

    /**
     * 核心泛型获取方法。
     * 所有的 getString, getInt 等最终都应该走这个逻辑。
     */
    inline fun <reified T : Serializable> get(key: String, defaultValue: T? = null): T? {
        checkInit()
        if (key.isBlank()) return defaultValue

        // --- 1. 内存查找 ---
        val memValue = memoryCache.get(key)
        if (memValue is T) return memValue

        // --- 2. SP 查找 ---
        val spValue = getFromSP<T>(key)
        if (spValue != null) {
            memoryCache.put(key, spValue) // 回填内存
            return spValue
        }

        // --- 3. 磁盘查找 ---
        val hashedKey = getHashedKey(key)
        val diskValue = readFromDisk(hashedKey)
        if (diskValue is T) {
            memoryCache.put(key, diskValue) // 回填内存
            // 这里可以根据需要决定是否回填 SP，基础类型通常不需要，复杂对象建议回填
            return diskValue
        }

        return defaultValue
    }

    @PublishedApi
    internal inline fun <reified T> getFromSP(key: String): T? {
        if (!sp.contains(key)) return null
        return when (T::class) {
            String::class -> sp.getString(key, null) as? T
            Int::class -> sp.getInt(key, 0) as? T
            Boolean::class -> sp.getBoolean(key, false) as? T
            Long::class -> sp.getLong(key, 0L) as? T
            Float::class -> sp.getFloat(key, 0.0f) as? T
            else -> {
                // 如果不是基础类型，尝试按 Json 解析
                val jsonStr = sp.getString(key, null) ?: return null
                try {
                    jsonStr.parseGson<T>();
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    // =================================================================================
    // 基础类型便捷获取方法 (现在全部走内存优先逻辑)
    // =================================================================================

    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String {
        val value = get<String>(key)
        return value ?: defaultValue
    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = 0): Int {
        val value = get<Int>(key)
        return value ?: defaultValue
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val value = get<Boolean>(key)
        return value ?: defaultValue
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        val value = get<Long>(key)
        return value ?: defaultValue
    }

    @JvmOverloads
    fun getFloat(key: String, defaultValue: Float = 0.0f): Float {
        val value = get<Float>(key)
        return value ?: defaultValue
    }

    // =================================================================================
    // 内部工具与清理
    // =================================================================================

    @PublishedApi
    internal fun checkInit() {
        if (!isInit) throw IllegalStateException("StorageManager has not been initialized.")
    }

    @PublishedApi
    internal fun getHashedKey(key: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val bytes = digest.digest(key.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            key.hashCode().toString()
        }
    }

    @PublishedApi
    internal fun readFromDisk(hashedKey: String): Any? {
        val file = File(diskCacheDir, hashedKey)
        if (!file.exists()) return null
        return try {
            ObjectInputStream(FileInputStream(file)).use { it.readObject() }
        } catch (e: Exception) { null }
    }

    private fun saveToDisk(hashedKey: String, value: Serializable) {
        try {
            val file = File(diskCacheDir, hashedKey)
            ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(value) }
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun remove(key: String) {
        checkInit()
        memoryCache.remove(key)
        sp.edit().remove(key).apply()
        File(diskCacheDir, getHashedKey(key)).let { if (it.exists()) it.delete() }
    }

    fun clearAll() {
        checkInit()
        memoryCache.evictAll()
        sp.edit().clear().apply()
        diskCacheDir.listFiles()?.forEach { it.delete() }
    }
}
