package com.wsvita.framework.ext

import android.graphics.Color
import android.text.*
import android.text.Annotation
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes

object StringResExt {

    /**
     * 为 TextView 设置带注解的点击文本
     * 专门用于解析 strings.xml 中 <annotation> 标签实现的多语言点击跳转
     *
     * @param resId 字符串资源 ID (必须包含 <annotation> 标签)
     * @param linkColor 链接部分的颜色，默认为蓝色
     * @param isUnderline 是否显示下划线，默认不显示
     * @param onAnnotationClick 点击回调，返回 (key, value)，如 ("action", "user_agreement")
     */
    fun TextView.setAnnotationClick(
        @StringRes resId: Int,
        @ColorInt linkColor: Int = Color.BLUE,
        isUnderline: Boolean = false,
        onAnnotationClick: (key: String, value: String) -> Unit
    ) {
        // 1. 必须使用 getText(resId) 才能获取到包含 AnnotationSpan 的 Spanned 对象
        val fullText = this.context.getText(resId)
        if (fullText !is Spanned) {
            this.text = fullText
            return
        }

        val spannable = SpannableStringBuilder(fullText)

        // 2. 检索文本中所有的 Annotation 跨度
        val annotations = spannable.getSpans(0, spannable.length, Annotation::class.java)

        for (annotation in annotations) {
            val start = spannable.getSpanStart(annotation)
            val end = spannable.getSpanEnd(annotation)

            // 3. 构建点击逻辑与样式
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // 将 xml 中定义的 key 和 value 回传给业务层
                    onAnnotationClick(annotation.key, annotation.value)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = linkColor
                    ds.isUnderlineText = isUnderline
                }
            }

            // 4. 应用 ClickableSpan
            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 5. 必须配置项：设置文本、激活点击方法、清除点击高亮背景
        this.text = spannable
        this.movementMethod = LinkMovementMethod.getInstance()
        this.highlightColor = Color.TRANSPARENT
    }
}
