package ext

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * 默认时间格式化值，yyyy-MM-dd HH:mm:ss
 */
const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"

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

    /**
     * 获取当前时间，并对时间进行格式化处理
     * create by Eastevil at 2022/9/14 10:19
     * @author Eastevil
     * @param sdf
     *      指定的格式
     * @return
     *      格式化后的当前时间
     */
    fun currentTime(sdf: String) : String{
        var sdf = SimpleDateFormat(sdf);
        return sdf.format(Date());
    }

    fun currentDate(): Date {
        return Date();
    }

    fun currentYear(): Int {
        val c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     * create by Eastevil at 2022/9/19 11:02
     * @author Eastevil
     * @return
     *      当前月份，该值为[java.util.Calendar]对象获取月份值加1，即：1-12
     */
    fun currentChineseMonth(): Int {
        val c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月份
     * create by Eastevil at 2022/9/19 11:01
     * @author Eastevil
     * @return
     *     前月份
     */
    fun currentMonth():Int{
        val c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    fun Calendar.getYear(): Int {
        return this.get(Calendar.YEAR);
    }

    fun Calendar.getMonth(): Int {
        return this.get(Calendar.MONTH);
    }

    fun Calendar.getDay(): Int {
        return this.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时间对应的小时值，24小时制
     * create by Eastevil at 2022/9/19 11:04
     * @author Eastevil
     * @return
     *      当前时间对应的小时值，24小时制
     */
    fun Calendar.getHour(): Int {
        return this.get(Calendar.HOUR_OF_DAY);
    }

    fun Calendar.getMinute(): Int {
        return this.get(Calendar.MINUTE);
    }

    fun Calendar.getSecond(): Int {
        return this.get(Calendar.SECOND);
    }

    /**
     * 获取当前月份，如果小于10，则在前位补0
     * create by Eastevil at 2022/9/20 10:30
     * @author Eastevil
     * @param
     * @return
     */
    fun Calendar.getMonthText(): String {
        return this.get(Calendar.MONTH).lessTenText();
    }

    /**
     * 获取当前日期，如果小于10，则在前位补0
     */
    fun Calendar.getDayText(): String {
        return this.get(Calendar.DAY_OF_MONTH).lessTenText();
    }

    /**
     * 获取当前小时，如果小于10，则在前位补0
     * create by Eastevil at 2022/9/20 10:30
     * @author Eastevil
     * @param
     * @return
     */
    fun Calendar.getHourText(): String {
        return this.get(Calendar.HOUR_OF_DAY).lessTenText();
    }

    /**
     * 获取当前分钟，如果小于10，则在前位补0
     */
    fun Calendar.getMinuteText(): String {
        return this.get(Calendar.MINUTE).lessTenText();
    }

    /**
     * 获取当前妙，如果小于10，则在前位补0
     */
    fun Calendar.getSecondText(): String {
        return this.get(Calendar.SECOND).lessTenText();
    }

    fun Calendar.getWeek(): Int {
        return this.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据[Calendar]对象，获取对应得最小天数值
     * create by Eastevil at 2022/9/20 10:44
     * @author Eastevil
     * @return
     *      最小天数值
     */
    fun Calendar.getMinDay(): Int {
        return this.getActualMinimum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据[Calendar]对象，获取对应得最大天数值
     * create by Eastevil at 2022/9/20 10:45
     * @author Eastevil
     * @return
     *      最大天数值
     */
    fun Calendar.getMaxDay() : Int{
        return this.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据[Calendar]对象，获取对应的月份第一天是星期几，返回的值范围为1-7
     * create by Eastevil at 2022/9/20 10:51
     * @author Eastevil
     * @return
     *      月份第一天是星期几，对应返回值已减1
     */
    fun Calendar.monthFirstDayWeek(): Int {
        val minDay = this.getMinDay();
        this.set(Calendar.DAY_OF_MONTH, minDay);
        val w = this.get(Calendar.DAY_OF_WEEK);
        if(w == 1){
            return 7;
        }else{
            return w-1;
        }
    }

    /**
     * 根据[Calendar]对象，获取对应的月份最后一天是星期几,返回的值范围为1-7
     * create by Eastevil at 2022/9/20 10:53
     * @author Eastevil
     * @return
     *      月份最后一天是星期几,对应返回值已减1
     */
    fun Calendar.monthLastDayWeek(): Int {
        val maxDay = this.getMaxDay();
        this.set(Calendar.DAY_OF_MONTH, maxDay);
        val w = this.get(Calendar.DAY_OF_WEEK);
        if(w == 1){
            return 7;
        }else{
            return w-1;
        }
    }

    /**
     * 日期天数增加指定值
     * create by Eastevil at 2022/9/20 10:55
     * @author Eastevil
     * @param value
     *      需要增加的值
     * @return
     *      增加后的[Calendar]对象
     */
    fun Calendar.addDay(value: Int): Calendar {
        this.add(Calendar.DAY_OF_MONTH, value);
        val c = this;
        return c;
    }

    /**
     * 月份增加指定值
     * create by Eastevil at 2022/9/20 10:56
     * @author Eastevil
     * @param value
     *      需要增加的值
     * @return
     *      增加后的[Calendar]对象
     */
    fun Calendar.addMonth(value: Int): Calendar {
        this.add(Calendar.MONTH, value);
        val c = this;
        return c;
    }

    fun Calendar.beforNow(): Boolean {
        val cc = Calendar.getInstance();
        cc.time = Date(System.currentTimeMillis());
        return this.before(cc);
    }

    fun Calendar.afterNow(): Boolean {
        val cc = Calendar.getInstance();
        cc.time = Date(System.currentTimeMillis());
        return this.after(cc);
    }

    /**
     * 格式化时间
     * create by Eastevil at 2022/9/19 11:09
     * @author Eastevil
     * @return
     *      按默认时间格式格式化后的时间字符串
     */
    fun Long.format(): String {
        val sdf = SimpleDateFormat(DEFAULT_FORMAT);
        val date = Date(this);
        return sdf.format(date)
    }

    /**
     * 传入的参数小于10，则在前面补0
     * create by Eastevil at 2022/9/20 10:27
     * @author Eastevil
     * @param num
     *      值
     * @return
     *      如果小于10，则前位补0，否则为传入值
     */
    private fun Int.lessTenText(): String {
        return if(this < 10) "0${this}" else this.toString();
    }

    /**
     * 将Long值的毫秒数值转换为时：分：秒
     * create by Eastevil at 2022/11/2 17:31
     * @author Eastevil
     * @param hourUnit
     *      小时单位
     * @param minuteUnit
     *      分钟单位
     * @param secondUnit
     *      秒单位
     * @param connUnit
     *      连接单位
     * @return
     *      转换后的时间
     */
    fun Long.durationTime(hourUnit : String, minuteUnit : String,
                          secondUnit : String,connUnit : String): String {
        val ss = 1000L
        val mi = ss * 60L
        val hh = mi * 60L

        val hour = this / hh;
        val minute = (this - hour * hh)/mi;
        val second = (this - hour * hh - minute * mi)/ss;
        val sb = StringBuilder();
        if(hour < 10){
            sb.append("0").append(hour);
        }else{
            sb.append(hour);
        }
        sb.append(hourUnit);
        sb.append(connUnit);
        if(minute < 10){
            sb.append("0").append(minute);
        }else{
            sb.append(minute);
        }
        sb.append(minuteUnit);
        sb.append(connUnit);
        if(second < 10){
            sb.append("0").append(second);
        }else{
            sb.append(second);
        }
        return sb.toString();
    }

    /**
     * 格式化时间戳
     * create by Eastevil at 2022/11/4 17:44
     * @author Eastevil
     * @param sdf
     *      格式
     * @return
     *      指定格式时间文本
     */
    fun Long.formatTime(sdf:String) : String{
        var sdf = SimpleDateFormat(sdf);
        return sdf.format(Date(this));
    }

    /**
     * 将毫秒数转换为“分钟:秒”的格式字符串。
     *
     * 例如，传入 125000 将返回 "02:05"。
     *
     * @author Eastevil
     * @createTime 2025/5/29 14:08
     * @return 格式化后的时间字符串，格式为 mm:ss
     * @see String.format
     * @since 1.0
     */
    fun Int.toMinuteSecond(): String {
        val totalSeconds = this / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * 将毫秒数转换为“分钟:秒”的格式字符串。
     *
     * 例如，传入 125000 将返回 "02:05"。
     *
     * @author Eastevil
     * @createTime 2025/5/29 14:08
     * @return 格式化后的时间字符串，格式为 mm:ss
     * @see String.format
     * @since 1.0
     */
    fun Long.toMinuteSecond(): String {
        val totalSeconds = this / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * 根据指定时间获取对应周一凌晨的时间
     * create by Eastevil at 2023/6/9 15:16
     * @author Eastevil
     * @param date
     *      [Date]
     * @return
     *      周一的时间
     */
    fun Date.mondayOfGivenWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this

        // 获取当前日期是星期几
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 计算星期一偏移量
        val mondayOffset = if (dayOfWeek == Calendar.SUNDAY) {
            -6 // 当前日期是星期日，偏移量为-6天
        } else {
            Calendar.MONDAY - dayOfWeek // 计算与星期一的偏移量
        }

        // 获取星期一的日期
        calendar.add(Calendar.DATE, mondayOffset)

        // 重置时、分、秒、毫秒为零
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

    /**
     * 根据传入时间获取对应星期的星期日最后时间，即23：59：59
     * create by Eastevil at 2023/6/9 15:17
     * @author Eastevil
     * @param date
     *      [Date]
     * @return
     *      星期的星期日最后时间
     */
    fun Date.sundayOfGivenWeek(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this

        // 获取当前日期是星期几
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // 计算星期日偏移量
        val sundayOffset = Calendar.SATURDAY - dayOfWeek + 1

        // 获取星期日的日期
        calendar.add(Calendar.DATE, sundayOffset)

        // 设置时、分、秒为最后时间
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)

        return calendar.time
    }

    fun Date.format(): String {
        val s = SimpleDateFormat(DEFAULT_FORMAT);
        return s.format(this);
    }

    fun Date.format(format: String): String {
        val s = SimpleDateFormat(format);
        return s.format(this);
    }


    fun Date.get(field : Int): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(field);
    }

    fun Long.toDate(): Date {
        val d = Date();
        d.time = this;
        return d;
    }

    fun Date.year(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.YEAR);
    }

    fun Date.month(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.MONTH);
    }

    fun Date.day(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.DAY_OF_MONTH);
    }

    fun Date.hour(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.HOUR_OF_DAY);
    }

    fun Date.minute(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.MINUTE);
    }

    fun Date.second(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.SECOND);
    }

    fun Date.millisecond(): Int {
        val c = Calendar.getInstance();
        c.time = this;
        return c.get(Calendar.MILLISECOND);
    }

    /**
     * 设置时间戳类型时间
     * create by Administrator at 2024/7/27 16:54
     * @author Administrator
     * @param field
     *      必须是[Calendar]对应的field值,例:Calendar.MONTH
     * @param value
     *      设置的具体值
     * @return
     *      修改后的日期
     */
    fun Long.setTime(field: Int,value : Int): Date {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        c.set(field,value);
        return c.time;
    }

    fun Long.year(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.YEAR);
    }

    fun Long.month(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.MONTH);
    }

    fun Long.monthAdd1(): Int {
        return this.month()+1;
    }

    fun Long.day(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    fun Long.week(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 从时间戳中获取12小时制的小时
     * create by Administrator at 2024/7/27 16:36
     * @author Administrator
     * @param
     * @return
     *      12小时制的小时
     */
    fun Long.hour(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.HOUR);
    }

    /**
     * 从时间戳中获取24小时制的小时
     * create by Administrator at 2024/7/27 16:36
     * @author Administrator
     * @param
     * @return
     *      24小时制的小时
     */
    fun Long.hourOfDay(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    fun Long.minute(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.MINUTE);
    }

    fun Long.second(): Int {
        val c = Calendar.getInstance();
        c.time = this.toDate();
        return c.get(Calendar.SECOND);
    }

    /**
     * 获取一个月的第一天是星期几
     * create by Administrator at 2024/7/26 21:02
     * @author Administrator
     * @param year
     *      年份
     * @param month
     *      月份
     * @return
     *      星期对应的int值。1-星期一，2-星期二，3-星期三，4-星期四，5-星期五，6-星期六，7-星期日
     */
    fun getFirstDayOfWeekChinse(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        // Calendar的月份是0-11，因此需要减1
        calendar.set(year, month, 1)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // 将Calendar的周日-周六（1-7）映射为我们需要的周一-周日（1-7）
        return if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
    }

    /**
     * 获取一个月最大天数
     * create by Eastevil at 2024/4/29 11:44
     * @author Eastevil
     * @param
     * @return
     */
    fun monthMaxDay(year : Int,month : Int): Int {
        val c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        val day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获取一个月最大天数。该方法传入的月份为中国记录用的月份，既1-12月，处理中会将月份减1进行计算
     * create by Eastevil at 2024/4/29 11:43
     * @author Eastevil
     * @param
     * @return
     */
    fun monthChinseMaxDay(year : Int,month : Int): Int {
        val c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month-1);
        val day = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获取今天的开始时间（当天 00:00:00.000）
     * create by Eastevil at 2024/9/5 11:20
     * @author Eastevil
     * @param
     * @return Date 当天开始时间
     */
    fun startOfToday(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    /**
     * 获取今天的结束时间（当天 23:59:59.999）
     * create by Eastevil at 2024/9/5 11:20
     * @author Eastevil
     * @param
     * @return Date 当天结束时间
     */
    fun endOfToday(): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }
    /**
     * 获取当前日期所在月份的第一天
     *
     * 时间部分会被重置为当天的开始（即 00:00:00.000）
     *
     * @receiver Date 当前日期
     * @return Date 本月第一天的日期时间
     */
    fun currentMonthStart(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date();
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * 获取当前日期所在月份的最后一天
     *
     * 时间部分会被设置为当天的结束（即 23:59:59.999）
     *
     * @receiver Date 当前日期
     * @return Date 本月最后一天的日期时间
     */
    fun currentMonthEnd(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }


    /**
     * 获取当前季度的第一天时间（季度第一天 00:00:00.000）
     * create by Eastevil at 2025/9/25 13:40
     * @author Eastevil
     * @param
     * @return Date 当前季度第一天时间
     */
    fun getCurrentQuarterStart(): Date {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) // 0=一月
        val startMonth = (month / 3) * 3
        cal.set(Calendar.MONTH, startMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    /**
     * 获取当前季度的最后一天时间（季度最后一天 23:59:59.999）
     * create by Eastevil at 2025/9/25 13:40
     * @author Eastevil
     * @param
     * @return Date 当前季度最后一天时间
     */
    fun getCurrentQuarterEnd(): Date {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val endMonth = (month / 3) * 3 + 2
        cal.set(Calendar.MONTH, endMonth)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    /**
     * 获取指定年份的第一天时间（01-01 00:00:00.000）
     * @param year 指定年份
     * @return Date 年份第一天时间
     */
    fun getYearStart(year: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, Calendar.JANUARY) // 一月
        cal.set(Calendar.DAY_OF_MONTH, 1) // 1号
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    /**
     * 获取指定年份的最后一天时间（12-31 23:59:59.999）
     * @param year 指定年份
     * @return Date 年份最后一天时间
     */
    fun getYearEnd(year: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, Calendar.DECEMBER) // 十二月
        cal.set(Calendar.DAY_OF_MONTH, 31) // 31号
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

}
