package com.wsvita.ui.dialog

import android.content.Context
import android.graphics.Color
import com.wsvita.ui.R
import com.wsvita.ui.common.BaseBuilderDialog
import com.wsvita.ui.common.DialogBuilder
import com.wsvita.ui.databinding.DialogConfirmBinding
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.dip2px

class ConfirmDialog : BaseBuilderDialog<DialogConfirmBinding,ConfirmDialog.Builder>{

    private constructor(builder: Builder) : super(builder, R.style.AppDialog){

    }

    override fun initDialog() {
        super.initDialog()
        dataBinding.titleText = title;
        dataBinding.messageText = message;
        dataBinding.submitText = submitText;
        dataBinding.wsUiPopupSubmit.setText(submitText.get());
    }

    override fun layoutRes(): Int {
        return R.layout.dialog_confirm;
    }

    override fun getHeight(): Int {
        return 210.dip2px();
    }

    override fun initView() {
        super.initView()

        val bgSubmit = submitColor.createComplexRectDrawable(12f);
        dataBinding.wsUiPopupSubmit.background = bgSubmit;
    }

    class Builder(context: Context) : DialogBuilder<ConfirmDialog>(context) {
        override fun builder(): ConfirmDialog {
            return ConfirmDialog(this);
        }
    }

}
