package com.wsvita.ui.view

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.wsvita.ui.R
import com.wsvita.ui.databinding.LayoutUiEmptyBinding

class EmptyView : FrameLayout {
    private lateinit var dataBinding : LayoutUiEmptyBinding;
    private var emptyImageWidth : Float = 50f;
    private var emptyImageHeight : Float = 50f;
    private var emptyImageResId : Int = R.drawable.ui_noimg_default_rectangle;
    private lateinit var emptyText : ObservableField<String>;

    constructor(context: Context) : super(context){
        initAttr(null);
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs){
        initAttr(attrs);
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context,attrs,defStyleAttr){
        initAttr(attrs);
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int, defStyleRes : Int) : super(context,attrs,defStyleAttr,defStyleRes){
        initAttr(attrs);
    }

    private fun initAttr(attrs : AttributeSet?){
        emptyText = ObservableField();
        attrs?.let {
            val ta: TypedArray = context.obtainStyledAttributes(it, R.styleable.EmptyView);
            emptyImageWidth = ta.getDimension(R.styleable.EmptyView_empty_img_width,50f);
            emptyImageHeight = ta.getDimension(R.styleable.EmptyView_empty_img_height,50f);
            val emptyT = ta.getString(R.styleable.EmptyView_empty_text);
            emptyT?.let {
                emptyText.set(it);
            }
            emptyImageResId = ta.getResourceId(R.styleable.EmptyView_empty_src,-1);
            ta.recycle();
        }
        init();
    }

    private fun init(){
        dataBinding = DataBindingUtil.inflate<LayoutUiEmptyBinding>(LayoutInflater.from(context),
            R.layout.layout_ui_empty, this, true);
        dataBinding.emptyText = emptyText;
        val imgParams = dataBinding.ivEmptyImage.layoutParams;
        imgParams.width = emptyImageWidth.dip2px().toInt();
        imgParams.height = emptyImageHeight.dip2px().toInt();
        dataBinding.ivEmptyImage.layoutParams = imgParams;
        if(-1 == emptyImageResId){
            dataBinding.ivEmptyImage.setImageResource(R.drawable.ui_list_item_no_data_default);
        }else{
            dataBinding.ivEmptyImage.setImageResource(emptyImageResId);
        }
    }

    private fun Float.dip2px(): Float {
        val scale = Resources.getSystem().displayMetrics.density
        return this * scale + 0.5f;
    }
}
