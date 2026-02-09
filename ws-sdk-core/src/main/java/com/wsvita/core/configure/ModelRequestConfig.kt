package com.wsvita.core.configure

import com.wsvita.framework.entity.VError
/**
 * 业务请求配置类。
 * <p>
 * <b>核心职责：</b><br>
 * 本类作为 Model 层发起网络请求时的参数配置载体，支持通过 Builder 模式定制错误的交互表现。<br>
 * 它将业务请求的标识（requestCode）、名称（requestTitle）与 UI 的展现形式（showType）高度聚合。<br>
 * </p>
 * <p>
 *
 * create by Eastevil at 2025/12/30 10:27
 * @author Eastevil
 */
class ModelRequestConfig{
    private var error  : VError;

    /** UI 展示类型 */
    var showType: Int = 0

    /** 确定按钮文本 */
    var submitText: String? = null

    /** 取消按钮文本 */
    var cancelText: String? = null

    /** 请求标识，用于在 View 层区分具体业务接口 */
    var requestCode: Int = 0

    /**
     * 本次借款请求的标识。例：获取配置的接口，一般定义为 接口配置。
     * 默认使用在dialog提示的时候，作为标题展示，也可自行获取使用
     */
    var requestTitle : String? = null;

    companion object{
        /** 吐司提示类型 */
        const val SHOW_TYPE_TOAST = 100

        /** 页面缺省视图类型 */
        const val SHOW_TYPE_VIEW = 200

        /** 仅提示弹框（无按钮或点击外部消失） */
        const val SHOW_TYPE_DIALOG_TIPS = 301

        /** 强确认弹框（单个确认按钮） */
        const val SHOW_TYPE_DIALOG_COFMIRT = 302

        /** 标准交互弹框（确定与取消双按钮） */
        const val SHOW_TYPE_DIALOG_CONVENTIONAL = 303

    }

    private constructor(builder : Builder){
        error = VError.error(builder.code,builder.msg);
        showType = builder.showType;
        submitText = builder.submitText;
        cancelText = builder.cancelText;
        requestCode = builder.requestCode;
        requestTitle = builder.requestTitle;
    }

    fun error(): VError {
        return error;
    }

    class Builder{
        /** 错误码 */
        internal var code: Int = -3000

        /** 错误提示信息 */
        internal var msg: String? = null

        /** UI 展示类型 */
        internal var showType: Int = 0

        /** 确定按钮文本 */
        internal var submitText: String? = null

        /** 取消按钮文本 */
        internal var cancelText: String? = null

        /** 请求标识，用于在 View 层区分具体业务接口 */
        internal var requestCode: Int = 0

        /**
         * 本次借款请求的标识。例：获取配置的接口，一般定义为 接口配置。
         * 默认使用在dialog提示的时候，作为标题展示，也可自行获取使用
         */
        internal var requestTitle : String? = null;

        /**
         * Description: 设置业务错误码。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param code 错误码
         * @return Builder 实例支持链式调用
         */
        fun setCode(code: Int): Builder {
            this.code = code
            return this
        }

        /**
         * Description: 设置错误提示文案。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param msg 文案内容
         * @return Builder 实例
         */
        fun setMsg(msg: String?): Builder {
            this.msg = msg
            return this
        }

        /**
         * Description: 设置 UI 展示类型。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param showType 展示类型常量
         * @return Builder 实例
         */
        fun setShowType(showType: Int): Builder {
            this.showType = showType
            return this
        }

        /**
         * Description: 设置确认按钮文案。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param submitText 按钮文本
         * @return Builder 实例
         */
        fun setSubmitText(submitText: String?): Builder {
            this.submitText = submitText
            return this
        }

        /**
         * Description: 设置取消按钮文案。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param cancelText 按钮文本
         * @return Builder 实例
         */
        fun setCancelText(cancelText: String?): Builder {
            this.cancelText = cancelText
            return this
        }

        /**
         * Description: 设置请求标识码。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param requestCode 标识码
         * @return Builder 实例
         */
        fun setRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        /**
         * Description: 设置请求业务名称（标题）。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param requestTitle 业务名称
         * @return Builder 实例
         */
        fun setRequestTitle(requestTitle: String?): Builder {
            this.requestTitle = requestTitle
            return this
        }

        fun builder(): ModelRequestConfig {
            return ModelRequestConfig(this);
        }
    }
}
