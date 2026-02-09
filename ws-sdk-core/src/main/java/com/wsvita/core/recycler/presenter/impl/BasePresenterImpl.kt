package com.wsvita.core.recycler.presenter.impl

import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.core.recycler.presenter.IPresenter
import com.wsvita.framework.utils.SLog

abstract class BasePresenterImpl<T : IRecyclerItem> : IPresenter<T>{

    override fun onItemClick(item: T) {
        SLog.d(TAG,"onItemClick")
    }

    override fun onEntityClick(entity: T, position: Int) {
        SLog.d(TAG,"onItemClick,position:${position}");
    }

    companion object{
        private const val TAG = "WSV_Adapter_BasePresenterImpl=>"
    }
}
