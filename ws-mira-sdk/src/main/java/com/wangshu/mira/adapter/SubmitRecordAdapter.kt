package com.wangshu.mira.adapter

import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.databinding.ViewDataBinding
import com.wangshu.mira.R
import com.wangshu.mira.databinding.RecyclerItemMiraSubmitRecordAuditErrorBinding
import com.wangshu.mira.databinding.RecyclerItemMiraSubmitRecordBinding
import com.wangshu.mira.entity.SubmitRecordEntity
import com.wangshu.mira.entity.enums.SubmitAuditState
import com.wangshu.mira.ext.MiraViewExt.getColor
import com.wsvita.core.common.adapter.AppAdapter
import com.wsvita.framework.GlideApp
import ext.TimeExt.format
import java.util.Calendar
import java.util.Date

class SubmitRecordAdapter : AppAdapter<SubmitRecordEntity>{
    private var timeFormat : String = "yyyy-MM-dd";

    constructor(context: Context, dataList : MutableList<SubmitRecordEntity>?) : super(context, dataList){
        timeFormat = context.getString(com.wsvita.core.R.string.ws_date_time_format_default);
    }

    override fun isUsedAdapterLayout(): Boolean {
        return false;
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

    override fun onBindItem(binding: ViewDataBinding, item: SubmitRecordEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        item?.let {s->
            if(binding is RecyclerItemMiraSubmitRecordBinding){
                binding.submitRecord = item
                bindAuditStateColor(s,binding.tvMiraStatus);
            }else if(binding is RecyclerItemMiraSubmitRecordAuditErrorBinding){
                binding.submitRecord = item
                bindAuditStateColor(s,binding.tvMiraStatus);
            }
        }
    }

    override fun getEmptyLayoutId(): Int {
        return R.layout.recycler_item_mira_loading_error;
    }

    override fun getFooterLayoutId(): Int {
        return R.layout.recycler_item_mira_loading_error;
    }

    private fun bindAuditStateColor(item : SubmitRecordEntity,tvMiraStatus : TextView){
        tvMiraStatus.setText(item.auditStateText);
        val state = item.submitAuditState;
        when(state){
            SubmitAuditState.WAIT-> tvMiraStatus.setTextColor(tvMiraStatus.getColor(R.color.mira_submit_audit_state_1));
            SubmitAuditState.AUDITING-> tvMiraStatus.setTextColor(tvMiraStatus.getColor(R.color.mira_submit_audit_state_2));
            SubmitAuditState.PASS-> tvMiraStatus.setTextColor(tvMiraStatus.getColor(R.color.mira_submit_audit_state_3));
            SubmitAuditState.REJECT-> tvMiraStatus.setTextColor(tvMiraStatus.getColor(R.color.mira_submit_audit_state_4));
            else-> tvMiraStatus.setTextColor(tvMiraStatus.getColor(com.wsvita.ui.R.color.base_app_text));
        }
    }
}
