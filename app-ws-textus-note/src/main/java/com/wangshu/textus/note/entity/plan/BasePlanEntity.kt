package com.wangshu.textus.note.entity.plan

import android.graphics.Color
import com.wangshu.note.app.entity.appenums.note.PlanPriority
import com.wangshu.note.app.entity.appenums.note.PlanState
import com.wangshu.note.app.entity.appenums.note.PlanType
import com.wangshu.textus.note.R
import com.wsvita.core.common.BaseEntity
import com.wsvita.core.ext.CoreExt.weekText
import ext.TimeExt.currentTime
import ext.TimeExt.format
import java.io.Serializable
import java.util.*


/**
 * 计划实体类
 * 支持时间计划、目标计划和代办计划
 */
abstract class BasePlanEntity : BaseEntity(),Serializable, IPlan {

    /** 用户ID */
    var userId: Long = 0L

    /** 计划标题 */
    var title: String = ""

    /** 计划描述 */
    var description: String = ""

    /** 计划类型（参考 PlanType 枚举） */
    var planType: Int = 0

    /** 开始日期（时间计划的起始日期/目标或代办计划的创建日期） */
    var startDate: Long = 0;

    /** 截止日期（时间计划结束日期，可为空表示无限期；目标或代办计划截止日期） */
    var endDate: Long = 0;

    /** 提醒类型：1-精确到日期，2-时分秒提醒 */
    var remindType: Int = 0

    /** 提醒日期（仅时间计划使用） */
    var remindDate: Long = 0;

    /** 提醒时间 (HH:mm:ss，仅时间计划使用) */
    var remindTime: String = ""

    /** 重复类型（仅时间计划使用）：0-无重复，1-每日，2-每周，3-每月，4-每年 */
    var repeatType: Int = 0

    /** 重复值（仅时间计划使用）例如每周哪几天：1,3,5 或每月哪几号：15 */
    var repeatValue: String = ""

    /** 优先级：1-紧急，2-重要,3-一般,4-可忽略，默认为3 */
    var priority: Int = 3

    /** 当前完成度（百分比，0~100） */
    var progress: Double = 0.0

    /**
     * 周期长度，例如：1、2、半
     */
    var cycleValue: String? = null

    /**
     * 周期单位，例如：天、周、月、年
     */
    var cycleUnit: String? = null

    /**
     * 频率次数，例如：3、若干、多
     */
    var frequencyValue: String? = null

    /**
     * 频率单位，例如：次
     */
    var frequencyUnit: String? = null

    /** 创建时间 */
    var createTime: Long = 0

    /** 更新时间 */
    var updateTime: Long = 0;

    val cycleText : String
        get() {
            val cv = cycleValue?.toString()?:"";
            val cu = cycleUnit?.toString()?:"";
            val fv = frequencyValue?.toString()?:"";
            val fu = frequencyUnit?.toString()?:"";
            return getString(R.string.plan_goal_cycle_format,cv,cu,fv,fu);
        }

    val remainingTimeText : String
        get() {
            val infiniteText = getString(R.string.time_infinite)
            val expired = getString(R.string.time_expired)

            if (endDate <= 0) return infiniteText

            val now = System.currentTimeMillis()
            var diff = endDate - now
            if (diff <= 0) return expired

            val year = getString(com.wsvita.ui.R.string.app_year);
            val month = getString(com.wsvita.ui.R.string.app_month);
            val day = getString(com.wsvita.ui.R.string.app_day);
            val hour = getString(com.wsvita.ui.R.string.app_hour);
            val minute = getString(com.wsvita.ui.R.string.app_minute);
            val second = getString(com.wsvita.ui.R.string.app_second);

            val secondMs = 1000L
            val minuteMs = 60 * secondMs
            val hourMs = 60 * minuteMs
            val dayMs = 24 * hourMs
            val monthMs = 30 * dayMs
            val yearMs = 365 * dayMs

            return when {
                diff >= yearMs -> {
                    val years = diff / yearMs
                    "$years$year"
                }
                diff >= monthMs -> {
                    val months = diff / monthMs
                    val days = (diff % monthMs) / dayMs
                    "${months}$month${days}$day"
                }
                diff >= dayMs -> {
                    val days = diff / dayMs
                    "${days}$day"
                }
                diff >= hourMs -> {
                    val hours = diff / hourMs
                    val minutes = (diff % hourMs) / minuteMs
                    "${hours}$hour${minutes}$minute"
                }
                diff >= minuteMs -> {
                    val minutes = diff / minuteMs
                    val seconds = (diff % minuteMs) / secondMs
                    "${minutes}$minute${seconds}$second"
                }
                else -> {
                    val seconds = diff / secondMs
                    "${seconds}$second"
                }
            }
        }

    val targetTimeText : String
        get() {
            return getString(R.string.plan_target_time_format,showEndTimeText())
        }

    val planTypeText : String
        get() {
            val type = PlanType.fromType(planType);
            return when (type) {
                PlanType.ONCE -> getString(R.string.plan_category_name_once)
                PlanType.TIME_DAILY -> getString(R.string.plan_category_name_daily)
                PlanType.TIME_WEEKLY -> getString(R.string.plan_category_name_week)
                PlanType.TIME_MONTHLY -> getString(R.string.plan_category_name_month)
                PlanType.TIME_QUARTER -> getString(R.string.plan_category_name_quarter)
                PlanType.TIME_YEARLY -> getString(R.string.plan_category_name_year)
                PlanType.GOAL -> getString(R.string.plan_category_name_goal)
                PlanType.TODO -> getString(R.string.plan_category_name_todo)
                else -> ""
            }
        }

    val todoTimeText : String
    get() {
        val format =  appDateFormat();
        val time = endDate.format(format);
        return getString(R.string.plan_todo_time_format,time);
    }

    /** 获取计划标题 */
    override fun showTitle(): String {
        return title;
    }

    /** 获取计划描述 */
    override fun showDescription(): String {
        return description;
    }

    /** 获取计划类型 */
    override fun getPlanType(): PlanType {
        return PlanType.fromType(planType);
    }

    /** 获取计划状态 */
    override fun getPlanState(): PlanState {
        return PlanState.fromState(state);
    }

    override fun categoryName(): String? {
        val p = getPlanType();
        return when(p){
            PlanType.ONCE-> getString(R.string.plan_category_name_once)
            PlanType.TIME_DAILY -> getString(R.string.plan_category_name_daily)
            PlanType.TIME_WEEKLY-> getString(R.string.plan_category_name_week)
            PlanType.TIME_MONTHLY-> getString(R.string.plan_category_name_month)
            PlanType.TIME_YEARLY-> getString(R.string.plan_category_name_year)
            PlanType.TIME_QUARTER-> getString(R.string.plan_category_name_quarter)
            PlanType.GOAL-> getString(R.string.plan_category_name_goal)
            PlanType.TODO-> getString(R.string.plan_category_name_todo)
            else-> unknowText();
        }
    }

    override fun categoryIconRes(): Int {
        val p = getPlanType();
        return when(p){
            PlanType.ONCE-> R.drawable.ic_plan_category_single;
            PlanType.TIME_DAILY,
            PlanType.TIME_WEEKLY,
            PlanType.TIME_MONTHLY,
            PlanType.TIME_YEARLY,
            PlanType.TIME_QUARTER->R.drawable.ic_plan_category_time;
            PlanType.GOAL-> R.drawable.ic_plan_category_todu;
            PlanType.TODO-> R.drawable.ic_plan_category_agent;
            else-> com.wsvita.ui.R.drawable.ui_list_item_no_data_default;
        }
    }

    /**
     * 显示计划创建时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划创建时间的文本说明
     */
    override fun showCreateTimeText(): String? {
        val format = appDateFormat();
        if(createTime > 0){
            return createTime.format(format);
        }else{
            return currentTime(format);
        }
    }

    /**
     * 显示计划更新时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划更新时间的文本说明
     */
    override fun showUpdateTimeText(): String? {
        val format = appDateFormat();
        if(updateTime > 0){
            return updateTime.format(format);
        }else{
            return currentTime(format);
        }
    }

    /**
     * 显示计划开始时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划开始时间的文本说明
     */
    override fun showStartTimeText(): String? {
        val format = appDateFormat();
        if(startDate > 0){
            return startDate.format(format);
        }else{
            return currentTime(format);
        }
    }

    /**
     * 显示计划结束时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划结束时间的文本说明
     */
    override fun showEndTimeText(): String? {
        val format = appDateFormat();
        if(endDate > 0){
            return endDate.format(format);
        }else{
            return currentTime(format);
        }
    }

    /**
     * 显示提醒日期的文本说明（仅时间计划使用）
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      提醒日期的文本说明
     */
    override fun showRemindDateText(): String? {
        val format = appDateFormat();
        if(remindDate > 0){
            return remindDate.format(format);
        }else{
            return currentTime(format);
        }
    }

    /**
     * 显示提醒时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划提醒时间的文本说明
     */
    override fun showRemindTimeText(): String? {
        return remindTime;
    }

    override fun redminTimeText(): String? {
        //目前没有设计到时间hh:mm:ss提醒，只有日期重复提醒，以后根据情况做调整
        //return showRemindDateText();
        val pType = PlanType.fromType(planType);
        return when(pType){
            PlanType.TIME_DAILY->{
                getString(R.string.plan_redmin_time_format_daily,remindTime)
            }
            PlanType.TIME_WEEKLY->{
                try {
                    return remindTime.toInt().weekText();
                }catch (e : Exception){
                    unknowText();
                }
            }
            PlanType.TIME_MONTHLY->{
                getString(R.string.plan_redmin_time_format_month,remindTime)
            }
            PlanType.TIME_YEARLY,
            PlanType.TIME_QUARTER->{
                showRemindDateText();
            }
            else-> showRemindDateText();
        }
    }

    override fun getPriority(): PlanPriority {
        return PlanPriority.fromLevel(priority);
    }

    override fun priorityIconRes(): Int {
        val p = getPriority();
        return when(p){
            PlanPriority.URGENT-> R.drawable.ic_plan_priority_1;
            PlanPriority.IMPORTANT-> R.drawable.ic_plan_priority_2;
            PlanPriority.NORMAL-> R.drawable.ic_plan_priority_3;
            PlanPriority.IGNORABLE-> R.drawable.ic_plan_priority_4;
            else-> R.drawable.ic_plan_priority_3;
        }
    }

    override fun priorityText(): String {
        val p = getPriority();
        return when(p){
            PlanPriority.URGENT-> getString(R.string.plan_priority_1)
            PlanPriority.IMPORTANT-> getString(R.string.plan_priority_2)
            PlanPriority.NORMAL-> getString(R.string.plan_priority_3)
            PlanPriority.IGNORABLE-> getString(R.string.plan_priority_4);
            else-> getString(R.string.plan_priority_3)
        }
    }

    override fun showStateText(): String? {
        val state = getPlanState();
        return when(state){
            PlanState.NOT_STARTED-> getString(R.string.plan_state_1);
            PlanState.IN_PROGRESS-> getString(R.string.plan_state_2);
            PlanState.COMPLETED-> getString(R.string.plan_state_3);
            PlanState.DELAYED-> getString(R.string.plan_state_4);
            PlanState.CANCELED-> getString(R.string.plan_state_5);
            else-> getString(R.string.plan_state_1);
        }
    }

    override fun stateIconRes(): Int {
        val state = getPlanState();
        return when(state){
            PlanState.NOT_STARTED-> R.drawable.ic_plan_state_1;
            PlanState.IN_PROGRESS-> R.drawable.ic_plan_state_2;
            PlanState.COMPLETED-> R.drawable.ic_plan_state_3;
            PlanState.DELAYED-> R.drawable.ic_plan_state_4;
            PlanState.CANCELED-> R.drawable.ic_plan_state_5;
            else-> R.drawable.ic_plan_state_1;
        }
    }

    override fun stateTextColor(): Int {
        val state = getPlanState();
        return when(state){
            PlanState.NOT_STARTED,
            PlanState.IN_PROGRESS-> Color.parseColor("#4CAF50")
            PlanState.COMPLETED-> Color.parseColor("#FF7826")
            PlanState.DELAYED-> Color.parseColor("#F44336")
            PlanState.CANCELED-> Color.parseColor("#707070")
            else-> Color.parseColor("#4CAF50")
        }
    }

    /**
     * 计划的百分比，这个百分比将用来作为[android.widget.ProgressBar]的百分比
     * 例如: 69%，则值应该为69
     *
     * create by Administrator at 2025/9/7 21:08
     * @author Administrator
     * @return
     *      计划的百分比
     */
    override fun planProgress(): Int {
        return progress.toInt().coerceIn(0, 100)
    }

    override fun showProgressText(): String {
        return "${planProgress()}%";
    }

    override fun isRedminTimeOut(): Boolean {
        val pType = PlanType.fromType(planType);
        return when(pType){
            PlanType.TIME_DAILY->{
                if (remindTime.isNullOrBlank()) {
                    false
                } else {
                    try {
                        val parts = remindTime!!.split(":")
                        if (parts.size != 3) return false
                        val hour = parts[0].toInt()
                        val minute = parts[1].toInt()
                        val second = parts[2].toInt()

                        val now = Calendar.getInstance()
                        val nowSeconds = now.get(Calendar.HOUR_OF_DAY) * 3600 +
                                now.get(Calendar.MINUTE) * 60 +
                                now.get(Calendar.SECOND)

                        val targetSeconds = hour * 3600 + minute * 60 + second
                        targetSeconds < nowSeconds
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            PlanType.TIME_WEEKLY->{
                // 每周计划 -> 数字 1~7（周一~周日）
                if (remindTime.isNullOrBlank()) {
                    false
                } else {
                    try {
                        val weekDay = remindTime.toInt() // 1~7
                        val now = Calendar.getInstance()
                        val nowWeekDay = now.get(Calendar.DAY_OF_WEEK)
                        // Calendar.DAY_OF_WEEK: 1=周日, 2=周一, ..., 7=周六
                        val adjustedNowWeekDay = if (nowWeekDay == Calendar.SUNDAY) 7 else nowWeekDay - 1
                        weekDay < adjustedNowWeekDay
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            PlanType.TIME_MONTHLY->{
                // 每月计划 -> 数字 1~31（几号）
                if (remindTime.isNullOrBlank()) {
                    false
                } else {
                    try {
                        val dayOfMonth = remindTime!!.toInt()
                        val now = Calendar.getInstance()
                        val nowDay = now.get(Calendar.DAY_OF_MONTH)
                        dayOfMonth < nowDay
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            PlanType.TIME_QUARTER,
            PlanType.TIME_YEARLY->{
                // 季度/年度计划 -> 日期
                if (remindDate <= 0) {
                    false
                } else {
                    val nowTime = System.currentTimeMillis()
                    remindDate < nowTime
                }
            }
            else-> true;
        }
    }

    override fun redminTimeLeftText(): String {
        val pType = PlanType.fromType(planType);
        when(pType){
            PlanType.TIME_DAILY->{
                if (remindTime.isNullOrBlank()) {
                    return unknowText();
                }
                try {
                    // 解析 HH:mm:ss
                    val parts = remindTime!!.split(":")
                    if (parts.size != 3) return unknowText()
                    val targetHour = parts[0].toInt()
                    val targetMinute = parts[1].toInt()
                    val targetSecond = parts[2].toInt()

                    val now = Calendar.getInstance()
                    val nowSeconds = now.get(Calendar.HOUR_OF_DAY) * 3600L +
                            now.get(Calendar.MINUTE) * 60L +
                            now.get(Calendar.SECOND)
                    val targetSeconds = targetHour * 3600L + targetMinute * 60L + targetSecond
                    val diff : Long = targetSeconds - nowSeconds

                    return if(isRedminTimeOut()){
                        disposeTimeOutDaily(diff);
                    }else{
                        disposeTimeLeftDaily(diff)
                    }
                }catch (e : Exception){
                    return unknowText();
                }
            }
            PlanType.TIME_WEEKLY,
            PlanType.TIME_MONTHLY->{
                //周和极度，计算差的天数即可
                if (remindTime.isNullOrBlank() && remindDate <= 0) {
                    return unknowText()
                }
                val diffDays: Int = try {
                    val now = Calendar.getInstance()

                    if (planType == PlanType.TIME_WEEKLY.type) {
                        // 每周计划：remindTime 1~7 表示周一到周日
                        val targetWeekDay = remindTime!!.toInt()
                        val nowWeekDay = now.get(Calendar.DAY_OF_WEEK).let { if (it == Calendar.SUNDAY) 7 else it - 1 }
                        targetWeekDay - nowWeekDay
                    } else {
                        // 每月计划：remindTime 1~31 表示每月几号
                        val targetDay = remindTime!!.toInt()
                        val nowDay = now.get(Calendar.DAY_OF_MONTH)
                        targetDay - nowDay
                    }
                } catch (e: Exception) {
                    return unknowText()
                }
                return if(isRedminTimeOut()){
                    getString(R.string.plan_redmin_time_out_day_format,diffDays.toString())
                }else{
                    getString(R.string.plan_redmin_time_left_day_format,diffDays.toString())
                }
            }
            PlanType.TIME_QUARTER,
            PlanType.TIME_YEARLY->{
                if (remindDate <= 0) {
                    return unknowText()
                }

                val diffDays: Long = try {
                    val nowTime = System.currentTimeMillis()
                    val diffMillis = remindDate - nowTime
                    diffMillis / (1000 * 60 * 60 * 24) // 毫秒转换为天
                } catch (e: Exception) {
                    return unknowText()
                }
                return if(isRedminTimeOut()){
                    getString(R.string.plan_redmin_time_out_day_format,diffDays.toString())
                }else{
                    getString(R.string.plan_redmin_time_left_day_format,diffDays.toString())
                }
            }
            else-> return unknowText();
        }
    }

    override fun isEndTimeOut(): Boolean {
        // 如果没有设置 endDate，则认为没有超时
        val end = endDate ?: return false
        return System.currentTimeMillis() > end
    }

    override fun endTimeLeftText(): String {
        if (endDate == null) return unknowText()

        val now = System.currentTimeMillis()
        val diffSeconds = (endDate - now) / 1000
        return if (diffSeconds >= 0) {
            // 还没到截止时间
            disposeTimeLeftDaily(diffSeconds)
        } else {
            // 已超时
            disposeTimeOutDaily(-diffSeconds)
        }
    }

    private fun disposeTimeLeftDaily(diff: Long): String {
        val stateText = getString(R.string.time_state_left_format)
        val dayText = getString(com.wsvita.ui.R.string.app_day)
        val hourText = getString(com.wsvita.ui.R.string.app_hour)
        val minuteText = getString(com.wsvita.ui.R.string.app_minute)
        val secondText = getString(com.wsvita.ui.R.string.app_second)

        val days = diff / (3600 * 24)
        val hours = (diff % (3600 * 24)) / 3600
        val minutes = (diff % 3600) / 60
        val seconds = diff % 60

        val sb = StringBuilder()
        if (days > 0) sb.append(days).append(dayText)
        if (hours > 0) sb.append(hours).append(hourText)
        if (minutes > 0) sb.append(minutes).append(minuteText)
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append(secondText)

        return stateText.format(sb.toString())
    }

    private fun disposeTimeOutDaily(diff: Long): String {
        val stateText = getString(R.string.time_state_out_format)
        val dayText = getString(com.wsvita.ui.R.string.app_day)
        val hourText = getString(com.wsvita.ui.R.string.app_hour)
        val minuteText = getString(com.wsvita.ui.R.string.app_minute)
        val secondText = getString(com.wsvita.ui.R.string.app_second)

        val days = diff / (3600 * 24)
        val hours = (diff % (3600 * 24)) / 3600
        val minutes = (diff % 3600) / 60
        val seconds = diff % 60

        val sb = StringBuilder()
        if (days > 0) sb.append(days).append(dayText)
        if (hours > 0) sb.append(hours).append(hourText)
        if (minutes > 0) sb.append(minutes).append(minuteText)
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append(secondText)

        return stateText.format(sb.toString())
    }


    companion object {
        private const val serialVersionUID = -7128910241814930020L
    }
}
