package com.wangshu.textus.note.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.wangshu.textus.note.R
import com.wangshu.textus.note.BR
import com.wangshu.textus.note.databinding.RecyclerItemAppNavigationBinding
import com.wsvita.biz.core.entity.NavigationEntity
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.framework.GlideApp
import ext.ViewExt.getScreenPair

class NavigationAdapter : AppAdapter<NavigationEntity>{
    private var params : LinearLayout.LayoutParams? = null;
    private var onNavigationClick : ((navigation : NavigationEntity)->Unit)? = null;

    constructor(context: Context,dataList : MutableList<NavigationEntity>?) : super(context,dataList) {
        val size = context.getScreenPair().first;
        val itemSize = size/5;
        params = LinearLayout.LayoutParams(itemSize,itemSize);
    }

    override fun isUsedAdapterLayout(): Boolean {
        return super.isUsedAdapterLayout()
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_app_navigation;
    }

    override fun onBindItemData(binding: ViewDataBinding, item: NavigationEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        binding.setVariable(BR.navigation,item);
    }

    override fun onBindingView(root: View, item: NavigationEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.layoutParams = params;
        root.setOnClickListener {
            if (item != null) {
                onNavigationClick?.invoke(item)
            };
        }
    }

    fun onNavigationClick(onNavigationClick : ((navigation : NavigationEntity)->Unit)){
        this.onNavigationClick = onNavigationClick;
    }

    override fun setBean(dataBinding: ViewDataBinding, entity: NavigationEntity, position: Int) {
        super.setBean(dataBinding, entity, position)
        if(dataBinding is RecyclerItemAppNavigationBinding){
            GlideApp.with(dataBinding.ivNavigationIconUrl)
                .load(entity.iconUrl)
                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .into(dataBinding.ivNavigationIconUrl);
        }
    }
}
