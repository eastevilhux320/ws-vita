package com.wsvita.biz.core.network.request

import com.wsvita.network.entity.BaseRequest

/**
 * 根据省份获取省份下城市列表请求参数
 */
class CityListRequest : BaseRequest() {
    var provinceCode : Int? = null;
}
