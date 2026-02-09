package com.wsvita.account.network.request

import com.wsvita.network.entity.BaseRequest

class UsernameLoginRequest : BaseRequest() {

    var account : String? = null;

    var password : String? = null;
}
