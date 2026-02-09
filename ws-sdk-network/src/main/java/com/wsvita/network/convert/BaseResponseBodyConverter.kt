package com.wsvita.network.convert

import com.google.gson.*
import com.wsvita.framework.utils.Base64Util
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import com.wsvita.network.NetworkOptions
import ext.StringExt.isNotInvalid
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type
import java.net.URLDecoder

class BaseResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val targetType: Type
) : Converter<ResponseBody, T> {

    companion object {
        private const val TAG = "WSV_NET_BodyConverter=>"
        private const val ERROR_DEF = -65536
    }

    override fun convert(value: ResponseBody): T? {
        // 1. 获取原始字符串
        val rawResponse = value.use { it.string() }
        SLog.d(TAG, "Response: $rawResponse")
        SLog.i(TAG, "step 1, start")

        val jsonElement = gson.fromJson(rawResponse, JsonElement::class.java)
        if (!jsonElement.isJsonObject) {
            SLog.i(TAG, "Not a JsonObject, parsing directly via adapter")
            return adapter.fromJson(rawResponse)
        }

        val jsonObject = jsonElement.asJsonObject
        SLog.i(TAG, "step 2, parse json success")

        // 2. 提取核心控制字段（用于逻辑判断和日志）
        val code = jsonObject.get("code")?.asInt ?: ERROR_DEF
        if (code == ERROR_DEF) {
            SLog.e(TAG, "response code error, return by adapter")
            return adapter.fromJson(rawResponse)
        }

        // 3. 执行你要求的完整日志打印
        printResponseLog(jsonObject)

        // 4. 处理数据转换逻辑
        SLog.i(TAG, "step 4, data transformation logic")
        val encryption = jsonObject.get("encryption")?.asBoolean ?: false
        val urlEncoder = jsonObject.get("urlEncoder")?.asBoolean ?: false
        var dataElement = jsonObject.get("data")
        var dataStr : String? = null;
        if(dataElement == null){
            //直接return
            return adapter.fromJson(rawResponse)
        }
        if(!dataElement.isJsonNull){
            if (dataElement.isJsonPrimitive){
                dataStr = dataElement.asString
            }else{
                dataStr = dataElement.toString();
            }
        }else{
            //直接return
            return adapter.fromJson(rawResponse)
        }
        SLog.i(TAG, "step 5, start dispose response data")
        if (dataStr.isNotInvalid()) {
            SLog.i(TAG, "------------------ RESPONSE DATA start, dispose the response data ------------------")
            // URL Decode
            if (urlEncoder || NetworkClient.instance.getOptions().isNeedUrlDecode) {
                SLog.i(TAG, "RESPONSE DATA, need urlDecode")
                dataStr = URLDecoder.decode(dataStr, "UTF-8")
                SLog.i(TAG, "RESPONSE DATA, result: $dataStr")
            }
            // Decrypt
            if (encryption) {
                SLog.d(TAG, "RESPONSE DATA, need encryption data")
                SLog.i(TAG, "step 6, decrypt data")
                try {
                    val decrypted = NetworkClient.instance.networkDataSecurity()?.decrypt(dataStr)
                    dataStr = decrypted
                    SLog.longD(TAG, "RESPONSE DATA, encryption result => $dataStr")
                } catch (e: Exception) {
                    SLog.e(TAG, "RESPONSE DATA,decrypt error")
                    //修改数据，认为本次处理是正确的，但会返回错误的标识
                    jsonObject.addProperty("code", NetworkOptions.RESPONSE_DECRYPT_ERROR)
                    jsonObject.addProperty("message", "response data decrypt error")
                    jsonObject.remove("data")
                    return adapter.fromJson(jsonObject.toString())
                }
            }else{
                SLog.d(TAG, "RESPONSE DATA,not need encryption data")
                SLog.i(TAG, "step 6, processing data data")
                val base64 = jsonObject.get("base64")?.asBoolean ?: false
                SLog.d(TAG,"RESPONSE DATA,base64:${base64}");
                if(base64){
                    SLog.i(TAG,"RESPONSE DATA,Service need base64")
                    dataStr = String(Base64Util.decode(dataStr), NetworkClient.instance.getOptions().httpCharset);
                    SLog.longD(TAG,"base64_to_string==>${dataStr}");
                }else{
                    if (NetworkClient.instance.getOptions().isNeedBase64) {
                        SLog.i(TAG,"RESPONSE DATA,local need base64")
                        dataStr = String(Base64Util.decode(dataStr), NetworkClient.instance.getOptions().httpCharset);
                        SLog.longD(TAG,"base64_to_string==>${dataStr}");
                    }else{
                        SLog.d(TAG,"not need base64")
                    }
                }
            }
        } else {
            SLog.d(TAG, "response data is empty")
            //直接返回
            return adapter.fromJson(rawResponse)
        }
        // 6. 组装最终 JSON 并返回
        SLog.i(TAG, "step 7, Assemble the final JSON and return it.")
        return try {
            if (!dataStr.isNullOrEmpty()) {
                // 将解密后的字符串还原为 JSON 结构插入，避免双重转义
                val finalDataElement = try {
                    gson.fromJson(dataStr, JsonElement::class.java)
                } catch (e: Exception) {
                    JsonPrimitive(dataStr)
                }
                jsonObject.add("data", finalDataElement)
            } else {
                jsonObject.remove("data")
            }
            // 关键：这里 toString() 包含了后端返回的所有原始字段 + 我们修改后的 data
            adapter.fromJson(jsonObject.toString())
        } catch (e: Exception) {
            SLog.e(TAG, "Final parse error: ${e.message}")
            adapter.fromJson(rawResponse)
        }
    }

    /**
     * 完整保留你的日志格式，并安全获取字段
     */
    private fun printResponseLog(jsonObject: JsonObject) {
        val code = jsonObject.get("code")?.asInt
        val msg = jsonObject.get("message")?.asString
        val state = jsonObject.get("state")?.asBoolean ?: false
        val encryption = jsonObject.get("encryption")?.asBoolean ?: false
        val tag = jsonObject.get("tag")?.asString ?: ""
        val base64 = jsonObject.get("base64")?.asBoolean ?: false
        val urlEncoder = jsonObject.get("urlEncoder")?.asBoolean ?: false
        val extended = jsonObject.get("extended")?.asString ?: ""

        SLog.i(TAG, "step 3, get response detail")
        SLog.i(TAG, """
            ┌──────────────── NETWORK RESPONSE ────────────────┐
            │ code       : $code                               │ 
            │ msg        : $msg                                │  
            │ state      : $state                              │  
            │ encryption : $encryption                         │ 
            │ tag        : $tag                                │
            │ base64     : $base64                             │
            │ urlEncoder : $urlEncoder                         │
            │ extended   : $extended                           │ 
            └──────────────────────────────────────────────────┘
        """.trimIndent())
    }
}
