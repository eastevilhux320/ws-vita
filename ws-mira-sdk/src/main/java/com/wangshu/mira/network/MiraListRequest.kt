package com.wangshu.mira.network

/**
 * [玄映 ws-mira] 商业分发平台 - 分页请求基础抽象类
 * * <p>功能定位：
 * 1. 作为所有“任务池”、“流量变现”业务分页接口的请求基类。
 * 2. 继承自 [MiraRequest]，承载了 ws-vita 底层协议：包含 appId 识别、AES 加密分发及验签逻辑。
 * 3. 对接后端“望舒系统”的 MiraPageDTO 实体结构。</p>
 * * <p>MVVM 应用：
 * create by Administrator at 2026/3/3 23:16
 * @author Administrator
 */
abstract class MiraListRequest : MiraRequest() {

    /**
     * 当前分页下标 (从1开始)
     * 对应后端 MiraPageDTO.page
     */
    var page: Long = 1

    /**
     * 每页显示记录数 (建议默认10或20)
     * 对应后端 MiraPageDTO.limit
     */
    var limit: Long = 10

    /**
     * 排序方式
     * 可选值：ASC (升序), DESC (降序)
     */
    var order: String? = "DESC"

    /**
     * 排序字段
     * 对应数据库实体字段名，例如 "create_time" 或 "priority"
     */
    var orderField: String? = null

}
