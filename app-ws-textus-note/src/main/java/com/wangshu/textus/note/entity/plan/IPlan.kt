package com.wangshu.textus.note.entity.plan

import com.wangshu.note.app.entity.appenums.note.PlanPriority
import com.wangshu.note.app.entity.appenums.note.PlanState
import com.wangshu.note.app.entity.appenums.note.PlanType
import com.wsvita.core.recycler.IRecyclerItem

/**
 * 定义计划接口
 */
interface IPlan : IRecyclerItem {

    /** 获取计划标题 */
    fun showTitle(): String

    /** 获取计划描述 */
    fun showDescription(): String

    /** 获取计划类型 */
    fun getPlanType(): PlanType

    /** 获取计划状态 */
    fun getPlanState(): PlanState

    /** 是否为重复计划 */
    fun isRepeating(): Boolean = false


    /**
     * 计划的百分比，这个百分比将用来作为[android.widget.ProgressBar]的百分比
     * 例如: 69%，则值应该为69
     *
     * create by Administrator at 2025/9/7 21:08
     * @author Administrator
     * @return
     *      计划的百分比
     */
    fun planProgress() : Int;

    /**
     * 计划百分比展示文本
     * create by Administrator at 2025/9/7 21:12
     * @author Administrator
     * @return
     *      计划百分比展示文本
     */
    fun showProgressText() : String

    /** 是否有提醒 */
    fun hasReminder(): Boolean = false

    /**
     * 大分类名称
     * create by Administrator at 2025/9/7 22:33
     * @author Administrator
     * @return
     *      大分类名称
     */
    fun categoryName() : String? = null;

    /**
     * 大分类图标资源id
     * create by Administrator at 2025/9/7 22:50
     * @author Administrator
     * @return
     *      大分类图标资源id
     */
    fun categoryIconRes() : Int;

    /**
     * 显示计划创建时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划创建时间的文本说明
     */
    fun showCreateTimeText(): String?

    /**
     * 显示计划更新时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划更新时间的文本说明
     */
    fun showUpdateTimeText(): String?

    /**
     * 显示计划开始时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划开始时间的文本说明
     */
    fun showStartTimeText(): String?

    /**
     * 显示计划结束时间的文本说明
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      计划结束时间的文本说明
     */
    fun showEndTimeText(): String?

    /**
     * 显示提醒日期的文本说明（仅时间计划使用）
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      提醒日期的文本说明
     */
    fun showRemindDateText(): String?

    /**
     * 显示提醒时间的文本说明（仅时间计划使用）
     * create by Administrator at 2025/9/7 17:50
     * @author Administrator
     * @return
     *      提醒时间的文本说明
     */
    fun showRemindTimeText(): String?

    /**
     * 提醒时间文本，综合[showRemindDateText]和[showRemindTimeText]方法后的返回值
     * create by Administrator at 2025/9/7 23:04
     * @author Administrator
     * @return
     *      提醒时间文本
     */
    fun redminTimeText() : String?

    /**
     * 获取计划紧急程度
     * create by Administrator at 2025/9/7 18:10
     * @author Administrator
     * @return
     *      计划紧急程度枚举
     */
    fun getPriority(): PlanPriority

    /**
     * 计划紧急程度图标
     * create by Administrator at 2025/9/7 18:11
     * @author Administrator
     * @return
     *      计划紧急程度图标
     */
    fun priorityIconRes() : Int;

    /**
     * 计划紧急程度文本
     * create by Administrator at 2025/9/7 18:13
     * @author Administrator
     * @return
     *      计划紧急程度文本
     */
    fun priorityText() : String;

    /**
     * 计划状态展示文本
     * create by Administrator at 2025/9/7 19:51
     * @author Administrator
     * @param
     * @return
     *      计划状态展示文本
     */
    fun showStateText() : String?;

    /**
     * 计划状态图标
     * create by Administrator at 2025/9/7 20:00
     * @author Administrator
     * @param
     * @return
     */
    fun stateIconRes() : Int;

    /**
     * 计划状态文本颜色
     * create by Administrator at 2025/9/7 21:04
     * @author Administrator
     * @return
     *      计划状态文本颜色
     */
    fun stateTextColor() : Int;

    /**
     * 提醒时间是否已经超时
     * @author Eastevil
     * @createTime 2025/9/22 16:00
     * @param
     * @since
     * @see
     * @return
     *      提醒时间是否已经超时
     */
    fun isRedminTimeOut() : Boolean;

    /**
     * 结束时间[endDate]是否已经超时
     * @author Eastevil
     * @createTime 2025/9/23 11:13
     * @param
     * @since
     * @see
     * @return
     */
    fun isEndTimeOut() : Boolean;

    /**
     * 剩余提醒时间展示文本
     * @author Eastevil
     * @createTime 2025/9/22 16:19
     * @param
     * @since
     * @see
     * @return
     */
    fun redminTimeLeftText() : String

    /**
     * 获取截至日期的展示文本
     *
     * 该方法用于显示计划的截止时间剩余或已超时的时间信息：
     * - 如果截止日期尚未到，则返回格式为“剩余x天x小时x分x秒”的文本
     * - 如果截止日期已过，则返回格式为“已超时x天x小时x分x秒”的文本
     *
     * 示例：
     * - 截止日期在未来：剩余2天3小时10分15秒
     * - 截止日期已过：已超时1天5小时20分8秒
     *
     * @author Eastevil
     * @createTime 2025/9/23 11:17
     * @since 1.0
     * @return 截至日期的剩余或超时文本，如果截止日期不存在或无法解析，则返回默认未知文本
     */
    fun endTimeLeftText() : String
}
