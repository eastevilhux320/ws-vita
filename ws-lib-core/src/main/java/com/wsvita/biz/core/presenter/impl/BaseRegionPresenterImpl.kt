package com.wsvita.biz.core.presenter.impl

import com.wsvita.biz.core.entity.region.BaseCityEntity
import com.wsvita.biz.core.presenter.IRegionPresenter

abstract class BaseRegionPresenterImpl<C : BaseCityEntity> : IRegionPresenter<C>{

    override fun onItemClick(item: C) {

    }

    override fun onEntityClick(entity: C, position: Int) {

    }

    override fun onRegionCityClick(city: C) {

    }

}
