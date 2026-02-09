package com.wsvita.core.common.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.framework.utils.SLog

class CoreAdapter : BuilderAdapter<IRecyclerItem> {
    private lateinit var builder: CoreAdapterBuilder

    // 显式构造方法
    constructor(builder: CoreAdapterBuilder) : super(builder.context) {
        this.builder = builder
    }

    override fun getLayoutId(): Int {
        return builder.getLayoutId();
    }

    override fun onBindItem(binding: ViewDataBinding, item: IRecyclerItem?, position: Int) {
        // 1. 如果设置了 variableId，则进行自动绑定
        if (builder.variableId != 0 && item != null) {
            try {
                binding.setVariable(builder.variableId, item)
            }catch (e : Exception){
                SLog.e(TAG,"bind item variableId error");
            }
        }
        // 2. 调用基类逻辑 (执行默认的 BR.recyclerItem 绑定)
        super.onBindItem(binding, item, position)

        // 3. 执行 Builder 注入的个性化逻辑
        builder.bindHandler?.invoke(binding, item, position)
    }

    override fun onBindingView(root: View, item: IRecyclerItem?, position: Int) {
        super.onBindingView(root, item, position)
        root.setOnClickListener {
            builder.onItemClick?.invoke(it, position)
        }
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    override fun hasEmpty(): Boolean {
        return builder.hasEmpty;
    }

    override fun getEmptyLayoutId(): Int {
        return super.getEmptyLayoutId()
    }

    companion object{
        private const val TAG = "WSV_Adapter_CoreAdapter=>";
    }
}
