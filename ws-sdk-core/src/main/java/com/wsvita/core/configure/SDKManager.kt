package com.wsvita.core.configure

import com.wsvita.framework.local.BaseManager
import org.greenrobot.eventbus.EventBus

/**
 * SDK 级业务管理基类
 * * 核心职责：
 * 1. 继承 [BaseManager] 的生命周期管理能力。
 * 2. 封装统一的消息总线 (EventBus) 访问入口，实现组件间解耦通信。
 * 3. 规范化事件发布流程，确保子类 Manager 能以统一的姿势分发业务变更通知。
 *
 * 设计说明：
 * - 采用“事件对象化”策略：建议为每种业务变更定义独立的 Data Class (如 AccountLoginEvent)，
 * 避免使用全局常量 Type 分支，提高代码可读性与类型安全。
 */
abstract class SDKManager : BaseManager() {

    /**
     * 发送普通事件 (Standard Event)
     * * 行为特点：
     * - 瞬时性：事件发出后，只有当前【已经注册并存活】的订阅者能收到通知。
     * - 无状态：事件不被缓存，发送完成后立即销毁。
     * * 适用场景：
     * - 强时效性通知（如：点击刷新按钮触发、支付成功弹窗、单次点击埋点上报）。
     * create by Eastevil at 2026/1/9 13:49
     * @author Eastevil
     * @param event 事件对象实例，建议使用不带状态的 Data Class 或 Object。
     * @return
     *      void
     */
    protected fun <T : Any> postEvent(event: T) {
        EventBus.getDefault().post(event)
    }

    /**
     * 发送粘性事件 (Sticky Event)
     * * 行为特点：
     * - 持久性：EventBus 会在内存中缓存该类型事件的【最后一个】实例。
     * - 追溯性：如果订阅者在事件发送【之后】才注册，注册的一瞬间会收到此缓存事件。
     * * 适用场景：
     * - 全局状态变更（如：登录状态切换、用户信息更新、配置信息变更）。
     * - 确保新打开的 Activity/Fragment 能感知到之前已经发生的关键状态。
     *
     * create by Eastevil at 2026/1/9 13:49
     * @author Eastevil
     * @param event 需缓存的事件实例。
     */
    protected fun <T : Any> postStickyEvent(event: T) {
        EventBus.getDefault().postSticky(event)
    }

    /**
     * 清除特定类型的粘性事件
     * * 行为特点：
     * - 手动清理：粘性事件如果不清理会一直保留在内存中，直到 App 进程结束。
     * * 适用场景：
     * - 状态失效（如：退出登录时，必须清除 [AccountLoginEvent] 的粘性缓存，
     * 防止新进入的页面误认为用户仍处于登录状态）。
     * create by Eastevil at 2026/1/9 13:49
     * @author Eastevil
     * @param eventClass 需清除的事件 Class 类型。
     */
    protected fun <T : Any> removeStickyEvent(eventClass: Class<T>) {
        EventBus.getDefault().removeStickyEvent(eventClass)
    }

    /**
     * 获取特定类型的当前粘性事件
     * * 适用场景：
     * - 在不方便使用订阅模式的非 UI 逻辑中，直接同步查询当前缓存的最新状态。
     */
    protected fun <T : Any> getStickyEvent(eventClass: Class<T>): T? {
        return EventBus.getDefault().getStickyEvent(eventClass)
    }
}
