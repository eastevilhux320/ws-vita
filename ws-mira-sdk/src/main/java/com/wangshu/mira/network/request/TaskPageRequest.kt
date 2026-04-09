package com.wangshu.mira.network.request

import com.wangshu.mira.entity.DeviceInfoEntity
import com.wangshu.mira.network.MiraListRequest
import com.wsvita.network.entity.BasePageRequest

/**
 * 商业分发平台 - 任务分页查询请求体
 * create by Administrator at 2026/3/3 23:17
 * @author Administrator
 */
class TaskPageRequest : MiraListRequest() {

    /**
     * 任务分类
     * 例如：1-新手任务, 2-每日任务, 3-限时任务
     */
    var taskType: Int? = null

    /**
     * 搜索关键字
     * 用于全局模糊匹配任务描述或备注
     */
    var keyword: String? = null

    /**
     * 任务标题
     */
    var title: String? = null

    /**
     * 任务名称
     */
    var taskName: String? = null

    /**
     * 上游类型
     * 用于标识对接的第三方广告联盟或推广主来源
     */
    var upstreamType: String? = null

    var device : DeviceInfoEntity? = null;
}
