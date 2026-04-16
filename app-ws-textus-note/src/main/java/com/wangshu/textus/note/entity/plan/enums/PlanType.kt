package com.wangshu.note.app.entity.appenums.note

import com.wangshu.note.app.entity.appenums.note.PlanType

/**
 * 计划类型枚举
 *
 * 时间类型计划增加 TIME_ 前缀，目标和代办保持原样。
 * 每个类型对应一个整数值 type：
 * ONCE = 1-一次性计划
 * TIME_DAILY = 10-每日计划
 * TIME_WEEKLY = 11-每周计划
 * TIME_MONTHLY = 12-每月计划
 * TIME_YEARLY = 12-每年计划
 * TIME_QUARTER = 13-每季度计划
 * GOAL = 100-目标计划
 * TODO = 101-代办计划
 */
enum class PlanType(
    /**
     * 枚举对应的整数类型
     */
    val type: Int
) {
    /**
     * 一次性计划
     */
    ONCE(1),

    /**
     * 每日计划
     */
    TIME_DAILY(10),

    /**
     * 每周计划
     */
    TIME_WEEKLY(11),

    /**
     * 每月计划
     */
    TIME_MONTHLY(12),

    /**
     * 每季度计划
     */
    TIME_QUARTER(13),

    /**
     * 每年计划
     */
    TIME_YEARLY(14),

    /**
     * 目标计划（短期/长期目标合并）
     */
    GOAL(100),

    /**
     * 代办计划（普通待办事项、事件合并）
     */
    TODO(101);

    /**
     * 获取计划类别
     * @return int 值：
     * 1 - 单次计划
     * 2 - 时间类型计划
     * 3 - 目标计划
     * 4 - 代办计划
     */
    val category: Int
        get() = when (this) {
            ONCE -> 1
            TIME_DAILY,
            TIME_WEEKLY,
            TIME_MONTHLY,
            TIME_YEARLY,
            TIME_QUARTER -> 2
            GOAL -> 3
            TODO -> 4
            else -> 1 // 默认单次计划
        }

    companion object {
        fun fromType(type: Int): PlanType {
            for (value in values()) {
                if (value.type == type) {
                    return value
                }
            }
            return ONCE
        }
    }
}
