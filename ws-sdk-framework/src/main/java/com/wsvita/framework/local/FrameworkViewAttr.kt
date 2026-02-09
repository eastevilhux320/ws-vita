package com.wsvita.framework.local

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wsvita.framework.GlideApp
import com.wsvita.framework.R
import com.wsvita.framework.utils.SLog
import java.io.File

private const val TAG = "WSVita_Framework_FrameworkViewAttr=>"

object FrameworkViewAttr {
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
    @BindingAdapter("frameUrl")
    fun setImage(view: ImageView, icon: String?){
        SLog.i(TAG, "VIEW_ID=>${view.id}")
        when(view.id){
            else->{
                GlideApp.with(view)
                    .load(icon)
                    .into(view);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("frameUrl")
    fun setImage(view: ImageView, resId: Int){
        SLog.i(TAG, "setImage_Int=>${resId}");
        when(view.id){
            else->{
                view.setImageResource(resId);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("frameUrl")
    fun setImage(view: ImageView, file: File?){
        when(view.id){
            else-> GlideApp.with(view)
                .load(file)
                .into(view);

        }
    }

    @JvmStatic
    @BindingAdapter("frameText")
    fun setText(textView: TextView, text: String?){
        when(textView.id){
            else->{
                textView.setText(text);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("frameText")
    fun setText(textView: TextView, text: Int){
        when(textView.id){
            else-> textView.setText(text);
        }
    }


    @JvmStatic
    @BindingAdapter("frameBg")
    fun setBackground(view: View, resId: Int){
        when(view.id){
            else-> {
                if(resId == 0){

                }else{
                    view.setBackgroundResource(resId)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view : View,visibility : Boolean){
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
