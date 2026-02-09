package com.wsvita.core.recycler.presenter

/**
 * String类型的点击事件监听处理
 */
interface IStringPresenter{

    fun onItemClick(item : String);

    fun onEntityClick(entity : String,position : Int);

}
