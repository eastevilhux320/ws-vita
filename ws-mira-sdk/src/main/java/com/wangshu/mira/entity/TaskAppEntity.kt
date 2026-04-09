package com.wangshu.mira.entity

import com.wsvita.core.common.BaseEntity

/**
 * 任务目标应用实体类
 */
class TaskAppEntity : BaseEntity() {

    /** 应用名称 (例如: 抖音、Bilibili) */
    var appName: String? = null

    /** Android应用包名 (用于检测安装和唤起) */
    var packageName: String? = null

    /** 应用图标URL */
    var appIcon: String? = null

    /** 唤起协议/Scheme (例如: snssdk1128://) */
    var schemeUrl: String? = null

    /** 下载地址 */
    var downloadUrl: String? = null

    /** 排序号 */
    var sortOrder: Int? = null

    /** 状态: 0-禁用, 1-启用 */
    var status: Int? = null

    /** 平台类型: 1-Android, 2-iOS, 3-H5, 4-小程序 */
    var platformType: Int? = null

    override fun customLayoutId(): Int {
        return 0;
    }
}
