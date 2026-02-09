package com.wsvita.framework.commons

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.wsvita.framework.utils.SLog

abstract class BaseActivity : AppCompatActivity() {

    fun systemTime(): Long {
        return System.currentTimeMillis();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState)
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【生命周期最前置钩子 (Pre-OnCreate Hook)】
     * -------------------------------------------------------------------------------------
     * [1. 作用说明]
     * 本方法是整个 Activity 生命周期中执行最早的可重写方法。它在执行系统的 [super.onCreate]
     * 以及框架层的布局加载（setContentView）之前触发。
     *
     * [2. 核心使用场景]
     * - **Window 属性配置**：如 requestWindowFeature(Window.FEATURE_NO_TITLE) 或设置全屏标志。
     * - **主题动态切换**：在系统加载 View 树之前，通过 setTheme(resId) 动态改变皮肤。
     * - **特殊 SDK 初始化**：某些第三方 SDK（如推送、加固、监控等）要求必须在 super.onCreate 之前初始化。
     * - **组件化配置注入**：在子类 SDKActivity 准备路由协议之前，注入一些全局的环境变量。
     *
     * [3. 注意事项]
     * - **View 不可用**：由于此时布局尚未加载，严禁在此方法中进行任何 findViewById 或 dataBinding 操作。
     * - **Context 限制**：此时 Activity 实例虽已创建，但尚未完成底层初始化，建议仅进行配置类操作。
     * - **性能敏感**：此方法的执行会直接推迟首屏渲染时间，请勿在此执行耗时（IO/网络）操作。
     *
     * [4. 执行顺序]
     * 1. 触发本方法 [beforeOnCreate]
     * 2. 触发系统的 super.onCreate()
     * 3. 触发 SDKActivity 中的路由注册逻辑
     * 4. 触发 setContentView()
     *
     * @param savedInstanceState 界面销毁重建时携带的数据束，可用于根据状态判断是否需要特殊的初始化配置。
     */
    protected open fun beforeOnCreate(savedInstanceState: Bundle?){
        SLog.d(TAG,"beforeOnCreate invoke,time:${systemTime()}");
    }

    companion object{
        private const val TAG = "WSV_F_BaseActivity=>";
    }
}
