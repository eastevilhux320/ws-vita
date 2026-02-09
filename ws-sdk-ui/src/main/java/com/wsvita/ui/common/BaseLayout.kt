package com.wsvita.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

abstract class BaseLayout<D : ViewDataBinding> : FrameLayout {

    companion object{
        const val VALUE_ZERO = 0;
    }

    protected lateinit var dataBinding : D;

    constructor(context: Context) : super(context) {
        init();
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttr(attrs)
        init();
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttr(attrs);
        init();
    }

    protected open fun initAttr(attrs: AttributeSet) {

    }

    private fun init(){
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId(), this, true)
        // 关键点：将 View 的 Lifecycle 绑定给 Binding，支持 LiveData 自动刷新
        if (context is LifecycleOwner) {
            dataBinding.lifecycleOwner = context as LifecycleOwner
        }
        initView();
        onBind();
    }


    protected open fun initView(){

    }

    abstract fun layoutId() : Int;

    abstract fun onBind()
}
