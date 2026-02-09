package com.wsvita.biz.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.biz.core.entity.MainTabEntity
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.databinding.RvItemBizcoreMainTabBinding
import ext.ViewExt.getScreenSize

class MainTabAdapter : AppAdapter<MainTabEntity> {
    private var maxNum : Int = 4;

    private var params : LinearLayout.LayoutParams;

    constructor(context : Context,num : Int,height : Int) : super(context){
        this.maxNum = num;
        val width = context.getScreenSize().get(0)/maxNum;
        params = LinearLayout.LayoutParams(width,height);
    }

    override fun onBindItemData(binding: ViewDataBinding, item: MainTabEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        binding.setVariable(BR.mainTab,item);
        if(binding is RvItemBizcoreMainTabBinding){

        }
    }

    override fun onBindingView(root: View, item: MainTabEntity?, position: Int) {
        super.onBindingView(root, item, position)
    }

}
