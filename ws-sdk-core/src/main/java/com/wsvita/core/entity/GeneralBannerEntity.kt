package com.wsvita.core.entity

import com.wsvita.core.R


open class GeneralBannerEntity : BaseBannerEntity() {

    override fun customLayoutId(): Int {
        return R.layout.banner_sdkcore_general;
    }
}
