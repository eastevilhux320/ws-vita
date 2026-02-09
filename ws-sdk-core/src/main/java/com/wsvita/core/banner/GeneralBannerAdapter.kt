package com.wsvita.core.banner

import com.wsvita.core.R
import com.wsvita.core.banner.BaseBannerAdapter
import com.wsvita.core.databinding.BannerSdkcoreGeneralBinding
import com.wsvita.core.entity.GeneralBannerEntity

class GeneralBannerAdapter : BaseBannerAdapter<GeneralBannerEntity, BannerSdkcoreGeneralBinding>{

    constructor(){

    }

    override fun layoutId(): Int {
        return R.layout.banner_sdkcore_general;
    }
}
