package com.wangshu.textus.note.network.request

import com.wsvita.network.entity.BasePageRequest


class BudgetDetailRequest : BasePageRequest() {

    /**
     * 预算类型，1-个人预算，2-家庭预算，3-团队预算
     */
    var budgetType: Int? = null

    /**
     * 预算时间类型，1-每天预算，2-每周预算，3-每月预算，4-每季度预算，5-每年预算
     */
    var timeType: Int? = null
}
