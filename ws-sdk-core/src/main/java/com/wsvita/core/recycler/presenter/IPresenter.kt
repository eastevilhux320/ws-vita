package com.wsvita.core.recycler.presenter

import com.wsvita.core.recycler.IRecyclerItem

interface IPresenter<T : IRecyclerItem>{

    fun onItemClick(item : T);

    fun onEntityClick(entity : T,position : Int);

    /**
     * 后续如果存在，继续添加其他方法
     */
}
