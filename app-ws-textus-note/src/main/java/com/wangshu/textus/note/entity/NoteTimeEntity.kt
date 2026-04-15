package com.wangshu.textus.note.entity

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.entity.time.BaseDateTimeEntity
import ext.TimeExt.formatTime

open class NoteTimeEntity : BaseDateTimeEntity(),INoteTime {

    // 在构造时就读取资源，只执行一次
    private val chineseShowFormat = NoteApp.app.getString(R.string.chinese_time_week_format)
    private val weekPattern = NoteApp.app.getString(R.string.time_week_format)

    override fun chineseShowTime(): String {
        return currentTime.formatTime(chineseShowFormat);
    }

    override fun showWeekTime(): String {
        return currentTime.formatTime(weekPattern);
    }

}
