package com.vita.xuantong.demo.commons

object NetworkContants {

    /**
     * 全局连接超时时间（秒）
     */
    const val TIME_OUT = 30L

    /**
     * 默认每页加载数量
     */
    const val PAGE_SIZE = 20


    object ServiceCode{
        /** 成功 */
        const val SUCCESS = 0;
        /** 服务器错误 */
        const val SERVER_ERROR = 500
        /** 数据为空 */
        const val EMPTY = 704

        // 鉴权相关
        const val TOKEN_ERROR = 401
        const val SIGN_ERROR = 700
        const val APPID_ERROR = 701
        const val CHANNEL_UNAUTHORIZED = 441

        // 业务相关
        const val ACCOUNT_ERROR = 400
        const val NOT_ENOUGH = 800
        const val DATA_EXISTS = 10002
    }
}
