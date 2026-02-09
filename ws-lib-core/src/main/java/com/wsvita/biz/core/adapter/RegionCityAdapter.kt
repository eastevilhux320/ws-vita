package com.wsvita.biz.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wsvita.biz.core.R
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.presenter.impl.CityPresenterImpl
import com.wsvita.core.common.adapter.AppAdapter
import ext.ViewExt.dip2px

class RegionCityAdapter : AppAdapter<CityEntity>{
    private var params : LinearLayout.LayoutParams? = null;
    private var onCityClick : ((city : CityEntity)->Unit)? = null;

    constructor(context: Context,width : Int,num : Int) : super(context){
        val itemWidth = (width - (3.dip2px()*(num-1)))/num;
        params = LinearLayout.LayoutParams(itemWidth,40.dip2px());
    }

    override fun getLayoutId(): Int {
        return R.layout.rv_item_region_city;
    }

    override fun onBindingView(root: View, item: CityEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
    }

    override fun onBindItem(binding: ViewDataBinding, item: CityEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        binding.setVariable(BR.cityPresenter,cityPresenter);
    }

    override fun hasEmpty(): Boolean {
        return false;
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    fun onCityClick(onCityClick : ((city : CityEntity)->Unit)){
        this.onCityClick = onCityClick;
    }

    private val cityPresenter = object : CityPresenterImpl() {
        override fun onRegionCityClick(city: CityEntity) {
            super.onRegionCityClick(city)
            onCityClick?.invoke(city);
        }
    }

}
