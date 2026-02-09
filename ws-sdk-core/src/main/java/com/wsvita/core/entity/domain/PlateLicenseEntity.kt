package com.wsvita.core.entity.domain

import com.wsvita.core.R
import com.wsvita.core.common.BaseEntity

/**
 * 车牌城市管理
 */
class PlateLicenseEntity : BaseEntity() {

    /**
     * 省份名称
     */
    var provinceName: String? = null

    /**
     * 省份Code
     */
    var provinceCode: Int = 0;


    /**
     * 简称
     */
    var abbreviation: String? = null

    /**
     * 车牌城市字母列表
     */
    var alphabeticList : MutableList<String>? = null;

    override fun customLayoutId(): Int {
        return R.layout.recycler_sdkitem_plate_city;
    }
}
