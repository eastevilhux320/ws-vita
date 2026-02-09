package com.wsvita.framework.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import com.wsvita.framework.commons.BaseApplication

/**
 * Description: 全局统一交互提示组件 (Toast)。
 * <p>
 * <b>核心职责：</b><br>
 * 1. 提供跨模块一致的轻量级反馈 UI 标准。<br>
 * 2. 封装线程调度逻辑，支持在任意线程发起调用。<br>
 * 3. 实现消息防抖处理，避免多并发请求导致的消息堆积。<br>
 * </p>
 * <p>
 * <b>使用约束：</b><br>
 * - 建议仅用于非阻断性的状态反馈（如：保存成功、网络重试提示）。<br>
 * - 若涉及关键业务决策，请优先使用 Dialog 系列组件。<br>
 * </p>
 * * create by Eastevil at 2025/12/30 11:30
 * @author Eastevil
 */
object VToast {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastToast: String? = null
    private var lastTime: Long = 0

    /**
     * 发送短时间提示消息。
     * @param msg 消息内容，若为空则自动忽略。
     */
    fun show(msg: CharSequence?) {
        if (msg.isNullOrBlank()) return
        execute {
            if (isDuplicate(msg.toString())) return@execute
            Toast.makeText(BaseApplication.app, msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 发送长时间提示消息。
     * @param msg 消息内容。
     */
    fun showLong(msg: CharSequence?) {
        if (msg.isNullOrBlank()) return
        execute {
            Toast.makeText(BaseApplication.app,msg, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 发送资源 ID 对应的提示消息。
     * @param resId 字符串资源标识。
     */
    fun show(@StringRes resId: Int) {
        show(BaseApplication.app.getString(resId))
    }

    /**
     * 发送资源 ID 对应的提示消息。
     * @param resId 字符串资源标识。
     */
    fun showLong(@StringRes resId: Int) {
        showLong(BaseApplication.app.getString(resId))
    }

    /**
     * 线程安全调度。
     */
    private fun execute(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    /**
     * 消息防抖校验。
     * 逻辑：2秒内若出现相同文案，则判定为重复。
     */
    private fun isDuplicate(msg: String): Boolean {
        val now = System.currentTimeMillis()
        val duplicate = (msg == lastToast && now - lastTime < 2000)
        if (!duplicate) {
            lastToast = msg
            lastTime = now
        }
        return duplicate
    }
}
