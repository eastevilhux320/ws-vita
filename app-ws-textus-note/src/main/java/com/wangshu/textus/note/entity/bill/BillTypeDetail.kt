package com.wangshu.textus.note.entity.bill

import com.wsvita.core.common.BaseEntity
import java.math.BigDecimal


class BillTypeDetail : BaseEntity() {

    var typeId: Long = 0;

    var typeName: String? = null

    var typeIcon: String? = null

    var amount: BigDecimal = BigDecimal.ZERO

    /**
     * 类型所占百分比
     */
    var percent: BigDecimal = BigDecimal.ZERO

    /**
     * 账单类型，1-支出，2-收入
     */
    var billType: Int = 0;


    val percentText : String
    get() {
        return "${percent.setScale(2, BigDecimal.ROUND_HALF_UP).toString()}%";
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
