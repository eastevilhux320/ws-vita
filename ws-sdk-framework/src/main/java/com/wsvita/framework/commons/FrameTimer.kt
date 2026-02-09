package com.wsvita.framework.commons

import android.os.CountDownTimer

/**
 * [框架通用倒计时工具]
 * 设计规范：
 * 1. 禁止隐式构造：必须通过 [build] 静态工厂方法创建。
 * 2. 显式构造：私有化 constructor 并显式调用 super(long, long)。
 * 3. 链式调用：支持 [onTick] 与 [onFinish] 的流式配置。
 * 4. 统一单位：全量使用毫秒 (ms)，不再进行内部单位转换。
 */
class FrameTimer : CountDownTimer {

    private var tickAction: ((Long) -> Unit)? = null
    private var finishAction: (() -> Unit)? = null

    companion object {
        private const val TAG = "WSV_F_FrameTimer=>"

        /**
         * 静态工厂方法：实例化 FrameTimer 的唯一入口。
         * @param millisInFuture 倒计时总时长（毫秒）。
         * @param countDownInterval 倒计时间隔（毫秒）。
         */
        @JvmStatic
        fun build(millisInFuture: Long, countDownInterval: Long): FrameTimer {
            return FrameTimer(millisInFuture, countDownInterval)
        }
    }

    // 私有构造函数，强制显式调用父类
    private constructor(millisInFuture: Long, countDownInterval: Long) : super(
        millisInFuture,
        countDownInterval
    )

    /**
     * 设置计时回调（毫秒）
     */
    fun onTick(action: (Long) -> Unit): FrameTimer {
        this.tickAction = action
        return this
    }

    /**
     * 设置结束回调
     */
    fun onFinish(action: () -> Unit): FrameTimer {
        this.finishAction = action
        return this
    }

    override fun onTick(millisUntilFinished: Long) {
        tickAction?.invoke(millisUntilFinished)
    }

    override fun onFinish() {
        finishAction?.invoke()
    }
}
