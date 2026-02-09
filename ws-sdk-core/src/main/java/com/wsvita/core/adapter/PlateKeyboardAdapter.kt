package com.wsvita.core.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.wsvita.core.R
import com.wsvita.core.common.StringAdapter

class PlateKeyboardAdapter : StringAdapter{

    constructor(context: Context) : super(context) {

    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.recycler_platelicense_keyboard;
    }

    override fun onBindItem(binding: ViewDataBinding, item: String?, position: Int) {
        super.onBindItem(binding, item, position)
    }

    override fun onBindStringView(root: View, item: String, position: Int) {
        super.onBindStringView(root, item, position)
        val lp = root.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            // 统一按键宽度占比为 0.1 (10%)
            // 这样 10 个键的行会占满，9 个键的行会余出 10% 空间由 Flexbox 居中处理
            when (item) {
                "删除" -> {
                    lp.flexBasisPercent = 0.2f // 删除键占 20%
                    lp.flexGrow = 1.0f         // 允许它伸缩填补最后一行剩余空间
                }
                else -> {
                    // 字母和数字统一占 10%，不要用 0.09f，
                    // 配合 JustifyContent.CENTER，9个键的行会产生半个身位的偏移
                    lp.flexBasisPercent = 0.1f
                }
            }
        }
    }


}
