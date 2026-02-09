package com.wsvita.network

import com.wsvita.framework.utils.JsonUtil
import com.wsvita.network.convert.BaseConverterFactory
import retrofit2.CallAdapter
import retrofit2.Converter

class DefaultNetworkAdapter : INetworkAdapter {

    override fun provideConverterFactory(): Converter.Factory {
        // 使用原有的 Base 转换逻辑
        return BaseConverterFactory.create(JsonUtil.getInstance().getGson())
    }

    override fun provideCallAdapterFactory(): CallAdapter.Factory {
        // 使用原有的同步错误处理适配器
        return BaseCallAdapterFactory.create()
    }
}
