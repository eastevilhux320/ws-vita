package com.wsvita.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.wsvita.ui.R
import com.wsvita.ui.databinding.LayoutUiInputSimpleBinding

/**
 * SimpleInputLayout - 基础输入行组件
 * 严格遵循 m 变量命名规范，解决 JVM 签名冲突，禁止简写。
 */
class SimpleInputLayout : BaseInputLayout<LayoutUiInputSimpleBinding> {

    // --- 严格三段构造函数 ---

    constructor(context: Context) : super(context){

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){

    }

    // --- 实现基类要求的布局与资源映射 ---

    override fun layoutId(): Int {
        return R.layout.layout_ui_input_simple
    }

    override fun styleableId(): IntArray {
        return R.styleable.SimpleInputLayout
    }

    // --- 实现基类要求的 View 引用方法 ---

    override fun getLabel(): TextView {
        return dataBinding.tvUiSimpleTips
    }

    override fun getInput(): EditText {
        return dataBinding.editUiSimpleInput
    }

    override fun getCleanView(): ImageView {
        return dataBinding.ivClean
    }

    override fun getDividerView(): View {
        return dataBinding.vUiSimpleDivider
    }

    /**
     * 解析 XML 属性并赋值给父类 m 开头的缓存变量
     * 严格遵循非简写规范
     */
    override fun parseCommonAttrs(ta: android.content.res.TypedArray) {
        // Label 相关属性解析
        mLabelText = ta.getString(R.styleable.SimpleInputLayout_wsui_label)
        mLabelColor = ta.getColor(R.styleable.SimpleInputLayout_wsui_labelColor, 0)
        mLabelSize = ta.getDimension(R.styleable.SimpleInputLayout_wsui_labelSize, 0f)
        mLabelWidth = ta.getDimensionPixelSize(R.styleable.SimpleInputLayout_wsui_labelWidth, -1)
        mLabelWeight = ta.getFloat(R.styleable.SimpleInputLayout_wsui_labelWeight, 0f)

        // Input 相关属性解析
        mInputText = ta.getString(R.styleable.SimpleInputLayout_wsui_input_text)
        mInputHint = ta.getString(R.styleable.SimpleInputLayout_wsui_input_hint)
        mInputType = ta.getInt(R.styleable.SimpleInputLayout_wsui_inputType, 1)
        mMaxLength = ta.getInteger(R.styleable.SimpleInputLayout_wsui_maxLength, -1)
        mIsInputEnabled = ta.getBoolean(R.styleable.SimpleInputLayout_wsui_enabled, true)
        mInputWeight = ta.getFloat(R.styleable.SimpleInputLayout_wsui_inputWeight, 0f)

        // 清除按钮与通用样式解析
        mCleanWidth = ta.getDimensionPixelSize(R.styleable.SimpleInputLayout_wsui_clean_width, 0)
        mCleanHeight = ta.getDimensionPixelSize(R.styleable.SimpleInputLayout_wsui_clean_height, 0)
        mCleanPadding = ta.getDimensionPixelSize(R.styleable.SimpleInputLayout_wsui_clean_padding, 0)
        mCleanMarginEnd = ta.getDimensionPixelSize(R.styleable.SimpleInputLayout_wsui_clean_marginEnd, 0)
        mIsCleanUnable = ta.getBoolean(R.styleable.SimpleInputLayout_wsui_clean_unable, false)

        mShowDivider = ta.getBoolean(R.styleable.SimpleInputLayout_wsui_showDivider, true)
    }

    fun getInputText(): String {
        return dataBinding.editUiSimpleInput.text.toString();
    }

    /**
     * 视图绑定后的初始化
     */
    override fun onBind() {
        // 调用父类统一的应用逻辑，自动完成所有 m 变量到 UI 的映射
        applyBaseAttributes()
    }
}
