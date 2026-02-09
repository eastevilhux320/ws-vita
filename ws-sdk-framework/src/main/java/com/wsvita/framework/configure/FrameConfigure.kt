package com.wsvita.framework.configure

import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog

/**
 * 框架配置全局管理类
 * 用于统一管理 FrameConfig 的初始化状态及其全局访问入口
 *
 * @author Eastevil
 * @createTime 2025/12/24
 */
class FrameConfigure private constructor() {

    companion object {
        private const val TAG = "WSVita_Framework_FrameConfigure=>";
        /**
         * 线程安全的单例对象
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { FrameConfigure() }

        /**
         * 初始化状态标识
         */
        private var isInit = false

        /**
         * 框架全量配置对象
         */
        private var frameConfig: FrameConfig? = null

        /**
         * 应用唯一标识
         */
        private var appId: Long = 0L
    }

    /**
     * 框架组件初始化方法
     * 必须在 Application 或程序入口处最先调用
     *
     * @param config 框架配置实例，通过 FrameConfig.Builder 构建
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun init(config: FrameConfig) {
        SLog.d(TAG,"init invoke");
        frameConfig = config
        appId = config.appId

        StorageManager.instance.init(appId);

        // 联动初始化版本管理类
        isInit = true
    }

    /**
     * 获取应用唯一标识
     * @return appId
     * @throws IllegalStateException 如果未初始化或 appId 异常则抛出
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun appId(): Long {
        checkInit()
        if (appId == 0L) {
            throw IllegalStateException("FrameConfigure: appId has not been set properly.")
        }
        return appId
    }

    /**
     * 获取当前框架的配置对象
     * @return FrameConfig 实例
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun getConfig(): FrameConfig? {
        checkInit()
        return frameConfig
    }

    /**
     * 内部初始化状态校验
     * @author Eastevil
     * @createTime 2025/12/24
     */
    private fun checkInit() {
        if (!isInit) {
            throw IllegalStateException("FrameConfigure: framework has not been initialized. Please call init(FrameConfig) first.")
        }
    }
}
