package com.wsvita.account.local

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wsvita.framework.GlideApp
import com.wsvita.framework.utils.SLog
import com.wsvita.module.account.R
import java.io.File

private const val TAG = "WS_AC_AcViewAttr=>"

object AcViewAttr {

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
    @BindingAdapter("acSrc")
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
    @BindingAdapter("acSrc")
    fun setImage(view: ImageView, resId: Int){
        SLog.i(TAG, "setImage_Int=>${resId}");
        when(view.id){
            R.id.iv_account_app_logo->{
                if(resId != 0){
                    view.setImageResource(resId);
                }
            }
            else->{
                view.setImageResource(resId);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("acSrc")
    fun setImage(view: ImageView, file: File?){
        when(view.id){
            else-> GlideApp.with(view)
                .load(file)
                .into(view);

        }
    }

    @JvmStatic
    @BindingAdapter("acText")
    fun setText(textView: TextView, text: String?){
        when(textView.id){
            else->{
                textView.setText(text);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("acText")
    fun setText(textView: TextView, text: Int){
        when(textView.id){
            else-> textView.setText(text);
        }
    }


    @JvmStatic
    @BindingAdapter("acBg")
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
    @BindingAdapter("acVisibility")
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
