package com.wangshu.mira.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wangshu.mira.R
import com.wangshu.mira.databinding.RecyclerItemMiraTaskBinding
import com.wangshu.mira.entity.TaskEntity
import com.wangshu.mira.entity.enums.OperationCode
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.framework.GlideApp
import okhttp3.internal.concurrent.Task

class TaskAdapter : AppAdapter<TaskEntity> {
    private var onTaskClick : ((task : TaskEntity)->Unit)? = null;

    constructor(context: Context,dataList : MutableList<TaskEntity>?) : super(context, dataList){
        updateFooterState(FooterState.HIDE);
    }

    override fun isUsedAdapterLayout(): Boolean {
        return super.isUsedAdapterLayout()
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_mira_task;
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    override fun getEmptyLayoutId(): Int {
        return R.layout.recycler_item_mira_loading_error;
    }

    override fun getFooterLayoutId(): Int {
        return R.layout.recycler_item_mira_loading_error;
    }

    override fun onBindingView(root: View, item: TaskEntity?, position: Int) {
        super.onBindingView(root, item, position)
        root.setOnClickListener {
            item?.let { it1 -> onTaskClick?.invoke(it1) };
        }
    }

    override fun onBindItemData(binding: ViewDataBinding, item: TaskEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        if(binding is RecyclerItemMiraTaskBinding){
            GlideApp.with(binding.ivMiraTaskIcon)
                .load(item.iconUrl)
                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .into(binding.ivMiraTaskIcon);

            binding.tvMiraTaskTitle.setText(item.title);
            if(item.haveShowPrice){
                binding.tvMiraTaskPrice.visibility = View.VISIBLE;
                binding.tvMiraTaskPrice.setText(item.showPrice);
            }else{
                binding.tvMiraTaskPrice.visibility = View.GONE;
            }

            //app
            GlideApp.with(binding.ivMiraTaskAppIcon)
                .load(item.targetApp?.appIcon)
                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .into(binding.ivMiraTaskAppIcon)
            binding.tvMiraTaskAppName.setText(item.targetApp?.appName);

            val operationText = OperationCode.from(item.operationCode).getName(binding.root.context);
            binding.tvMiraTaskOperation.setText(operationText);
        }
    }

    fun onTaskClick(onTaskClick : ((task : TaskEntity)->Unit)){
        this.onTaskClick = onTaskClick;
    }
}
