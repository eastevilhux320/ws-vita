package com.wsvita.network

import com.wsvita.framework.utils.SLog
import com.wsvita.network.interceptor.DataSecurityInterceptor
import com.wsvita.network.interceptor.HeaderInterceptor
import com.wsvita.network.interceptor.LoggingInterceptor
import com.wsvita.network.interceptor.ParamsInterceptor
import ext.TimeExt.systemTime
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * 网络请求核心客户端，负责 OkHttpClient 的维护及 Retrofit 服务的创建
 * create by Eastevil at 2025/12/25 10:20
 * @author Eastevil
 */
class NetworkClient private constructor() {

    companion object {
        private const val TAG = "WSV_NET_NetworkClient=>"
        private const val DEFAULT_TIMEOUT = 15L

        /**
         * 获取 NetworkClient 唯一单例
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NetworkClient() }
    }

    private var adapterProvider: INetworkAdapter = DefaultNetworkAdapter()

    // 内部最终使用的配置
    private lateinit var networkOptions: NetworkOptions
    private lateinit var baseOkHttpClient: OkHttpClient;

    private var dataSecurity : IDataSecurity? = null;


    /**
     * 初始化方法：接受 appId 并生成默认的网络配置
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param appId
     *      应用唯一标识
     * @return Unit
     */
    fun init(appId : String) {
        //生成默认的网络配置
        SLog.d(TAG,"init start,time:${systemTime()}");
        val config = NetworkOptions.Builder(appId)
            .build();
        this.networkOptions = config
        baseOkHttpClient = buildOkHttpClient();
        SLog.d(TAG,"init end,time:${systemTime()}");
    }

    /**
     * 初始化方法：直接接受外部构造好的 NetworkOptions 对象
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param optios
     *      网络配置项
     */
    fun init(optios : NetworkOptions){
        SLog.d(TAG,"init start,time:${systemTime()}");

        this.networkOptions = optios
        baseOkHttpClient = buildOkHttpClient();

        SLog.d(TAG,"init end,time:${systemTime()}");
    }

    /**
     * 暴露给外部修改配置的方法（例如：运行中需要动态调整 Options 开关）
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param options
     *      新的网络配置项
     */
    fun updateOptions(options: NetworkOptions) {
        synchronized(this) {
            this.networkOptions = options
        }
    }

    /**
     * 注入数据安全处理器实现类。
     * 在组件化初始化阶段调用，用于处理网络请求中的数据加解密逻辑。
     *
     * create by Administrator at 2025/12/28 1:52
     * @author Administrator
     * @param dataSecurity
     *      com.wsvita.network.IDataSecurity 的具体实现实例
     * @return
     *      void
     */
    fun addDataSecurity(dataSecurity : IDataSecurity){
        SLog.d(TAG,"addDataSecurity,time:${systemTime()}");
        this.dataSecurity = dataSecurity;
    }

    fun networkDataSecurity(): IDataSecurity? {
        return dataSecurity;
    }

    private fun buildOkHttpClient(): OkHttpClient {
        SLog.d(TAG,"build okHttp client");
        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            // 按顺序添加拦截器
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(ParamsInterceptor())
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(DataSecurityInterceptor())
            .build()
    }

    /**
     * 核心方法：构建 Retrofit Service 实例。
     * 整个网络组件的所有 API 接口（包括基础组件和子业务组件）均通过此方法完成 Retrofit 代理对象的创建。
     * 子组件使用示例：
     * 1. 定义接口：interface UserService { @GET("path") suspend fun info(): Result<User> }
     * 2. 调用创建：val service = NetworkClient.instance.createService(UserService::class.java, "https://api.example.com")
     * * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @param serviceClass
     *      接口类类型
     * @param baseUrl
     *      基础请求路径
     * @return T
     *      接口实例
     */
    fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
        return createService(serviceClass, baseUrl, adapterProvider)
    }

    /**
     * 核心方法：创建 Service 实例 (支持自定义适配器)。
     * 该方法是网络组件的高级扩展入口，允许子组件根据业务需求注入不同的 [INetworkAdapter]。
     *
     * [adapterProvider] 说明：
     * 用于提供自定义的 [Converter.Factory] (解析器，如 Gson, Xml, ProtoBuf)
     * 和 [CallAdapter.Factory] (调用适配器，如 RxJava, LiveData, Coroutines)。
     *
     * * 子组件扩展示例：
     * 1. 实现 INetworkAdapter 接口，在 provideConverterFactory 中返回 FastJsonConverterFactory。
     * 2. 调用：val service = NetworkClient.instance.createService(MyService::class.java, url, MyFastJsonAdapter())
     * create by Eastevil at 2025/12/25 10:20
     *
     * @author Eastevil
     * @param serviceClass 接口类类型，即定义的 Retrofit Service 接口
     * @param baseUrl 基础请求路径，支持不同业务模块指向不同的服务器地址
     * @param adapterProvider 网络适配策略提供者，决定了数据的序列化方式与异步回调类型
     * @return T 返回对应的接口代理实例
     */
    fun <T> createService(
        serviceClass: Class<T>,
        baseUrl: String,
        adapterProvider: INetworkAdapter
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(baseOkHttpClient)
            .addConverterFactory(adapterProvider.provideConverterFactory())
            .addCallAdapterFactory(adapterProvider.provideCallAdapterFactory())
            .build()
            .create(serviceClass)
    }

    /**
     * 获取当前运行时的网络配置对象
     * create by Eastevil at 2025/12/25 10:20
     *
     * @author Eastevil
     * @return NetworkOptions 网络配置项
     */
    fun getOptions(): NetworkOptions {
        if (!::networkOptions.isInitialized) {
            throw UninitializedPropertyAccessException("NetworkClient has not been initialized. Please call init(NetworkConfig) first.")
        }
        return networkOptions
    }
}
