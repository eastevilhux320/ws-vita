package com.wsvita.core.network.request

import com.wsvita.network.entity.BaseRequest

/**
 * 查询车牌城市的字母检测列表请求参数
 */
class PlateAlphabeticsRequest : BaseRequest() {

    /**
     * 城市主键ID
     */
    var plateId : Long? = null;
}
