package com.wsvita.account.entity

import com.wsvita.core.common.BaseEntity

class HonorNameEntity : BaseEntity() {

    /**
     * 用户id
     */
    var userId: Long = 0L;

    /**
     * 字号名称
     */
    var name: String? = null

    /**
     * 获得字号的原因解释
     */
    var reasoned: String? = null

    override fun customLayoutId(): Int {
        return 0;
    }
}
