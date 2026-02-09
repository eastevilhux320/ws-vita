package com.wsvita.framework.entity

/**
 * [ErrorHolder] - 基础失败/异常数据透传对象
 * * 核心用途：
 * 1. 在 MVVM 架构中，作为 Model 层（Repository/Service）向 View 层传递异常信息的载体。
 * 2. 配合 StateFlow 或 SharedFlow 使用，触发 View 层的错误处理观察者。
 * 3. 统一组件化开发中的错误响应格式，便于全局异常拦截。
 */
open class ErrorHolder {

    companion object {
        /** 默认错误业务代码：10000101 */
        const val DEFAULT_ERROR = 10000101

        /**
         * 创建默认错误对象
         * @return 状态为 DEFAULT_ERROR 的 [ErrorHolder] 实例
         */
        fun error(): ErrorHolder {
            val e = ErrorHolder()
            e.code = DEFAULT_ERROR
            e.msg = "error"
            return e
        }

        /**
         * 创建带自定义错误码的对象
         * @param code 自定义错误状态码
         */
        fun error(code: Int): ErrorHolder {
            val e = ErrorHolder()
            e.code = code
            e.msg = "error"
            return e
        }

        /**
         * 创建完整的错误响应对象
         * @param code 自定义错误状态码
         * @param msg  错误描述信息（将直接用于 View 层的提示，如 Toast）
         */
        fun error(code: Int, msg: String?): ErrorHolder {
            val e = ErrorHolder()
            e.code = code
            e.msg = msg ?: "error"
            return e
        }
    }

    /** 错误状态码 */
    var code: Int = 0

    /** 错误详细信息 */
    var msg: String? = null
}
