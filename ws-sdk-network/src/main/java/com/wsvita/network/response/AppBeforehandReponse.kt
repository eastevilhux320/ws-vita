package com.wsvita.network.response

import com.wsvita.network.entity.BaseResponse

class AppBeforehandReponse : BaseResponse() {

    var token : String? = null;
    var secretKey : String? = null;

    /**
     * 是否登录标识，1：是
     */
    var state : Int = 0;

    /**
     * 数据验签标识，1：RSA,2:AES
     */
    var keyType : Int = 1;
}
