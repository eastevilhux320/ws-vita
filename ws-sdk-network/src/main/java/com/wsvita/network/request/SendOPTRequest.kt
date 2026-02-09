package com.wsvita.network.request

import com.wsvita.network.entity.BaseRequest

/**
 * 发送验证码表单
 */
class SendOPTRequest : BaseRequest() {

    /**
     * 验证码类型,1:账号登录,2:注册
     */
    var type : Int? = null;

    /**
     * 手机号码
     */
    var mobile : String? = null;
}
