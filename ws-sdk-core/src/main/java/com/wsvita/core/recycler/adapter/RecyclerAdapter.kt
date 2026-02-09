package com.wsvita.core.recycler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.wsvita.framework.utils.SLog

/**
 * RecyclerView Adapter 顶层抽象基类（支持局部刷新与 DataBinding）
 *
 * 优化说明：
 * 1. 增加了支持 Payloads 的 bindVariables 接口。
 * 2. 重写了三参数的 onBindViewHolder 以支持局部刷新。
 * 3. 严格遵循多行编码规范，所有逻辑块均包含大括号。
 */
abstract class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.BindingViewHolder> {

    protected var isInitialized: Boolean = false
        private set

    private lateinit var layoutInflater: LayoutInflater

    constructor(context: Context) : super() {
        SLog.d(TAG,"RecyclerAdapter");
        this.layoutInflater = LayoutInflater.from(context)
        this.isInitialized = true
        this.onAdapterInit()
    }

    protected open fun onAdapterInit() {
        // 默认空实现
    }

    @LayoutRes
    protected abstract fun getLayoutId(viewType: Int): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            getLayoutId(viewType),
            parent,
            false
        )
        return BindingViewHolder(binding)
    }

    /* ===================================================== */
    /* ================= 核心绑定逻辑 (支持局部刷新) ============ */
    /* ===================================================== */

    /**
     * 标准的双参数绑定（全量刷新）
     */
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        this.bindVariables(holder.binding, position)
        holder.binding.executePendingBindings()
    }

    /**
     * 标准的三参数绑定（局部刷新入口）
     */
    override fun onBindViewHolder(
        holder: BindingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            // 如果没有 payload，执行默认的全量刷新逻辑
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // 如果有 payload，执行局部变量绑定
            this.bindVariables(holder.binding, position, payloads)
            holder.binding.executePendingBindings()
        }
    }

    /**
     * 抽象方法：全量绑定变量
     */
    protected abstract fun bindVariables(
        binding: ViewDataBinding,
        position: Int
    )

    /**
     * 可重写方法：局部刷新绑定
     * 默认实现为直接调用全量绑定，子类可按需优化
     */
    protected open fun bindVariables(
        binding: ViewDataBinding,
        position: Int,
        payloads: List<Any>
    ) {
        this.bindVariables(binding, position)
    }

    /* ===================================================== */
    /* ================= ViewType 与 生命周期 ================ */
    /* ===================================================== */

    override fun getItemViewType(position: Int): Int {
        return DEFAULT_VIEW_TYPE
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.onAdapterAttached(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.onAdapterDetached(recyclerView)
    }

    protected open fun onAdapterAttached(recyclerView: RecyclerView) {
        // 子类实现
    }

    protected open fun onAdapterDetached(recyclerView: RecyclerView) {
        // 子类实现
    }

    /* ===================================================== */
    /* ================= 刷新与状态规则 =================== */
    /* ===================================================== */

    open fun notifyDataChangedSafe() {
        if (!this.isInitialized) {
            return
        }
        this.notifyDataSetChanged()
    }

    open fun isEmpty(): Boolean {
        return this.itemCount <= 0
    }

    open fun canLoadMore(): Boolean {
        return false
    }

    /* ===================================================== */
    /* ================= ViewHolder 定义 =================== */
    /* ===================================================== */

    open class BindingViewHolder : RecyclerView.ViewHolder {
        var binding: ViewDataBinding

        constructor(binding: ViewDataBinding) : super(binding.root) {
            this.binding = binding
        }
    }

    companion object {
        private const val TAG = "WSV_SDK_SDKAdapter=>"
        const val DEFAULT_VIEW_TYPE = 0
    }
}
