package com.wsvita.framework.configure

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

class TitleConfigure private constructor(builder: Builder) {

    companion object {
        fun getDefault(): TitleConfigure {
            val builder = Builder()
            return builder.build()
        }
    }

    // 属性显式定义
    var title: String = builder.title
    var haveTitle: Boolean = builder.haveTitle
    var showBack: Boolean = builder.showBack
    var backText: String? = builder.backText
    var menuText: String? = builder.menuText
    var menuIconResId: Int? = builder.menuIconResId
    var menuIconUrl: String? = builder.menuIconUrl
    var menuTextColor : Int = Color.BLACK;

    var backIconResId: Int = builder.backIconResId

    var onTitleClick: ((View) -> Unit)? = builder.onTitleClick
    var onBackClick: ((View) -> Unit)? = builder.onBackClick
    var onMenuClick: ((View) -> Unit)? = builder.onMenuClick

    @ColorInt var titleColor: Int = builder.titleColor
    var titleSizePx: Float = builder.titleSizePx
    var titleTypeface: Typeface? = builder.titleTypeface
    var isTitleBold: Boolean = builder.isTitleBold
    var background: Drawable? = builder.background

    var backType: Int = builder.backType
    var menuType: Int = builder.menuType

    var backWidth: Int = builder.backWidth
    var backHeight: Int = builder.backHeight
    var menuIconWidth: Int = builder.menuIconWidth
    var menuIconHeight: Int = builder.menuIconHeight

    var backMarginLeft: Int = builder.backMarginLeft
    var backMarginTop: Int = builder.backMarginTop
    var backMarginRight: Int = builder.backMarginRight
    var backMarginBottom: Int = builder.backMarginBottom

    var menuMarginLeft: Int = builder.menuMarginLeft
    var menuMarginTop: Int = builder.menuMarginTop
    var menuMarginRight: Int = builder.menuMarginRight
    var menuMarginBottom: Int = builder.menuMarginBottom

    // 新增 Padding 属性
    var backPaddingLeft: Int = builder.backPaddingLeft
    var backPaddingTop: Int = builder.backPaddingTop
    var backPaddingRight: Int = builder.backPaddingRight
    var backPaddingBottom: Int = builder.backPaddingBottom

    var menuPaddingLeft: Int = builder.menuPaddingLeft
    var menuPaddingTop: Int = builder.menuPaddingTop
    var menuPaddingRight: Int = builder.menuPaddingRight
    var menuPaddingBottom: Int = builder.menuPaddingBottom

    class Builder {
        // 显式赋初始值
        var title: String = ""
        var haveTitle: Boolean = true
        var showBack: Boolean = true
        var backText: String? = null
        var menuText: String? = null
        var menuIconResId: Int? = null
        var menuIconUrl: String? = null
        var backIconResId: Int = 0

        var onTitleClick: ((View) -> Unit)? = null
        var onBackClick: ((View) -> Unit)? = null
        var onMenuClick: ((View) -> Unit)? = null
        var menuTextColor : Int = Color.BLACK;

        @ColorInt var titleColor: Int = Color.BLACK
        var titleSizePx: Float = 0f
        var titleTypeface: Typeface? = null
        var isTitleBold: Boolean = false
        var background: Drawable? = null

        var backType: Int = 1
        var menuType: Int = 0

        var backWidth: Int = 0
        var backHeight: Int = 0
        var menuIconWidth: Int = 0
        var menuIconHeight: Int = 0

        var backMarginLeft: Int = 0
        var backMarginTop: Int = 0
        var backMarginRight: Int = 0
        var backMarginBottom: Int = 0

        var menuMarginLeft: Int = 0
        var menuMarginTop: Int = 0
        var menuMarginRight: Int = 0
        var menuMarginBottom: Int = 0

        // 初始值定义
        var backPaddingLeft: Int = 0
        var backPaddingTop: Int = 0
        var backPaddingRight: Int = 0
        var backPaddingBottom: Int = 0

        var menuPaddingLeft: Int = 0
        var menuPaddingTop: Int = 0
        var menuPaddingRight: Int = 0
        var menuPaddingBottom: Int = 0

        constructor() {
            // 默认构造
        }

        constructor(config: TitleConfigure) {
            this.title = config.title
            this.haveTitle = config.haveTitle
            this.showBack = config.showBack
            this.backText = config.backText
            this.menuText = config.menuText
            this.menuIconResId = config.menuIconResId
            this.menuIconUrl = config.menuIconUrl
            this.backIconResId = config.backIconResId
            this.onTitleClick = config.onTitleClick
            this.onBackClick = config.onBackClick
            this.onMenuClick = config.onMenuClick
            this.titleColor = config.titleColor
            this.titleSizePx = config.titleSizePx
            this.titleTypeface = config.titleTypeface
            this.isTitleBold = config.isTitleBold
            this.background = config.background
            this.backType = config.backType
            this.menuType = config.menuType
            this.backWidth = config.backWidth
            this.backHeight = config.backHeight
            this.menuIconWidth = config.menuIconWidth
            this.menuIconHeight = config.menuIconHeight
            this.backMarginLeft = config.backMarginLeft
            this.backMarginTop = config.backMarginTop
            this.backMarginRight = config.backMarginRight
            this.backMarginBottom = config.backMarginBottom
            this.menuMarginLeft = config.menuMarginLeft
            this.menuMarginTop = config.menuMarginTop
            this.menuMarginRight = config.menuMarginRight
            this.menuMarginBottom = config.menuMarginBottom
            this.backPaddingLeft = config.backPaddingLeft
            this.backPaddingTop = config.backPaddingTop
            this.backPaddingRight = config.backPaddingRight
            this.backPaddingBottom = config.backPaddingBottom
            this.menuPaddingLeft = config.menuPaddingLeft
            this.menuPaddingTop = config.menuPaddingTop
            this.menuPaddingRight = config.menuPaddingRight
            this.menuPaddingBottom = config.menuPaddingBottom
            this.menuTextColor = config.menuTextColor;
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setHaveTitle(have: Boolean): Builder {
            this.haveTitle = have
            return this
        }

        fun setShowBack(show: Boolean): Builder {
            this.showBack = show
            return this
        }

        fun setBackText(text: String?): Builder {
            this.backText = text
            return this
        }

        fun setMenuText(text: String?): Builder {
            this.menuText = text
            return this
        }

        fun setMenuTextColor(menuTextColor : Int): Builder {
            this.menuTextColor = menuTextColor;
            return this;
        }

        fun setMenuIconResId(@DrawableRes resId: Int?): Builder {
            this.menuIconResId = resId
            return this
        }

        fun setMenuIconUrl(url: String?): Builder {
            this.menuIconUrl = url
            return this
        }

        fun setBackIconResId(@DrawableRes resId: Int): Builder {
            this.backIconResId = resId
            return this
        }

        fun setTitleColor(@ColorInt color: Int): Builder {
            this.titleColor = color
            return this
        }

        fun setTitleSize(sizePx: Float): Builder {
            this.titleSizePx = sizePx
            return this
        }

        fun setTitleTypeface(typeface: Typeface?): Builder {
            this.titleTypeface = typeface
            return this
        }

        fun setTitleBold(bold: Boolean): Builder {
            this.isTitleBold = bold
            return this
        }

        fun setBackground(drawable: Drawable?): Builder {
            this.background = drawable
            return this
        }

        fun setBackType(type: Int): Builder {
            this.backType = type
            return this
        }

        fun setMenuType(type: Int): Builder {
            this.menuType = type
            return this
        }

        fun setBackSize(w: Int, h: Int): Builder {
            this.backWidth = w
            this.backHeight = h
            return this
        }

        fun setMenuSize(w: Int, h: Int): Builder {
            this.menuIconWidth = w
            this.menuIconHeight = h
            return this
        }

        fun setBackMargins(l: Int, t: Int, r: Int, b: Int): Builder {
            this.backMarginLeft = l
            this.backMarginTop = t
            this.backMarginRight = r
            this.backMarginBottom = b
            return this
        }

        fun setMenuMargins(l: Int, t: Int, r: Int, b: Int): Builder {
            this.menuMarginLeft = l
            this.menuMarginTop = t
            this.menuMarginRight = r
            this.menuMarginBottom = b
            return this
        }

        // 提供 Setter 方法
        fun setBackPadding(l: Int, t: Int, r: Int, b: Int): Builder {
            this.backPaddingLeft = l
            this.backPaddingTop = t
            this.backPaddingRight = r
            this.backPaddingBottom = b
            return this
        }

        fun setMenuPadding(l: Int, t: Int, r: Int, b: Int): Builder {
            this.menuPaddingLeft = l
            this.menuPaddingTop = t
            this.menuPaddingRight = r
            this.menuPaddingBottom = b
            return this
        }

        fun setOnBackClick(click: (View) -> Unit): Builder {
            this.onBackClick = click
            return this
        }

        fun setOnMenuClick(click: (View) -> Unit): Builder {
            this.onMenuClick = click
            return this
        }

        fun build(): TitleConfigure {
            return TitleConfigure(this)
        }
    }
}
