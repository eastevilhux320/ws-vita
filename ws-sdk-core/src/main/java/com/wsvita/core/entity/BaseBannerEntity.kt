package com.wsvita.core.entity

import com.wsvita.core.banner.IBanner
import com.wsvita.core.common.BaseEntity

abstract class BaseBannerEntity : BaseEntity(),IBanner {
    var title : String? = null;

    override fun bannerTitle(): String? {
        return title;
    }

    override fun bannerUrl(): String? {
        return itemIconUrl();
    }

    override fun showTips(): Boolean {
        return true;
    }
}
