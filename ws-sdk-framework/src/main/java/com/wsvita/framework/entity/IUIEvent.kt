package com.wsvita.framework.entity

import androidx.annotation.StringRes

/**
 * 核心 UI 事件定义 - 独立于 ViewModel 之外
 */
sealed class IUIEvent {
    data class ShowLoading(val msg: String? = null) : IUIEvent()
    object HideLoading : IUIEvent()
    data class ShowToast(val message: String) : IUIEvent()
    data class ShowResToast(@StringRes val resId: Int) : IUIEvent()

    // 预留组件化常用的导航事件
    data class Navigate(val path: String, val params: Map<String, Any>? = null) : IUIEvent()
    object Back : IUIEvent()
}
