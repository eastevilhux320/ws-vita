package com.wangshu.mira.view.dialog

import android.content.Context
import android.graphics.Color
import android.view.View
import com.wangshu.mira.R
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.databinding.DialogMiraTaskSubmitBinding
import com.wsvita.ui.common.BaseBuilderDialog
import com.wsvita.ui.common.DialogBuilder
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.dip2px

class MiraTaskSubmitResultDialog : BaseBuilderDialog<DialogMiraTaskSubmitBinding,MiraTaskSubmitResultDialog.Builder>{
    private var onWatchSubmit : (()->Unit)? = null;
    private var onBackTaskList : (()->Unit)? = null;

    private constructor(builder: Builder) : super(builder,com.wsvita.ui.R.style.AppDialog){
        this.onBackTaskList = builder.onBackTaskList;
        this.onWatchSubmit = builder.onWatchSubmit;
    }

    override fun getHeight(): Int {
        return 260.dip2px();
    }

    override fun layoutRes(): Int {
        return R.layout.dialog_mira_task_submit;
    }

    override fun initView() {
        super.initView()
        dataBinding.dialog = this;

        val color = MiraConfigure.instance.getConfig()?.mainThemeColor?: context.getColor(R.color.mira_main_theme_color);
        val bg = color.createComplexRectDrawable(5f,1f,getColor(com.wsvita.ui.R.color.color_base_line))
        dataBinding.tvMiraSubmitResultGoon.background = bg;
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.tv_mira_submit_result_watch->{
                //查看提交详情
                onWatchSubmit?.invoke();
            }
            R.id.tv_mira_submit_result_goon->{
                //返回继续做任务
                onBackTaskList?.invoke();
            }
        }
    }

    class Builder(context: Context) : DialogBuilder<MiraTaskSubmitResultDialog>(context) {
        internal var onWatchSubmit : (()->Unit)? = null;
        internal var onBackTaskList : (()->Unit)? = null;

        fun onWatchSubmit(onWatchSubmit : (()->Unit)): Builder {
            this.onWatchSubmit = onWatchSubmit;
            return this
        }

        fun onBackTaskList(onBackTaskList : (()->Unit)): Builder {
            this.onBackTaskList = onBackTaskList;
            return this
        }

        override fun builder(): MiraTaskSubmitResultDialog {
            return MiraTaskSubmitResultDialog(this);
        }

    }
}
