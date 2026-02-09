package com.wsvita.core.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wsvita.core.R
import com.wsvita.core.BR
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.core.entity.domain.PlateLicenseEntity
import com.wsvita.core.recycler.presenter.impl.PlateLicensePresenterImpl
import ext.ViewExt.dip2px
import ext.ViewExt.getScreenPair

class PlateLicenseAdapter : AppAdapter<PlateLicenseEntity> {

    companion object{
        private const val DIVIR_WIDTH = 3;
    }

    private var maxNum : Int = 4;

    private var params : LinearLayout.LayoutParams;

    private var plateLicenseClick : ((plateLicense : PlateLicenseEntity?)->Unit)? = null;

    constructor(context : Context,num : Int,height : Int) : super(context){
        this.maxNum = num;
        val screenWidth = context.getScreenPair().first;

        val dw = DIVIR_WIDTH.dip2px();
        var width = (screenWidth - 20.dip2px() - (maxNum * dw))/maxNum;

        params = LinearLayout.LayoutParams(width,height);
    }

    override fun onBindingView(root: View, item: PlateLicenseEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
    }

    override fun onBindItem(binding: ViewDataBinding, item: PlateLicenseEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        binding.setVariable(BR.platePresenter,presenter);
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_sdkitem_plate_city;
    }

    fun setOnPlateLicenseClick(onPlateLicenseClick : ((plateLicense : PlateLicenseEntity?)->Unit)){
        this.plateLicenseClick = onPlateLicenseClick;
    }

    private val presenter = object : PlateLicensePresenterImpl() {

        override fun onItemClick(item: PlateLicenseEntity) {
            super.onItemClick(item)
            plateLicenseClick?.invoke(item);
        }
    }
}
