package com.wsvita.core.recycler

import androidx.annotation.LayoutRes
import com.wsvita.framework.commons.IFrameEntity

/**
 * 所有列表展示的 Item 基础接口，用于规范组件化开发中数据的 UI 表现层协议。
 * create by Eastevil at 2025/12/31 10:31
 * @author Eastevil
 */
interface IRecyclerItem : IFrameEntity{

    /**
     * 获取 Item 展示的图标 URL 地址。
     * create by Eastevil at 2025/12/31 10:31
     * @author Eastevil
     * @return
     * 返回图标的远程地址或图片路径；若当前项不需要展示图标，则返回 null。
     */
    fun itemIconUrl(): String? = null

    /**
     * 获取 Item 的详情或副标题描述文本。
     * create by Eastevil at 2025/12/31 10:31
     * @author Eastevil
     * @return
     * 返回详细描述内容；若无需展示详情则返回 null。
     */
    fun itemDetail(): String? = null

    /**
     * 获取当前对象的唯一标识 ID。
     * create by Eastevil at 2025/12/31 10:31
     * @author Eastevil
     * @return
     * 当前对象的唯一 ID，建议用于 DiffUtil 计算以优化列表刷新性能。
     */
    fun recyclerItemId(): Long

    /**
     * 获取当前 Item 的展示类型。
     * create by Eastevil at 2025/12/31 10:31
     * @author Eastevil
     * @return Adapter 将据此自动匹配对应的布局。
     */
    fun recyclerItemType(): Int

    /**
     * 获取自定义布局资源 ID。
     * create by Eastevil at 2025/12/31 10:31
     * @author Eastevil
     * @return 返回对应的 R.layout.xxx 资源 ID。
     */
    @LayoutRes
    fun customLayoutId(): Int;

    /**
     * 当前Item是否被选中
     * create by Eastevil at 2026/1/6 14:47
     * @author Eastevil
     * @param
     * @return
     */
    fun isSelected() : Boolean;
}
