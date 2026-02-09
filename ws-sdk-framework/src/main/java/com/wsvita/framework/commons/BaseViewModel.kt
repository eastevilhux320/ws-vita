package com.wsvita.framework.commons

import android.app.Application
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 组件化框架基础 ViewModel
 * 生命周期感知：通过实现 DefaultLifecycleObserver，支持在子类中直接通过 override 监听 Activity/Fragment 状态。
 * 规范化日志：自动绑定子类类名，便于在组件化多模块环境下快速定位日志来源。
 *
 * create by Administrator at 2025/12/21 16:38
 * @author Administrator
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application), DefaultLifecycleObserver {
    
    /**
     * 生命周期回调：如果子类需要监听则重写
     * 需在 UI 层通过 lifecycle.addObserver(viewModel) 绑定
     */
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        SLog.d(TAG,"onCreate");
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        SLog.d(TAG,"onDestroy");
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        SLog.d(TAG,"onStart");
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        SLog.d(TAG,"onPause");
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        SLog.d(TAG,"onResume");
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SLog.d(TAG,"onStop");
    }

    override fun onCleared() {
        super.onCleared()
        SLog.d(TAG,"onCleared");
    }

    fun systemTime(): Long {
        return System.currentTimeMillis();
    }

    /**
     * Description: 确保逻辑在主线程执行。
     * <p>
     * 利用协程调度器切换上下文，适用于已经在协程作用域内但需要更新 UI 的场景。
     * </p>
     * * create by Eastevil at 2025/12/29 16:12
     * @author Eastevil
     */
    suspend fun withMain(block: suspend () -> Unit) {
        withContext(Dispatchers.Main) {
            block()
        }
    }

    /**
     * 判断当前是否处于主线程
     * @return true 表示在主线程
     */
    fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread === Thread.currentThread()
    }

    companion object{
        private const val TAG = "WSVita_Framework_BaseViewModel==>"
    }
}
