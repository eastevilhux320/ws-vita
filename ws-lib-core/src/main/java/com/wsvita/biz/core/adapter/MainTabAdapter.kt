package com.wsvita.biz.core.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.toColorInt
import androidx.databinding.ViewDataBinding
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.biz.core.entity.MainTabEntity
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.databinding.RvItemBizcoreMainTabBinding
import com.wsvita.biz.core.local.GlideExt.glide
import com.wsvita.framework.GlideApp
import ext.StringExt.parseColor
import ext.ViewExt.getScreenSize

class MainTabAdapter : AppAdapter<MainTabEntity> {
    private var maxNum : Int = 4;

    private var params : LinearLayout.LayoutParams;

    private var onMainTabClick : ((tab : MainTabEntity,position : Int)->Unit)? = null;

    constructor(context : Context,num : Int,height : Int) : super(context){
        this.maxNum = num;
        val width = context.getScreenSize().get(0)/maxNum;
        params = LinearLayout.LayoutParams(width,height);
    }

    override fun onBindItemData(binding: ViewDataBinding, item: MainTabEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        binding.setVariable(BR.mainTab,item);
        if(binding is RvItemBizcoreMainTabBinding){
            if(item.itemSelect){
                binding.ivBizcoreTabMain.glide(item.selIcon);
                binding.tvBizcoreTabMain.setTextColor(item.selColor?.parseColor()?: Color.BLACK);
            }else{
                binding.ivBizcoreTabMain.glide(item.norIcon);
                binding.tvBizcoreTabMain.setTextColor(item.norColor?.parseColor()?: Color.YELLOW);
            }
        }
    }

    override fun onBindingView(root: View, item: MainTabEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
        root.setOnClickListener {
            if (item != null) {
                onMainTabClick?.invoke(item,position)
            }
        }
    }

    fun onMainTabClick(onMainTabClick : ((tab : MainTabEntity,position : Int)->Unit)){
        this.onMainTabClick = onMainTabClick;
    }

}
