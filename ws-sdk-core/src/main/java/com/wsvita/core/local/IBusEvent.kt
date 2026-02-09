package com.wsvita.core.local

/**
 * 业务总线事件顶级契约接口 (Bus Event Contract)
 *
 * [设计背景]
 * 在分布式组件架构中，EventBus 的消息散落在各个 Module。如果没有统一的基类，
 * 消息追踪、日志排查、以及 AOP 拦截将变得异常困难。
 *
 * [核心作用]
 * 1. 规范标识：强制所有通过 [SDKManager] 发送的对象必须实现此接口，避免随意发送原始数据类型（如 String/Int）。
 * 2. 链路追踪：通过 [timestamp] 等字段，可以在日志中还原事件发生的先后顺序。
 * 3. 性能监控：配合拦截器，可以测量从事件发送到各订阅者执行完成的耗时。
 *
 * [混淆说明]
 * 开启混淆时，必须保留实现此接口的所有类名：
 * -keep class * implements com.wsvita.core.local.IBusEvent { *; }
 */
interface IBusEvent {

    /**
     * 事件产生的精确时间戳 (单位：毫秒)
     *
     * [用途]
     * 1. UI 逻辑：用于判断事件的时效性（例如，过期的粘性事件可以被过滤掉）。
     * 2. 日志分析：在 Logcat 或埋点日志中，作为排序和排查问题的关键依据。
     *
     * [实现建议]
     * 在具体的 data class 实现中，可以直接通过默认参数获取：
     * override fun timestamp() = System.currentTimeMillis()
     */
    fun timestamp(): Long

    /**
     * 【扩展预留】唯一链路 ID (Trace ID)
     *
     * 如果未来需要进行全链路日志聚合，可以在此处增加此方法，
     * 用于关联“用户操作 -> 接口请求 -> 状态变更 -> 事件发送”的完整过程。
     */
    // fun traceId(): String
}
