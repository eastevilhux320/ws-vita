package com.wsvita.core.common

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.wsvita.core.adapter.AppStringAdapter
import com.wsvita.core.recycler.presenter.impl.StringPresenterImpl
import com.wsvita.core.BR

abstract class StringAdapter : AppStringAdapter{

    private var onStringClick : ((s : String,position : Int)->Unit)? = null;
    private var onItemClick : ((s : String)->Unit)? = null;

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, dataList: MutableList<String>?) : super(context,dataList) {
        setList(dataList);
    }

    override fun onBindItem(binding: ViewDataBinding, item: String?, position: Int) {
        super.onBindItem(binding, item, position)
        binding.setVariable(BR.stringPresenter,stringPresenter);
    }

    fun onItemClick(onItemClick : ((s : String)->Unit)){
        this.onItemClick = onItemClick;
    }

    fun onStringClick(onStringClick : ((s : String,position : Int)->Unit)){
        this.onStringClick = onStringClick;
    }

    private val stringPresenter = object : StringPresenterImpl() {
        override fun onItemClick(item: String) {
            super.onItemClick(item)
            onItemClick?.invoke(item);
        }

        override fun onEntityClick(entity: String, position: Int) {
            super.onEntityClick(entity, position)
            onStringClick?.invoke(entity,position);
        }
    }
}
