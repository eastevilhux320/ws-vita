package com.wangshu.mira.network.convert

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wsvita.framework.utils.SLog
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.NullPointerException
import java.lang.reflect.Type

@SuppressLint("LongLogTag")
class MiraConverterFactory(private val gson:Gson) : Converter.Factory(){

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *> {
        var adapter = gson.getAdapter(TypeToken.get(type));
        SLog.d(TAG,"responseBodyConverter==>")
        return MiraResponseBodyConverter(gson,adapter,type);
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        SLog.d(TAG,"requestBodyConverter==>");
        return MiraReqeustBodyConverter(gson,adapter);
    }



    companion object{
        private  val TAG = "WSV_NET_BaseConverterFactory=>";

        fun create() : MiraConverterFactory {
            return create(Gson());
        }

        fun create(gson:Gson?) : MiraConverterFactory {
            SLog.d(TAG,"create==>");
            if(gson == null) {
                Log.e(TAG,"create EastConverterFactory when gson is null")
                throw NullPointerException("gson not allow null")
            };
            return MiraConverterFactory(gson);
        }
    }
}
