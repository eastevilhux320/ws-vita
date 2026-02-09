package com.wsvita.core.entity

import com.wsvita.core.local.IBusEvent
import ext.TimeExt.systemTime

/**
 * 业务总线事件抽象基类
 *
 * [设计目标]
 * 1. 自动完成：为所有子类提供默认的 [timestamp] 实现，避免在每个具体的 Data Class 中重复编写时间戳逻辑。
 * 2. 契约统一：确保所有继承自此类的对象都天然符合 [IBusEvent] 契约，能够被 [SDKManager] 安全发送。
 * 3. 架构解耦：业务模块只需关注自身数据，无需关心事件元数据的提取逻辑。
 */
abstract class BaseBusEvent : IBusEvent {

    /**
     * 获取事件产生的系统时间戳
     * 采用项目统一的时间扩展工具 [systemTime]，确保在多组件环境下时间逻辑的一致性。
     */
    override fun timestamp(): Long {
        return systemTime()
    }
}
