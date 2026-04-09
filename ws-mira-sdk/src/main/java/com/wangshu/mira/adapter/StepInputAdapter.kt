package com.wangshu.mira.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.wangshu.mira.R
import com.wangshu.mira.databinding.RecyclerItemMiraStepInputBinding
import com.wangshu.mira.entity.StepInputEntity
import com.wangshu.mira.entity.SubmitRecordEntity
import com.wangshu.mira.entity.TaskStepInputItemEntity
import com.wsvita.core.common.adapter.AppAdapter

class StepInputAdapter : AppAdapter<StepInputEntity>{

    private var inputItemList : MutableList<TaskStepInputItemEntity>;

    constructor(context: Context, dataList : MutableList<StepInputEntity>?) : super(context, dataList){
        updateFooterState(FooterState.HIDE);
        inputItemList = mutableListOf();
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_mira_step_input;
    }

    override fun hasFooter(): Boolean {
        return false;
    }

    override fun hasHeader(): Boolean {
        return false;
    }

    override fun hasEmpty(): Boolean {
        return false;
    }

    override fun onBindItemData(binding: ViewDataBinding, item: StepInputEntity, position: Int) {
        super.onBindItemData(binding, item, position)
        if(binding is RecyclerItemMiraStepInputBinding){
            binding.editMiraStepInput.setHint(item.hintText);
            binding.tvStepInputLabel.setText(item.label);
            binding.editMiraStepInput.setHintTextColor(binding.root.context.getColor(com.wsvita.ui.R.color.base_text_hint))

            if(!item.localInputText.isNullOrEmpty()){
                binding.editMiraStepInput.setText(item.localInputText);
            }

            val i = TaskStepInputItemEntity();
            i.inputData = item;
            i.edit = binding.editMiraStepInput;
            inputItemList.add(i);
        }
    }

    fun getInputList(): MutableList<TaskStepInputItemEntity>? {
        inputItemList.forEach {
            it.localInput = it.edit?.text?.toString();
        }
        return inputItemList;
    }

    override fun setList(newData: MutableList<StepInputEntity>?) {
        if(inputItemList != null){
            inputItemList.clear();
        }
        super.setList(newData)
    }
}
