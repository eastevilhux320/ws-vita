package com.wsvita.network.convert

import com.wsvita.framework.utils.JsonUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.BaseCallAdapterFactory
import com.wsvita.network.INetworkAdapter
import retrofit2.CallAdapter
import retrofit2.Converter

class DefaultNetworkAdapter : INetworkAdapter {
    companion object{
        private const val TAG = "WSV_NET_DefaultNetworkAdapter=>"
    }

    override fun provideConverterFactory(): Converter.Factory {
        // 使用原有的 Base 转换逻辑
        SLog.d(TAG,"provideConverterFactory");
        return BaseConverterFactory.create(JsonUtil.getInstance().getGson())
    }

    override fun provideCallAdapterFactory(): CallAdapter.Factory {
        SLog.d(TAG,"provideCallAdapterFactory");
        // 使用原有的同步错误处理适配器
        return BaseCallAdapterFactory.create()
    }
}
