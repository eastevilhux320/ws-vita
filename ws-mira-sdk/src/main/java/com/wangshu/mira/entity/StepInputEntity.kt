package com.wangshu.mira.entity

import com.wsvita.core.common.BaseEntity

class StepInputEntity : BaseEntity() {


    /**
     * 所属的任务ID
     */
    var taskId: Long = 0L

    /**
     * 步骤ID
     */
    var stepId: Long = 0L

    /**
     * 所属类型，1-任务，2-步骤
     */
    var sourceType: Int = 0

    /**
     * 输入项唯一标识，例如 'user_phone'
     */
    var fieldKey: String? = null

    /**
     * 输入项展示名称，例如 '手机号'
     */
    var label: String? = null

    /**
     * 输入类型：text, number, image, select, date 等
     */
    var inputType: String? = null

    /**
     * 输入提示词
     */
    var hintText: String? = null

    /**
     * 是否必填：0-否，1-是
     */
    var isRequired: Int = 0

    /**
     * 错误提示信息
     */
    var errorMessage: String? = null

    /**
     * 最大长度或最大张数限制
     */
    var maxLength: Long = 0L

    /**
     * 本地已经输入的内容
     */
    var localInputText : String? = null;

    override fun customLayoutId(): Int {
        return 0;
    }
}
