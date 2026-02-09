package com.wsvita.ui.view.side

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.wsvita.ui.R
import com.wsvita.ui.common.BaseLayout
import com.wsvita.ui.databinding.LayoutUiMeteSimpleBinding

class SimpleMete : BaseLayout<LayoutUiMeteSimpleBinding>{

    // Label 成员变量
    private var mLabelText: String? = null
    private var mLabelColor: Int = 0
    private var mLabelSize: Int = VALUE_ZERO
    private var mLabelWidth: Int = VALUE_ZERO
    private var mLableMarginLeft: Int = VALUE_ZERO
    private var mLableMarginRight: Int = VALUE_ZERO
    private var mLableMarginTop: Int = VALUE_ZERO
    private var mLableMarginBottom: Int = VALUE_ZERO

    // Action 成员变量
    private var mActionText: String? = null
    private var mActionColor: Int = 0
    private var mActionSize: Int = VALUE_ZERO
    private var mActionMarginLeft: Int = VALUE_ZERO
    private var mActionMarginRight: Int = VALUE_ZERO
    private var mActionMarginTop: Int = VALUE_ZERO
    private var mActionMarginBottom: Int = VALUE_ZERO

    // 其他控制
    private var mShowArrow: Boolean = true
    private var mArrwoDrawable : Drawable? = null;
    private var mArrwoWidth: Int = VALUE_ZERO
    private var mArrwoHeight: Int = VALUE_ZERO

    private var mArrwoMarginLeft: Int = VALUE_ZERO
    private var mArrwoMarginRight: Int = VALUE_ZERO
    private var mArrwoMarginTop: Int = VALUE_ZERO
    private var mArrwoMarginBottom: Int = VALUE_ZERO

    constructor(context: Context) : super(context){

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){

    }

    override fun layoutId(): Int {
        return R.layout.layout_ui_mete_simple;
    }

    override fun initAttr(attrs: AttributeSet) {
        super.initAttr(attrs)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SimpleMete)

        // 解析并赋值给 m 前缀变量
        mLabelText = ta.getString(R.styleable.SimpleMete_wsui_label)
        mLabelColor = ta.getColor(R.styleable.SimpleMete_wsui_labelColor, 0)
        mLabelSize = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_labelSize, VALUE_ZERO)
        mLabelWidth = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_labelWidth, VALUE_ZERO)

        mLableMarginLeft = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_lableMarginLeft, VALUE_ZERO)
        mLableMarginRight = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_lableMarginRight, VALUE_ZERO)
        mLableMarginTop = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_lableMarginTop, VALUE_ZERO)
        mLableMarginBottom = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_lableMarginBottom, VALUE_ZERO)

        mActionText = ta.getString(R.styleable.SimpleMete_wsui_action_text)
        mActionColor = ta.getColor(R.styleable.SimpleMete_wsui_action_color, 0)
        mActionSize = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_action_size, VALUE_ZERO)

        mActionMarginLeft = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_actionMarginLeft, VALUE_ZERO)
        mActionMarginRight = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_actionMarginRight, VALUE_ZERO)
        mActionMarginTop = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_actionMarginTop, VALUE_ZERO)
        mActionMarginBottom = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_actionMarginBottom, VALUE_ZERO)

        mShowArrow = ta.getBoolean(R.styleable.SimpleMete_wsui_showArrow, true)
        mArrwoDrawable = ta.getDrawable(R.styleable.SimpleMete_wsui_arrow_src);
        mArrwoWidth = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrow_width, VALUE_ZERO)
        mArrwoHeight = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrow_height, VALUE_ZERO)

        mArrwoMarginLeft = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrowMarginLeft, VALUE_ZERO)
        mArrwoMarginRight = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrowMarginRight, VALUE_ZERO)
        mArrwoMarginTop = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrowMarginTop, VALUE_ZERO)
        mArrwoMarginBottom = ta.getDimensionPixelSize(R.styleable.SimpleMete_wsui_arrowMarginBottom, VALUE_ZERO)

        ta.recycle()
    }

    override fun initView() {
        super.initView()
        renderView();
    }

    override fun onBind() {

    }

    private fun renderView() {
        // --- Label 视图更新 (禁止使用 apply) ---
        dataBinding.tvUiSideLabel.text = mLabelText
        if (mLabelColor != 0) {
            dataBinding.tvUiSideLabel.setTextColor(mLabelColor)
        }
        if (mLabelSize > VALUE_ZERO) {
            dataBinding.tvUiSideLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelSize.toFloat())
        }
        val lParams = dataBinding.tvUiSideLabel.layoutParams as ConstraintLayout.LayoutParams
        if (mLabelWidth > VALUE_ZERO) {
            lParams.width = mLabelWidth
        }
        lParams.setMargins(mLableMarginLeft, mLableMarginTop, mLableMarginRight, mLableMarginBottom)
        dataBinding.tvUiSideLabel.layoutParams = lParams

        // --- Action 视图更新 ---
        dataBinding.tvUiSideAction.text = mActionText
        if (mActionColor != 0) {
            dataBinding.tvUiSideAction.setTextColor(mActionColor)
        }
        if (mActionSize > VALUE_ZERO) {
            dataBinding.tvUiSideAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActionSize.toFloat())
        }
        val aParams = dataBinding.tvUiSideAction.layoutParams as ConstraintLayout.LayoutParams
        aParams.setMargins(mActionMarginLeft, mActionMarginTop, mActionMarginRight, mActionMarginBottom)
        dataBinding.tvUiSideAction.layoutParams = aParams

        // --- Arrow 显隐与样式设置 ---
        if (mShowArrow) {
            dataBinding.tvUiSideArrow.visibility = View.VISIBLE

            // 设置图片源
            if (mArrwoDrawable != null) {
                dataBinding.tvUiSideArrow.setImageDrawable(mArrwoDrawable)
            }

            // 处理 LayoutParams (尺寸与边距)
            val arrowParams = dataBinding.tvUiSideArrow.layoutParams as ConstraintLayout.LayoutParams
            if (mArrwoWidth > VALUE_ZERO) {
                arrowParams.width = mArrwoWidth
            }
            if (mArrwoHeight > VALUE_ZERO) {
                arrowParams.height = mArrwoHeight
            }
            // 显式应用模拟实现的四个方向边距
            arrowParams.setMargins(mArrwoMarginLeft, mArrwoMarginTop, mArrwoMarginRight, mArrwoMarginBottom)

            dataBinding.tvUiSideArrow.layoutParams = arrowParams
        } else {
            dataBinding.tvUiSideArrow.visibility = View.GONE
        }
    }

    fun setLabel(label : String?){
        mLabelText = label;
        dataBinding.tvUiSideLabel.setText(mLabelText);
    }
}
