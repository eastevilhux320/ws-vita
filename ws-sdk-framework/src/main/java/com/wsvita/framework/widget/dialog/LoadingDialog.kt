package com.wsvita.framework.widget.dialog

import android.app.Dialog
import android.content.Context
import androidx.databinding.DataBindingUtil
import com.wsvita.framework.R
import com.wsvita.framework.databinding.LayoutFrameworkLoadingBinding
import ext.ViewExt.dip2px

class LoadingDialog(context: Context) : Dialog(context) {
    lateinit var dataBinding : LayoutFrameworkLoadingBinding;

    init {
        dataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_framework_loading,
            null,false);
        setContentView(dataBinding.root);

        //点击提示框外面是否取消提示框
        setCanceledOnTouchOutside(false)
        //点击返回键是否取消提示框
        setCancelable(false)

        val dialogWindow = window
        val lp = dialogWindow!!.attributes

        lp.width = 80.dip2px()
        lp.height = 80.dip2px()
        //lp.alpha = 0.7f; // 透明度
        //lp.alpha = 0.7f; // 透明度
        dialogWindow!!.attributes = lp
    }

    override fun show() {
        super.show()
    }

    override fun dismiss() {
        super.dismiss()
    }
}
