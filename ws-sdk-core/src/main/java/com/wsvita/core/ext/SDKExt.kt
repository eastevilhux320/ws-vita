package com.wsvita.core.ext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * SDK 核心扩展工具类
 * * 专门用于处理组件化开发中的线程调度与常用逻辑扩展。
 */
object SDKExt {

    /**
     * 强制切换到主线程执行指定的 UI 操作。
     * * ### 使用场景
     * 在 `suspend` 挂起函数中进行网络请求、数据库查询或 SDK 数据处理后，
     * 若需操作 View、DataBinding 或更新 `wsui` 前缀的自定义属性，必须调用此方法。
     * * ### 核心逻辑
     * - 使用 [Dispatchers.Main] 调度器。
     * - 这是一个挂起函数，会等待主线程代码块执行完毕后才继续向下执行。
     * - 专门针对“只执行、不返回结果”的任务进行了语义优化。
     * * @param block 需在主线程执行的异步逻辑块
     */
    suspend fun mainThread(block: suspend () -> Unit) {
        withContext(Dispatchers.Main) {
            block()
        }
    }
}
