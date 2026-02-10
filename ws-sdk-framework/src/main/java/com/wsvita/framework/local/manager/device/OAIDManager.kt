package com.wsvita.framework.local.manager.device

import android.app.Application
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.github.gzuliyujiang.oaid.IGetter
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.local.WsContext
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import ext.StringExt.isInvalid
import ext.StringExt.isNotInvalid

/**
 * 匿名设备标识符 (OAID) 统一管理组件
 * * [1. OAID 的背景与作用]:
 * - 背景：在 Android 10 (API 29) 及以上版本，系统限制了对 IMEI、序列号等不可重置设备 ID 的访问。
 * - 作用：OAID 是由移动安全联盟 (MSA) 提供的替代方案，用于在保护隐私的前提下，进行广告归因、
 * 用户唯一性识别、风控反欺诈及个性化推荐。
 * - 特性：用户可以在系统设置中重置 OAID，符合《个人信息保护法》等监管要求。
 *
 * [2. 类的核心职责]:
 * - 状态恢复：[onInit] 时从 StorageManager 自动恢复历史缓存，实现冷启动“秒回”数据。
 * - 异步同步化：通过回调队列处理底层 SDK 的异步回调，对外暴露简洁的同步与回调接口。
 * - 合规控频：内置 MIN_CALL_INTERVAL (10s)，防止因频繁调用系统敏感 API 导致的合规性违规。
 * - 持久化：确保 OAID 在本地可靠存储，减少对系统 API 的依赖。
 *
 * [3. 使用方法]:
 * * // 方式 A：同步获取 (适合非阻塞、对实时性要求不高的场景，如一般埋点)
 * val id = OAIDManager.instance.oaid()
 * * // 方式 B：回调获取 (适合强依赖场景，如广告激活、新人礼包领取)
 * OAIDManager.instance.getOAID { id ->
 * id?.let { /* 执行核心业务逻辑 */ }
 * }
 *
 * [4. 注意事项]:
 * - 初始化时机：必须在用户点击“隐私协议同意”后调用 init，严禁在同意前获取。
 * - 结果空处理：在首次安装或 SDK 尚未返回结果时，[oaid()] 可能返回 null。
 * - 模拟器兼容：部分模拟器或老旧机型可能无法获取 OAID，业务侧需做好兜底处理。
 * =================================================================================

 * create by Eastevil at 2026/1/5 13:26
 * @author Eastevil
 */
class OAIDManager private constructor() : BaseManager() {

    // 待处理的回调队列，确保并发调用时都能收到通知
    private val callbacks = mutableListOf<(String?) -> Unit>()

    companion object {
        private const val TAG = "WSVita_F_M_OAIDManager=>"
        private const val MIN_CALL_INTERVAL = 10000L
        private const val KEY_OAID = "key_device_oaid"

        val instance: OAIDManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { OAIDManager() }

        private var lastImeiTime: Long = -1

        @Volatile
        private var cacheOaid: String? = null
        private var isFetching = false
    }

    fun register(application: Application){
        // 1. 注册底层 SDK
        DeviceIdentifier.register(application);
        fetchOaidFromSystem();
    }

    /**
     * 组件初始化：从持久化层恢复数据，并按需触发物理获取
     */
    override fun onInit() {
        SLog.d(TAG,"onInit");
        // 2. 从存储恢复缓存，确保后续 oaid() 同步调用能秒回
        val savedOaid = StorageManager.instance.getString(KEY_OAID, "")
        if (!savedOaid.isNullOrBlank()) {
            cacheOaid = savedOaid
            SLog.d(TAG, "onInit: Loaded from storage -> $cacheOaid")
        }

        // 3. 判断是否需要静默更新（如果缓存无效，则物理获取）
        // 这里建议保留你之前的 isInvalid() 逻辑判断
        if (cacheOaid.isNullOrBlank()) {
            SLog.d(TAG, "onInit: Cache invalid, pre-fetching...")
            fetchOaidFromSystem()
        }
    }

    /**
     * 同步获取：直接返回当前内存值
     */
    fun oaid(): String? {
        checkInit()
        if (cacheOaid.isNullOrEmpty()) {
            fetchOaidFromSystem()
        }
        return cacheOaid
    }

    /**
     * 带回调的获取：有缓存给缓存，没缓存等异步结果
     */
    fun getOAID(callback: (String?) -> Unit) {
        checkInit()

        // 1. 命中内存缓存，直接回调
        if (!cacheOaid.isNullOrEmpty()) {
            callback.invoke(cacheOaid)
            return
        }

        // 2. 无缓存，将本次回调加入待处理队列
        synchronized(callbacks) {
            callbacks.add(callback)
        }

        // 3. 触发物理获取
        fetchOaidFromSystem()
    }

    private fun fetchOaidFromSystem() {
        if (isFetching) return

        val now = System.currentTimeMillis()
        if (lastImeiTime > 0 && now - lastImeiTime < MIN_CALL_INTERVAL) {
            SLog.w(TAG, "fetchOaid: Call too frequent, skipped.")
            return
        }

        isFetching = true
        lastImeiTime = now

        SLog.d(TAG, "fetchOaid: Triggering DeviceID.getOAID")
        DeviceID.getOAID(WsContext.context, getter)
    }

    private val getter = object : IGetter {
        override fun onOAIDGetComplete(result: String?) {
            SLog.d(TAG,"IGetter_onOAIDGetComplete,oaid:${oaid()}");
            isFetching = false
            if (result.isNotInvalid()) {
                cacheOaid = result
                // 存入 StorageManager 供下次 onInit 使用
                StorageManager.instance.put(KEY_OAID, result!!)
                SLog.i(TAG, "onOAIDGetComplete: $result")
            }
            dispatchResult(result)
        }

        override fun onOAIDGetError(error: Exception?) {
            isFetching = false
            SLog.e(TAG, "IGetter_onOAIDGetError")
            dispatchResult(null)
        }
    }

    private fun dispatchResult(result: String?) {
        synchronized(callbacks) {
            val iterator = callbacks.iterator()
            while (iterator.hasNext()) {
                iterator.next().invoke(result)
                iterator.remove()
            }
        }
    }
}
