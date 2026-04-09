package com.wangshu.mira.entity

import com.wsvita.core.common.BaseEntity

class UserTaskEntity : BaseEntity() {

    /**
     * 用户 ID
     */
    var userId: Long? = null

    /**
     * 任务 ID
     */
    var taskId: Long? = null

    /**
     * 设备 ID
     */
    var deviceId: Long? = null

    /**
     * 设备行为追踪记录 ID
     */
    var deviceRecordId: Long? = null

    /**
     * 创建时间
     */
    var createDate: Long = 0;

    /**
     * 状态更新时间
     */
    var updateDate: Long = 0;

    /**
     * 任务过期时间
     */
    var expiredDate: Long = 0;

    override fun customLayoutId(): Int {
        return 0;
    }

}
