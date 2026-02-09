package com.wsvita.core.recycler

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * [wsui] 专用 Grid 列表分割线
 * * 设计目标：
 * 1. 边缘对齐：第0列左间距为0，最后一列右间距为0。
 * 2. 等分逻辑：通过动态计算，确保所有 Item 在视觉上宽度完全一致。
 * 3. 链式调用：支持 build() 模式，方便业务层配置。
 */
class GridSpaceItemDecoration private constructor() : RecyclerView.ItemDecoration() {

    private var spanCount: Int = 0          // 网格列数
    private var spacingHorizontal: Int = 0  // 内部列间距 (px)
    private var spacingVertical: Int = 0    // 内部行间距 (px)
    private var lineColor: Int = Color.TRANSPARENT // 分割线颜色

    /**
     * 绘制分割线颜色的画笔
     */
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    companion object {
        /**
         * 初始化构建者
         */
        @JvmStatic
        fun build(): GridSpaceItemDecoration {
            return GridSpaceItemDecoration()
        }
    }

    /**
     * 设置网格列数
     * @param spanCount 必须与 LayoutManager 的 spanCount 保持一致
     */
    fun spanCount(spanCount: Int): GridSpaceItemDecoration {
        this.spanCount = spanCount
        return this
    }

    /**
     * 设置间距 (单位均为 px)
     * @param h 内部水平间距
     * @param v 内部垂直间距
     */
    fun spacing(h: Int, v: Int): GridSpaceItemDecoration {
        this.spacingHorizontal = h
        this.spacingVertical = v
        return this
    }

    /**
     * 设置分割线颜色
     * @param color 若不设置或设为 TRANSPARENT，则仅占位不绘制
     */
    fun color(color: Int): GridSpaceItemDecoration {
        this.lineColor = color
        this.paint.color = color
        return this
    }

    /**
     * 计算 Item 的偏移量
     * 核心逻辑：利用 column 的索引比例，动态分配左右间距，抵消边缘留白
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position < 0 || spanCount <= 0) return

        val column = position % spanCount // 当前元素所在的列索引 (0 到 spanCount-1)

        /* * 水平分配原则：
         * left = column * (spacing / spanCount)
         * right = spacing - (column + 1) * (spacing / spanCount)
         * * 举例 (spanCount=10, spacing=10px):
         * column=0 (首列): left=0, right=9px. (完美靠左)
         * column=9 (末列): left=9px, right=0. (完美靠右)
         */
        outRect.left = column * spacingHorizontal / spanCount
        outRect.right = spacingHorizontal - (column + 1) * spacingHorizontal / spanCount

        // 垂直间距：第一行不需要 top 偏移，后续行添加行间距
        if (position >= spanCount) {
            outRect.top = spacingVertical
        }
    }

    /**
     * 在 Item 绘制完成后，绘制颜色线条
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        // 如果没有设置颜色或列数为0，不进行绘制提升性能
        if (lineColor == Color.TRANSPARENT || spanCount <= 0) return

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            val column = position % spanCount
            val params = child.layoutParams as RecyclerView.LayoutParams

            // --- 绘制垂直方向分割线 (Item 右侧间隙) ---
            if (column < spanCount - 1) {
                val vLeft = child.right + params.rightMargin
                val vRight = vLeft + spacingHorizontal
                val vTop = child.top - params.topMargin
                // 底部延伸到 spacingVertical 区域，确保交叉点被填充
                val vBottom = child.bottom + params.bottomMargin + spacingVertical
                c.drawRect(vLeft.toFloat(), vTop.toFloat(), vRight.toFloat(), vBottom.toFloat(), paint)
            }

            // --- 绘制水平方向分割线 (Item 底部间隙) ---
            val hLeft = child.left - params.leftMargin
            // 如果不是最后一列，需要补齐右下角的空白点
            val hRight = child.right + params.rightMargin + (if (column < spanCount - 1) spacingHorizontal else 0)
            val hTop = child.bottom + params.bottomMargin
            val hBottom = hTop + spacingVertical
            c.drawRect(hLeft.toFloat(), hTop.toFloat(), hRight.toFloat(), hBottom.toFloat(), paint)
        }
    }
}
