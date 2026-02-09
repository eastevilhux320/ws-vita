package com.wsvita.network.configure

import com.wsvita.framework.utils.SLog
import com.wsvita.network.IDataSecurity
import com.wsvita.network.NetworkClient
import com.wsvita.network.NetworkOptions

/**
 * 网络组件配置入口类，负责连接业务层配置与底层网络引擎
 * create by Eastevil at 2025/12/25 10:20
 * @author Eastevil
 */
class NetworkConfigure private constructor() {

    companion object {
        private const val TAG = "WSV_NET_Config_NetworkConfigure=>"

        /**
         * 获取 NetworkConfigure 唯一单例
         * create by Eastevil at 2025/12/25 10:20
         * @author Eastevil
         * @return NetworkConfigure 实例
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NetworkConfigure() }

        private var isInit = false
        private var config: NetworkConfig? = null
    }

    /**
     * 业务核心初始化，直接接受 NetworkConfig 参数，内部会生成默认的 NetworkOptions
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param config
     *      业务基础配置对象（包含 appId, baseUrl 等）
     * @return
     *      Unit
     */
    fun init(config: NetworkConfig) {
        SLog.d(TAG, "NetworkConfigure init invoke")
        NetworkConfigure.config = config
        // 联动初始化 NetworkClient
        NetworkClient.instance.init(config.appId.toString())
        isInit = true
    }

    /**
     * 业务核心初始化，支持同时传入业务配置 [NetworkConfig] 和自定义引擎配置 [NetworkOptions]
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param config
     *      业务基础配置对象
     * @param options
     *      底层引擎高级配置项
     * @return Unit
     */
    fun init(config: NetworkConfig, options: NetworkOptions) {
        SLog.d(TAG, "NetworkConfigure init invoke")
        NetworkConfigure.config = config

        NetworkClient.instance.init(options);
        isInit = true
    }

    /**
     * 快捷创建 Service 实例。
     * 如果不传 baseUrl，则默认使用初始化时 [NetworkConfig] 中提供的地址。
     * 子组件建议通过此方法获取业务接口实例。
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param serviceClass
     *      接口类类型
     * @param baseUrl
     *      覆盖默认的基础请求路径
     * @return T
     *      接口实例
     */
    fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
        checkInit()
        return NetworkClient.instance.createService(serviceClass, baseUrl)
    }

    /**
     * Description 检查组件是否已完成初始化，未初始化则抛出异常
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    private fun checkInit() {
        if (!isInit) throw IllegalStateException("Please call init(NetworkConfig) first.")
    }

    /**
     * Description 获取当前配置的 appId
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @return Long 应用唯一标识，默认返回 0L
     */
    fun appId(): Long = config?.appId ?: 0L

    /**
     * Description 获取当前配置的基础请求路径
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @return String? 基础请求路径
     */
    fun baseUrl(): String? {
        return config?.baseUrl;
    }

    fun getConfig(): NetworkConfig? {
        return config;
    }


    fun addDataSecurity(dataSecurity : IDataSecurity){
        NetworkClient.instance.addDataSecurity(dataSecurity);
    }
}
