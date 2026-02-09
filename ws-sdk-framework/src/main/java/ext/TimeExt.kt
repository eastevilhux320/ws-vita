package ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 时间相关扩展工具类
 *
 * 在组件化架构中，建议通过此类统一管理时间获取逻辑，
 * 便于后续进行单元测试（Mock）或统一调整时间精度。
 */
object TimeExt {

    /**
     * 获取当前系统毫秒时间戳
     * @see System.currentTimeMillis
     *
     * create by Eastevil at 2025/12/22 13:13
     * @author Eastevil
     * @return
     *      当前时间与协调世界时（UTC）1970年1月1日午夜之间的时间差（以毫秒为单位）
     */
    fun systemTime(): Long {
        return System.currentTimeMillis();
    }

    /**
     * 将毫秒时间戳格式化为指定的日期字符串
     *
     * create by Eastevil at 2026/01/28 13:30
     * @author Eastevil
     * @param pattern 日期格式模式，如 "yyyy-MM-dd HH:mm:ss"
     * @return
     * 格式化后的时间字符串，若格式化异常则返回空字符串
     */
    fun Long.format(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this))
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 将符合特定格式的时间字符串转换为毫秒时间戳
     *
     * create by Eastevil at 2026/01/28 13:30
     * @author Eastevil
     * @param pattern 字符串对应的日期模式，必须与字符串格式一致
     * @return
     * 转换后的毫秒时间戳，若解析失败或字符串为空则返回 0L
     */
    fun String.toTimestamp(pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
        if (this.isBlank()) return 0L
        return try {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(this)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 判断当前毫秒值与另一个时间戳是否属于同一天
     *
     * create by Eastevil at 2026/01/28 13:30
     * @author Eastevil
     * @param otherMillis 待对比的毫秒时间戳
     * @return
     * true 表示两个时间戳在系统默认时区下属于同一天
     */
    fun Long.isSameDay(otherMillis: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = this@isSameDay }
        val cal2 = Calendar.getInstance().apply { timeInMillis = otherMillis }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 将时间毫秒值转换为倒计时/时长格式 (mm:ss 或 HH:mm:ss)
     *
     * create by Eastevil at 2026/01/28 13:30
     * @author Eastevil
     * @return
     * 返回格式化后的时长字符串，小时位大于0时自动显示 HH:mm:ss
     */
    fun Long.toDuration(): String {
        val totalSeconds = this / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return if (h > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", m, s)
        }
    }
}
