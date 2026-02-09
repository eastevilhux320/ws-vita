package com.wsvita.core.banner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.wsvita.framework.utils.SLog
import com.youth.banner.adapter.BannerAdapter

abstract class BaseBannerAdapter<T : IBanner,D : ViewDataBinding> : BannerAdapter<T, BaseBannerAdapter.BannerViewHolder<D>> {

    companion object{
        private const val TAG = "WSVita_App_BaseBannerAdapter=>"
    }

    /**
     * 显式构造方法
     */
    constructor(dataList: List<T>) : super(dataList) {

    }

    constructor() : super(null) {

    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder<D> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: D = DataBindingUtil.inflate(layoutInflater, layoutId(), parent, false)
        // 显式调用 ViewHolder 构造
        return BannerViewHolder(binding)
    }

    override fun onBindView(holder: BannerViewHolder<D>, data: T, position: Int, size: Int) {
        // 显式设置 DataBinding 变量
        setBean(data,holder.dataBinding);
        holder.dataBinding.executePendingBindings()
    }


    abstract fun layoutId() : Int;

    open fun setBean(entity : T,dataBinding : D){
        SLog.d(TAG,"setBean");
    }

    /**
     * 显式定义的 ViewHolder
     */
    class BannerViewHolder<D : ViewDataBinding> : RecyclerView.ViewHolder {
        val dataBinding: D;

        /**
         * 显式构造方法，禁止隐式初始化
         * @param binding 传入 DataBinding 实例
         */
        constructor(binding: D) : super(binding.root) {
            this.dataBinding = binding
        }
    }
}
