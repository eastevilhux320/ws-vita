package com.wsvita.ui.view

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ViewDataBinding
import com.wsvita.ui.common.BaseLayout

/**
 * BaseInputLayout - 输入组件抽象基类
 * 1. 变量严格采用 m 开头命名（如 mLabelText），确保与 Setter 方法名区分。
 * 2. 彻底解决 JVM 签名冲突问题。
 * 3. 逻辑严谨，禁止任何形式的简写。
 */
abstract class BaseInputLayout<D : ViewDataBinding> : BaseLayout<D> {

    companion object{
        /** 默认标签权重占比 (32%) */
        const val DEFAULT_LABEL_WEIGHT: Float = 0.32f

        /** 默认输入框权重占比 (68%) */
        const val DEFAULT_INPUT_WEIGHT: Float = 0.68f

        /** 权重总和上限 */
        const val WEIGHT_SUM_MAX: Float = 1.0f

        /** 权重无效或未设置的临界值 */
        const val WEIGHT_INVALID: Float = 0.0f

        /** 默认清除按钮尺寸 (dp) */
        const val DEFAULT_CLEAN_SIZE_DP: Float = 30f

        /** 默认未设置宽度的标记值 */
        const val DIMENSION_NOT_SET: Int = -1
    }

    // --- 属性定义 (严格使用 m 开头) ---
    protected var mLabelText: String? = null
    protected var mLabelColor: Int = 0
    protected var mLabelSize: Float = 0f
    protected var mLabelWidth: Int = -1
    protected var mLabelWeight: Float = 0f

    protected var mInputText: String? = null
    protected var mInputHint: String? = null
    protected var mInputType: Int = 1
    protected var mInputWeight: Float = 0f
    protected var mMaxLength: Int = -1
    protected var mIsInputEnabled: Boolean = true

    protected var mCleanWidth: Int = 0
    protected var mCleanHeight: Int = 0
    protected var mCleanPadding: Int = 0
    protected var mCleanMarginEnd: Int = 0
    protected var mIsCleanUnable: Boolean = false

    protected var mShowDivider: Boolean = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // --- 抽象方法 ---
    protected abstract fun getLabel(): TextView
    protected abstract fun getInput(): EditText
    protected abstract fun getCleanView(): ImageView
    protected abstract fun getDividerView(): View

    abstract fun styleableId(): IntArray
    protected abstract fun parseCommonAttrs(ta: android.content.res.TypedArray)

    override fun initAttr(attrs: AttributeSet) {
        // 传入 0, 0 以支持从 style 资源继承属性
        val ta = context.obtainStyledAttributes(attrs, styleableId(), 0, 0)
        try {
            parseCommonAttrs(ta)
        } finally {
            ta.recycle()
        }
    }

    /**
     * 初始化应用所有基础属性
     */
    protected fun applyBaseAttributes() {
        setLabelText(this.mLabelText)
        setLabelTextColor(this.mLabelColor)
        setLabelTextSize(this.mLabelSize)

        setInputText(this.mInputText)
        setInputHint(this.mInputHint)
        setInputType(this.mInputType)
        setMaxLength(this.mMaxLength)
        setInputEnabled(this.mIsInputEnabled)

        setDividerVisible(this.mShowDivider)

        setupCleanAction()
        updateDimensions()
    }

    // --- 公共 Setter 方法 (此时与 mXXX 变量生成的 Setter 无冲突) ---

    fun setLabelText(text: String?) {
        this.mLabelText = text
        getLabel().text = text ?: ""
    }

    fun setLabelTextColor(color: Int) {
        this.mLabelColor = color
        if (color != 0) {
            getLabel().setTextColor(color)
        }
    }

    fun setLabelTextSize(pxSize: Float) {
        this.mLabelSize = pxSize
        if (pxSize > 0f) {
            getLabel().setTextSize(TypedValue.COMPLEX_UNIT_PX, pxSize)
        }
    }

    fun setInputText(text: String?) {
        this.mInputText = text
        getInput().setText(text ?: "")
    }

    fun setInputHint(hint: String?) {
        this.mInputHint = hint
        getInput().hint = hint ?: ""
    }

    fun setInputType(type: Int) {
        this.mInputType = type
        val input = getInput()

        when (type) {
            // 对应 XML 中的 password (129)
            129 -> {
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                // 确保密码样式下字体不缩进或改变
                input.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // 对应 XML 中的 number (2)
            2 -> {
                input.inputType = InputType.TYPE_CLASS_NUMBER
            }
            // 对应 XML 中的 phone (3)
            3 -> {
                input.inputType = InputType.TYPE_CLASS_PHONE
            }
            // 对应 XML 中的 email (33)
            33 -> {
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            // 默认 text (1) 或其他
            else -> {
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.transformationMethod = null
            }
        }

        // 保持光标在末尾（防止动态设置时光标跳到开头）
        val text = input.text
        if (text != null) {
            input.setSelection(text.length)
        }
    }

    fun setMaxLength(max: Int) {
        this.mMaxLength = max
        if (max >= 0) {
            val filters = arrayOf<InputFilter>(InputFilter.LengthFilter(max))
            getInput().filters = filters
        } else {
            getInput().filters = emptyArray<InputFilter>()
        }
    }

    fun setInputEnabled(enabled: Boolean) {
        this.mIsInputEnabled = enabled
        getInput().isEnabled = enabled
    }

    fun setDividerVisible(visible: Boolean) {
        this.mShowDivider = visible
        val divider = getDividerView()
        if (visible) {
            divider.visibility = View.VISIBLE
        } else {
            divider.visibility = View.GONE
        }
    }

    fun setCleanUnable(unable: Boolean) {
        this.mIsCleanUnable = unable
        setupCleanAction()
    }

    fun setCleanPadding(pxSize: Int) {
        this.mCleanPadding = pxSize
        val cleanView = getCleanView()
        cleanView.setPadding(pxSize, pxSize, pxSize, pxSize)
    }

    fun setCleanMarginEnd(pxSize: Int) {
        this.mCleanMarginEnd = pxSize
        val cleanView = getCleanView()
        val layoutParams = cleanView.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.marginEnd = pxSize
            cleanView.layoutParams = layoutParams
        }
    }

    // --- 内部逻辑处理 ---

    private fun setupCleanAction() {
        val input = getInput()
        val clean = getCleanView()

        if (mIsCleanUnable) {
            clean.visibility = View.GONE
            return
        }

        val defaultSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            30f,
            resources.displayMetrics
        ).toInt()

        val layoutParams = clean.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            if (mCleanWidth > 0) {
                layoutParams.width = mCleanWidth
            } else {
                layoutParams.width = defaultSize
            }

            if (mCleanHeight > 0) {
                layoutParams.height = mCleanHeight
            } else {
                layoutParams.height = defaultSize
            }
            layoutParams.marginEnd = mCleanMarginEnd
            clean.layoutParams = layoutParams
        }

        clean.setPadding(mCleanPadding, mCleanPadding, mCleanPadding, mCleanPadding)

        // 初始状态显隐判断
        val currentText = input.text
        if (currentText.isNullOrEmpty()) {
            clean.visibility = View.GONE
        } else {
            clean.visibility = View.VISIBLE
        }

        // 文本变化监听
        input.addTextChangedListener { editable ->
            if (editable.isNullOrEmpty()) {
                clean.visibility = View.GONE
            } else {
                clean.visibility = View.VISIBLE
            }
        }

        clean.setOnClickListener {
            input.setText("")
        }
    }

    fun updateDimensions() {
        val label = getLabel()
        val input = getInput()
        val labelLp = label.layoutParams as LinearLayout.LayoutParams
        val inputLp = input.layoutParams as LinearLayout.LayoutParams

        // 优先级 1: 固定宽度处理 (不为 -1 时)
        if (mLabelWidth != DIMENSION_NOT_SET) {
            labelLp.width = mLabelWidth
            labelLp.weight = WEIGHT_INVALID
            inputLp.width = 0
            inputLp.weight = WEIGHT_SUM_MAX
        }
        // 优先级 2: 权重动态计算
        else {
            var finalLabelWeight: Float
            var finalInputWeight: Float

            // 逻辑处理：全未设置时使用默认值
            if (mLabelWeight <= WEIGHT_INVALID && mInputWeight <= WEIGHT_INVALID) {
                finalLabelWeight = DEFAULT_LABEL_WEIGHT
                finalInputWeight = DEFAULT_INPUT_WEIGHT
            }
            // 逻辑处理：仅设置了 Label 权重
            else if (mLabelWeight > WEIGHT_INVALID && mInputWeight <= WEIGHT_INVALID) {
                if (mLabelWeight <= WEIGHT_SUM_MAX) {
                    finalLabelWeight = mLabelWeight
                    finalInputWeight = WEIGHT_SUM_MAX - mLabelWeight
                } else {
                    finalLabelWeight = DEFAULT_LABEL_WEIGHT
                    finalInputWeight = DEFAULT_INPUT_WEIGHT
                }
            }
            // 逻辑处理：仅设置了 Input 权重
            else if (mInputWeight > WEIGHT_INVALID && mLabelWeight <= WEIGHT_INVALID) {
                if (mInputWeight <= WEIGHT_SUM_MAX) {
                    finalInputWeight = mInputWeight
                    finalLabelWeight = WEIGHT_SUM_MAX - mInputWeight
                } else {
                    finalLabelWeight = DEFAULT_LABEL_WEIGHT
                    finalInputWeight = DEFAULT_INPUT_WEIGHT
                }
            }
            // 逻辑处理：两者都设置了，需校验总和
            else {
                val totalWeightSum = mLabelWeight + mInputWeight
                if (totalWeightSum <= WEIGHT_SUM_MAX && mLabelWeight <= WEIGHT_SUM_MAX && mInputWeight <= WEIGHT_SUM_MAX) {
                    finalLabelWeight = mLabelWeight
                    finalInputWeight = mInputWeight
                } else {
                    // 超限回退到 0.32 : 0.68
                    finalLabelWeight = DEFAULT_LABEL_WEIGHT
                    finalInputWeight = DEFAULT_INPUT_WEIGHT
                }
            }

            labelLp.width = 0
            labelLp.weight = finalLabelWeight
            inputLp.width = 0
            inputLp.weight = finalInputWeight
        }

        label.layoutParams = labelLp
        input.layoutParams = inputLp
    }
}
