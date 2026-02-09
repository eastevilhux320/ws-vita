package com.wsvita.biz.core.network.request

import com.wsvita.network.entity.BaseRequest

class EnrollmentAddressRequest : BaseRequest() {

    var provinceId : Long? = null;
    var cityId : Long? = null;
    var districtId : Long? = null;
    var provinceName : String? = null;
    var cityName : String? = null;
    var districtName : String? = null;

    var address : String? = null;
}
