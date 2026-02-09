package com.wsvita.account.network.request

import com.wsvita.framework.entity.DeviceInfoEntity
import com.wsvita.network.entity.BaseRequest

class PhoneLoginRequest : BaseRequest() {

    var mobile : String? = null;

    var msgcode : String? = null;

    var deviceInfo : DeviceInfoEntity? = null;
}
