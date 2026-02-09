package com.wsvita.framework.commons

import android.app.Application
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

/**
 * 框架层基础 Application，所有后续开发的app的application都需要继承这个类。
 * 在所有组件中，可能都需要使用此类来获取各种content等对象
 *
 * @author Eastevil
 * @createTime 2025/12/24
 */
abstract class BaseApplication : Application() {

    companion object {
        private const val TAG = "WSVita_Framework_BaseApplication==>";
        /**
         * 全局 Application 实例代理
         */
        private lateinit var instance: BaseApplication

        @JvmStatic
        val app: BaseApplication
            get() = instance
    }

    override fun onCreate() {
        super.onCreate()
        SLog.d(TAG,"onCreate invoke");
        SLog.i(TAG,"onCreate start,time:${systemTime()}");
        instance = this
        // 2. 触发子类自定义初始化逻辑
        onInit();
        onInitialize()
        SLog.i(TAG,"onCreate end,time:${systemTime()}");
    }

    open fun onInit(){
        SLog.d(TAG,"onInit");
    }


    /**
     * 框架初始化完成后的回调，子类可在此进行三方 SDK 初始化
     * @author Eastevil
     * @createTime 2025/12/24
     */
    open fun onInitialize() {
        SLog.d(TAG,"onInitialize")
        // 子类可选实现

    }
}
