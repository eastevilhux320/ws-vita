package com.wsvita.biz.core.entity.region

import com.wsvita.core.common.BaseEntity


open class BaseCityEntity : BaseEntity() {

    var code: Int? = null;

    var name: String? = null

    var pcode: Int? = null

    /**
     * 类型，1-省，2-城市，2-区域
     */
    var type: Int = 0;

    /**
     * 简称
     */
    var abbreviation: String? = null

    /**
     * 字母代号
     */
    var alphabetic: String? = null

    override fun customLayoutId(): Int {
        return 0;
    }
}
