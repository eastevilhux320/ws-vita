package com.wangshu.textus.note.entity

import com.wangshu.textus.note.entity.bill.BillEntity


class BudgetBillDetailEntity {

    /**
     * 预算ID
     */
    var budgetId: Long? = null

    /**
     * 预算开始时间
     */
    var startTime: Long = 0L;

    /**
     * 预算结束时间
     */
    var endTime: Long = 0L;

    /**
     * 预算内的收入订单列表
     */
    var incomeList: MutableList<BillEntity>? = null

    /**
     * 预算内的支出订单列表
     */
    var expenditureList: MutableList<BillEntity>? = null
}
