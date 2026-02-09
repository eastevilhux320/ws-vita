package com.wsvita.framework.local

import com.wsvita.framework.utils.SLog
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseManager {
    companion object{
        private const val TAG = "WSF_Manager_BaseManager=>";
    }
    // 使用 AtomicBoolean 确保多线程环境下的原子性
    private val isInitialized = AtomicBoolean(false)

    /**
     * 初始化入口
     */
    open fun init() {
        SLog.d(TAG,"init invoke")
        if (isInitialized.compareAndSet(false, true)) {
            onInit()
        }
    }

    /**
     * 子类实现具体的初始化业务
     */
    protected abstract fun onInit()

    /**
     * 校验初始化状态，未初始化则抛出异常
     */
    protected fun checkInit() {
        if (!isInitialized.get()) {
            throw IllegalStateException("${this.javaClass.simpleName}: has not been initialized. Please call init() first.")
        }
    }

    /**
     * 获取初始化状态
     */
    fun isInit(): Boolean = isInitialized.get()

}
