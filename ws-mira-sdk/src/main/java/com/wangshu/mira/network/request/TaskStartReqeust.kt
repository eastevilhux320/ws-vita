package com.wangshu.mira.network.request

import com.wangshu.mira.entity.DeviceInfoEntity
import com.wsvita.network.entity.BaseRequest

class TaskStartReqeust : BaseRequest() {
    var taskId : Long? = null;

    var userId : Long? = null;

    var userDeviceId : Long? = null;

    var device : DeviceInfoEntity? = null;
}
