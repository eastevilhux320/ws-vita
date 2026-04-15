package com.wangshu.textus.note.entity

import com.wsvita.core.entity.time.IDateTime

interface INoteTime : IDateTime{

    fun chineseShowTime() : String;

    fun showWeekTime() : String;
}
