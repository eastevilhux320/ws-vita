package com.wsvita.account.network.response

import com.wsvita.network.entity.BaseResponse

class LoginResultResponse : BaseResponse() {

    var token : String? = null;

    var secretKey : String? = null;

    var state : Int? = null;

    /**
     * 数据处理方式,如果是RSA,则secretKey为RSA密钥，如果为AES，则secretKey为AES密钥
     * 1:RSA-RSA算法
     * 2:AES-AES算法，登录用户情况下
     * 3:UAES-未登录情况下，随机生成的id,也使用的AES算法
     */
    var keyType : Int? = null;
}
