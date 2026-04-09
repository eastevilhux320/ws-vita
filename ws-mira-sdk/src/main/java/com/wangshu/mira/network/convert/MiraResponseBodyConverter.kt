package com.wangshu.mira.network.convert

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.TypeAdapter
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.ext.StringExt.md5
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import ext.StringExt.isInvalid
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * 玄同 (ws-vita) 核心转换器
 * 适配 MiraResult 结构：处理解密 (AES)、验签 (Sign) 及灰度分发
 */
class MiraResponseBodyConverter<T> internal constructor(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val targetType: Type
) : Converter<ResponseBody, T> {

    companion object {
        private const val TAG = "WSV_NET_MiraConverter=>"
        private const val ERROR_DEF = -65536

        /**
         * 默认值,验签失败
         */
        private const val SIGN_ERROR = 5006;

        /**
         * 前端定义的特殊标识code
         */
        private const val SIGN_TIME_ERROR = -2;

        /**
         * 解析数据处理异常，数据为空
         */
        private const val EMPTY_DATA_ERROR = -3;

        private const val SUCCESS_CODE = 0;
    }

    override fun convert(value: ResponseBody): T? {
        // 1. 获取原始响应字符串
        val rawResponse = value.use { it.string() }
        SLog.i(TAG, "Step 1: Receive Raw Response")

        val jsonElement = gson.fromJson(rawResponse, JsonElement::class.java)
        if (!jsonElement.isJsonObject) {
            SLog.e(TAG, "Not a JsonObject, parsing directly")
            return adapter.fromJson(rawResponse)
        }

        val jsonObject = jsonElement.asJsonObject
        // 2. 提取 MiraResult 核心控制字段
        val code = jsonObject.get("code")?.asInt ?: ERROR_DEF
        var message = try {
             jsonObject.get("msg")?.asString
        }catch (e : Exception){
            "";
        }

        val time = jsonObject.get("time").let {
            if (it == null || it.isJsonNull) -1L else it.asLong
        }
        //业务code错误
        if(SUCCESS_CODE != code){
            SLog.e(TAG, "Business Error: $code, Message: $message")
            // 抛弃原始 jsonObject 的冗余字段，只保留核心三要素
            val cleanResult = JsonObject().apply {
                addProperty("code", code)
                addProperty("msg", message ?: "Unknown Error")
                addProperty("time", time)
            }
            return adapter.fromJson(cleanResult.toString())
        }
        //继续进行解析
        val appId = jsonObject.get("appId")?.asLong ?: 0L
        val merchantNo = jsonObject.get("merchantNo")?.asString ?: ""
        // 针对你说的可能为空的字段，必须处理 JsonNull，否则 asString 会崩
        val signData = jsonObject.get("signData").let {
            if (it == null || it.isJsonNull) "" else it.asString
        }

        var dataStr = jsonObject.get("data").let {
            if (it == null || it.isJsonNull) "" else it.asString
        }

        // 3. 打印玄映 (ws-mira) 业务日志
        printMiraLog(jsonObject, appId, merchantNo)
        SLog.i(TAG, "Step 2: Get params successfully")

        if(-1L == time){
            //时间错误
            jsonObject.addProperty("code", SIGN_TIME_ERROR)
            jsonObject.addProperty("message", "Sign verify failed")
            return adapter.fromJson(jsonObject.toString())
        }
        // 4. 安全校验：验签 (Sign Check)
        val signResult = signData(merchantNo,appId,time, signData);
        if(!signResult){
            //验签失败
            jsonObject.addProperty("code", SIGN_ERROR)
            jsonObject.addProperty("message", "Sign verify failed")
            return adapter.fromJson(jsonObject.toString())
        }
        SLog.i(TAG, "Step 3: Signature verified successfully")

        // 5. 数据脱壳：解密 (Decryption)
        if (dataStr.isNotEmpty() && code != ERROR_DEF) {
            SLog.i(TAG, "Step 4: Decrypting data for appId: $appId")
            try {
                //数据解密
                dataStr = NetworkClient.instance.networkDataSecurity()?.decrypt(dataStr)?:"";
                if(dataStr.isInvalid()){
                    //数据解析失败
                    jsonObject.addProperty("code", EMPTY_DATA_ERROR)
                    jsonObject.addProperty("message", "data is empty")
                    return adapter.fromJson(jsonObject.toString())
                }
                SLog.longD(TAG, "Decrypted Content: $dataStr")

                // 6. 重新装载：将解密后的 JSON 字符串还原并替换回 Data 字段
                val finalDataElement = try {
                    gson.fromJson(dataStr, JsonElement::class.java)
                } catch (e: Exception) {
                    JsonPrimitive(dataStr) // 如果不是JSON，按原样存为Primitive
                }
                jsonObject.add("data", finalDataElement)

            } catch (e: Exception) {
                SLog.e(TAG, "Decryption Error: ${e.message}")
                // 构造解密失败的返回结果
                jsonObject.addProperty("code", SIGN_ERROR)
                jsonObject.addProperty("message", "Response data decrypt error")
                jsonObject.remove("data")
                return adapter.fromJson(jsonObject.toString())
            }
        }
        // 7. 返回最终实体类，供 ViewModel 和 Adapter 驱动 UI
        SLog.i(TAG, "Step 5: Final assembly completed.")
        return try {
            adapter.fromJson(jsonObject.toString())
        } catch (e: Exception) {
            SLog.e(TAG, "Final parse error: ${e.message}")
            adapter.fromJson(rawResponse)
        }
    }

    /**
     * 适配 MiraResult 的结构化日志打印
     */
    private fun printMiraLog(jsonObject: JsonObject, appId: Long, merchantNo: String) {
        val code = jsonObject.get("code")?.asInt
        val msg = jsonObject.get("message")?.asString
        val time = jsonObject.get("time")?.asLong

        SLog.i(TAG, """
            ┌──────────────── MIRA RESPONSE (ws-vita) ──────────┐
            │ MerchantNo : $merchantNo
            │ AppId      : $appId
            │ Code       : $code
            │ Message    : $msg
            │ Time       : $time
            └──────────────────────────────────────────────────┘
        """.trimIndent())
    }

    private fun signData(merchantNo: String,appId: Long,time : Long,signData : String): Boolean {
        //同样的逻辑生成验签数据，进行比对，判断是否符合验签
        val merchantKey = MiraConfigure.instance.getConfig()?.secretKey;
        val sb = StringBuilder()
        sb.append(merchantNo)
            .append(appId)
            .append(merchantKey)
            .append(time)
        val tempSign = sb.toString().md5();
        return tempSign.equals(signData);
    }

}
