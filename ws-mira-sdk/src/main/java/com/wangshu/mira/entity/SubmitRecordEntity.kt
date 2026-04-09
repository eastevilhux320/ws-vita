package com.wangshu.mira.entity


import com.wangshu.mira.R
import com.wangshu.mira.entity.enums.SubmitAuditState
import com.wsvita.core.common.BaseEntity
import java.util.Date

/**
 * 用户提交记录数据管理
 * 适配玄同 (ws-vita) 架构下的基础实体定义
 */
class SubmitRecordEntity : BaseEntity() {

    // 关联的任务id
    var taskId: Long? = null

    // 提交的设备id
    var deviceId: Long? = null

    /**
     * 1-待审核，2-审核中，3-审核通过，4-审核驳回
     */
    var auditState: Int = 0;

    // 驳回原因/审核备注
    var auditRemark: String? = null

    // 创建时间
    var createTime: Long = 0;

    // 任务标题
    var taskTitle: String? = null

    // 任务图标
    var taskIcon: String? = null

    // 审核时间
    var auditTime: Long = System.currentTimeMillis();

    // 预计审核时间
    var expectedAuditTime: Long = 0;

    var auditStateText : String? = null;

    val submitAuditState : SubmitAuditState?
        get() = SubmitAuditState.getByState(auditState);


    override fun customLayoutId(): Int {
        val state = SubmitAuditState.getByState(auditState);
        return state?.let {
            when(it){
                SubmitAuditState.REJECT-> R.layout.recycler_item_mira_submit_record_audit_error;
                else-> R.layout.recycler_item_mira_submit_record;
            }
        }?:R.layout.recycler_item_mira_submit_record;
    }
}
