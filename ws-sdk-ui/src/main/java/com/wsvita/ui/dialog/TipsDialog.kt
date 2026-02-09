package com.wsvita.ui.dialog

import android.content.Context
import com.wsvita.ui.R
import com.wsvita.ui.common.BaseBuilderDialog
import com.wsvita.ui.common.DialogBuilder
import com.wsvita.ui.databinding.DialogMessageTipsBinding
import ext.ViewExt.dip2px

class TipsDialog : BaseBuilderDialog<DialogMessageTipsBinding,TipsDialog.Builder>{

    private constructor(builder: Builder) : super(builder, R.style.AppDialog) {

    }

    override fun initDialog() {
        super.initDialog()
        dataBinding.titleText = title;
        dataBinding.messageText = message;
    }

    class Builder(context: Context) : DialogBuilder<TipsDialog>(context) {

        override fun builder(): TipsDialog {
            return TipsDialog(this);
        }
    }

    override fun layoutRes(): Int {
        return R.layout.dialog_message_tips;
    }

    override fun getHeight(): Int {
        return 120.dip2px();
    }
}
