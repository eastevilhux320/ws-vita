package com.wangshu.textus.note.entity.bill

import com.wangshu.textus.note.R
import com.wsvita.core.common.BaseEntity
import ext.BigDecimalExt.scale
import java.math.BigDecimal


/**
 * 账单类型管理
 */
class BillTypePercentEntity : BaseEntity() {

    var typeId: Long = 0;

    var typeName: String? = null

    var typeIcon: String? = null

    var amount: BigDecimal = BigDecimal.ZERO;

    /**
     * 类型所占百分比
     */
    var percent: BigDecimal = BigDecimal.ZERO;

    /**
     * 账单类型，1-支出，2-收入
     */
    var billType: Int = 1;

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

    val percentText : String
    get() {
        return "${percent.scale(2)}%";
    }

    val amountText : String
    get() {
        return getString(R.string.home_month_total_format,amount.scale(2));
    }
}
