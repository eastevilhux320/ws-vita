package com.wsvita.biz.core.configure

import com.wsvita.biz.core.local.manager.RegionManager
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.utils.SLog

/**
 * 业务核心层（BizCore）全局配置管理类
 * 负责管理业务通用逻辑的相关配置，通常在底层框架初始化后进行
 *
 * @author Eastevil
 * @createTime 2025/12/24
 */
class BizcoreConfigure private constructor(){

    companion object {
        private const val TAG = "WSVita_Bizcore_BizcoreConfigure=>"

        /**
         * 线程安全的单例对象
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { BizcoreConfigure() }

        /**
         * 业务组件初始化状态
         */
        private var isInit = false

        /**
         * 业务核心层全量配置对象
         */
        private var bizConfig: BizcoreConfig? = null

        /**
         * 业务关联的应用唯一标识
         */
        private var appId: Long = 0L
    }

    /**
     * 业务组件初始化方法
     * 建议在 Application 中紧随底层框架初始化之后调用
     *
     * @param config
     *      业务配置实例，通过 BizcoreConfig.Builder 构建
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun init(config: BizcoreConfig) {
        SLog.d(TAG, "Bizcore init invoke")
        // 修正：使用 companion 变量进行赋值
        BizcoreConfigure.bizConfig = config
        BizcoreConfigure.appId = config.appId
        // 此处可添加联动初始化业务逻辑，如：用户信息初始化、业务埋点初始化等
        RegionManager.getInstance().init();
        isInit = true
    }

    /**
     * 获取业务层定义的应用唯一标识
     * @return appId
     * @throws IllegalStateException 如果业务层未初始化或 appId 异常则抛出
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun appId(): Long {
        checkInit()
        if (appId == 0L) {
            throw IllegalStateException("BizcoreConfigure: appId has not been set properly.")
        }
        return appId
    }

    /**
     * 获取业务层当前配置对象
     * @return BizcoreConfig 实例
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun getConfig(): BizcoreConfig? {
        checkInit()
        return bizConfig
    }


    /**
     * 内部初始化状态校验
     * 确保业务组件在使用前已正确配置
     * @author Eastevil
     * @createTime 2025/12/24
     */
    private fun checkInit() {
        if (!isInit) {
            throw IllegalStateException("BizcoreConfigure: business core has not been initialized. Please call init(BizcoreConfig) first.")
        }
    }
}
