package com.wangshu.textus.note.common

/**
 * Description: 通用数据事件包装类，用于消除魔鬼数字，支持无数据事件分发。
 * create by Administrator at 2026/2/10 23:25
 * @author Administrator
 */
sealed class TextusDataEvent<out T>(val data: T?) {
    class IntEvent(value: Int) : TextusDataEvent<Int>(value)
    class LongEvent(value: Long) : TextusDataEvent<Long>(value)
    class DoubleEvent(value: Double) : TextusDataEvent<Double>(value)
    class StringEvent(value: String) : TextusDataEvent<String>(value)
    /**
     * 代表仅发送通知，不携带具体业务数据（如：刷新指令、关闭弹窗）。
     */
    class Empty : TextusDataEvent<Nothing>(null)
}
