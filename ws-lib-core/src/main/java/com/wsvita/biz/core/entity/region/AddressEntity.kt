package com.wsvita.biz.core.entity.region

import com.wsvita.biz.core.R
import com.wsvita.core.common.BaseEntity

class AddressEntity : BaseEntity() {
    var contactName: String? = null
    var mobile: String? = null

    // 行政区划 ID 扩展
    var provinceId: Long? = -1L
    var province: String? = null
    var cityId: Long? = -1L
    var city: String? = null
    var districtId: Long? = -1L
    var district: String? = null

    var addressDetail: String? = null

    /**
     * 经纬度：采用 BigDecimal 保证高精度
     */
    var lng: String? = null
    var lat: String? = null

    override fun customLayoutId(): Int {
        return R.layout.rv_item_bizcore_address;
    }
}
