package com.wangshu.mira.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.widget.NestedScrollView

class FixedNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    override fun requestChildFocus(child: View?, focused: View?) {
        if (focused != null && isViewDescendant(focused)) {
            // 只有当 View 确实属于当前层级时，才允许执行焦点请求和坐标计算
            super.requestChildFocus(child, focused)
        }
    }

    private fun isViewDescendant(view: View): Boolean {
        var parent = view.parent
        while (parent != null) {
            if (parent === this) return true
            parent = parent.parent
        }
        return false
    }
}
