package com.wsvita.core.banner

import com.wsvita.core.recycler.IRecyclerItem

interface IBanner : IRecyclerItem{

    fun bannerUrl() : String?;

    fun bannerTitle() : String?;

    /**
     * 是否显示banner的提示
     * create by Eastevil at 2026/1/7 13:35
     * @author Eastevil
     * @param
     * @return
     */
    fun showTips() : Boolean;
}
