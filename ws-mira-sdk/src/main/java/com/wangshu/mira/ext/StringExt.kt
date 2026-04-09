package com.wangshu.mira.ext

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * 字符串扩展工具类
 * 承载望舒 APP 系统中常用的字符串处理逻辑
 */
object StringExt {

    /**
     * 将字符串直接转为 MD5 大写十六进制
     * 常用场景：val sign = (rawData + secret_key).md5()
     */
    fun String?.md5(): String {
        return MD5Util.encryptDate(this) ?: ""
    }

    /**
     * 将现有 Spanned/String 转换为 HTML 格式字符串
     * create by Eastevil at 2026/3/5 13:21
     * @author Eastevil
     * @param
     * @return
     */
    fun CharSequence?.toHtml(): String {
        if (this == null) return ""
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.toHtml(this as? Spanned ?: android.text.SpannableString(this), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
        } else {
            Html.toHtml(this as? Spanned ?: android.text.SpannableString(this))
        }
    }

    fun String?.parseHtml(): Spanned {
        if (this.isNullOrEmpty()) return android.text.SpannableString("")

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 使用 FROM_HTML_MODE_LEGACY 保持与旧版本一致的分段行为
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(this)
        }
    }

}
