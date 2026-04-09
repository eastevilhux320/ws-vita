package com.wangshu.mira.ext

import android.widget.TextView

object MiraViewExt {

    fun TextView.getColor(colorResId : Int): Int {
        return this.context.getColor(colorResId);
    }
}
