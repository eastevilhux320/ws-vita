package com.wangshu.mira.entity

import com.wangshu.mira.entity.enums.OperationCode
import com.wsvita.core.common.BaseEntity
import java.math.BigDecimal

class TaskEntity : BaseEntity() {

    /** 任务标题 */
    var title: String? = null

    /** 任务名称 */
    var taskName: String? = null

    /** 任务简短描述/内容 */
    var introduction: String? = null

    /** 任务展示封面图URL */
    var coverUrl: String? = null

    /** 预设给用户的基准价格 */
    var basePrice: Double = 0.0

    /** 上游类型 (BLOGGER-博主, MCN-机构等) */
    var upstreamType: String? = null

    /** 需要操作的目标应用ID (关联 AppEntity) */
    var targetAppId: Long? = null

    /** * 操作类型标识 (如: FOCUS_ON-关注, LIKE-点赞)
     * 驱动 ws-vita 调度引擎匹配对应的自动化脚本协议
     */
    var operationCode: String? = null

    /** 更新时间 */
    var updateTime: String? = null

    /**
     * 操作的目标应用
     */
    var targetApp : TaskAppEntity? = null;

    var showPrice : String? = null;

    var haveShowPrice : Boolean = true;

    override fun customLayoutId(): Int {
        return 0;
    }
}
