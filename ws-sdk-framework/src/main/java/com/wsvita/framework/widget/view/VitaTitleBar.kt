package com.wsvita.framework.widget.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.wsvita.framework.GlideApp
import com.wsvita.framework.R
import com.wsvita.framework.configure.TitleConfigure
import com.wsvita.framework.databinding.LayoutFrameworkTitleBinding

class VitaTitleBar : FrameLayout {

    private lateinit var dataBinding: LayoutFrameworkTitleBinding
    private lateinit var config: ObservableField<TitleConfigure>

    private var listener : OnVitaTitleBarListener? = null;

    constructor(context: Context) : super(context) {
        this.initDefaultConfig()
        this.initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.initAttr(attrs)
        this.initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.initAttr(attrs)
        this.initView()
    }

    private fun initDefaultConfig() {
        val builder = TitleConfigure.Builder()
        val defaultCompose = builder.build()
        this.config = ObservableField(defaultCompose)
    }

    private fun initAttr(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VitaTitleBar)
        val builder = TitleConfigure.Builder()

        // 基础内容设置
        val titleText = typedArray.getString(R.styleable.VitaTitleBar_vita_title_text)
        if (titleText != null) {
            builder.setTitle(titleText)
        }

        builder.setShowBack(typedArray.getBoolean(R.styleable.VitaTitleBar_vita_show_back, true))
        builder.setBackText(typedArray.getString(R.styleable.VitaTitleBar_vita_back_text))
        builder.setMenuText(typedArray.getString(R.styleable.VitaTitleBar_vita_menu_text))
        builder.setBackType(typedArray.getInt(R.styleable.VitaTitleBar_vita_back_type, 1))
        builder.setMenuType(typedArray.getInt(R.styleable.VitaTitleBar_vita_menu_type, 0))
        builder.setBackIconResId(typedArray.getResourceId(R.styleable.VitaTitleBar_vita_back_icon, 0))
        builder.setMenuIconResId(typedArray.getResourceId(R.styleable.VitaTitleBar_vita_menu_icon, 0))
        builder.setMenuTextColor(typedArray.getColor(R.styleable.VitaTitleBar_vita_menu_text_color,Color.BLACK));

        // 样式设置
        val titleColor = typedArray.getColor(R.styleable.VitaTitleBar_vita_title_color, Color.BLACK)
        builder.setTitleColor(titleColor)

        val titleSize = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_vita_title_size, 0)
        builder.setTitleSize(titleSize.toFloat())

        val backgroundDrawable = typedArray.getDrawable(R.styleable.VitaTitleBar_vita_bar_background)
        if (backgroundDrawable != null) {
            builder.setBackground(backgroundDrawable)
        }

        // 尺寸设置
        val backW = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_vita_back_width, 0)
        val backH = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_vita_back_height, 0)
        builder.setBackSize(backW, backH)

        val menuW = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_vita_menuicon_width, 0)
        val menuH = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_vita_menuicon_height, 0)
        builder.setMenuSize(menuW, menuH)

        // 边距设置
        val backMarginAll = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_back_icon_margin, 0)
        val bml = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_back_icon_marginLeft, backMarginAll)
        val bmt = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_back_icon_marginTop, backMarginAll)
        val bmr = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_back_icon_marginRight, backMarginAll)
        val bmb = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_back_icon_marginBottom, backMarginAll)
        builder.setBackMargins(bml, bmt, bmr, bmb)

        val menuMarginAll = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_menu_icon_margin, 0)
        val mml = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_menu_icon_marginLeft, menuMarginAll)
        val mmt = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_menu_icon_marginTop, menuMarginAll)
        val mmr = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_menu_icon_marginRight, menuMarginAll)
        val mmb = typedArray.getDimensionPixelSize(R.styleable.VitaTitleBar_menu_icon_marginBottom, menuMarginAll)
        builder.setMenuMargins(mml, mmt, mmr, mmb)

        typedArray.recycle()
        this.config = ObservableField(builder.build())
    }

    private fun initView() {
        val inflater = LayoutInflater.from(context)
        this.dataBinding = DataBindingUtil.inflate(inflater, R.layout.layout_framework_title, this, true)
        this.dataBinding.config = this.config
        this.dataBinding.vitaTitleBar = this;

        this.applyStyles()
        this.syncViewState()

        this.dataBinding.executePendingBindings()
    }

    private fun applyStyles() {
        val currentConfig = this.config.get()
        if (currentConfig == null) {
            return
        }

        // 1. 标题文字样式
        this.dataBinding.tvSlTitle.setTextColor(currentConfig.titleColor)
        if (currentConfig.titleSizePx > 0) {
            this.dataBinding.tvSlTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentConfig.titleSizePx)
        }
        if (currentConfig.titleTypeface != null) {
            this.dataBinding.tvSlTitle.typeface = currentConfig.titleTypeface
        }
        this.dataBinding.tvSlTitle.paint.isFakeBoldText = currentConfig.isTitleBold

        // 2. 背景设置
        if (currentConfig.background != null) {
            this.dataBinding.root.background = currentConfig.background
        }

        // 3. 返回键布局参数
        val backParams = this.dataBinding.ivSlBack.layoutParams
        if (backParams is ConstraintLayout.LayoutParams) {
            if (currentConfig.backWidth > 0) {
                backParams.width = currentConfig.backWidth
            }
            if (currentConfig.backHeight > 0) {
                backParams.height = currentConfig.backHeight
            }
            backParams.setMargins(
                currentConfig.backMarginLeft,
                currentConfig.backMarginTop,
                currentConfig.backMarginRight,
                currentConfig.backMarginBottom
            )
            this.dataBinding.ivSlBack.layoutParams = backParams
        }

        // 4. 菜单键布局参数
        val menuParams = this.dataBinding.ivSlMenuimage.layoutParams
        if (menuParams is ConstraintLayout.LayoutParams) {
            if (currentConfig.menuIconWidth > 0) {
                menuParams.width = currentConfig.menuIconWidth
            }
            if (currentConfig.menuIconHeight > 0) {
                menuParams.height = currentConfig.menuIconHeight
            }
            menuParams.setMargins(
                currentConfig.menuMarginLeft,
                currentConfig.menuMarginTop,
                currentConfig.menuMarginRight,
                currentConfig.menuMarginBottom
            )
            this.dataBinding.ivSlMenuimage.layoutParams = menuParams
        }

        // 3. 应用返回键 Padding
        this.dataBinding.ivSlBack.setPadding(
            currentConfig.backPaddingLeft,
            currentConfig.backPaddingTop,
            currentConfig.backPaddingRight,
            currentConfig.backPaddingBottom
        )

        // 4. 应用菜单键 Padding
        this.dataBinding.ivSlMenuimage.setPadding(
            currentConfig.menuPaddingLeft,
            currentConfig.menuPaddingTop,
            currentConfig.menuPaddingRight,
            currentConfig.menuPaddingBottom
        )
    }

    private fun syncViewState() {
        val currentConfig = this.config.get()
        if (currentConfig == null) {
            return
        }

        // 标题显示逻辑
        if (currentConfig.haveTitle) {
            this.dataBinding.llSlTitle.visibility = View.VISIBLE
        } else {
            this.dataBinding.llSlTitle.visibility = View.GONE
        }

        // 返回键区域逻辑 (1:img, 2:text, 3:both)
        val bType = currentConfig.backType
        if (bType == 1 || bType == 3) {
            this.dataBinding.ivSlBack.visibility = View.VISIBLE
        } else {
            this.dataBinding.ivSlBack.visibility = View.GONE
        }

        if (bType == 2 || bType == 3) {
            this.dataBinding.tvSlBack.visibility = View.VISIBLE
        } else {
            this.dataBinding.tvSlBack.visibility = View.GONE
        }

        if (currentConfig.backIconResId != 0) {
            this.dataBinding.ivSlBack.setImageResource(currentConfig.backIconResId)
        }
        this.dataBinding.tvSlBack.text = currentConfig.backText

        // 菜单键区域逻辑
        val mType = currentConfig.menuType
        if (mType == 1 || mType == 3) {
            this.dataBinding.ivSlMenuimage.visibility = View.VISIBLE
        } else {
            this.dataBinding.ivSlMenuimage.visibility = View.GONE
        }

        if (mType == 2 || mType == 3) {
            this.dataBinding.tvSlMenutext.visibility = View.VISIBLE
            this.dataBinding.tvSlMenutext.setTextColor(config.get()?.menuTextColor?:Color.BLACK);
        } else {
            this.dataBinding.tvSlMenutext.visibility = View.GONE
        }

        this.syncMenuIcon()
    }

    private fun syncMenuIcon() {
        val currentConfig = this.config.get()
        if (currentConfig == null) {
            return
        }

        val url = currentConfig.menuIconUrl
        if (url != null && url.isNotEmpty()) {
            GlideApp.with(this.dataBinding.ivSlMenuimage)
                .load(url)
                .into(this.dataBinding.ivSlMenuimage)
        } else {
            val resId = currentConfig.menuIconResId
            if (resId != null && resId != 0) {
                this.dataBinding.ivSlMenuimage.setImageResource(resId)
            }
        }
    }

    fun onViewClick(view : View){
        when(view.id){
            R.id.tv_sl_back,
            R.id.iv_sl_back->{
                listener?.onBackClick(view);
            }
            R.id.tv_sl_menutext,
            R.id.iv_sl_menuimage->{
                listener?.onMenuClick(view);
            }
        }
    }

    fun setOnVitaTitleListener(onVitaTitleBarListener: OnVitaTitleBarListener){
        this.listener = onVitaTitleBarListener;
    }

    // 显式接口定义
    fun setTitleText(title: String) {
        val builder = TitleConfigure.Builder(this.config.get()!!)
        builder.setTitle(title)
        this.updateConfigByBuilder(builder)
    }

    fun setTitleText(titleResId : Int){
        val str = context.getString(titleResId);
        setTitleText(str);
    }

    fun updateConfigByBuilder(builder: TitleConfigure.Builder) {
        val newConfig = builder.build()
        this.config.set(newConfig)
        this.applyStyles()
        this.syncViewState()
        this.dataBinding.executePendingBindings()
    }

    /**
     * 设置标题文字颜色
     */
    fun setTitleColor(@ColorInt color: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setTitleColor(color)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置返回键图标资源
     */
    fun setBackIconResource(@DrawableRes resId: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setBackIconResId(resId)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置返回键图标的宽高尺寸 (单位: Px)
     */
    fun setBackSize(widthPx: Int, heightPx: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setBackSize(widthPx, heightPx)
            this.updateConfigByBuilder(builder)
        }
    }

    fun setMenuSize(widthPx: Int, heightPx: Int){
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setMenuSize(widthPx, heightPx)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 统一设置菜单键四个方向的内边距 (单位: Px)
     */
    fun setMenuPadding(paddingPx: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setMenuPadding(paddingPx,paddingPx,paddingPx,paddingPx)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 统一设置返回键四个方向的内边距 (单位: Px)
     */
    fun setBackPadding(paddingPx: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setBackPadding(paddingPx,paddingPx,paddingPx,paddingPx)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置返回键显示类型
     * @param type 0:不显示, 1:图标, 2:文字, 3:图标和文字
     */
    fun setBackType(type: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setBackType(type)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置菜单显示类型
     * @param type 0:不显示, 1:图标, 2:文字, 3:图标和文字
     */
    fun setMenuType(type : Int){
        val current = this.config.get();
        if(current != null){
            val builder = TitleConfigure.Builder(current)
            builder.setMenuType(type)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置标题文字大小 (单位: Px)
     */
    fun setTitleSize(sizePx: Float) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setTitleSize(sizePx)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置标题是否加粗
     */
    fun setTitleBold(isBold: Boolean) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setTitleBold(isBold)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置菜单键文字
     */
    fun setMenuText(text: String) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setMenuText(text)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置菜单键图标资源
     */
    fun setMenuIconResource(@DrawableRes resId: Int) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setMenuIconResId(resId)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置返回按钮的点击事件监听
     */
    fun setOnBackClickListener(listener: (View) -> Unit) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setOnBackClick(listener)
            this.updateConfigByBuilder(builder)
        }
    }

    /**
     * 设置菜单按钮的点击事件监听
     */
    fun setOnMenuClickListener(listener: (View) -> Unit) {
        val current = this.config.get()
        if (current != null) {
            val builder = TitleConfigure.Builder(current)
            builder.setOnMenuClick(listener)
            this.updateConfigByBuilder(builder)
        }
    }

    open interface OnVitaTitleBarListener{

        fun onBackClick(view : View);

        fun onMenuClick(view : View);
    }

}
