package com.wsvita.ui.dialog

import android.content.Context
import com.wsvita.ui.R
import com.wsvita.ui.common.BaseBuilderDialog
import com.wsvita.ui.common.DialogBuilder
import com.wsvita.ui.databinding.DialogConventionalBinding
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.dip2px

class ConventionalDialog : BaseBuilderDialog<DialogConventionalBinding,ConventionalDialog.Builder> {

    private constructor(builder: ConventionalDialog.Builder) : super(builder, R.style.AppDialog) {

    }

    override fun layoutRes(): Int {
        return R.layout.dialog_conventional;
    }

    override fun initDialog() {
        super.initDialog()
        dataBinding.titleText = title;
        dataBinding.messageText = message;
        dataBinding.submitText = submitText;
        dataBinding.cancelText = cancelText;

        dataBinding.wsUiPopupSubmit.setOnClickListener {
            onSubmit();
        }

        dataBinding.wsUiPopupCancel.setOnClickListener {
            onCancel();
        }
    }

    override fun initView() {
        super.initView()
        val bgCancel = cancelColor.createComplexRectDrawable(0f,0f,0f,12f);
        dataBinding.wsUiPopupCancel.background = bgCancel;

        val bgSubmit = submitColor.createComplexRectDrawable(0f,0f,12f,0f);
        dataBinding.wsUiPopupSubmit.background = bgSubmit;
    }

    override fun getHeight(): Int {
        return 420.dip2px();
    }

    class Builder(context: Context) : DialogBuilder<ConventionalDialog>(context) {
        override fun builder(): ConventionalDialog {
            return ConventionalDialog(this);
        }
    }
}
