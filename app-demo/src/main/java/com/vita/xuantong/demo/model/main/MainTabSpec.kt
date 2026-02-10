package com.vita.xuantong.demo.model.main

import android.graphics.Color
import com.vita.xuantong.demo.commons.KYFragment

class MainTabSpec private constructor() {
    var id: Long = 0L
        private set
    var title: String? = null
        private set
    var tag: String? = null
        private set

    /**
     * 0-不展示，1-资源类型，2-远程url类型
     */
    var iconType: Int = 0
        private set

    // 资源类型路径
    var iconNorRes: Int = 0
        private set
    var iconSelRes: Int = 0
        private set

    // 网络 URL 路径
    var iconNorUrl: String? = null
        private set
    var iconSelUrl: String? = null
        private set

    /** 选中文本颜色 */
    var selColor: String? = null
        private set

    /** 非选中文本颜色 */
    var norColor: String? = null
        private set

    var createFragment: () -> KYFragment<*, *> = {
        throw IllegalStateException("createFragment must be initialized")
    }
        private set

    /**
     * 获取选中的颜色 Int 值
     * 默认：#D4AF37
     */
    fun selColor(): Int {
        return getHexColor(selColor, "#D4AF37")
    }

    /**
     * 获取未选中的颜色 Int 值
     * 默认：#8A8A8A
     */
    fun norColor(): Int {
        return getHexColor(norColor, "#8A8A8A")
    }

    /**
     * 解析颜色字符串，校验格式（如 #000000）
     */
    private fun getHexColor(colorStr: String?, default: String): Int {
        return try {
            if (!colorStr.isNullOrEmpty() && colorStr.startsWith("#") && (colorStr.length == 7 || colorStr.length == 9)) {
                Color.parseColor(colorStr)
            } else {
                Color.parseColor(default)
            }
        } catch (e: Exception) {
            Color.parseColor(default)
        }
    }

    class Builder {
        private val spec = MainTabSpec()

        fun setId(id: Long) = apply { spec.id = id }

        fun setTitle(title: String?) = apply { spec.title = title }

        fun setTag(tag: String?) = apply { spec.tag = tag }

        /**
         * 场景 0：不展示图片
         */
        fun setNoIcon() = apply {
            spec.iconType = 0
        }

        /**
         * 场景 1：使用本地资源图片
         */
        fun setIconRes(normal: Int, selected: Int) = apply {
            spec.iconType = 1
            spec.iconNorRes = normal
            spec.iconSelRes = selected
        }

        /**
         * 场景 2：使用远程网络图片
         */
        fun setIconUrl(normal: String?, selected: String?) = apply {
            spec.iconType = 2
            spec.iconNorUrl = normal
            spec.iconSelUrl = selected
        }

        fun setFragment(creator: () -> KYFragment<*, *>) = apply {
            spec.createFragment = creator
        }

        /**
         * 设置 Tab 颜色
         * @param normal 非选中颜色 (例如: #8A8A8A)
         * @param selected 选中颜色 (例如: #D4AF37)
         */
        fun setColors(normal: String?, selected: String?) = apply {
            spec.norColor = normal
            spec.selColor = selected
        }

        fun build(): MainTabSpec = spec
    }
}
