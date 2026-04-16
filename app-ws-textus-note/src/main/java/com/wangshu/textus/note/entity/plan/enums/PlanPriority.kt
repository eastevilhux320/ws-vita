package com.wangshu.note.app.entity.appenums.note

/**
 * 计划紧急程度
 * 优先级：
 * 1-紧急，2-重要,3-一般,4-可忽略
 */
enum class PlanPriority(val level: Int) {

    /** 紧急 */
    URGENT(1),

    /** 重要 */
    IMPORTANT(2),

    /** 一般 */
    NORMAL(3),

    /** 可忽略 */
    IGNORABLE(4);

    companion object {
        /**
         * 根据整数获取枚举
         */
        fun fromLevel(level: Int): PlanPriority {
            return values().firstOrNull { it.level == level } ?: NORMAL
        }
    }
}
