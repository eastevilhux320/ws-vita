package com.wsvita.biz.core.model.region

import androidx.lifecycle.Observer
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreContainerActivity
import com.wsvita.core.common.AppContainerActivity

class RegionActivity : BizcoreContainerActivity<RegionViewModel>() {

    override fun startDestinationId(): Int {
        return R.id.f_bizcore_region_city_list;
    }

    override fun destinationIdList(): MutableList<Int> {
        return mutableListOf<Int>(R.id.f_bizcore_region_city_list);
    }

    override fun getNavGraphResId(): Int {
        return R.navigation.nav_region;
    }

    override fun getVMClass(): Class<RegionViewModel> {
        return RegionViewModel::class.java;
    }

    override fun addObserve() {
        super.addObserve()
    }

    override fun autoIntentValue(): MutableList<String>? {
        return mutableListOf(BizConstants.IntentKey.REGION_CITY_HOT_FLAG);
    }
}
