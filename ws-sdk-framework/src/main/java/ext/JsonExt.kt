package ext

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 基于 Gson 的 JSON 扩展工具类
 * * 针对组件化开发优化，支持 Java 8 时间类 (LocalDate, LocalDateTime, LocalTime) 的序列化与反序列化。
 * 提供两种模式：时间戳模式（Timestamp）和 标准字符串模式（ISO Format）。
 * * create by Administrator at 2026/1/2 1:41
 * @author Administrator
 */
object JsonExt {

    /** 默认模式下的 Gson 实例（时间显示为字符串） */
    private val gsonNormal by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createGson(timestampMode = false)
    }

    /** 时间戳模式下的 Gson 实例（时间显示为 Long） */
    private val gsonTimestamp by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createGson(timestampMode = true)
    }



    /**
     * 将对象转换为 JSON 字符串
     * * create by Administrator at 2026/1/2 1:41
     * @author Administrator
     * @receiver Any? 源对象
     * @param useTimestamp 是否开启时间戳模式，默认 false (使用 yyyy-MM-dd 等格式)
     * @return String JSON 字符串
     */
    fun Any?.toJson(useTimestamp: Boolean = false): String {
        return try {
            val gson = if (useTimestamp) gsonTimestamp else gsonNormal
            gson.toJson(this)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun Any?.toJson(): String {
        return this.toJson(false);
    }

    /**
     * 构建配置好的 Gson 实例
     * * 包含对 Date 和 Java 8 Time API 的类型适配器注册
     * * create by Administrator at 2026/1/2 1:41
     * @author Administrator
     * @param timestampMode 是否将日期序列化为长整型时间戳
     * @return Gson
     */
    private fun createGson(timestampMode: Boolean): Gson {
        val builder = GsonBuilder()

        // 1. Date 类型处理：反序列化始终支持 Long -> Date
        builder.registerTypeAdapter(Date::class.java, JsonDeserializer { json, _, _ ->
            Date(json.asJsonPrimitive.asLong)
        })

        if (timestampMode) {
            // Date -> Long
            builder.registerTypeAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
                JsonPrimitive(src.time)
            })
            // LocalDate -> Long (当天零点)
            builder.registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
                JsonPrimitive(src.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
            })
            // LocalDateTime -> Long
            builder.registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            })
        } else {
            // 默认模式：LocalDate -> "yyyy-MM-dd"
            builder.registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE))
            })
            // LocalDateTime -> "yyyy-MM-dd'T'HH:mm:ss"
            builder.registerTypeAdapter(LocalDateTime::class.java, JsonSerializer<LocalDateTime> { src, _, _ ->
                JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            })
        }

        // LocalTime 始终保持 "HH:mm:ss"
        builder.registerTypeAdapter(LocalTime::class.java, JsonSerializer<LocalTime> { src, _, _ ->
            JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME))
        })

        // --- 反序列化配置 ---
        builder.registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
        })
        builder.registerTypeAdapter(LocalTime::class.java, JsonDeserializer { json, _, _ ->
            LocalTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_TIME)
        })
        builder.registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        })

        return builder.create()
    }
}
