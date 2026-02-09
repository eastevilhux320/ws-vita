package com.wsvita.core.common.adapter

import com.contrarywind.adapter.WheelAdapter
import com.wsvita.core.entity.domain.IWheelPickerItem

abstract class VitaWheelAdapter<T : IWheelPickerItem> : WheelAdapter<T>{

    private var mDataList : MutableList<T>? = null;

    override fun getItemsCount(): Int {
        return if(mDataList == null) 0 else mDataList!!.size;
    }

    override fun getItem(index: Int): T? {
        return if (index >= 0 && index < getItemsCount()) mDataList?.get(index) else null
    }

    override fun indexOf(o: T): Int {
        return mDataList?.indexOf(o)?:-1;
    }

    override fun toString(): String {
        return super.toString()
    }

    fun setDataList(dataList : MutableList<T>){
        this.mDataList = dataList;

    }

    fun getDataList(): MutableList<T>? {
        return mDataList;
    }

}
