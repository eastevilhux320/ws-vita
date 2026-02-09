package com.wsvita.core.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wsvita.core.R
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.core.databinding.RecyclerSdkitemPopupBinding
import com.wsvita.core.entity.domain.SLItem
import com.wsvita.core.recycler.presenter.impl.SLItemPresenterImpl
import com.wsvita.framework.GlideApp

class ListPopupAdapter : AppAdapter<SLItem> {
    private var onSLItemClick : ((item : SLItem)->Unit)? = null;
    private var isMultiSelect : Boolean = false;

    private var seletedList : MutableList<SLItem>? = null;

    constructor(context: Context) : super(context){

    }

    constructor(context: Context,datList : MutableList<SLItem>?) : super(context,datList){

    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_sdkitem_popup
    }

    fun onSLItemClick(onSLItemClick : ((item : SLItem)->Unit)){
        this.onSLItemClick = onSLItemClick;
    }

    override fun onBindItem(binding: ViewDataBinding, item: SLItem?, position: Int) {
        super.onBindItem(binding, item, position)
        item?.let {
            if(binding is RecyclerSdkitemPopupBinding){
                binding.slPresenter = presenter;
                //icon处理
                if(it.haveIcon){
                    binding.ivSlitemIcon.visibility = View.VISIBLE;
                    if(it.iconResId > 0){
                        binding.ivSlitemIcon.setImageResource(it.iconResId);
                    }else{
                        GlideApp.with(binding.ivSlitemIcon)
                            .load(it.iconUrl)
                            .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                            .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                            .into(binding.ivSlitemIcon);
                    }
                }else{
                    binding.ivSlitemIcon.visibility = View.GONE;
                }
                binding.tvSlitemTitle.setText(it.title);

                if(it.canSeleted){
                    binding.ivSlitemSelected.visibility = View.VISIBLE;
                    if(it.isSelected()){
                        binding.ivSlitemSelected.setImageResource(it.selResId)
                    }else{
                        if(it.norSeletedGone){
                            GlideApp.with(binding.ivSlitemIcon)
                                .load(it.norResId)
                                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                                .into(binding.ivSlitemIcon);
                        }else{
                            binding.ivSlitemSelected.visibility = View.GONE;
                        }
                    }
                }else{
                    binding.ivSlitemSelected.visibility = View.GONE;
                }
            }
        }
    }

    fun isMultiSelect(isMultiSelect : Boolean){
        this.isMultiSelect = isMultiSelect;
    }

    private val presenter = object : SLItemPresenterImpl() {

        override fun onEntityClick(entity: SLItem, position: Int) {
            super.onEntityClick(entity, position)
            if(isMultiSelect){
                //支持多选
                seletedList?.let {
                    //已经选择过，需要判断本次选中的item是否在已经选中过的列表中，
                    //如果在，则表示本次取消选择
                    //如果不再，则需要进行选中
                    if(it.contains(entity)){
                        // 在列表中，说明本次是“取消选择”
                        dataList?.get(position)?.itemSelect = false;
                        it.remove(entity);
                    }else{
                        // 不在列表中，说明本次是“进行选中”
                        dataList?.get(position)?.itemSelect = true;
                        it.add(entity);
                    }
                    // 3. 刷新 UI
                    notifyItemChanged(position)
                }?:let {
                    //还未曾选中过
                    seletedList = mutableListOf();
                    seletedList?.add(entity);
                    dataList?.get(position)?.itemSelect = true;
                    //更新
                    notifyItemChanged(position)
                }
            }else{
                //不支持多选，
                onSLItemClick?.invoke(entity);
            }
        }
    }
}
