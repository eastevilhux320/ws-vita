package com.wsvita.core.common.adapter

import android.content.Context
import com.wsvita.core.recycler.IRecyclerItem

/**
 * Adapter 构建器基类
 */
abstract class AdapterBuilder<I : IRecyclerItem, A : BuilderAdapter<I>> {

    // 内部持有的 Adapter 实例
    protected lateinit var adapter: A

    // 显式构造方法
    constructor(context: Context) {
        // 构造函数内不执行业务逻辑，防止子类属性未初始化导致的 NPE
    }

    /**
     * 内部工厂方法：由子类实现具体的创建逻辑
     * 设为 protected，不对外暴露
     */
    protected abstract fun createAdapter(): A

    /**
     * 唯一对外公开的构建入口
     * 确保在调用此方法时，Builder 的所有属性（context, layoutId 等）已赋值完毕
     */
    fun build(): A {
        if (!::adapter.isInitialized) {
            this.adapter = createAdapter()
        }
        return this.adapter
    }
}
