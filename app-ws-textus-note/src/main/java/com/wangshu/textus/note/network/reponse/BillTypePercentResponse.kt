package com.wangshu.textus.note.network.reponse

import com.wangshu.textus.note.entity.bill.BillTypePercentEntity
import com.wsvita.network.entity.BaseResponse

class BillTypePercentResponse : BaseResponse() {

    /**
     * 支出百分比
     */
    var expenditurePercentList : MutableList<BillTypePercentEntity>? = null;

    /**
     * 收入百分比
     */
    var incomePercentList : MutableList<BillTypePercentEntity>? = null;

}
