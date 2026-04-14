package com.wangshu.textus.note.network.reponse

import com.wangshu.textus.note.entity.BudgetBillDetailEntity
import com.wangshu.textus.note.entity.BudgetEntity
import com.wsvita.network.entity.BaseResponse

class BudgetDetailResponse : BaseResponse() {
    var budget : BudgetEntity? = null;

    var budgetDetail : BudgetBillDetailEntity? = null;
}
