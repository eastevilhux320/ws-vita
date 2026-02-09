package com.wsvita.framework.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
// 严格基于你反馈的包名路径：没有 com.uber 前缀
import autodispose2.AutoDispose
import autodispose2.AutoDisposeConverter
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider

/**
 * 自动管理 RxJava 订阅生命周期的工具类
 * 适配 RxJava 3 + AndroidX Lifecycle
 */
object AutoDisposeUtil {

    /**
     * 将 RxJava 流绑定到 LifecycleOwner
     * 默认在 ON_DESTROY 时自动断开
     */
    fun <T : Any> bindLifecycle(owner: LifecycleOwner): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(owner, Lifecycle.Event.ON_DESTROY)
        )
    }
}
