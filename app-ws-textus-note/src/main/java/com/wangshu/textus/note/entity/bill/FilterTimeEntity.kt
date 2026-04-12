package com.wangshu.textus.note.entity.bill

import com.wsvita.core.common.BaseEntity
import java.util.*

class FilterTimeEntity : BaseEntity() {

    /**
     * 时间显示文本
     * 1-今日，2-近3天，3-本周内，4-本月内，5-3个月内，6-6个月内
     * 后续则根据[timeValue]加 "年" 的方式展示，例：2025年
     */
    var timeText : String? = null;

    /**
     * 具体的值，1-今日，2-最近3天，3-本周，4-本月，5-3个月内，6-6个月内
     * 后续则从上线之日开始计算，暂定为从2025开始计算，即2025+
     */
    var timeValue : Int = 0;


    /**
     * 如果[recyclerItemType]方法返回为[com.star.starlight.ui.view.commons.RecyclerItemType]中定义的自定义布局展示类型，
     * 展示的item资源布局将通过调用次方法获得
     * create by Eastevil at 2022/10/28 17:15
     * @author Eastevil
     * @param
     * @return
     */
    override fun customLayoutId(): Int {
        return 0;
    }


    /**
     * 获取筛选条件的开始时间 (Date)
     */
    fun getStartDate(): Date {
        val cal = Calendar.getInstance()
        when (timeValue) {
            // 今日
            1 -> {
                cal.setToDayStart()
            }
            // 近3天（含今天）
            2 -> {
                cal.setToDayStart()
                cal.add(Calendar.DAY_OF_MONTH, -2)
            }
            // 本周（周一开始）
            3 -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.setToDayStart()
            }
            // 本月
            4 -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.setToDayStart()
            }
            // 近3个月
            5 -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.setToDayStart()
                cal.add(Calendar.MONTH, -2)
            }
            // 近6个月
            6 -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.setToDayStart()
                cal.add(Calendar.MONTH, -5)
            }
            // 年份 (>=2025)
            else -> {
                cal.set(Calendar.YEAR, timeValue)
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.setToDayStart()
            }
        }
        return cal.time
    }

    /**
     * 获取筛选条件的结束时间 (Date)
     */
    fun getEndDate(): Date {
        val cal = Calendar.getInstance()
        when (timeValue) {
            // 今日
            1 -> {
                cal.setToDayEnd()
            }
            // 近3天（含今天）
            2 -> {
                cal.setToDayEnd()
            }
            // 本周（周日结束）
            3 -> {
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                cal.setToDayEnd()
            }
            // 本月
            4 -> {
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.setToDayEnd()
            }
            // 近3个月（到当月最后一天）
            5 -> {
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.setToDayEnd()
            }
            // 近6个月
            6 -> {
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.setToDayEnd()
            }
            // 年份
            else -> {
                cal.set(Calendar.YEAR, timeValue)
                cal.set(Calendar.MONTH, Calendar.DECEMBER)
                cal.set(Calendar.DAY_OF_MONTH, 31)
                cal.setToDayEnd()
            }
        }
        return cal.time
    }

    /**
     * 扩展方法 - 设置为当天 00:00:00.000
     */
    private fun Calendar.setToDayStart() {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 扩展方法 - 设置为当天 23:59:59.999
     */
    private fun Calendar.setToDayEnd() {
        this.set(Calendar.HOUR_OF_DAY, 23)
        this.set(Calendar.MINUTE, 59)
        this.set(Calendar.SECOND, 59)
        this.set(Calendar.MILLISECOND, 999)
    }
}
