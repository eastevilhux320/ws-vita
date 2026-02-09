package com.wsvita.core.local

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wsvita.core.R
import com.wsvita.framework.GlideApp
import ext.TimeExt.format
import ext.ViewExt.getString
import ext.ViewExt.showTime

object SDKCoreViewAttr {
    private val circleCrop = CircleCrop();
    private val roundedCorners = RoundedCorners(10);
    private val roundedCorners15 = RoundedCorners(15);
    private val roundedCorners12 = RoundedCorners(12);
    private val roundedCorners10 = RoundedCorners(10);
    private val roundedCorners20 = RoundedCorners(20);
    private val roundedCorners50 = RoundedCorners(50);
    private val roundedCorners8 = RoundedCorners(8)
    private val roundedCorners5 = RoundedCorners(5);
    private val roundedCorners3 = RoundedCorners(3);


    @JvmStatic
    @BindingAdapter("coreSrc")
    fun setImage(view: ImageView, src : String?){
        when(view.id){
            else->{
                GlideApp.with(view)
                    .load(src)
                    .into(view);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("coreSrc")
    fun setImage(view: ImageView, resId: Int) {
        when (view.id) {
            // 示例：根据不同业务 ID 处理不同的资源图逻辑
            else -> {
                if(resId != 0){
                    view.setImageResource(resId)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("coreText")
    fun setText(view: TextView, text: String?) {
        when (view.id) {
            else -> {
                view.text = text
            }
        }
    }

    @JvmStatic
    @BindingAdapter("coreText")
    fun setText(view: TextView, resId: Int) {
        when (view.id) {
            else -> {
                view.setText(resId)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("coreText")
    fun setText(textView: TextView, text: Long) {
        when (textView.id) {
            R.id.tv_date_time_show->{
                textView.showTime(text,R.string.ws_date_time_format_default);
            }
            R.id.tv_sdkcore_month->{
                textView.showTime(text,R.string.sdkcore_month_format);
            }
            else -> {
                textView.setText(text.toString())
            }
        }
    }
}
