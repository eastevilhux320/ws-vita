package com.wangshu.textus.note.entity.main

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.BigDecimalExt.format
import java.math.BigDecimal

class MonthBillDetail : BaseEntity() {

    /**
     * 本月支出总和
     */
    var totalExpenditure : BigDecimal? = null;

    /**
     * 本欲收入总和
     */
    var totalIncome : BigDecimal? = null;

    /**
     * 本月结余
     */
    var totalBalance : BigDecimal? = null;

    var month : Int = 0;

    /**
     * 支出总比数
     */
    var expenditureNum : Int = 0;

    /**
     * 收入总比数
     */
    var incomeNum : Int=0;

    val totalExpenditureAmount : String
    get() {
        return totalExpenditure.format(2);
    }

    val totalIncomeAmount : String
    get() {
        return totalIncome.format(2);
    }

    val totalBalanceAmount : String
        get() {
            return totalBalance.format(2).toString();
        }

    val monthText : String
    get() {
        return month.toString();
    }

    val expenditureNumText : String
    get() {
        return NoteApp.app.getString(R.string.bill_expenditure_num_total_format,expenditureNum.toString());
    }

    val incomeNumText : String
        get() {
            return NoteApp.app.getString(R.string.bill_income_num_total_format,incomeNum.toString());
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
