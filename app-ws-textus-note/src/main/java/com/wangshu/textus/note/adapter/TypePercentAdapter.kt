package com.wangshu.textus.note.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.wangshu.textus.note.R
import com.wangshu.textus.note.databinding.RvItemBillTypePercentBinding
import com.wangshu.textus.note.entity.bill.BillTypePercentEntity
import com.wsvita.core.common.adapter.AppAdapter

class TypePercentAdapter : AppAdapter<BillTypePercentEntity>{

    constructor(context: Context,dataList: MutableList<BillTypePercentEntity>?) : super(context,dataList){

    }

    override fun isUsedAdapterLayout(): Boolean {
        return true;
    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_billtype
    }

    override fun onBindItem(dataBinding: ViewDataBinding, item: BillTypePercentEntity?, position: Int) {
        super.onBindItem(dataBinding, item, position)
        if(dataBinding is RvItemBillTypePercentBinding){
            val p = item?.percent?.toInt();
            //账单类型，1-支出，2-收入
            if(item?.billType == 1){
                dataBinding.barTypeExpenditure.visibility = View.VISIBLE;
                dataBinding.barTypeIncome.visibility = View.GONE;
                if(p != null && p < 1){
                    dataBinding.barTypeExpenditure.setProgress(1);
                }else{
                    dataBinding.barTypeExpenditure.setProgress(p?:0);
                }
            }else{
                dataBinding.barTypeExpenditure.visibility = View.GONE;
                dataBinding.barTypeIncome.visibility = View.VISIBLE;
                if(p != null && p < 1){
                    dataBinding.barTypeIncome.setProgress(1);
                }else{
                    dataBinding.barTypeIncome.setProgress(p?:0);
                }
            }
        }
    }
}
