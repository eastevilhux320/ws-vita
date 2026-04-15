package com.wsvita.core.entity.time

import ext.TimeExt.formatTime

abstract class BaseDateTimeEntity : IDateTime{

    open var currentTime : Long = System.currentTimeMillis();

    private val defaultPattern: String = "yyyy-MM-dd HH:mm:ss"

    override fun setTime(time: Long) {
        this.currentTime = time;
    }

    override fun getTime(): Long {
        return currentTime;
    }

    override fun showTimeText(): String {
        return currentTime.formatTime(defaultPattern);
    }
}
