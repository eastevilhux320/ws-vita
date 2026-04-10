package com.wangshu.textus.note.entity.bill

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.TimeExt.format
import java.math.BigDecimal
import java.util.*


open class BillEntity : BaseEntity() {

    /**
     * 用户ID
     */
    var  userId: Long=0

    /**
     * 订单金额
     */
    var  amount: BigDecimal = BigDecimal.ZERO;

    /**
     * 账单类型，1-支出，2-收入
     */
    var  billType: Int? = null

    /**
     * 一级账单类型ID
     * @see {@link BillTypeEntity}
     */
    var  topTypeId: Long=0

    /**
     * 一级账单类型名称
     * @see {@link BillTypeEntity}
     */
    var  topTypeName: String? = null

    /**
     * 一级账单类型ICON
     * @see {@link BillTypeEntity}
     */
    var  topTypeIcon: String? = null

    /**
     * 二级账单类型ID
     * @see {@link BillTypeEntity}
     */
    var  childTypeId: Long=0

    /**
     * 二级账单类型名称
     * @see {@link BillTypeEntity}
     */
    var  childTypeName: String? = null

    /**
     * 二级账单类型ICON
     * @see {@link BillTypeEntity}
     */
    var  childTypeIcon: String? = null

    /**
     * 订单时间
     */
    var  billTime: Long=0

    /**
     * 创建时间
     */
    var  createDate: Long=0

    /**
     * 订单备注
     */
    var  remark: String? = null

    val billMonthText : String
    get() {
        val c = Calendar.getInstance();
        val date = Date();
        date.time = billTime;
        c.time = date;
        return NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_format_month,c.get(Calendar.MONTH).toString());
    }

    val billDayText : String
    get() {
        val c = Calendar.getInstance();
        val date = Date();
        date.time = billTime;
        c.time = date;
        return NoteApp.app.getString(com.wsvita.core.R.string.sdkcore_format_day,c.get(Calendar.DAY_OF_MONTH).toString());
    }

    val billShowDateText : String
    get() {
        return billTime.format(NoteApp.app.getString(com.wsvita.core.R.string.ws_date_year_month_format))
    }

    val showTimeText : String
    get() {
        return billTime.format(NoteApp.app.getString(com.wsvita.core.R.string.ws_time_format_default))
    }

    val showAmountText : String
    get() {
        if(2 == billType){
            return "+${amount.setScale(2,BigDecimal.ROUND_HALF_UP).toString()}";
        }else{
            return "-${amount.setScale(2,BigDecimal.ROUND_HALF_UP).toString()}";
        }
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
