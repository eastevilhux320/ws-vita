package com.wsvita.ui.common

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import com.wsvita.framework.utils.SLog
import com.wsvita.ui.R
import ext.TimeExt.systemTime
import ext.ViewExt.dip2px
import ext.ViewExt.getScreenPair

/**
 * 具有 Builder 配置能力的 Dialog 基类
 * create by Eastevil at 2025/12/22 15:15
 * * @param V 布局绑定的 ViewDataBinding 类型
 * @param B 对应的构建器类型，继承自 [DialogBuilder]
 * @author Eastevil
 */
abstract class BaseBuilderDialog<V : ViewDataBinding, B : DialogBuilder<*>> : BaseDialog<V> {

    /** 具体的构建配置实例 */
    protected var builder: B

    // --- 响应式字段，供 XML 布局通过 @{dialog.title} 引用 ---
    val title = ObservableField<String>()
    val message = ObservableField<String>()
    val submitText = ObservableField<String>()
    val cancelText = ObservableField<String>()

    // --- 控制 View 显示隐藏的字段 ---
    val haveTitle = ObservableField<Boolean>(false)
    val haveMessage = ObservableField<Boolean>(false)
    val haveSubmit = ObservableField<Boolean>(false)
    val haveCancel = ObservableField<Boolean>(false)

    // -- 事件回调
    internal var onCancel: (() -> Unit)? = null
    internal var onMessage: (() -> Unit)? = null
    internal var onSubmit: (() -> Unit)? = null
    internal var onDismiss: (() -> Unit)? = null
    internal var onClose :  (() -> Unit)? = null;

    var themeColor : Int = Color.WHITE;
    var submitColor : Int = Color.WHITE;
    var cancelColor : Int = Color.WHITE;

    /**
     * 构造函数：必须传入 Builder 以获取配置信息
     */
    constructor(builder: B, themeResId: Int) : super(builder.context, themeResId) {
        this.builder = builder
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog();
    }


    open fun initView() {
        SLog.d(TAG,"initView invoke")
        title.set(builder.title)
        message.set(builder.message)
        submitText.set(builder.submitText)
        cancelText.set(builder.cancelText)

        haveTitle.set(builder.haveTitle)
        haveMessage.set(builder.haveMessage)
        haveSubmit.set(builder.haveSubmit)
        haveCancel.set(builder.haveCancel)

        onSubmit = builder.onSubmit;
        onClose = builder.onClose;
        onCancel = builder.onCancel;
        onDismiss = builder.onDismiss;
        onMessage = builder.onMessage;
        this.themeColor = builder.themeColor;
        this.submitColor = builder.submitColor;
        this.cancelColor = builder.cancelColor;
    }

    /**
     * 由子类实现具体的业务逻辑，例如 Adapter 绑定等
     */
    protected open fun initDialog(){
        SLog.d(TAG,"initDialog invoke");
    }

    /**
     * 计算对话框宽度
     * 策略：如果 Builder 指定了 width 则使用指定的，否则根据屏幕宽度减去间距
     */
    override fun getWidth(): Int {
        return if (builder.width > 0) {
            builder.width
        } else {
            // 使用 Pair 扩展获取屏幕宽度
            context.getScreenPair().first - spaceWidth()
        }
    }


    /**
     * 侧边留白间距 (px)
     */
    open fun spaceWidth(): Int {
        return DEFAULT_SPACE.dip2px()
    }

    open fun onViewClick(view : View){
        SLog.i(TAG,"onViewClick invoke");
        when(view.id){
            R.id.ws_ui_dialog_submit->{
                onSubmit();
            }
            R.id.ws_ui_dialog_close->{
                onClose();
            }
            R.id.ws_ui_dialog_cancel->{
                onCancel();
            }
            R.id.ws_ui_dialog_dismiss->{
                onDismiss();
            }
        }
    }

    open fun onSubmit(){
        SLog.d(TAG,"onSubmit invoke");
        onSubmit?.invoke();
    }

    open fun onClose(){
        SLog.d(TAG,"onClose invoke");
        onClose?.invoke();
    }

    open fun onCancel(){
        SLog.d(TAG,"onCancel invoke");
        onCancel?.invoke();
    }

    open fun onDismiss(){
        SLog.d(TAG,"onDismiss invoke");
        onDismiss?.invoke();
    }

    fun setTitleText(title : String){
        this.title.set(title);
    }

    fun setTitleText(resId : Int){
        val s = getString(resId);
        setTitleText(s);
    }

    fun setTitleText(resId : Int,vararg args : String){
        val s = getString(resId,*args);
        setTitleText(s);
    }

    /**
     * 设置消息文本内容
     */
    fun setMessageText(message: String) {
        this.message.set(message)
        // 自动根据内容判断是否需要显示 Message 区域
        this.haveMessage.set(message.isNotEmpty())
    }

    /**
     * 通过资源 ID 设置消息文本
     */
    fun setMessageText(resId: Int) {
        val s = context.getString(resId)
        setMessageText(s)
    }

    /**
     * 通过资源 ID 和 格式化参数设置消息文本
     * 例如：setMessageText(R.string.delete_confirm, "文件名.txt")
     */
    fun setMessageText(resId: Int, vararg args: Any) {
        val s = context.getString(resId, *args)
        setMessageText(s)
    }

    /**
     * 直接设置确定按钮文本内容
     */
    fun setSubmitText(text: String) {
        this.submitText.set(text)
        this.haveSubmit.set(text.isNotEmpty())
    }

    /**
     * 通过资源 ID 设置确定按钮文本
     */
    fun setSubmitText(@StringRes resId: Int) {
        setSubmitText(getString(resId))
    }

    /**
     * 通过资源 ID 和格式化参数设置确定按钮文本
     */
    fun setSubmitText(@StringRes resId: Int, vararg args: Any) {
        setSubmitText(getString(resId, *args))
    }

    // --- 取消按钮文本设置 (Cancel Text) ---

    /**
     * 直接设置取消按钮文本内容
     */
    fun setCancelText(text: String) {
        this.cancelText.set(text)
        this.haveCancel.set(text.isNotEmpty())
    }

    /**
     * 通过资源 ID 设置取消按钮文本
     */
    fun setCancelText(@StringRes resId: Int) {
        setCancelText(getString(resId))
    }

    /**
     * 通过资源 ID 和格式化参数设置取消按钮文本
     */
    fun setCancelText(@StringRes resId: Int, vararg args: Any) {
        setCancelText(getString(resId, *args))
    }

    companion object {
        private const val TAG = "WSVita_UI_BaseBuilderDialog=>"
        /** 默认左右间距总和 (dp) */
        private const val DEFAULT_SPACE = 60
    }
}
