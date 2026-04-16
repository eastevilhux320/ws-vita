package com.wangshu.textus.note.network.request

import com.wsvita.network.entity.BasePageRequest


/**
 * 用户计划列表查询接口请求参数
 */
class UserPlanListRequest : BasePageRequest() {
    /**
     * 计划类型
     */
    var planType: Int? = null

    /**
     * 计划状态：0-未开始，1-进行中，2-已完成，3-延期，4-已取消
     */
    var state: Int? = null

    /**
     * 优先级：1-紧急，2-重要,3-一般,4-可忽略
     */
    var priority: Int? = null

    /**
     * 开始时间范围（毫秒时间戳）
     */
    var startDateFrom: Long? = null
    var startDateTo: Long? = null

    /**
     * 截止时间范围（毫秒时间戳）
     */
    var endDateFrom: Long? = null
    var endDateTo: Long? = null

    /**
     * 搜索文本（标题/描述）
     */
    var searchText: String? = null

    /**
     * 查询的具体日期
     */
    var queryTime : Long? = null;
}
