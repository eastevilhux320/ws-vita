package com.wangshu.mira.network.response

import com.wangshu.mira.entity.TaskAppEntity
import com.wangshu.mira.entity.TaskEntity
import com.wangshu.mira.entity.TaskStepEntity
import com.wangshu.mira.entity.UserTaskEntity
import com.wsvita.network.entity.BaseResponse

open class TaskOperationBaseReponse : BaseResponse() {

    var state : Int? = null;

    var userTask : UserTaskEntity? = null;

    var mainButtonText : String? = null;

    var subButtonText : String? = null;

    /**
     * 下一个节点的到期时间
     */
    var expirationTime: Long = 0L;

    /**
     * 下一个节点的时间文本描述
     */
    var expirationTimeText: String? = null
}
