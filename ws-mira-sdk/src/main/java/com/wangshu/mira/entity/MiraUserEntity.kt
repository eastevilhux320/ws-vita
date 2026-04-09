package com.wangshu.mira.entity

import com.wsvita.core.common.BaseEntity

class MiraUserEntity {
    var id : Long = -1L;
    /**
     * 业务唯一标识 (String类型)
     * 用于望舒系统加密处理、验签及 AppResult 数据交互
     */
    var userId: String? = null

    /**
     * 所属商户的商户号
     */
    var merchantNo: String? = null

}
