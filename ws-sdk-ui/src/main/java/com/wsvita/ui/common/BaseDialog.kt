package com.wsvita.ui.common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * 组件化框架 Dialog 顶层基类
 * 封装了 DataBinding 自动初始化、Window 尺寸管理及资源获取工具。
 *
 * @param D 布局绑定的 ViewDataBinding 类型
 * @author Eastevil
 * @date 2025/12/22
 */
abstract class BaseDialog<D : ViewDataBinding> : Dialog,
    DialogInterface.OnDismissListener,
    DialogInterface.OnCancelListener {

    protected lateinit var dataBinding: D

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        SLog.d(TAG, "BaseDialog instance created")
        // 初始化 DataBinding
        dataBinding = DataBindingUtil.inflate(layoutInflater, layoutRes(), null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SLog.i(TAG, "onCreate start, time: ${systemTime()}")

        setContentView(dataBinding.root)

        // 窗口属性配置
        setupWindowAttributes()
        // 监听器绑定
        setOnDismissListener(this)
        setOnCancelListener(this)
        SLog.i(TAG, "onCreate end, time: ${systemTime()}")
    }

    /**
     * 设置 Window 宽高及属性
     */
    private fun setupWindowAttributes() {
        window?.let { win ->
            val params = win.attributes
            params.width = getWidth()
            params.height = getHeight()
            win.attributes = params
            SLog.d(TAG, "setupWindowAttributes => WIDTH: ${params.width}, HEIGHT: ${params.height}")
        }
    }

    /** 返回布局资源 ID */
    @LayoutRes
    abstract fun layoutRes(): Int

    /** 获取期望宽度，默认为 WRAP_CONTENT */
    abstract fun getWidth(): Int;

    /** 获取期望高度，默认为 WRAP_CONTENT */
    abstract fun getHeight(): Int;

    /**
     * 动态更新对话框大小
     */
    fun updateSize(width: Int, height: Int) {
        window?.attributes?.apply {
            this.width = width
            this.height = height
            window?.attributes = this
        }
    }

    // --- 资源获取工具方法 ---

    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        context.getString(resId, *formatArgs)

    fun getColor(@ColorRes colorResId: Int): Int = context.getColor(colorResId)

    // --- 监听器默认实现 (子类可按需重写) ---

    override fun onDismiss(dialog: DialogInterface?) {
        SLog.d(TAG, "Dialog dismissed")
    }

    override fun onCancel(dialog: DialogInterface?) {
        SLog.d(TAG, "Dialog cancelled")
    }

    companion object {
        private const val TAG = "WSVita_UI_BaseDialog=>";
    }
}
