package com.wsvita.core.common.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.core.recycler.RecyclerItemEntity

abstract class BuilderAdapter<I : IRecyclerItem,> : AppAdapter<I> {

    constructor(context: Context) : super(context) {

    }

    override fun onBindItem(binding: ViewDataBinding, item: I?, position: Int) {
        super.onBindItem(binding, item, position)
    }

}
