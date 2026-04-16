package com.wangshu.note.app.entity.appenums.note

import com.wangshu.note.app.entity.appenums.note.PlanState

/**
 * 计划状态枚举
 * 状态：1-未开始，2-进行中，3-已完成，4-已逾期，5-已取消
 */
enum class PlanState(
    /**
     * 枚举对应的整数状态
     */
    val state: Int
) {
    /**
     * 未开始
     */
    NOT_STARTED(1),

    /**
     * 进行中
     */
    IN_PROGRESS(2),

    /**
     * 已完成
     */
    COMPLETED(3),

    /**
     * 延期
     */
    DELAYED(4),

    /**
     * 已取消
     */
    CANCELED(5);

    companion object {
        /**
         * 根据整数状态获取枚举
         * @param state 整数状态
         * @return 对应的 PlanState，找不到返回 null
         */
        fun fromState(state: Int): PlanState {
            for (value in values()) {
                if (value.state == state) {
                    return value
                }
            }
            return NOT_STARTED
        }
    }
}
