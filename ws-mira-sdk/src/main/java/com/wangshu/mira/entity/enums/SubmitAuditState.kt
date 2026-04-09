package com.wangshu.mira.entity.enums

/**
 * 任务提交审核状态枚举
 */
enum class SubmitAuditState(val state: Int) {

    /**
     * 待审核
     */
    WAIT(1),

    /**
     * 审核中
     */
    AUDITING(2),

    /**
     * 审核通过
     */
    PASS(3),

    /**
     * 审核驳回
     */
    REJECT(4);

    companion object {
        /**
         * 根据状态值获取枚举
         */
        fun getByState(state: Int?): SubmitAuditState? {
            if (state == null) {
                return null
            }
            // 遍历匹配状态值
            return values().find { it.state == state }
        }
    }
}
