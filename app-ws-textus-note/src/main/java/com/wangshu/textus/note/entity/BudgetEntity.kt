package com.wangshu.textus.note.entity

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.BigDecimalExt.scale
import ext.NumberExt.formatDecimalPlaces
import ext.TimeExt.currentDate
import ext.TimeExt.format
import ext.TimeExt.get
import ext.TimeExt.mondayOfGivenWeek
import ext.TimeExt.month
import ext.TimeExt.sundayOfGivenWeek
import ext.TimeExt.toDate
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

open class BudgetEntity : BaseEntity() {
    /**
     * 用户ID
     */
    var userId: Long = 0L

    /**
     * 预算类型，1-个人预算，2-家庭预算，3-团队预算
     */
    var budgetType: Int = 0

    /**
     * 预算时间类型，1-每天预算，2-每周预算，3-每月预算，4-每年预算
     */
    var timeType: Int = 0

    /**
     * 创建时间
     */
    var createDate: Long = System.currentTimeMillis();

    /**
     * 订单备注
     */
    var remark: String? = null

    /**
     * 是否允许修改，1-不允许，0-允许
     */
    var modificationType: Int = 0

    /**
     * 预算金额
     */
    var budgetAmount: BigDecimal = BigDecimal.ZERO

    /**
     * 已使用金额
     */
    var usedAmount: BigDecimal = BigDecimal.ZERO

    /**
     * 剩余预算金额
     */
    var remainingAmount: BigDecimal = BigDecimal.ZERO;

    /**
     * 是否可以编辑
     * create by Eastevil at 2024/8/16 14:40
     * @author Eastevil
     * @param
     * @return
     */
    val editable : Boolean
    get() {
        return 1 == modificationType;
    }

    val budgetAmountText : String
    get() {
        return budgetAmount.scale(2);
    }

    val budgetName : String
    get() {
        return when(timeType){
            1-> NoteApp.app.getString(R.string.budget_name_time_1);
            2-> {
                val d = currentDate();
                val f = NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_datatime_format_month_day);
                val monday = d.mondayOfGivenWeek().format(f);
                val sunday = d.sundayOfGivenWeek().format(f);
                NoteApp.app.getString(R.string.budget_name_time_2,"${monday}-${sunday}");
            }
            3->{
                val month = currentDate().get(Calendar.MONTH);
                NoteApp.app.getString(R.string.budget_name_time_3,month.toString());
            }
            4->{
                val year = currentDate().get(Calendar.YEAR);
                NoteApp.app.getString(R.string.budget_name_time_4,year.toString());
            }
            else->NoteApp.app.getString(R.string.budget_name_default);
        }
    }

    val remainingText : String
    get() {
        return remainingAmount.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
    }

    val percent : Float
    get() {
        val big100 = BigDecimal(100.00);
        val p = usedAmount.multiply(big100).divide(budgetAmount,BigDecimal.ROUND_HALF_UP).toFloat();
        if(p > 100.0F){
            return 100.0F;
        }
        return p;
    }

    val percentText : String
    get() {
        val big100 = BigDecimal(100.00);
        val percent = usedAmount.multiply(big100).divide(budgetAmount,BigDecimal.ROUND_HALF_UP).toFloat();
        return "${percent.formatDecimalPlaces(2)}%";
    }

    val surplusAmountText : String
        get() {
            return remainingAmount.scale(2);
        }

    val surplusPercentText : String
        get() {
            val b100 = BigDecimal("100.00");
            val s = remainingAmount.multiply(b100).divide(budgetAmount,BigDecimal.ROUND_HALF_DOWN);
            return "${s.scale(2)}%";
        }

    val usedPercentText : String
        get() {
            val b100 = BigDecimal("100.00");
            val s = usedAmount.multiply(b100).divide(budgetAmount,BigDecimal.ROUND_HALF_DOWN);
            return "${s.scale(2)}%";
        }

    val usedSurplusText : String
        get() {
            return getString(R.string.budget_used_surplus_format,usedPercentText,surplusPercentText);
        }

    val usedAmountText : String
        get() {
            return usedAmount.scale(2);
        }

    val surplusPercentageCircle : Float
        get() {
            val f = remainingAmount.divide(budgetAmount, 2, RoundingMode.HALF_UP);
            return f.toFloat();
        }

    val usedPercentageText : String
        get() {
            val s = getString(R.string.budget_used_percent);
            return "${s}${usedPercentText}"
        }

    val surplusPercentageText : String
        get() {
            return getString(R.string.budget_surplus_percent,surplusPercentText)
        }

    val budgetMonthTimeText : String
        get() {
            return getString(R.string.month_budget_format,createDate.toDate().month());
        }

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

}
