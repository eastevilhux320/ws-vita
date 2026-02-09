package com.wsvita.biz.core.presenter.impl

import com.wsvita.biz.core.entity.region.ProvinceEntity
import com.wsvita.biz.core.presenter.IRegionPresenter

abstract class ProvincePresenterImpl : BaseRegionPresenterImpl<ProvinceEntity>() {

    override fun onRegionCityClick(city: ProvinceEntity) {
        super.onRegionCityClick(city)
    }

}
