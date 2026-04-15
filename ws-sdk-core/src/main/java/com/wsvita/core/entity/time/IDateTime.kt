package com.wsvita.core.entity.time

interface IDateTime {

    fun setTime(time : Long);

    fun getTime() : Long;

    fun showTimeText() : String;
}
