package com.wsvita.core.common.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.R
import com.wsvita.core.recycler.IRecyclerItem
import com.wsvita.core.recycler.adapter.SDKAdapter
import com.wsvita.framework.utils.SLog

abstract class VitaAdapter<T : IRecyclerItem> : SDKAdapter<T> {

    companion object {
        private const val TAG = "WSV_Adapter_VitaAdapter=>"
        const val VIEW_TYPE_HEADER = 0x10000001
        const val VIEW_TYPE_FOOTER = 0x10000002
        const val VIEW_TYPE_EMPTY = 0x10000003
    }

    constructor(context: Context) : super(context) {
        SLog.d(TAG,"VitaAdapter")
    }

    constructor(context: Context,dataList: MutableList<T>?) : super(context) {
        if(dataList != null){
            setList(dataList);
        }
    }


    /**
     * 内部虚拟索引表
     * 使用 volatile 确保跨线程可见性
     */
    @Volatile
    private var validIndices: List<Int> = emptyList()

    /* ===================================================== */
    /* ================= 数据同步拦截 (核心优化) ============= */
    /* ===================================================== */

    /**
     * 重写 SDKAdapter 的钩子函数
     * 此方法在异步线程执行，且在 dispatchUpdatesTo 之前调用
     */
    override fun onDataPreUpdated(oldList: List<T>, newList: List<T>) {
        // 在异步线程预先计算好有效索引映射
        val newValidIndices = mutableListOf<Int>()
        newList.forEachIndexed { index, item ->
            if(isUsedAdapterLayout()){
                newValidIndices.add(index)
            }else{
                if (item.recyclerItemType() > 0) {
                    newValidIndices.add(index)
                }
            }
        }
        // 一次性替换引用，保证线程安全
        validIndices = newValidIndices
    }

    override fun setList(newData: MutableList<T>?) {
        super.setList(newData)
        // 在主线程紧急计算一次，确保同步
        refreshValidMappingSync(newData)
        super.setList(newData)
    }

    private fun refreshValidMappingSync(source: List<T>?) {
        val newValidIndices = mutableListOf<Int>()
        source?.forEachIndexed { index, item ->
            if(isUsedAdapterLayout()){
                newValidIndices.add(index)
            }else{
                if (item.recyclerItemType() > 0) {
                    newValidIndices.add(index)
                }
            }
        }
        validIndices = newValidIndices
    }

    /* ===================================================== */
    /* ================= 索引与数量计算 ===================== */
    /* ===================================================== */

    override fun getItemCount(): Int {
        // 1. 如果有数据，返回 Header + 有效数据 + Footer
        if (validIndices.isNotEmpty()) {
            var count = validIndices.size
            if (hasHeader()) count++
            if (hasFooter()) count++
            return count
        }

        // 2. 如果没数据，判断是否显示 Empty 占位图
        if (isEmptyState()) {
            return 1
        }

        // 3. 既无数据也无 Empty 占位，且有 Header 时只显 Header
        return if (hasHeader()) 1 else 0
    }

    private fun getValidDataIndex(position: Int): Int {
        val dataPosition = if (hasHeader()) position - 1 else position
        if (dataPosition >= 0 && dataPosition < validIndices.size) {
            return validIndices[dataPosition]
        }
        throw IndexOutOfBoundsException("BizcoreAdapter: Invalid virtual index access at $position")
    }

    /* ===================================================== */
    /* ================= 布局与绑定逻辑 ===================== */
    /* ===================================================== */

    override fun getItemViewType(position: Int): Int {
        val isHeader = hasHeader() && position == 0
        if (isHeader) {
            return VIEW_TYPE_HEADER
        }

        if (isEmptyState()) {
            return VIEW_TYPE_EMPTY
        }

        val isFooter = hasFooter() && position == itemCount - 1
        if (isFooter) {
            return VIEW_TYPE_FOOTER
        }

        if(isUsedAdapterLayout()){
            val layoutId = getLayoutId();
            if(layoutId > 0){
                return layoutId;
            }
        }

        val validIndex = getValidDataIndex(position)
        return getItem(validIndex)?.recyclerItemType()
            ?: throw IllegalStateException("Item at $validIndex is null")
    }

    override fun onBindItem(binding: ViewDataBinding, item: T?, position: Int) {
        // 由 AppAdapter 实现具体的变量绑定逻辑

    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val binding = holder.binding
        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> onBindHeader(binding)
            VIEW_TYPE_FOOTER -> onBindFooter(binding)
            VIEW_TYPE_EMPTY -> onBindEmpty(binding)
            else -> {
                val validIndex = getValidDataIndex(position)
                val item = getItem(validIndex)
                onBindItem(binding, item, validIndex)
                onBindingView(binding.root,item,validIndex);
                binding.executePendingBindings()
            }
        }
    }

    /**
     * 是否启用 Header 布局。
     *
     * * [作用]：决定列表顶部是否插入一个特殊类型的视图（如广告位、筛选栏、分类 Tab）。
     * * [使用]：子类重写返回 `true`，并确保 [getHeaderLayoutId] 提供正确的布局资源 ID。
     * * [注意]：
     * 1. 开启后，[getItemCount] 会自动自增 1。
     * 2. 索引偏移：内部 [getValidDataIndex] 已封装偏移逻辑，在 [onBindItem] 中收到的 position 已是正确的业务数据索引。
     *
     * create by Eastevil at 2026/1/21 11:12
     * @author Eastevil
     *
     * @return 是否显示头部。
     * * `true`: 列表第 0 位固定为 Header，不参与业务数据逻辑。
     * * `false`: 列表起始位置即为业务数据项。
     */
    protected open fun hasHeader(): Boolean{
        return false;
    }

    /**
     * 是否启用 Footer 布局。
     *
     * * [作用]：决定列表末尾是否挂载功能性布局（如：加载更多提示、版权说明、底部留白）。
     * * [使用]：默认返回 `true`。若需展示特定 UI，请重写 [getFooterLayoutId]。
     * * [注意]：
     * 1. 触发条件：通常只有在 [validIndices] 不为空时才会渲染 Footer，避免与 Empty 视图冲突。
     * 2. 分页场景：在组件化开发中，Footer 常用于承载“正在加载更多”或“已经到底了”的状态切换。
     *
     * create by Eastevil at 2026/1/21 11:15
     * @author Eastevil
     *
     * @return 是否显示尾部。
     * * `true`: 在所有业务数据项之后，追加一个 Footer 视图。
     * * `false`: 列表在最后一个业务项后直接结束。
     */
    protected open fun hasFooter(): Boolean{
        return true;
    }
    /**
     * 是否启用空状态占位布局 (Empty View)。
     *
     * * [作用]：当业务数据源为空时，提供视觉反馈（如“暂无数据”、“搜索结果为空”）。
     * * [使用]：标准列表建议开启。若页面由外部 Fragment 容器的 StateLayout 管理，可返回 `false`。
     * * [注意]：
     * 1. 优先级：此布局受 [isEmptyState] 逻辑控制，优先级通常低于 Loading 和 Error。
     * 2. 交互设计：空布局通常占据列表全屏，确保 [getEmptyLayoutId] 指向的布局根节点为 `match_parent`。
     *
     * create by Eastevil at 2026/1/21 11:15
     * @author Eastevil
     *
     * @return 是否允许显示缺省页。
     * * `true`: 当数据为空且不处于加载/错误状态时，渲染全屏占位图。
     * * `false`: 数据为空时列表保持空白，或仅展示 Header。
     */
    protected open fun hasEmpty(): Boolean{
        return true;
    }

    protected open fun isEmptyState(): Boolean {
        // 只有当有效业务数据为空，且设置了显示 Empty 布局时才触发
        return validIndices.isEmpty() && hasEmpty()
    }

    protected open fun getHeaderLayoutId(): Int{
        return R.layout.rv_foot_sdkcore_header;
    }

    protected open fun getFooterLayoutId(): Int{
        return R.layout.rv_foot_sdkcore_footer;
    }

    protected open fun getEmptyLayoutId(): Int{
        return R.layout.rv_foot_sdkcore_empty;
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_HEADER -> getHeaderLayoutId()
            VIEW_TYPE_FOOTER -> getFooterLayoutId()
            VIEW_TYPE_EMPTY -> getEmptyLayoutId()
            else -> viewType // 业务 viewType 即为 layoutId
        }
    }

    protected open fun onBindHeader(binding: ViewDataBinding){
        binding.executePendingBindings()
    }
    protected open fun onBindFooter(binding: ViewDataBinding){
        binding.executePendingBindings()
    }
    protected open fun onBindEmpty(binding: ViewDataBinding){
        binding.executePendingBindings()
    }

    protected open fun onBindingView(root : View,item : T?,position : Int){

    }

    /**
     * 判断是否为同一个对象（通常比较唯一 ID）
     */
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        // 逻辑：通过 IRecyclerItem 定义的 recyclerItemId 进行比对
        if (oldItem.recyclerItemId() == newItem.recyclerItemId()) {
            return true
        }
        return false
    }

    /**
     * 判断内容是否完全一致（决定是否需要触发重绘或局部刷新）
     */
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        // 逻辑 1：利用 Kotlin Data Class 的 equals 自动生成的比较（推荐实体类使用 data class）
        if (oldItem.equals(newItem)) {
            return true
        }
        // 逻辑 2：如果不是同一个对象引用，但在业务逻辑上内容一致，也可以返回 true
        return false
    }

    /**
     * 是否使用 Adapter 统一配置的布局 (模式切换开关)。
     * * [策略说明]
     * 1. true (单布局模式): 适用于列表项样式统一的场景。此时直接调用 [getLayoutId] 返回固定布局 ID。
     * 优势：简单高效，业务实体类无需关注 viewType，减少代码耦合。
     * * 2. false (组件化/多布局模式): 适用于复杂列表（如首页、信息流）。此时根据数据项的
     * [IRecyclerItem.recyclerItemType] 动态返回布局 ID。
     * 优势：灵活性高，支持在同一个列表中展示多种完全不同的 UI 组件。
     * @return
     * - true (单布局模式): 列表所有 Item 使用同一个布局 ID。此时系统会调用 [getLayoutId()] (无参版)。
     * 适用简单列表等样式统一的场景。
     *
     * - false (组件化/多布局模式): 列表根据数据项动态决定布局。此时系统会调用 [com.wsvita.core.recycler.IRecyclerItem.recyclerItemType]
     * 适用于首页、混合配置页等需要展示多种不同组件的场景。
     */
    protected open fun isUsedAdapterLayout(): Boolean {
        return true;
    }

    /**
     * 获取布局资源 ID。
     * * [逻辑分发说明]：
     * 1. 当 [isUsedAdapterLayout] 返回 true 时：
     * 此方法由子类实现，返回整个列表通用的单一布局资源 ID（如 R.layout.item_common）。
     * * 2. 当 [isUsedAdapterLayout] 返回 false 时：
     * 此方法作为模板方法，在 [getItemViewType] 中被调用。
     * 此时参数 [viewType] 实际上是由 [IRecyclerItem.recyclerItemType] 提供的具体布局 ID。
     * 默认实现直接返回 [viewType]，即将业务类型直接映射为布局资源。
     * * @param viewType 视图类型。在多布局模式下，通常对应具体的 Layout 资源 ID。
     * @return 最终用于加载的布局资源 ID。
     */
    abstract fun getLayoutId() : Int;

}
