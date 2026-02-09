package com.wsvita.core.common.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.BR
import com.wsvita.core.R
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.framework.utils.SLog

/**
 * 最终完整版 AppAdapter
 * * 核心设计：
 * 1. 模式化切换：通过 isPureListData 联动 Header/Footer/Empty。
 * 2. 状态机增强：FooterState 现在持有原始 Int 状态值。
 * 3. 规范化编码：强制使用大括号，禁止单行逻辑。
 * 4. 自动 Diff：默认提供基于 IRecyclerItem 的对比逻辑。
 */
abstract class AppAdapter<T : IRecyclerItem> : VitaAdapter<T> {

    protected var itemClick : ((item : T?)->Unit)? = null;

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context,dataList : MutableList<T>?) : super(context,dataList){

    }

    /**
     * 定义 Footer 状态机，持有 Int 类型的状态值
     */
    enum class FooterState(val state: Int) {
        LOADING(1),
        ERROR(2),
        NO_MORE(3),
        HIDE(0)
    }

    private var currentFooterState: FooterState = FooterState.HIDE

    /* ===================================================== */
    /* ================= 模式化配置 (核心) ================= */
    /* ===================================================== */

    /**
     * 标识是否为“纯列表数据”模式
     * 默认开启。开启后：无 Header，无 Footer，有 Empty 布局。
     */
    protected open fun isPureListData(): Boolean {
        return true
    }

    override fun hasHeader(): Boolean {
        if (this.isPureListData()) {
            return false
        }
        return false
    }

    override fun hasFooter(): Boolean {
        if (this.isPureListData()) {
            return false
        }
        return when (this.currentFooterState) {
            FooterState.HIDE -> {
                false
            }
            else -> {
                true
            }
        }
    }

    override fun hasEmpty(): Boolean {
        // 纯数据模式或普通模式下，默认均支持空状态
        if (this.isPureListData()) {
            return true
        }
        return true
    }

    /* ===================================================== */
    /* ================= 状态管理与刷新 =================== */
    /* ===================================================== */

    /**
     * 更新 Footer 状态并执行局部刷新
     */
    fun updateFooterState(state: FooterState) {
        val oldState = this.currentFooterState
        if (oldState != state) {
            this.currentFooterState = state
            // 获取 Footer 在列表中的真实位置
            val footerPos = this.itemCount - 1
            if (footerPos >= 0) {
                this.notifyItemChanged(footerPos)
            } else {
                this.notifyDataChangedSafe()
            }
        }
    }

    /**
     * 获取当前 Footer 的 Int 状态值
     */
    protected fun getFooterStateValue(): Int {
        return this.currentFooterState.state
    }

    /* ===================================================== */
    /* ================= 资源 ID 默认实现 =================== */
    /* ===================================================== */

    override fun getHeaderLayoutId(): Int {
        return R.layout.rv_foot_sdkcore_header;
    }

    override fun getFooterLayoutId(): Int {
        return when (this.currentFooterState) {
            FooterState.LOADING -> {
                R.layout.rv_foot_sdkcore_loading
            }
            FooterState.ERROR -> {
                R.layout.rv_foot_sdkcore_error
            }
            FooterState.NO_MORE -> {
                R.layout.rv_foot_sdkcore_footer
            }
            else -> {
                R.layout.rv_foot_sdkcore_footer;
            }
        }
    }

    override fun getEmptyLayoutId(): Int {
        return R.layout.rv_foot_sdkcore_empty
    }

    /* ===================================================== */
    /* ================= 默认对比协议 (Diff) ================ */
    /* ===================================================== */

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem.recyclerItemId() == newItem.recyclerItemId()) {
            return true
        }
        return false
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem == newItem) {
            return true
        }
        return false
    }

    /* ===================================================== */
    /* ================= 核心绑定逻辑 ====================== */
    /* ===================================================== */

    override fun onBindItem(binding: ViewDataBinding, item: T?, position: Int) {
        SLog.i(TAG,"onBindItem,position:${position}");
        if (item == null) {
            return
        }
        // 自动化绑定组件化协议变量
        binding.setVariable(BR.recyclerItem, item)
        binding.setVariable(BR.position, position)

        // 执行业务层全量绑定
        this.onBindItemData(binding, item, position)
        binding.executePendingBindings()
    }

    override fun onBindItemPayload(
        binding: ViewDataBinding,
        item: T?,
        position: Int,
        payloads: List<Any>
    ) {
        SLog.i(TAG,"onBindItemPayload,position:${position}");
        if (item == null) {
            return
        }
        // 重新注入变量以触发 DataBinding 内部 Diff
        binding.setVariable(BR.recyclerItem, item)

        // 执行业务层局部刷新
        this.onBindItemDataPayload(binding, item, position, payloads)

        binding.executePendingBindings()
    }

    override fun onBindingView(root: View, item: T?, position: Int) {
        super.onBindingView(root, item, position)
        /*root?.setOnClickListener {
            itemClick?.invoke(item);
        }*/
    }

    override fun onBindFooter(binding: ViewDataBinding) {
        SLog.i(TAG,"onBindFooter");
        if (this.currentFooterState == FooterState.ERROR) {
            binding.root.setOnClickListener {
                this.onRetryClick()
            }
        } else {
            binding.root.setOnClickListener(null)
        }
        binding.executePendingBindings()
    }

    /* ===================================================== */
    /* ================= 业务扩展钩子 (Open) ================= */
    /* ===================================================== */

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        SLog.i(TAG,"onBindViewHolder,posotion:${position}");
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        SLog.i(TAG,"onBindViewHolder,posotion:${position},payloads:${payloads}");
    }

    /**
     * 业务数据全量绑定（子类按需重写）
     */
    protected open fun onBindItemData(binding: ViewDataBinding, item: T, position: Int) {
        // 默认空实现
        SLog.i(TAG,"onBindItemData,posotion:${position}");
    }

    /**
     * 业务数据局部刷新（子类按需重写）
     */
    protected open fun onBindItemDataPayload(
        binding: ViewDataBinding,
        item: T,
        position: Int,
        payloads: List<Any>
    ) {
        // 默认回退到全量绑定
        this.onBindItemData(binding, item, position)
        SLog.i(TAG,"onBindItemDataPayload,posotion:${position}");
    }

    /**
     * 底部重试点击回调
     */
    protected open fun onRetryClick() {
        // 子类实现加载更多重试逻辑
        SLog.i(TAG,"onRetryClick");
    }

    /**
     * 获取布局资源 ID。
     * * [逻辑分发说明]：
     * * 当 [isUsedAdapterLayout] 返回 true 时：
     * 此方法由子类实现，返回整个列表通用的单一布局资源 ID（如 R.layout.item_common）。
     * * 当 [isUsedAdapterLayout] 返回 false 时：
     * 此方法作为模板方法，在 [getItemViewType] 中被调用。
     * 此时参数 [viewType] 实际上是由 [IRecyclerItem.recyclerItemType] 提供的具体布局 ID。
     * 默认实现直接返回 [viewType]，即将业务类型直接映射为布局资源。
     *
     * create by Eastevil at 2026/1/15 13:31
     * @author Eastevil
     *
     * @param viewType 视图类型。在多布局模式下，通常对应具体的 Layout 资源 ID。
     * @return 最终用于加载的布局资源 ID。
     */
    override fun getLayoutId(): Int {
        return 0;
    }

    fun setOnItemClick(onItemClick : ((item : T?)->Unit)){
        this.itemClick = onItemClick;
    }

    companion object{
        private const val TAG = "WSV_Adapter_AppAdapter=>";
    }
}
