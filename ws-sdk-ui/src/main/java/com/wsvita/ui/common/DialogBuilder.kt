package com.wsvita.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import ext.ViewExt.createComplexRectDrawable

/**
 * 通用对话框构建器基类 (SDK 级组件)
 * 采用 Builder 模式封装 Dialog 的常用配置参数，支持链式调用。
 *
 * @param D 最终构建生成的 Dialog 具体类型
 * @author Eastevil
 * @date 2025/12/22
 */
abstract class DialogBuilder<D : Dialog> {

    // --- 核心上下文 ---
    internal var context: Context

    // --- 文本配置 ---
    internal var title: String? = null
    internal var message: String? = null
    internal var cancelText: String? = null
    internal var submitText: String? = null

    // --- 视图显示控制标志 ---
    internal var haveTitle: Boolean = false
    internal var haveMessage: Boolean = false
    internal var haveSubmit: Boolean = false
    internal var haveCancel: Boolean = false

    // --- 交互配置 ---
    internal var isCancelable: Boolean = false

    // --- 事件回调监听 ---
    internal var onCancel: (() -> Unit)? = null
    internal var onMessage: (() -> Unit)? = null // 预留点击消息体的回调
    internal var onSubmit: (() -> Unit)? = null
    internal var onDismiss: (() -> Unit)? = null
    internal var onClose :  (() -> Unit)? = null;

    // --- 布局尺寸控制 ---
    internal var width: Int = 0
    internal var height: Int = 0

    // --- 颜色样式配置 ---
     internal var submitColor: Int = Color.WHITE
     internal var cancelColor: Int = Color.WHITE
     internal var themeColor: Int = Color.WHITE

    /**
     * 显式构造函数，强制要求传入 Context
     */
    constructor(context: Context) {
        this.context = context
    }

    // --- 标题设置 (Title) ---

    /** 设置标题文本 */
    fun title(title: String): DialogBuilder<D> {
        this.title = title
        this.haveTitle = true
        return this
    }

    /** 通过资源 ID 设置标题 */
    fun title(@StringRes titleResId: Int): DialogBuilder<D> {
        return title(context.getString(titleResId))
    }

    /** 通过资源 ID 和格式化参数设置标题 */
    fun title(@StringRes titleResId: Int, vararg args: Any): DialogBuilder<D> {
        return title(context.getString(titleResId, *args))
    }

    // --- 内容设置 (Message) ---

    /** 设置正文内容文本 */
    fun message(message: String): DialogBuilder<D> {
        this.message = message
        this.haveMessage = true
        return this
    }

    /** 通过资源 ID 设置正文内容 */
    fun message(@StringRes messageResId: Int): DialogBuilder<D> {
        return message(context.getString(messageResId))
    }

    /** 通过资源 ID 和格式化参数设置正文内容 */
    fun message(@StringRes messageResId: Int, vararg args: Any): DialogBuilder<D> {
        return message(context.getString(messageResId, *args))
    }

    // --- 取消按钮设置 (Cancel Button) ---

    /** 设置取消按钮文本 */
    fun cancelText(cancelText: String): DialogBuilder<D> {
        this.cancelText = cancelText
        this.haveCancel = true
        return this
    }

    /** 通过资源 ID 设置取消按钮文本 */
    fun cancelText(@StringRes cancelTextResId: Int): DialogBuilder<D> {
        return cancelText(context.getString(cancelTextResId))
    }

    // --- 确定按钮设置 (Submit Button) ---

    /** 设置确定按钮文本 */
    fun submitText(submitText: String): DialogBuilder<D> {
        this.submitText = submitText
        this.haveSubmit = true
        return this
    }

    /** 通过资源 ID 设置确定按钮文本 */
    fun submitText(@StringRes submitTextResId: Int): DialogBuilder<D> {
        return submitText(context.getString(submitTextResId))
    }

    // --- 交互与回调设置 ---

    /** 设置点击外部或返回键是否可以取消对话框 */
    fun isCancelable(isCancelable: Boolean): DialogBuilder<D> {
        this.isCancelable = isCancelable
        return this
    }

    /** 设置点击取消按钮的回调 */
    fun onCancel(onCancel: (() -> Unit)): DialogBuilder<D> {
        this.onCancel = onCancel
        return this
    }

    /** 设置点击消息区域的回调 */
    fun onMessage(onMessage: (() -> Unit)): DialogBuilder<D> {
        this.onMessage = onMessage
        return this
    }

    /** 设置点击确定按钮的回调 */
    fun onSubmit(onSubmit: (() -> Unit)): DialogBuilder<D> {
        this.onSubmit = onSubmit
        return this
    }

    /** 设置对话框消失时的回调 */
    fun onDismiss(onDismiss: (() -> Unit)): DialogBuilder<D> {
        this.onDismiss = onDismiss
        return this
    }

    fun onClose(onClose : (() -> Unit)): DialogBuilder<D> {
        this.onClose = onClose;
        return this;
    }

    // --- 尺寸与样式设置 ---

    /** 设置对话框宽度 */
    fun width(width: Int): DialogBuilder<D> {
        this.width = width
        return this
    }

    /** 设置对话框高度 */
    fun height(height: Int): DialogBuilder<D> {
        this.height = height
        return this
    }

    /** 设置确定按钮文本颜色 */
    fun submitColor( submitColor: Int): DialogBuilder<D> {
        this.submitColor = submitColor
        return this
    }

    /** 设置取消按钮文本颜色 */
    fun cancelColor( cancelColor: Int): DialogBuilder<D> {
        this.cancelColor = cancelColor
        return this
    }

    /** 设置对话框主题色 */
    fun themeColor( themeColor: Int): DialogBuilder<D> {
        this.themeColor = themeColor
        return this
    }


    /**
     * 核心构建方法：由具体子类实现，返回对应的 Dialog 实例
     */
    abstract fun builder(): D
}
