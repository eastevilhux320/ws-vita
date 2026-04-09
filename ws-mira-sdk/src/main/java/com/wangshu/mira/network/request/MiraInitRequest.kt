package com.wangshu.mira.network.request

import com.wangshu.mira.local.manager.MiraManager
import com.wangshu.mira.network.MiraRequest

class MiraInitRequest : MiraRequest() {
    var miraUserId : String? = MiraManager.instance.getMiraUserId();
}
