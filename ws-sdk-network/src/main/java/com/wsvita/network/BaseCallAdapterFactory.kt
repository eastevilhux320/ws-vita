package com.wsvita.network

import com.wsvita.network.adapter.BaseCallAdapter
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class BaseCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): CallAdapter<*, *> {
        return BaseCallAdapter<Any>(returnType!!);
    }

    companion object{
        fun create() : BaseCallAdapterFactory = BaseCallAdapterFactory();
    }
}
