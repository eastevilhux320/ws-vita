package com.wsvita.core.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.BR
import com.wsvita.core.recycler.adapter.SDKAdapter

/**
 * 直接继承 SDKAdapter 以跳过 VitaAdapter 的 IRecyclerItem 约束
 * 适用于简单的字符串列表，不涉及 Header/Footer/虚拟索引映射
 */
abstract class AppStringAdapter : SDKAdapter<String> {

    constructor(context : Context) : super(context) {

    }

    constructor(context: Context,dataList : MutableList<String>?) : super(context) {
        setList(dataList);
    }

    /**
     * 实现 SDKAdapter 要求的变量绑定逻辑
     */
    override fun onBindItem(binding: ViewDataBinding, item: String?, position: Int) {
        if (item == null) {
            return
        }
        // 依然可以使用通用变量名，或者根据 XML 自定义
        binding.setVariable(BR.stringItem, item)
        binding.setVariable(BR.position, position)

        // 执行业务绑定
        onBindStringItem(binding, item, position)
        onBindStringView(binding.root,item,position);
        binding.executePendingBindings()
    }

    /**
     * 业务层钩子
     */
    protected open fun onBindStringItem(binding: ViewDataBinding, item: String, position: Int) {
    }

    protected open fun onBindStringView(root : View,item: String, position: Int){

    }

    /* ===================================================== */
    /* ================= DiffUtil 逻辑实现 ================== */
    /* ===================================================== */

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        // 内容比对
        return oldItem.equals(newItem)
    }
}
