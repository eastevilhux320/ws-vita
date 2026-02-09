package com.wsvita.framework.entity

/**
 * ### 业务/视觉错误实体类 (VError)
 *
 * **设计定位：**
 * 该类是 Model 层与 View 层之间传递错误信息的标准化载体。
 * 在组件化开发中，它作为一种“契约”，定义了子组件在请求失败时应接收到的数据结构。
 *
 * **核心功能：**
 * 1. 承载错误状态码 [code]，用于子组件执行分支逻辑。
 * 2. 承载提示文案 [msg]，用于子组件直接渲染 UI。
 *
 * @author Eastevil
 * @date 2025/12/29
 */
class VError private constructor() {

    companion object {
        /**
         * 快速构造方法：仅指定错误码
         * @param code 错误码。通常由后端定义或 Framework 层统一约定。
         * @return 初始化的 VError 对象，msg 默认为空字符串。
         */
        fun error(code : Int): VError {
            val e = VError()
            e.code = code
            e.msg = ""
            return e
        }

        /**
         * 完整构造方法：指定错误码与提示文案
         * @param code 错误码。
         * @param msg 具体的错误描述信息，子组件可直接用于 Toast 或文本展示。
         * @return 初始化的 VError 对象。
         */
        fun error(code : Int, msg : String?): VError {
            val e = VError()
            e.code = code
            e.msg = msg
            return e
        }
    }

    /**
     * 返回id，由后端返回，唯一
     */
    var resultId : Long = 0L;

    /** * **错误码**
     * 默认值：-3000。
     * 子组件通过判断该码值来决定后续行为（如重试、报错、跳转等）。
     */
    var code: Int = -3000

    /** * **错误提示信息**
     * 具体的业务逻辑描述，由子组件进行视觉呈现。
     */
    var msg: String? = null

    /**
     * 返回时间
     */
    var timestamp : Long = 0;
}
