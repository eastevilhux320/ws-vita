package com.wangshu.textus.note.local

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.framework.GlideApp
import com.wsvita.ui.view.SuffixInputLayout
import ext.BigDecimalExt.scale
import ext.StringExt.isInvalid
import java.io.File
import java.math.BigDecimal

object AppVeiwAttr {

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
    @BindingAdapter("appSrc")
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
    @BindingAdapter("appSrc")
    fun setImage(view: ImageView, resId: Int) {
        when (view.id) {
            else -> {
                if(resId != 0){
                    view.setImageResource(resId)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("appSrc")
    fun setImage(view: ImageView, file : File?){
        when (view.id) {
        }
    }

    @JvmStatic
    @BindingAdapter("appText")
    fun setText(view: TextView, text: String?) {
        when (view.id) {
            else -> {
                view.text = text
            }
        }
    }

    @JvmStatic
    @BindingAdapter("appText")
    fun setText(view: TextView, resId: Int) {
        when (view.id) {
            else -> {
                view.setText(resId)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("textIntString")
    fun setIntText(view : TextView,text : Int){
        when(view.id){
            else-> view.setText(text.toString());
        }
    }

    @JvmStatic
    @BindingAdapter("appInputText")
    fun setInputText(view: SuffixInputLayout, text : String?) {
        when(view.id){
            else-> view.setInputText(text);
        }
    }

    @JvmStatic
    @BindingAdapter("noteBigDecimal")
    fun setBigDecimal(view : TextView,bigDecimal: BigDecimal?){
        when(view.id){
            else-> view.setText(bigDecimal?.scale(2))
        }
    }

    @JvmStatic
    @BindingAdapter("appVisibility")
    fun setVisibility(view : View, visibility : Int){
        when(view.id){
            else-> view.visibility = visibility;
        }
    }

    @JvmStatic
    @BindingAdapter("appVisibility")
    fun setVisibility(view : View, visibility : Boolean){
        when(view.id){
            else-> {
                if(visibility){
                    view.visibility = View.VISIBLE;
                }else{
                    view.visibility = View.GONE;
                }
            }
        }
    }
}
