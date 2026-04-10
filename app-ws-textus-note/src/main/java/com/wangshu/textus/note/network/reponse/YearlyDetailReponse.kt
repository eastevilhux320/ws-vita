package com.wangshu.textus.note.network.reponse

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import ext.BigDecimalExt.scale
import ext.TimeExt.format
import java.math.BigDecimal
import java.util.*

class YearlyDetailReponse {
    /**
     * 用户ID
     */
    var  userId: Long? = null

    /**
     * 年支出
     */
    var  yearExpenditure: BigDecimal = BigDecimal.ZERO

    /**
     * 年支出
     */
    var  yearIncome: BigDecimal = BigDecimal.ZERO

    /**
     * 月支出
     */
    var  monthExpenditure: BigDecimal = BigDecimal.ZERO

    /**
     * 月收入
     */
    var  monthIncome: BigDecimal = BigDecimal.ZERO

    /**
     * 本周支出
     */
    var  weekExpenditure: BigDecimal = BigDecimal.ZERO

    /**
     * 本周收入
     */
    var  weekIncome: BigDecimal = BigDecimal.ZERO

    /**
     * 今日支出
     */
    var  dayExpenditure: BigDecimal = BigDecimal.ZERO

    /**
     * 今日收入
     */
    var  dayIncome: BigDecimal = BigDecimal.ZERO

    /**
     * 年开始时间
     */
    var  yearStartTime: Long=0

    /**
     * 年度结束时间
     */
    var  yearEndTime: Long=0

    /**
     * 月开始时间
     */
    var  monthStartTime: Long=0

    /**
     * 月度结束时间
     */
    var  monthEndTime: Long=0

    /**
     * 本周开始时间
     */
    var  weekStartTime: Long=0

    /**
     * 本周结束时间
     */
    var  weekEndTime: Long=0

    /**
     * 今日开始时间
     */
    var  dayStartTime: Long=0

    /**
     * 今日结束时间
     */
    var  dayEndTime: Long=0


    val todayTimeText : String
        get() {
            return "00:00-23:59"
        }

    val monthTimeText : String
        get() {
            val sTime = monthStartTime.format(NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_datatime_format_month_day));
            val eTime = monthEndTime.format(NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_datatime_format_month_day));
            return "${sTime}-${eTime}"
        }

    val weekTimeText : String
        get() {
            val sTime = weekStartTime.format(NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_datatime_format_month_day));
            val eTime = weekEndTime.format(NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_datatime_format_month_day));
            return "${sTime}-${eTime}"
        }

    val yearTimeText : String
        get() {
            val c = Calendar.getInstance();
            c.timeInMillis = yearStartTime;
            val year = c.get(Calendar.YEAR);
            return NoteApp.app.getString(R.string.bill_detail_year_format,year.toString());
        }

    val todayIncomeText : String
        get() {
            return dayIncome.scale(2);
        }

    val todayExpenditureText : String
        get() {
            return dayExpenditure.scale(2);
        }

    val todayBalanceText : String
        get() {
            val b = dayIncome.subtract(dayExpenditure);
            return b.scale(2);
        }

    val monthIncomeText : String
        get() {
            return monthIncome.scale(2);
        }

    val homeMonthIncomeText : String
        get() {
            return NoteApp.app.getString(R.string.home_month_total_format,monthIncome.scale(2));
        }

    val homeMonthExpenditureText : String
        get() {
            return NoteApp.app.getString(R.string.home_month_total_format,monthExpenditure.scale(2));
        }
}

