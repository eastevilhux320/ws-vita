package com.wsvita.biz.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.wsvita.biz.core.R
import com.wsvita.biz.core.entity.region.HotCityEntity
import com.wsvita.core.common.adapter.AppAdapter
import ext.ViewExt.dip2px

class HotCityAdapter : AppAdapter<HotCityEntity>{
    private var params : LinearLayout.LayoutParams? = null;

    constructor(context: Context,width : Int,num : Int) : super(context){
        val itemWidth = (width - (3.dip2px()*(num-1)))/num;
        params = LinearLayout.LayoutParams(itemWidth,40.dip2px());
    }

    override fun getLayoutId(): Int {
        return R.layout.rv_item_bizcore_hot_city;
    }

    override fun onBindingView(root: View, item: HotCityEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
    }

    override fun hasFooter(): Boolean {
        return false;
    }
}
