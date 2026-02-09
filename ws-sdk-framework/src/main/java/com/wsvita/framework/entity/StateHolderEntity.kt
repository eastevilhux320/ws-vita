package com.wsvita.framework.entity

import androidx.lifecycle.MutableLiveData

class StateHolderEntity<T> {

    // 内部使用 LiveData 负责分发
    private val _liveData = MutableLiveData<T>()

    // 暴露给 View 的观察者
    val liveData get() = _liveData

    // Model 层调用此方法更新数据
    fun postUpdate(value: T) {
        // 如果在后台线程，使用 postValue；如果在主线程，可用 value = ...
        _liveData.postValue(value!!)
    }

    // 获取当前值
    fun getValue(): T? = _liveData.value
}
