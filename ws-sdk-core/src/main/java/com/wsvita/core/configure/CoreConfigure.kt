package com.wsvita.core.configure

import com.wsvita.core.local.manager.ContainerManager
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.utils.SLog

class CoreConfigure private constructor(){

    companion object {
        private const val TAG = "WSVita_Core_CoreConfigure=>";
        /**
         * 线程安全的单例对象
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CoreConfigure() }

        /**
         * 初始化状态标识
         */
        private var isInit = false

        /**
         * 框架全量配置对象
         */
        private var coreConfig: CoreConfig? = null

        /**
         * 日期时间配置对象
         */
        private var dateTimeConfig : DateTimeConfig? = null;

        /**
         * 应用唯一标识
         */
        private var appId: Long = 0L
    }

    fun init(config: CoreConfig) {
        SLog.d(TAG,"init invoke");
        coreConfig = config
        appId = config.appId
        // 联动初始化版本管理类
        ContainerManager.instance.init();
        isInit = true
    }

    fun setDateTimeConfig(dateTimeConfig: DateTimeConfig){
        checkInit();
        CoreConfigure.dateTimeConfig = dateTimeConfig;
    }

    fun appId(): Long {
        checkInit()
        if (appId == 0L) {
            throw IllegalStateException("FrameConfigure: appId has not been set properly.")
        }
        return appId
    }

    fun getConfig(): CoreConfig? {
        checkInit();
        return coreConfig;
    }

    fun getDateTimeConfig(): DateTimeConfig? {
        return dateTimeConfig;
    }

    private fun checkInit() {
        if (!isInit) {
            throw IllegalStateException("FrameConfigure: framework has not been initialized. Please call init(FrameConfig) first.")
        }
    }
}
