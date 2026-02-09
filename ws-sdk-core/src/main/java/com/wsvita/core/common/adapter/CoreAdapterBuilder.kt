package com.wsvita.core.common.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.recycler.IRecyclerItem

class CoreAdapterBuilder : AdapterBuilder<IRecyclerItem, CoreAdapter> {
    private var layoutId: Int
    internal var context: Context
    // 用于存储 DataBinding 的变量 ID，例如 BR.province
    internal var variableId: Int = 0

    // 属性绑定与点击事件回调
    internal var bindHandler: ((binding: ViewDataBinding, item: IRecyclerItem?, position: Int) -> Unit)? = null
    internal var onItemClick: ((root: View, position: Int) -> Unit)? = null

    internal var hasEmpty : Boolean = true;

    constructor(context: Context, layoutId: Int) : super(context) {
        this.context = context
        this.layoutId = layoutId
    }

    /**
     * 设置数据绑定逻辑
     */
    fun onBind(handler: (binding: ViewDataBinding, item: IRecyclerItem?, position: Int) -> Unit): CoreAdapterBuilder {
        this.bindHandler = handler
        return this
    }

    /**
     * 设置 DataBinding 的变量 ID (如 BR.province)
     */
    fun setVariableId(variableId: Int): CoreAdapterBuilder {
        this.variableId = variableId
        return this
    }

    fun hasEmpty(hasEmpty : Boolean): CoreAdapterBuilder {
        this.hasEmpty = hasEmpty;
        return this;
    }

    /**
     * 设置点击事件逻辑
     */
    fun onItemClick(onItemClick: ((root: View, position: Int) -> Unit)): CoreAdapterBuilder {
        this.onItemClick = onItemClick
        return this
    }

    fun getLayoutId(): Int {
        return this.layoutId
    }

    /**
     * 实现基类的抽象方法，返回具体的 CoreAdapter 实例 [cite: 2026-01-16]
     */
    override fun createAdapter(): CoreAdapter {
        return CoreAdapter(this)
    }
}
