package com.wangshu.mira.entity.enums

import android.content.Context
import com.wangshu.mira.R

/**
 * 操作类型标识枚举
 * 用于调度引擎匹配对应的 Android 自动化脚本或交互逻辑
 *
 * @author Eastevil
 * @version 1.0.0
 * @date 2026/2/5
 */
enum class OperationCode(val code: String, val resId: Int) {

    /** 关注 - 引导用户或自动化脚本执行关注操作 */
    FOCUS_ON("FOCUS_ON", R.string.mira_task_operation_focus_on),

    /** 点赞 - 引导用户或自动化脚本执行点赞操作 */
    LIKE("LIKE", R.string.mira_task_operation_like),

    /** 收藏 - 引导用户或自动化脚本执行收藏/加粉操作 */
    COLLECT("COLLECT", R.string.mira_task_operation_collect),

    /** 浏览 - 引导用户或自动化脚本执行内容浏览任务 */
    BROWSE("BROWSE", R.string.mira_task_operation_browse),

    /** 三连（关注+点赞+收藏）- 组合动作指令，触发高价值任务逻辑 */
    TRIPLE_ATTACK("TRIPLE_ATTACK", R.string.mira_task_operation_triple_attack);

    /**
     * 获取当前操作类型的显示名称（多语言）
     * @param context 上下文，用于访问资源系统
     * @return 对应的多语言字符串，如 "点赞" 或 "Like"
     */
    fun getName(context: Context): String {
        return context.getString(this.resId)
    }

    companion object {
        /**
         * 根据字符串 Code 匹配对应的枚举类型
         * 适配后端 ApiDataService 解密后的字符串标识
         */
        fun from(code: String?): OperationCode {
            if (code == null) return BROWSE

            // 使用传统 for 循环，确保不依赖 Lambda
            for (op in values()) {
                if (op.code.equals(code, ignoreCase = true)) {
                    return op
                }
            }
            return BROWSE
        }
    }
}
