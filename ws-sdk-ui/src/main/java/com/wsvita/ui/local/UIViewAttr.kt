package com.wsvita.ui.local

import android.text.InputFilter
import android.widget.EditText
import androidx.databinding.BindingAdapter

/**
 * UI 视图自定义属性适配器
 * 统一管理基于 wsui 前缀的 DataBinding 属性，增强基础控件的功能。
 */
object UIViewAttr {

    /**
     * 自动转大写功能适配器
     *
     * 【功能描述】
     * 1. 自动注入 [InputFilter.AllCaps] 过滤器。
     * 2. 兼容原有 filters 链，不会覆盖 xml 中定义的 maxLength 等其他过滤器。
     * 3. 相比在 TextWatcher 中处理，此方式性能更高，且不会引起光标重置或无限循环触发监听。
     *
     * create by Eastevil at 2026/1/15 10:16
     * @author Eastevil
     *
     * @param editText 目标输入框
     * @param autoUpper 是否开启自动转大写。若为 true，则用户输入的任何小写字母都会被实时转换为大写。
     *
     * @return
     *      void
     */
    @BindingAdapter("wsui:autoUpper")
    fun setAutoUpper(editText: EditText, autoUpper: Boolean?) {
        if (autoUpper == true) {
            val filters = editText.filters?.toMutableList() ?: mutableListOf()
            if (filters.none { it is InputFilter.AllCaps }) {
                filters.add(InputFilter.AllCaps())
                editText.filters = filters.toTypedArray()
            }
        }
    }
}
