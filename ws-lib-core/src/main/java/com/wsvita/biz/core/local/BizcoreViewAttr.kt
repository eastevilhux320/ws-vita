package com.wsvita.biz.core.local

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wsvita.biz.core.R
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.framework.GlideApp
import com.wsvita.ui.view.side.SimpleMete
import ext.StringExt.isInvalid

/**
 * BizCore 共有业务视图属性适配器 (DataBinding Adapter)
 *
 * 【定位与职责】
 * 1. 作为 ws-lib-core 组件的公共 UI 逻辑中心，负责处理跨模块的共有业务 UI 行为。
 * 2. 统一封装图片加载 (srcUrl) 和文本处理 (bizText)，替代原生的 android:src 和 android:text 以实现业务解耦。
 * 3. 允许通过 View ID 分发特殊逻辑，实现“全局统一管理，局部特殊处理”的组件化策略。
 *
 * 【包结构说明】
 * 归属于 com.wsvita.biz.core.commons 路径下，属于共有业务支撑逻辑。
 *
 * 【扩展指南】
 * - 若需增加全局通用的图片处理（如圆角、占位图），在 [setImage] 的 else 分支统一修改。
 * - 若需针对特定业务组件（如广告位）进行 UI 定制，请在 when(view.id) 中匹配对应业务模块的 R.id。
 *
 * create by Administrator at 2026/1/2 1:17
 * @author Administrator
 */
object BizcoreViewAttr {

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
    @BindingAdapter("srcUrl")
    fun setImage(view: ImageView,srcUrl : String?){
        when(view.id){
            R.id.iv_bizcore_splash->{
                GlideApp.with(view)
                    .load(srcUrl)
                    .error(BizcoreConfigure.instance.getConfig()?.splashDefaultId?:com.wsvita.ui.R.drawable.shape_white)
                    .placeholder(BizcoreConfigure.instance.getConfig()?.splashDefaultId?: com.wsvita.ui.R.drawable.shape_white)
                    .into(view);
            }
            else->{
                GlideApp.with(view)
                    .load(srcUrl)
                    .into(view);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("srcUrl")
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
    @BindingAdapter("bizText")
    fun setText(view: TextView, text: String?) {
        when (view.id) {
            R.id.tv_bizcore_district,
            R.id.tv_bizcore_province,
            R.id.tv_bizcore_city->{
                if(text.isInvalid()){
                    view.setText(R.string.bizcore_selected_tips)
                }else{
                    view.setText(text);
                }
            }
            else -> {
                view.text = text
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bizText")
    fun setText(view: TextView, resId: Int) {
        when (view.id) {
            else -> {
                view.setText(resId)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bizLabel")
    fun setLabel(view : SimpleMete,label : String?){
        when(view.id){
            R.id.mete_city->{
                view.setLabel(label);
            }
        }
    }
}
