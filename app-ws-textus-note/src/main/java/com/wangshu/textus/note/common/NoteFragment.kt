package com.wangshu.textus.note.common

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.core.common.NavigationFragment
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.launch

/**
 * Description: MVVM 基类 Fragment，负责监听 ViewModel 的通用数据流并分发到钩子方法。
 * create by Administrator at 2026/2/10 23:25
 * @author Administrator
 */
abstract class NoteFragment<D : ViewDataBinding, V : NoteViewModel> : BizcoreFragment<D, V>() {

    /**
     * 核心监听逻辑：订阅 commonDataFlow 并根据 TextusDataEvent 类型分发，禁止省略。
     */
    override fun addObserve() {
        super.addObserve()
        lifecycleScope.launch {
            // 使用 repeatOnLifecycle 确保在 STARTED 状态下收集数据，保证生命周期安全
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.commonDataFlow.collect { event ->
                    when (event) {
                        is TextusDataEvent.Empty -> onReceiveModelEmpty()
                        is TextusDataEvent.IntEvent -> onReceiveModelInt(event.data ?: 0)
                        is TextusDataEvent.LongEvent -> onReceiveModelLong(event.data ?: 0L)
                        is TextusDataEvent.DoubleEvent -> onReceiveModelDouble(event.data ?: 0.0)
                        is TextusDataEvent.StringEvent -> onReceiveModelString(event.data ?: "")
                    }
                }
            }
        }
    }

    /**
     * Description: 接收来自 Model 层的空指令事件
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @return
     */
    open fun onReceiveModelEmpty() {
        SLog.d(TAG, "onReceiveModelEmpty")
    }

    /**
     * Description: 接收来自 Model 层的 Int 数据
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @param value Int数值
     * @return
     */
    open fun onReceiveModelInt(value: Int) {
        SLog.d(TAG, "onReceiveModelInt,value:${value}")
    }

    /**
     * Description: 接收来自 Model 层的 Long 数据
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @param value Long数值
     * @return
     */
    open fun onReceiveModelLong(value: Long) {
        SLog.d(TAG, "onReceiveModelLong,value:${value}")
    }

    /**
     * Description: 接收来自 Model 层的 Double 数据
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @param value Double数值
     * @return
     */
    open fun onReceiveModelDouble(value: Double) {
        SLog.d(TAG, "onReceiveModelDouble,value:${value}")
    }

    /**
     * Description: 接收来自 Model 层的 String 数据
     * create by Administrator at 2026/2/10 23:25
     * @author Administrator
     * @param value String字符串
     * @return
     */
    open fun onReceiveModelString(value: String) {
        SLog.d(TAG, "onReceiveModelString,value:${value}")
    }

    override fun toLogin() {
        val ac = activity;
        if(ac is NoteActivity<*,*>){
            ac.toLogin();
        }
    }

    companion object {
        private const val TAG = "Mirror_Main_DemoFragment=>"
    }
}
