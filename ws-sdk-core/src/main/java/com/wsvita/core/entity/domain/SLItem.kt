package com.wsvita.core.entity.domain

import com.wsvita.core.common.BaseEntity

/**
 * 针对组件化项目封装的通用选择项实体
 * 遵循 wsui 命名规范，支持链式调用
 */
open class SLItem : BaseEntity() {

    /**
     * 一个Object类型的标识位，用来存放自定义的值
     */
    private var tagValue : Any? = null;

    // 资源 ID 默认指向 wsui 库中的资源
    var selResId: Int = com.wsvita.ui.R.drawable.ic_ui_seleted_gray
    var norResId: Int = com.wsvita.ui.R.drawable.ic_ui_seleted_gray
    var iconResId: Int = -1
    var title: String? = null
    var iconType: Int = 1
    var haveIcon: Boolean = false

    /**
     * 是否支持选中，
     * true-是，基本上属于符合多选的情况
     */
    var canSeleted: Boolean = false

    var norSeletedGone: Boolean = false

    // --- 链式调用方法开始 ---

    fun selResId(resId: Int) = apply { this.selResId = resId }

    fun norResId(resId: Int) = apply { this.norResId = resId }

    fun iconResId(resId: Int) = apply {
        this.iconResId = resId
        this.haveIcon = resId != -1
    }

    fun title(title: String?) = apply { this.title = title }

    fun iconType(type: Int) = apply { this.iconType = type }

    fun haveIcon(have: Boolean) = apply { this.haveIcon = have }

    fun canSeleted(can: Boolean) = apply { this.canSeleted = can }

    fun norSeletedGone(gone: Boolean) = apply { this.norSeletedGone = gone }

    /**
     * 存入自定义值，支持链式调用
     */
    fun <T : Any> putValue(value: T?) = apply {
        this.tagValue = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(clazz: Class<T>): T? {
        // 1. 判空处理
        val value = tagValue ?: return null

        // 2. 核心修复：处理基本类型(primitive)与包装类的映射
        // 当 clazz 是 int.class 时，将其转换为 Integer.class 供 isInstance 判断
        val targetClass: Class<*> = if (clazz.isPrimitive) {
            when (clazz) {
                java.lang.Integer.TYPE -> java.lang.Integer::class.java
                java.lang.Long.TYPE -> java.lang.Long::class.java
                java.lang.Boolean.TYPE -> java.lang.Boolean::class.java
                java.lang.Double.TYPE -> java.lang.Double::class.java
                java.lang.Float.TYPE -> java.lang.Float::class.java
                else -> clazz
            }
        } else {
            clazz
        }

        // 3. 类型判断与转换
        return if (targetClass.isInstance(value)) {
            value as T
        } else {
            null
        }
    }

    // --- 链式调用方法结束 ---

    companion object {
        @JvmStatic
        fun build(): SLItem = SLItem()
    }

    override fun customLayoutId(): Int {
        // 通常配合 Adapter 架构中的 DataBinding 布局
        return 0
    }
}
