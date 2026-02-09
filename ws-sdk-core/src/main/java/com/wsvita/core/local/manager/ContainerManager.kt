package com.wsvita.core.local.manager

import android.os.Bundle
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.SLog

/**
 * 容器数据管理中心
 * 职责：负责 AppContainerActivity 生命周期内所有 Fragment 的数据分发与隔离缓存
 */
class ContainerManager private constructor(): BaseManager() {

    companion object {
        private const val TAG = "WSV_Manager_ContainerManager==>"

        val instance: ContainerManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ContainerManager()
        }
    }

    // 分舱缓存：Key 是 Fragment 的 ID，Value 是对应的参数 Bundle
    private val mPockets = mutableMapOf<Int, Bundle>()

    // 全局/共享缓存：存储那些不限目的地的通用数据
    private val mGlobalPocket = Bundle()

    override fun onInit() {
        SLog.d(TAG, "ContainerManager initialized")
    }

    /**
     * 【存】将数据存入指定 Fragment 的口袋
     * @param targetId 目标 Fragment 的资源 ID
     */
    fun put(targetId: Int, key: String, value: Any?) {
        val pocket = mPockets.getOrPut(targetId) { Bundle() }
        putIntoBundle(pocket, key, value)
    }

    /**
     * 【取】Fragment 专用拉取接口
     * @param targetId 调用者的 ID
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(targetId: Int, key: String): T? {
        return mPockets[targetId]?.get(key) as? T
    }

    fun getObj(targetId: Int,key : String): Any? {
        return mPockets.get(targetId)?.get(key);
    }

    /**
     * 【存全局】
     */
    fun putGlobal(key: String, value: Any?) {
        putIntoBundle(mGlobalPocket, key, value)
    }

    /**
     * 【取全局】
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getGlobal(key: String): T? {
        return mGlobalPocket.get(key) as? T
    }

    /**
     * 【销毁】当 Fragment 彻底销毁或容器关闭时调用，防止内存溢出
     */
    fun clear(targetId: Int) {
        mPockets.remove(targetId)
        SLog.d(TAG, "Clear pocket for: $targetId")
    }

    fun clearAll() {
        mPockets.clear()
        mGlobalPocket.clear()
    }

    /**
     * 内部工具：安全写入 Bundle
     */
    private fun putIntoBundle(bundle: Bundle, key: String, value: Any?) {
        when (value) {
            is String -> bundle.putString(key, value)
            is Int -> bundle.putInt(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            is Long -> bundle.putLong(key, value)
            is java.io.Serializable -> bundle.putSerializable(key, value)
            is android.os.Parcelable -> bundle.putParcelable(key, value)
            // 扩展你的框架支持的其他类型...
        }
    }
}
