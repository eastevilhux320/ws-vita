package com.wsvita.ui.ext

import android.util.TypedValue
import android.widget.TextView

object ViewExt {

    /**
     * 设置 TextView 的字体大小（单位：SP）
     * * SP (Scale-independent Pixels) 是 Android 字体大小的标准单位，
     * 会随系统设置中的“字体大小”选项进行缩放，有利于视力障碍人士的无障碍体验。
     *
     * create by Administrator at 2026/1/10 23:19
     * @author Administrator
     * @param textSizeSP 字体大小数值，建议传入 Float 型以保证精度
     * @return
     *      void
     */
    fun TextView.setTextSize(textSizeSP: Float){
        // 使用 TypedValue.COMPLEX_UNIT_SP 确保符合屏幕适配规范
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    }

    /**
     * 设置 TextView 的字体大小（单位：DP/DIP）
     * * DP (Density-independent Pixels) 是设备独立像素。
     * 使用此方法设置的字体【不会】随系统字体大小改变而缩放。
     * 场景：通常用于 UI 设计中要求文字在任何情况下都不能变大（如固定高度的按钮或 Banner）。
     *
     * create by Administrator at 2026/1/10 23:20
     * @author Administrator
     * @param textSizeDP 字体大小数值
     * @return
     *      void
     */
    fun TextView.setTextSize(textSizeDP: Int){
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDP.toFloat())
    }
}
