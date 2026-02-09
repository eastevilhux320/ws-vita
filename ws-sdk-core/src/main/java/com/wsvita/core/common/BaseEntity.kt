package com.wsvita.core.common

import android.content.ContentProvider
import androidx.annotation.StringRes
import com.wsvita.core.R
import com.wsvita.core.recycler.RecyclerItemEntity
import com.wsvita.framework.commons.BaseApplication

/**
 * 业务实体基础抽象类（Base Entity）。
 *
 * 该类是整个项目中业务数据实体的顶层基类之一，
 * 在 [RecyclerItemEntity] 的基础上，进一步提供了
 * 通用的数据字段与 RecyclerView 展示能力的默认实现。
 *
 * 主要职责：
 * - 统一定义业务实体的唯一标识（[id]）
 * - 提供 RecyclerView Item 常用展示字段（图标、详情、选中状态等）
 * - 为列表组件提供默认的 Item 协议实现，减少业务层样板代码
 *
 * 适用场景：
 * - 需要同时作为「业务数据实体」和「RecyclerView Item」的对象
 * - 多模块复用的列表数据模型
 *
 * 说明：
 * - 业务实体若需要参与 RecyclerView 展示，推荐继承本类
 * - 若实体仅用于数据传输或业务计算，可不继承本类
 * - 子类需实现 [recyclerItemType] 以指定具体的 Item 展示类型
 *
 * 示例：
 * ```
 * class UserEntity : BaseEntity() {
 *
 *     override fun recyclerItemType(): Int {
 *         return RecyclerItemType.USER
 *     }
 * }
 * ```
 *
 * create by Eastevil
 * @author Eastevil
 */
abstract class BaseEntity : RecyclerItemEntity() {

    companion object {
        /**
         * BaseEntity 相关日志 Tag。
         */
        private const val TAG = "WSVita_BaseEntity=>"

        /**
         * 未知文本占位符常量。
         *
         * 当业务字段为空或不可用时，可使用该常量作为默认展示文本。
         */
        const val UNKNOW_TEXT = "unknow"
    }

    /**
     * 业务实体唯一标识 ID。
     *
     * 默认值为 -1，表示当前实体尚未持久化或未初始化。
     * 同时作为 RecyclerView Item 的唯一标识，
     * 用于 DiffUtil 等列表差分计算。
     */
    var id: Long = -1

    var appId : Long = 0;

    var state : Int = 0;

    /**
     * 实体创建或生成时间戳（毫秒）。
     *
     * 可用于列表排序、时间展示或埋点分析等场景。
     * 默认值为当前系统时间。
     */
    var itemTime: Long = System.currentTimeMillis()

    /**
     * Item 的详情描述文本。
     *
     * 通常用于 RecyclerView 中的副标题或补充说明展示。
     */
    var detail: String? = null

    /**
     * Item 图标的远程 URL 或本地资源路径。
     *
     * 若为空，则表示当前 Item 不需要展示图标。
     */
    var iconUrl: String? = null

    /**
     * Item 是否处于选中状态。
     *
     * 常用于多选列表、编辑模式或状态切换场景。
     */
    var itemSelect: Boolean = false

    /**
     * 返回 Item 图标地址。
     *
     * 默认使用 [iconUrl] 字段作为 RecyclerView 展示的图标来源。
     */
    override fun itemIconUrl(): String? {
        return iconUrl
    }

    /**
     * 返回 Item 的详情描述内容。
     *
     * 默认使用 [detail] 字段作为 RecyclerView 展示的副标题或说明文本。
     */
    override fun itemDetail(): String? {
        return detail
    }

    /**
     * 返回 RecyclerView Item 的唯一标识 ID。
     *
     * 默认使用 [id] 作为唯一标识，
     * 推荐该值在业务层保持全局唯一，以确保列表差分计算的正确性。
     */
    override fun recyclerItemId(): Long {
        return id
    }

    override fun isSelected(): Boolean {
        return itemSelect;
    }

    override fun recyclerItemType(): Int {
        //默认返回的是对象的布局id，已布局id作为item的展示类型，每一个布局都是不同的类型，子类可重写实现
        return customLayoutId();
    }

    fun unknowText(): String {
        return BaseApplication.app.getString(R.string.app_unknow);
    }

    fun getString(resId : Int): String {
        return BaseApplication.app.getString(resId);
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return BaseApplication.app.getString(resId, *formatArgs)
    }

    fun getColor(resId: Int): Int {
        return BaseApplication.app.getColor(resId);
    }

    fun appDateFormat(): String {
        return getString(R.string.ws_date_time_format_default);
    }
}
