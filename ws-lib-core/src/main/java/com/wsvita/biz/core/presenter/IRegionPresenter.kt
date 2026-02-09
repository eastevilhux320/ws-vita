package com.wsvita.biz.core.presenter

import com.wsvita.biz.core.entity.region.BaseCityEntity
import com.wsvita.biz.core.entity.region.HotCityEntity
import com.wsvita.core.recycler.presenter.IPresenter

interface IRegionPresenter<C : BaseCityEntity> : IPresenter<C> {

    fun onRegionCityClick(city : C);

}
