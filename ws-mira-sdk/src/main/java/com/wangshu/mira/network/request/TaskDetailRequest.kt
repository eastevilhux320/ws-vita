package com.wangshu.mira.network.request

import com.wangshu.mira.entity.DeviceInfoEntity
import com.wsvita.network.entity.BaseRequest

class TaskDetailRequest : BaseRequest() {
    var taskId : Long? = null;

    var device : DeviceInfoEntity? = null;

    var userId : Long? = null;

    var userDeviceId : Long? = null;
}
