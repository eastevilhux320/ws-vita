package com.wsvita.account.network.response

import com.wsvita.account.entity.Account
import com.wsvita.account.entity.HonorNameEntity

class MemberInfoResponse {

    var account : Account? = null;

    var honorNameList : MutableList<HonorNameEntity>? = null;
}
