package com.wsvita.account.local

import com.wsvita.core.entity.BaseBusEvent

/**
 * 账号组件业务事件基类
 * * [设计职责]
 * 1. 业务隔离：继承自 [BaseBusEvent]，代表所有账号相关的业务变动协议。
 * 2. 标识作用：在 EventBus 订阅端可以通过此基类进行过滤或统一日志记录。
 * 3. 规范化：确保账号模块发出的所有事件都携带了 [BaseBusEvent] 的时间戳等元数据。
 */
abstract class AccountEvent : BaseBusEvent() {

}
