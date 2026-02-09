package com.wsvita.core.recycler

/**
 * Recycler 列表 Item 的基础抽象实体类。
 *
 * 该类作为 [IRecyclerItem] 的抽象实现，主要用于在组件化架构中
 * 为各业务模块提供统一的 RecyclerView 数据实体基类。
 *
 * 使用该抽象类的目的包括但不限于：
 * - 统一实现 RecyclerView Item 的基础能力与协议约束
 * - 作为业务 Item 实体的公共父类，减少重复代码
 * - 方便在 core 层对 Recycler Item 进行统一扩展（如 DiffUtil、日志、埋点等）
 *
 * 业务模块在实现 Recycler 列表数据时，推荐继承本类而非直接实现接口：
 *
 * 示例：
 * ```
 * class UserItemEntity(
 *     private val id: Long
 * ) : RecyclerItemEntity() {
 *
 *     override fun recyclerItemId(): Long = id
 *
 *     override fun recyclerItemType(): Int = RecyclerItemType.USER
 * }
 * ```
 *
 * 说明：
 * - 本类不提供任何默认实现，仅作为结构性抽象基类存在
 * - 若无需继承层级，可直接实现 [IRecyclerItem] 接口
 *
 * create by Eastevil at 2025/12/31
 * @author Eastevil
 */
abstract class RecyclerItemEntity : IRecyclerItem{

    override fun isSelected(): Boolean {
        return false;
    }
}
