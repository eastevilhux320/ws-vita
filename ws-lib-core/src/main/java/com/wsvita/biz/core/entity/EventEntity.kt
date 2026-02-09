package com.wsvita.biz.core.entity

import com.wsvita.core.entity.GeneralBannerEntity

/**
 * 应用活动实体类
 */
abstract class EventEntity : GeneralBannerEntity() {

    /**
     * 活动链接地址
     */
    var linkUrl: String? = null

    /**
     * 活动详细说明
     */
    var details: String? = null

    /**
     * 活动开始时间 (使用 Long 时间戳)
     */
    var startTime: Long = 0L

    /**
     * 活动结束时间 (使用 Long 时间戳)
     */
    var endTime: Long = 0L


    /**
     * 类型，1-欢迎页活动,2-首页banner,3-历史tab栏banner
     */
    var type: Int = 0

    /**
     * 是否为官方，即自己平台活动
     */
    var official: Boolean = true

    /**
     * 备注
     */
    var remark: String? = null

    /**
     * 专题ID (仅精选专题类型会透出)
     */
    var topicId: Long? = null

    /**
     * 官方活动ID (仅淘宝类型会透出)
     */
    var activityId: Long? = null
}
