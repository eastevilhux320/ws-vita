package com.wangshu.mira.network.request

import com.wangshu.mira.entity.DeviceInfoEntity
import com.wangshu.mira.entity.InputContentEntity
import com.wangshu.mira.entity.TaskStepParamEntity
import com.wsvita.network.entity.BaseRequest

class TaskSubmitReqeust : BaseRequest() {
    var taskId : Long? = null;

    var userId : Long? = null;

    var userDeviceId : Long? = null;

    var device : DeviceInfoEntity? = null;

    var stepList : MutableList<TaskStepParamEntity>? = null;

    var inputList : MutableList<InputContentEntity>? = null;
}
