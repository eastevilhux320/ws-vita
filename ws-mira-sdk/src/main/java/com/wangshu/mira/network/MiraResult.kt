package com.wangshu.mira.network

import com.wsvita.network.NetworkClient

class MiraResult<T> {
    /**
     * 商户编号
     */
    var merchantNo: String? = null

    /**
     * 应用ID
     */
    var appId: Long? = null

    var data: T? = null

    /**
     * 响应消息描述
     */
    var message: String? = null

    /**
     * 服务端验签数据
     */
    var signData: String? = null

    /**
     * 返回数据code
     */
    var code: Int? = null

    /**
     * 返回时间
     */
    var time: Long? = null

    val isSuccess
        get() = code == NetworkClient.instance.getOptions().successCode;
}
