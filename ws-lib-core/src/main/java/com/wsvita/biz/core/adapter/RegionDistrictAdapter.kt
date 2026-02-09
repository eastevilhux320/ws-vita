package com.wsvita.biz.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wsvita.biz.core.R
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.entity.region.DistrictEntity
import com.wsvita.biz.core.presenter.impl.DistrictPresenterImpl
import com.wsvita.core.common.adapter.AppAdapter
import ext.ViewExt.dip2px

class RegionDistrictAdapter : AppAdapter<DistrictEntity>{

    private var params : LinearLayout.LayoutParams? = null;
    private var onDistrictClick : ((district : DistrictEntity)->Unit)? = null;

    constructor(context: Context, width : Int, num : Int) : super(context){
        val itemWidth = (width - (3.dip2px()*(num-1)))/num;
        params = LinearLayout.LayoutParams(itemWidth,40.dip2px());
    }

    override fun getLayoutId(): Int {
        return R.layout.rv_item_region_district;
    }

    override fun onBindingView(root: View, item: DistrictEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
    }

    override fun onBindItem(binding: ViewDataBinding, item: DistrictEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        binding.setVariable(BR.districtPresenter,districtPresenter);
    }

    fun onDistrictClick(onDistrictClick : ((district : DistrictEntity)->Unit)){
        this.onDistrictClick = onDistrictClick;
    }

    override fun hasEmpty(): Boolean {
        return false;
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    private val districtPresenter = object : DistrictPresenterImpl() {
        override fun onRegionCityClick(city: DistrictEntity) {
            super.onRegionCityClick(city)
            onDistrictClick?.invoke(city);
        }
    }
}
