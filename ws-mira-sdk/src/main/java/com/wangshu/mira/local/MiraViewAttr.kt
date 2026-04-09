package com.wangshu.mira.local

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.wangshu.mira.R
import com.wangshu.mira.entity.SubmitRecordEntity
import com.wangshu.mira.entity.enums.SubmitAuditState
import com.wangshu.mira.ext.MiraExt.addDays
import com.wsvita.framework.GlideApp
import ext.TimeExt
import ext.TimeExt.format
import ext.ViewExt.getString
import java.time.Instant
import java.time.temporal.ChronoUnit

object MiraViewAttr {

    @JvmStatic
    @BindingAdapter("miraTime")
    fun setTime(textView: TextView,time : Long?){
        when(textView.id){
            R.id.tv_mira_submit_time->{
                val t = time?:System.currentTimeMillis();
                val text = textView.getString(R.string.mira_submit_create_time,t.format());
                textView.setText(text);
            }
        }
    }

    @JvmStatic
    @BindingAdapter("miraText")
    fun setText(textView: TextView,value : Int?){
        when(textView.id){
            else-> {
                if(value != null && value != 0){
                    textView.setText(value)
                }
            }
        }
    }


    @JvmStatic
    @BindingAdapter("miraAuditState")
    fun setAuditState(textView: TextView,state : Int?){
        val auditState = SubmitAuditState.getByState(state);
        auditState?.let {
            when(it){
                SubmitAuditState.WAIT-> textView.setText(R.string.mira_submit_audit_state_1);
                SubmitAuditState.AUDITING-> textView.setText(R.string.mira_submit_audit_state_2);
                SubmitAuditState.PASS-> textView.setText(R.string.mira_submit_audit_state_3);
                SubmitAuditState.REJECT-> textView.setText(R.string.mira_submit_audit_state_4);
            }
        }?:let {
            textView.setText(com.wsvita.core.R.string.app_unknow);
        }
    }

    @JvmStatic
    @BindingAdapter("miraAuditTime")
    fun setAuditTime(textView: TextView,record : SubmitRecordEntity?){
        when(textView.id){
            R.id.tv_mira_audit_time->{
                val state = SubmitAuditState.getByState(record?.auditState);
                when(state){
                    SubmitAuditState.WAIT->{
                        textView.setText(R.string.mira_submit_expected_audit_time_default);
                    }
                    SubmitAuditState.AUDITING->{
                        val time = record?.auditTime?:System.currentTimeMillis()
                        val ts = time.addDays(1).format();
                        textView.setText(textView.getString(R.string.mira_submit_expected_audit_time,ts))
                    }
                    SubmitAuditState.PASS,
                    SubmitAuditState.REJECT->{
                        val time = record?.auditTime?:System.currentTimeMillis();
                        val ts = time.format();
                        textView.setText(textView.getString(R.string.mira_submit_audit_time,ts));
                    }
                    else->{
                        textView.setText(R.string.mira_submit_expected_audit_time_default);
                    }
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("miraSrc")
    fun setMiraImage(imageView : ImageView,url : String?){
        when(imageView.id){
            R.id.iv_mira_task_icon->{
                GlideApp.with(imageView)
                    .load(url)
                    .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                    .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                    .into(imageView);
            }
            else-> {
                GlideApp.with(imageView)
                    .load(url)
                    .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                    .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                    .into(imageView);
            }
        }
    }
}
