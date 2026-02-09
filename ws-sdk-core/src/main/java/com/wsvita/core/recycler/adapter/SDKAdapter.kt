package com.wsvita.core.recycler.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 优化版 SDKAdapter (支持可空数据源)
 * 职责：处理异步数据比对、局部刷新逻辑以及协程生命周期管理。
 */
abstract class SDKAdapter<T : Any> : RecyclerAdapter {

    constructor(context: Context) : super(context) {
        SLog.d(TAG,"SDKAdapter")
    }

    // 核心数据源，允许为 null
    protected var dataList: MutableList<T>? = null

    // 协程作用域，用于管理后台计算任务
    private var adapterScope: CoroutineScope? = null

    // 版本计数器，防止并发任务覆盖
    private val mGeneration = AtomicInteger()

    /* ===================================================== */
    /* ================= 数据源安全访问 =================== */
    /* ===================================================== */

    override fun getItemCount(): Int {
        val list = this.dataList
        if (list == null) {
            return 0
        }
        return list.size
    }

    open fun getItem(position: Int): T? {
        val list = this.dataList
        if (list == null) {
            return null
        }
        return list.getOrNull(position)
    }

    override fun isEmpty(): Boolean {
        val count = this.getItemCount()
        if (count <= 0) {
            return true
        }
        return false
    }

    /* ===================================================== */
    /* ================= 异步 Diff 更新 =================== */
    /* ===================================================== */

    /**
     * 异步提交列表数据
     */
    open fun submitList(newList: List<T>?) {
        val runGeneration = this.mGeneration.incrementAndGet()
        val targetList = newList ?: emptyList()

        // 安全获取旧列表快照
        val currentList = this.dataList
        val oldList = if (currentList != null) {
            currentList.toList()
        } else {
            emptyList()
        }

        if (targetList.isEmpty() && oldList.isEmpty()) {
            return
        }

        this.getSafeScope().launch {
            val diffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return oldList.size
                    }

                    override fun getNewListSize(): Int {
                        return targetList.size
                    }

                    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                        return this@SDKAdapter.areItemsTheSame(oldList[oldPos], targetList[newPos])
                    }

                    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                        return this@SDKAdapter.areContentsTheSame(oldList[oldPos], targetList[newPos])
                    }

                    override fun getChangePayload(oldPos: Int, newPos: Int): Any? {
                        return this@SDKAdapter.getChangePayload(oldList[oldPos], targetList[newPos])
                    }
                })
            }

            if (runGeneration != this@SDKAdapter.mGeneration.get()) {
                return@launch
            }

            // 数据同步：如果 dataList 为空则创建，否则清理并添加
            val currentData = this@SDKAdapter.dataList
            if (currentData == null) {
                this@SDKAdapter.dataList = targetList.toMutableList()
            } else {
                currentData.clear()
                currentData.addAll(targetList)
            }

            this@SDKAdapter.onDataPreUpdated(oldList, targetList)

            diffResult.dispatchUpdatesTo(this@SDKAdapter)
        }
    }

    protected open fun onDataPreUpdated(oldList: List<T>, newList: List<T>) {
        // 子类实现
    }

    /* ===================================================== */
    /* ================= 绑定与刷新协议 ==================== */
    /* ===================================================== */

    override fun bindVariables(binding: ViewDataBinding, position: Int) {
        val item = this.getItem(position)
        this.onBindItem(binding, item, position)
    }

    override fun bindVariables(
        binding: ViewDataBinding,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            this.bindVariables(binding, position)
        } else {
            val item = this.getItem(position)
            this.onBindItemPayload(binding, item, position, payloads)
        }
    }

    protected open fun getChangePayload(oldItem: T, newItem: T): Any? {
        return null
    }

    protected open fun onBindItemPayload(
        binding: ViewDataBinding,
        item: T?,
        position: Int,
        payloads: List<Any>
    ) {
        this.onBindItem(binding, item, position)
    }

    protected abstract fun onBindItem(binding: ViewDataBinding, item: T?, position: Int)

    /* ===================================================== */
    /* ================= 生命周期与工具 =================== */
    /* ===================================================== */

    protected abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    protected open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem == newItem) {
            return true
        }
        return false
    }

    private fun getSafeScope(): CoroutineScope {
        val currentScope = this.adapterScope
        if (currentScope != null) {
            return currentScope
        }
        val newScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        this.adapterScope = newScope
        return newScope
    }

    override fun onAdapterDetached(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        super.onAdapterDetached(recyclerView)
        this.adapterScope?.let {
            it.cancel()
        }
        this.adapterScope = null
        this.mGeneration.incrementAndGet()
    }

    /**
     * 更新全量数据源 (修改名称以避开 JVM 签名冲突)
     */
    open fun setList(newData: MutableList<T>?) {
        this.mGeneration.incrementAndGet()
        this.dataList = newData
    }

    fun getItemData(position: Int) : T? {
        return dataList?.let {
            it.get(position);
        }?:let {
            null;
        }
    }

    companion object{
        private const val TAG = "WSV_SDK_SDKAdapter=>"
    }
}
