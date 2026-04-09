package com.wangshu.mira.entity.enums


/**
 * 用户任务状态定义 - 适配玄映 (ws-mira) 任务分发逻辑
 * * @author Eastevil
 * @version 1.0.0
 * @date 2026/03/15
 */
enum class UserTaskState(val state: Int) {
    /**
     * 暂无记录
     */
    NONE(0),
    /**
     * 1 - 已申请未开始
     * 前端 (ws-vision) 交互：[开始任务]
     */
    APPLIED(1),

    /**
     * 2 - 进行中
     * 前端 (ws-vision) 交互：[继续任务] / [提交任务] / [取消任务]
     */
    IN_PROGRESS(2),

    /**
     * 3 - 已取消
     * 前端 (ws-vision) 交互：任务终止，进入历史记录或释放名额
     */
    CANCELLED(3),

    /**
     * 4 - 已完成
     * 前端 (ws-vision) 交互：任务达成，展示收益标记或置灰不可点
     */
    COMPLETED(4),

    /**
     * 5 - 已提交
     */
    SUBMIT(5);


    companion object {
        /**
         * 根据数值获取对应的枚举实例
         */
        fun from(state: Int): UserTaskState? {
            for (type in values()) {
                if (type.state == state) {
                    return type
                }
            }
            return null
        }
    }
}
