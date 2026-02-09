package com.wsvita.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.wsvita.core.R
import com.wsvita.core.common.StringAdapter
import ext.ViewExt.dip2px
import ext.ViewExt.getScreenPair

class PlateLetterAdapter : StringAdapter{
    companion object{
        private const val DIVIR_WIDTH = 3;
    }

    private var maxNum : Int = 4;

    private var params : LinearLayout.LayoutParams;


    constructor(context : Context,num : Int,height : Int) : super(context){
        this.maxNum = num;
        val screenWidth = context.getScreenPair().first;

        val dw = DIVIR_WIDTH.dip2px();
        var width = (screenWidth - 20.dip2px() - (maxNum * dw))/maxNum;

        params = LinearLayout.LayoutParams(width,height);
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.recycler_sdkitem_plate_letter;
    }

    override fun onBindStringView(root: View, item: String, position: Int) {
        super.onBindStringView(root, item, position)
        root.layoutParams = params;
    }

}
