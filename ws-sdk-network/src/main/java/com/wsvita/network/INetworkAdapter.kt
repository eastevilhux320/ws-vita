package com.wsvita.network

import retrofit2.CallAdapter
import retrofit2.Converter

/**
 * 网络层适配器策略接口。
 * 用于解耦业务组件与底层网络框架的具体实现。
 * 通过实现此接口，各业务模块可以灵活定义自己的数据转换策略（如 Gson/FastJson/ProtoBuf）
 * 以及请求响应的包装方式（如 同步Result/RxJava/Coroutines）。
 *
 * create by Eastevil at 2025/12/26 16:16
 * @author Eastevil
 */
interface INetworkAdapter {

    /**
     * Description 提供数据转换工厂
     * 用于决定如何将 Http 响应体（ResponseBody）转换为业务实体对象，
     * 以及将请求实体转换为请求体（RequestBody）。
     *
     * create by Eastevil at 2025/12/26 16:16
     * @author Eastevil
     * @return Converter.Factory 转换工厂实例
     */
    fun provideConverterFactory(): Converter.Factory

    /**
     * Description 提供调用适配器工厂
     * 用于定义 Retrofit 接口方法的返回类型。
     * 例如：返回同步的 [com.wsvita.network.entity.Result]、RxJava 的 Observable 或协程的 Deferred。
     *
     * create by Eastevil at 2025/12/26 16:16
     * @author Eastevil
     * @return CallAdapter.Factory 适配器工厂实例
     */
    fun provideCallAdapterFactory(): CallAdapter.Factory
}
