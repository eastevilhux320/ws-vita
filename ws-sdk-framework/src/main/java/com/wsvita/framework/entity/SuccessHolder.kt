package com.wsvita.framework.entity

/**
 * [SuccessHolder] - 基础响应状态透传对象
 * * 核心用途：
 * 1. 在 MVVM 架构中，作为 Repository/Model 层向 ViewModel/View 层传递执行结果的载体。
 * 2. 配合 StateFlow 使用，通过更新此对象的实例触发 View 层的观察者监听。
 * 3. 统一规范组件化开发中的基础响应格式。
 * * 属性规范：
 * 按照项目约定，UI 相关属性建议映射至带有 `wsui` 前缀的逻辑中。
 */
open class SuccessHolder {

    companion object {
        /** 默认成功业务代码：10000100 */
        const val DEFAULT_SUCCESS = 10000100

        /**
         * 创建默认成功对象
         * @return 状态为 DEFAULT_SUCCESS 的 [SuccessHolder] 实例
         */
        fun success(): SuccessHolder {
            val s = SuccessHolder()
            s.code = DEFAULT_SUCCESS
            s.msg = "success"
            return s
        }

        /**
         * 创建带自定义业务码的成功对象
         * @param code 自定义业务状态码
         */
        fun success(code: Int): SuccessHolder {
            val s = SuccessHolder()
            s.code = code
            s.msg = "success"
            return s
        }

        /**
         * 创建完整的成功响应对象
         * @param code 自定义业务状态码
         * @param msg  传递给 View 层的提示信息（支持 null，默认 "success"）
         */
        fun success(code: Int, msg: String?): SuccessHolder {
            val s = SuccessHolder()
            s.code = code
            s.msg = msg ?: "success"
            return s
        }
    }

    /** 业务状态码 */
    var code: Int = 0

    /** 业务携带的消息说明 */
    var msg: String? = null
}
