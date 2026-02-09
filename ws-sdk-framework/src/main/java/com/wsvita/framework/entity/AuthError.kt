package com.wsvita.framework.entity

class AuthError {

    companion object{
        /**
         * 用户信息未找到
         */
        const val ERROR_402 = 402;

        /**
         * UNAUTHORIZED
         */
        const val ERROR_401 = 401;

        /**
         * 账号异常
         */
        const val ERROR_400 = 400;

        /**
         * token错误
         */
        const val ERROR_5001 = 5001;

        /**
         * token异常
         */
        const val ERROR_5002 = 5002;

        /**
         * token空错误
         */
        const val ERROR_10020 = 10020;

        /**
         * token错误
         */
        const val ERROR_10021 = 10021;

        /**
         * 账号锁定
         */
        const val ERROR_10022 = 10022;

        /**
         * ### 专项鉴权/账户异常错误实体
         *
         * **框架定位：**
         * 该类专门用于承载身份验证相关的异常（如 Token 失效、账号锁定等）。
         * 在业务请求流程中，用于触发全局性的登录拦截逻辑。
         *
         * **设计规范：**
         * 1. 区别于通用业务错误 [VError]，此类错误通常意味着当前用户 Session 已不可用。
         * 2. 建议在基类 [AppViewModel] 中通过 [isAuthError] 统一拦截。
         *
         * @property code 错误状态码，对应 [AuthError.Companion] 中定义的常量。
         * @property message 原始错误消息，通常由后端接口返回。
         * @property isSessionExpired 标识当前会话是否已过期（默认 true），用于 UI 层决定是否强制清除登录状态。
         * @property timestamp 错误发生的时间戳。
         *
         * @author Administrator
         * @date 2026/1/8
         */
        fun isAuthError(code: Int): Boolean {
            return when (code) {
                ERROR_400, ERROR_401, ERROR_402,
                ERROR_5001, ERROR_5002,
                ERROR_10020, ERROR_10021, ERROR_10022 -> true
                else -> false
            }
        }
    }

    /**
     * 返回id，由后端返回，唯一
     */
    var resultId : Long = 0L;

    var code: Int = 500;

    var msg: String? = null;

    var timestamp: Long = System.currentTimeMillis();

    var isSessionExpired: Boolean = true;

}
