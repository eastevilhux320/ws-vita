package com.wsvita.biz.core.local

import android.widget.ImageView
import com.wsvita.framework.GlideApp

object GlideExt {

    fun ImageView.glide(url : String?){
        GlideApp.with(this)
            .load(url)
            .into(this);
    }

    fun ImageView.glide(url : String?,defaultResId : Int){
        GlideApp.with(this)
            .load(url)
            .error(defaultResId)
            .placeholder(defaultResId)
            .into(this);
    }

    fun ImageView.itemDefault(url : String?){
        GlideApp.with(this)
            .load(url)
            .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
            .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
            .into(this);
    }

}
