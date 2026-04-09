package com.wangshu.mira.network

import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.local.manager.MiraManager
import com.wsvita.network.entity.BaseRequest

open class MiraRequest : BaseRequest() {
    var merchantNo : String? = MiraConfigure.instance.getConfig()?.merchantNo;

    var userId : Long? = MiraManager.instance.getUserId();
}
