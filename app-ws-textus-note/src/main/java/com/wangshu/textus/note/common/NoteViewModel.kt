package com.wangshu.textus.note.common

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.core.common.NavigationViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class NoteViewModel(application: Application) : BizcoreViewModel(application) {

    /**
     * 【核心通道】
     */
    private val _commonDataFlow = MutableSharedFlow<TextusDataEvent<*>>()
    val commonDataFlow = _commonDataFlow.asSharedFlow()

    /**
     * 【公共分发】发送 Int 类型基础数据事件。
     *
     * 核心逻辑：
     * 1. 自动将原始 Int 包装为 [TextusDataEvent.IntEvent]，彻底消除硬编码 String 标识。
     * 2. 通过 [viewModelScope] 启动协程，确保在 UI 线程安全分发。
     * 3. 利用 SharedFlow 的非粘性特性，驱动 View 层执行一次性 UI 逻辑。
     *
     * @param value 需要传递的整型数值（如：任务状态码、列表索引、业务标识位）。
     * @author Administrator
     * @date 2026/2/10 23:25
     */
    fun sendInt(value : Int){
        sendCommonData(value);
    }

    /**
     * 【公共分发】发送 Long 类型基础数据事件。
     *
     * @param value 长整型数值（如：RELEASE_TIME 时间戳 ）。
     * @author Administrator
     * @date 2026/2/10 23:25
     */
    fun sendLong(value: Long) {
        sendCommonData(value)
    }

    /**
     * 【公共分发】发送 Double 类型基础数据事件。
     *
     * @param value 浮点型数值（如：分发任务金额、汇率等）。
     * @author Administrator
     * @date 2026/2/10 23:25
     */
    fun sendDouble(value: Double) {
        sendCommonData(value)
    }

    /**
     * 【公共分发】发送 String 类型基础数据事件。
     *
     * @param value 字符串数据（如：VERSION_DESCRIPTION ）。
     * @author Administrator
     * @date 2026/2/10 23:25
     */
    fun sendString(value: String) {
        sendCommonData(value)
    }

    /**
     * Description: 发送空事件指令（不带数据）
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @return
     */
    fun sendEmpty() {
        viewModelScope.launch {
            _commonDataFlow.emit(TextusDataEvent.Empty())
        }
    }

    /**
     * 【架构核心】通用数据封装与分发中枢。
     *
     * 【设计初衷】：
     * 为了解决全站 MVVM 架构中，业务层通过散乱的 String 或魔鬼数字（Magic Numbers）发送通知的问题 [cite: 1, 3]。
     *
     * 【执行流程】：
     * 1. 类型检查：通过 `when` 表达式强制匹配预定义的 [TextusDataEvent] 类型 [cite: 1]。
     * 2. 安全包装：将 [T] 转换为对应的密封类子类，确保 View 层通过 `is` 即可判断类型 [cite: 1]。
     * 3. 异步发射：利用 [MutableSharedFlow.emit] 挂起函数，保证分发顺序与线程安全 [cite: 1]。
     *
     * @param T 限定为 Any，支持 Int, Long, Double, String。
     * @param value 待分发的原始业务数据。
     * @throws IllegalArgumentException 当传入望舒系统未定义的非法类型时抛出，用于强制规范开发。
     */
    private fun <T : Any> sendCommonData(value: T) {
        viewModelScope.launch {
            val event = when (value) {
                is Int -> TextusDataEvent.IntEvent(value)
                is Long -> TextusDataEvent.LongEvent(value)
                is Double -> TextusDataEvent.DoubleEvent(value)
                is String -> TextusDataEvent.StringEvent(value)
                else -> throw IllegalArgumentException("不支持的类型")
            }
            _commonDataFlow.emit(event)
        }
    }
}
