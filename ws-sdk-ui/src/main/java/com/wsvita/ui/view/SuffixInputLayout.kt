package com.wsvita.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.wsvita.ui.R
import com.wsvita.ui.databinding.LayoutUiInputSuffixBinding
import ext.StringExt.isInvalid
import ext.StringExt.isNumber

/**
 * SuffixInputLayout - 带后缀的输入行组件
 * 严格遵循 m 变量命名规范，提供完整的尺寸、内外边距及样式控制。
 */
class SuffixInputLayout : BaseInputLayout<LayoutUiInputSuffixBinding> {

    companion object {
        private const val TAG = "WSV_UI_SuffixInputLayout=>"

        const val SUFFIX_TYPE_NONE = 0
        const val SUFFIX_TYPE_TEXT = 1
        const val SUFFIX_TYPE_IMAGE = 2
        const val SUFFIX_TYPE_BOTH = 3
    }

    // --- Suffix 核心属性缓存 ---
    private var mSuffixText: String? = null
    private var mSuffixColor: Int = VALUE_ZERO
    private var mSuffixSize: Float = 0f
    private var mSuffixBackground: Drawable? = null

    // --- Suffix 维度属性缓存 (Width/Height/Padding/Margin) ---
    private var mSuffixWidth: Int = DIMENSION_NOT_SET
    private var mSuffixHeight: Int = DIMENSION_NOT_SET

    private var mSuffixMarginLeft: Int = VALUE_ZERO
    private var mSuffixMarginRight: Int = VALUE_ZERO
    private var mSuffixMarginTop: Int = VALUE_ZERO
    private var mSuffixMarginBottom: Int = VALUE_ZERO

    private var mSuffixType : Int = 1;
    // --- Suffix 图片专用尺寸 ---
    private var mSuffixImageWidth: Int = DIMENSION_NOT_SET
    private var mSuffixImageHeight: Int = DIMENSION_NOT_SET
    // --- 新增图片资源变量 ---
    private var mSuffixSrc: Int = 0

    private var onSuffixClick : ((suffixView : View,parent : SuffixInputLayout)->Unit)? = null;

    // --- 三段构造函数 ---

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // --- 实现基类要求的布局与资源映射 ---

    override fun layoutId(): Int {
        return R.layout.layout_ui_input_suffix
    }

    override fun styleableId(): IntArray {
        return R.styleable.SuffixInputLayout
    }

    // --- 实现基类要求的 View 引用方法 ---

    override fun getLabel(): TextView {
        return dataBinding.tvUiSuffixLabel
    }

    override fun getInput(): EditText {
        return dataBinding.editUiSuffixInput
    }

    override fun getCleanView(): ImageView {
        return dataBinding.ivClean
    }

    override fun getDividerView(): View {
        return dataBinding.vUiSuffixDivider
    }

    /**
     * 解析 XML 属性并赋值给缓存变量
     */
    override fun parseCommonAttrs(ta: android.content.res.TypedArray) {
        // 1. 解析基类公共属性 (Label/Input/Clean/Divider)
        mLabelText = ta.getString(R.styleable.SuffixInputLayout_wsui_label)
        mLabelColor = ta.getColor(R.styleable.SuffixInputLayout_wsui_labelColor, VALUE_ZERO)
        mLabelSize = ta.getDimension(R.styleable.SuffixInputLayout_wsui_labelSize, 0f)
        mLabelWidth = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_labelWidth, DIMENSION_NOT_SET)
        mLabelWeight = ta.getFloat(R.styleable.SuffixInputLayout_wsui_labelWeight, 0f)

        mInputText = ta.getString(R.styleable.SuffixInputLayout_wsui_input_text)
        mInputHint = ta.getString(R.styleable.SuffixInputLayout_wsui_input_hint)
        mInputType = ta.getInt(R.styleable.SuffixInputLayout_wsui_inputType, 1)
        mMaxLength = ta.getInteger(R.styleable.SuffixInputLayout_wsui_maxLength, DIMENSION_NOT_SET)
        mIsInputEnabled = ta.getBoolean(R.styleable.SuffixInputLayout_wsui_enabled, true)
        mInputWeight = ta.getFloat(R.styleable.SuffixInputLayout_wsui_inputWeight, 0f)

        mCleanWidth = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_clean_width, VALUE_ZERO)
        mCleanHeight = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_clean_height, VALUE_ZERO)
        mCleanPadding = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_clean_padding, VALUE_ZERO)
        mCleanMarginEnd = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_clean_marginEnd, VALUE_ZERO)
        mIsCleanUnable = ta.getBoolean(R.styleable.SuffixInputLayout_wsui_clean_unable, false)

        mShowDivider = ta.getBoolean(R.styleable.SuffixInputLayout_wsui_showDivider, true)

        // 2. 解析 Suffix 特有文本与样式属性
        mSuffixText = ta.getString(R.styleable.SuffixInputLayout_wsui_suffixText)
        mSuffixColor = ta.getColor(R.styleable.SuffixInputLayout_wsui_suffixColor, VALUE_ZERO)
        mSuffixSize = ta.getDimension(R.styleable.SuffixInputLayout_wsui_suffixSize, 0f)
        mSuffixBackground = ta.getDrawable(R.styleable.SuffixInputLayout_wsui_suffixBackground)

        // 3. 解析 Suffix 维度属性 (Width/Height)
        mSuffixWidth = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixWidth, DIMENSION_NOT_SET)
        mSuffixHeight = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixHeight, DIMENSION_NOT_SET)

        // 5. 解析 Suffix 外边距 (Margin)
        mSuffixMarginLeft = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixMarginLeft, VALUE_ZERO)
        mSuffixMarginRight = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixMarginRight, VALUE_ZERO)
        mSuffixMarginTop = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixMarginTop, VALUE_ZERO)
        mSuffixMarginBottom = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixMarginBottom, VALUE_ZERO)

        mSuffixType = ta.getInt(R.styleable.SuffixInputLayout_wsui_suffixType, SUFFIX_TYPE_NONE)
        // 6. 解析 Suffix 专用图片尺寸
        mSuffixImageWidth = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixImageWidth, DIMENSION_NOT_SET)
        mSuffixImageHeight = ta.getDimensionPixelSize(R.styleable.SuffixInputLayout_wsui_suffixImageHeight, DIMENSION_NOT_SET)
        // --- 关键：解析图片资源 ---
        mSuffixSrc = ta.getResourceId(R.styleable.SuffixInputLayout_wsui_suffixSrc, 0)
    }

    /**
     * 视图绑定逻辑
     */
    override fun onBind() {
        // 应用基类公共属性（Label/Input/Clean/Divider/Dimensions）
        applyBaseAttributes()
        applySuffixType(mSuffixText,mSuffixSrc)
        // 应用子类特有的 Suffix 属性
        setSuffixText(this.mSuffixText)
        setSuffixColor(this.mSuffixColor)
        setSuffixSize(this.mSuffixSize)

        // 应用后缀布局参数 (尺寸、背景、边距)
        applySuffixLayoutParams()

        // 统一点击处理：给容器设置监听，扩大点击热区
        dataBinding.llSuffixEnd.setOnClickListener {
            onSuffixClick?.invoke(it, this)
        }
    }

    fun onSuffixClick(onSuffixClick : ((suffixView : View,parent : SuffixInputLayout)->Unit)){
        this.onSuffixClick = onSuffixClick;
    }

    /**
     * 核心方法：统一应用后缀的物理布局参数
     */
    private fun applySuffixLayoutParams() {
        val sSuffixEndLayout = dataBinding.llSuffixEnd;

        // 1. 处理 LayoutParams (尺寸与外边距)
        val lp = sSuffixEndLayout.layoutParams
        if (lp is ViewGroup.MarginLayoutParams) {
            // 设置宽度
            if (mSuffixWidth != DIMENSION_NOT_SET) {
                lp.width = mSuffixWidth
            }
            // 设置高度
            if (mSuffixHeight != DIMENSION_NOT_SET) {
                lp.height = mSuffixHeight
            }

            // 设置外边距
            lp.setMargins(
                mSuffixMarginLeft,
                mSuffixMarginTop,
                mSuffixMarginRight,
                mSuffixMarginBottom
            )
            lp.marginEnd = mSuffixMarginRight
            lp.marginStart = mSuffixMarginLeft

            sSuffixEndLayout.layoutParams = lp
        }

        // 2. 设置背景
        if (mSuffixBackground != null) {
            sSuffixEndLayout.background = mSuffixBackground
        }
    }

    private fun applySuffixType(text: String?, resId: Int) {
        // 1. 初始化：默认隐藏容器及内部所有 View
        dataBinding.llSuffixEnd.visibility = View.GONE
        dataBinding.tvUiSuffixText.visibility = View.GONE
        dataBinding.tvUiSuffixImg.visibility = View.GONE

        if (mSuffixType == SUFFIX_TYPE_NONE) return

        // 2. 只要不是 NONE，就显示容器
        dataBinding.llSuffixEnd.visibility = View.VISIBLE

        when (mSuffixType) {
            SUFFIX_TYPE_TEXT -> {
                showTextSuffix(text)
            }
            SUFFIX_TYPE_IMAGE -> {
                showImageSuffix(resId)
            }
            SUFFIX_TYPE_BOTH -> {
                // 同时调用，顺序由 XML 布局中的顺序决定
                showTextSuffix(text)
                showImageSuffix(resId)
            }
        }
    }

    private fun showTextSuffix(text: String?) {
        dataBinding.tvUiSuffixText.apply {
            visibility = View.VISIBLE
            this.text = text
            if (mSuffixColor != VALUE_ZERO) {
                setTextColor(mSuffixColor)
            }
            if (mSuffixSize > 0f) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, mSuffixSize)
            }
        }
    }

    private fun showImageSuffix(resId: Int) {
        dataBinding.tvUiSuffixImg.apply {
            visibility = View.VISIBLE
            if (resId != 0) setImageResource(resId)

            // 应用专用宽高
            val imgLp = layoutParams
            var changed = false
            if (mSuffixImageWidth != DIMENSION_NOT_SET) {
                imgLp.width = mSuffixImageWidth
                changed = true
            }
            if (mSuffixImageHeight != DIMENSION_NOT_SET) {
                imgLp.height = mSuffixImageHeight
                changed = true
            }
            if (changed) layoutParams = imgLp
        }
    }

    fun setSuffixText(text: String?) {
        this.mSuffixText = text
        dataBinding.tvUiSuffixText.text = text ?: ""
    }

    fun setSuffixColor(color: Int) {
        this.mSuffixColor = color
        if (color != VALUE_ZERO) {
            dataBinding.tvUiSuffixText.setTextColor(color)
        }
    }

    fun setSuffixSize(pxSize: Float) {
        this.mSuffixSize = pxSize
        if (pxSize > 0f) {
            dataBinding.tvUiSuffixText.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxSize)
        }
    }

    fun setSuffixBackground(drawable: Drawable?) {
        this.mSuffixBackground = drawable
        applySuffixLayoutParams()
    }

    /**
     * 综合设置后缀布局尺寸
     */
    fun setSuffixLayout(width: Int, height: Int) {
        this.mSuffixWidth = width
        this.mSuffixHeight = height
        applySuffixLayoutParams()
    }

    /**
     * 综合设置后缀外边距
     */
    fun setSuffixMargins(left: Int, top: Int, right: Int, bottom: Int) {
        this.mSuffixMarginLeft = left
        this.mSuffixMarginTop = top
        this.mSuffixMarginRight = right
        this.mSuffixMarginBottom = bottom
        applySuffixLayoutParams()
    }

    /**
     * 获取后缀 View 对象
     */
    fun getSuffixView(): TextView {
        return dataBinding.tvUiSuffixText
    }

    fun getInputText(): String {
        return dataBinding.editUiSuffixInput.text.toString();
    }

    fun getInputNum(defaultVaule : Int): Int {
        val n = getInputText();
        if(n.isInvalid()){
            return defaultVaule;
        }
        if(n.isNumber()){
            return n.toInt();
        }
        return defaultVaule;
    }
}
