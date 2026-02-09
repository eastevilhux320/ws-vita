package com.wsvita.account.configure

import com.wsvita.framework.utils.SLog
import com.wsvita.module.account.AccountEventIndex
import org.greenrobot.eventbus.EventBus

/**
 * @author Eastevil
 * @createTime 2025/12/24
 */
class AccountConfigure private constructor(){

    companion object {
        private const val TAG = "WS_AC_AccountConfigure=>"

        /**
         * 线程安全的单例对象
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AccountConfigure() }

        /**
         * 业务组件初始化状态
         */
        private var isInit = false

        /**
         * 业务核心层全量配置对象
         */
        private var accountConfig: AccountConfig? = null

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
    fun init(config: AccountConfig) {
        SLog.d(TAG, "AccountConfigure init invoke")
        // 修正：使用 companion 变量进行赋值
        accountConfig = config
        appId = config.appId
        // 此处可添加联动初始化业务逻辑，如：用户信息初始化、业务埋点初始化等

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
            throw IllegalStateException("AccountConfigure: appId has not been set properly.")
        }
        return appId
    }

    /**
     * 获取业务层当前配置对象
     * @return BizcoreConfig 实例
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun getConfig(): AccountConfig? {
        checkInit()
        return accountConfig
    }

    /**
     * 内部初始化状态校验
     * 确保业务组件在使用前已正确配置
     * @author Eastevil
     * @createTime 2025/12/24
     */
    private fun checkInit() {
        if (!isInit) {
            throw IllegalStateException("AccountConfigure: business core has not been initialized. Please call init(AccountConfig) first.")
        }
    }
}
