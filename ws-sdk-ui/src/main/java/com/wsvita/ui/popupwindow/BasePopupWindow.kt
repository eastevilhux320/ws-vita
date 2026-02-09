package com.wsvita.ui.popupwindow

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import com.wsvita.framework.utils.SLog
import ext.ViewExt.getScreenPair

abstract class BasePopupWindow<D : ViewDataBinding, B : PopupBaseBuilder<*>> : PopupWindow {

    protected lateinit var dataBinding: D;
    protected lateinit var builder: B;

    // --- 响应式字段声明 ---
    protected lateinit var titleText: ObservableField<String>;
    protected lateinit var submitText: ObservableField<String>;
    protected lateinit var cancelText: ObservableField<String>;

    protected lateinit var haveMenu : ObservableField<Boolean>;
    protected lateinit var haveTitle: ObservableField<Boolean>;
    protected lateinit var haveSubmit: ObservableField<Boolean>;
    protected lateinit var haveCancel: ObservableField<Boolean>;

    protected constructor(builder: B) : super(builder.activity) {
        this.builder = builder;
        this.initParams(builder);
        this.init(builder.activity);
    }

    /**
     * 初始化参数：将 Builder 中的普通类型转换为 ObservableField
     */
    private fun initParams(b: B) {
        this.titleText = ObservableField(b.titleText);
        this.submitText = ObservableField(b.submitText);
        this.cancelText = ObservableField(b.cancelText);

        this.haveTitle = ObservableField(b.haveTitle);
        this.haveSubmit = ObservableField(b.haveSubmit);
        this.haveCancel = ObservableField(b.haveCancel);
        this.haveMenu = ObservableField(b.haveMenu);
    }

    private fun init(activity: Activity) {
        this.dataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            this.layoutRes(),
            null,
            false
        );
        this.setContentView(this.dataBinding.root);

        this.updateSize(this.builder.width, this.builder.height);

        if (this.getOutsideTouchable()) {
            this.setFocusable(true);
            this.setOutsideTouchable(true);
        }

        this.initBg();
        this.onInit(activity);
    }


    open fun onViewClick(view : View){
        when(view.id){

        }
    }

    abstract fun layoutRes(): Int;

    private fun updateSize(width: Int, height: Int) {
        val screenPair = this.dataBinding.root.context.getScreenPair();
        var w = screenPair.first;
        var h = screenPair.second / 4;

        if (width > 0) {
            w = width;
        }

        if (height > 0) {
            h = height;
        }

        this.width = w;
        this.height = h;
        this.update();
    }

    protected open fun getOutsideTouchable(): Boolean {
        return false;
    }

    protected open fun initBg() {
        this.setAnimationStyle(android.R.style.Animation_Dialog);
        val dw = ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
    }

    protected open fun onInit(activity: Activity){
        SLog.d(TAG,"onInit")
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor)
        onShow();
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        super.showAsDropDown(anchor, xoff, yoff)
        onShow();
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        onShow();
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        onShow();
    }

    protected open fun onShow(){
        if(builder.isTranslucent){
            val window = builder.activity.window;
            val lp = window.attributes
            lp.alpha = builder.windowAlpha;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.attributes = lp
        }
    }

    override fun dismiss() {
        if (builder.isTranslucent) {
            val window = builder.activity.window
            val lp = window.attributes
            lp.alpha = 1.0f
            window.attributes = lp
        }

        // 彻底释放 Binding 引用，防止内存泄漏
        this.dataBinding.unbind();
        super.dismiss();
    }

    fun getContext(): Context {
        return this.dataBinding.root.context;
    }

    companion object {
        private const val TAG = "WSV_UI_BasePopupWindow=>";
    }
}
