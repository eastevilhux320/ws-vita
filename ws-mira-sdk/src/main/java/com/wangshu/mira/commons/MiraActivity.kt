package com.wangshu.mira.commons

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wangshu.mira.R
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.mira.configure.MiraConfigure
import com.wsvita.core.common.AppActivity
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.launch

/**
 * Description: MVVM 基类 Activity，负责监听 ViewModel 的通用数据流并分发到钩子方法。
 * create by Administrator at 2026/2/10 23:25
 * @author Administrator
 */
abstract class MiraActivity<D : ViewDataBinding, V : MiraViewModel> : AppActivity<D, V>() {

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
    }

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig()
        val color = MiraConfigure.instance.getConfig()?.mainThemeColor?:let {
            getColor(R.color.mira_main_theme_color);
        }
        c.isFullScreen = false
        c.statusBarColor = color;
        c.lightIcons = false
        return c
    }

    /**
     * 核心监听逻辑：禁止省略任何类型的处理
     */
    override fun addObserve() {
        super.addObserve()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.commonDataFlow.collect { event ->
                    when (event) {
                        is WsDataEvent.Empty -> onReceiveModelEmpty()
                        is WsDataEvent.IntEvent -> onReceiveModelInt(event.data ?: 0)
                        is WsDataEvent.LongEvent -> onReceiveModelLong(event.data ?: 0L)
                        is WsDataEvent.DoubleEvent -> onReceiveModelDouble(event.data ?: 0.0)
                        is WsDataEvent.StringEvent -> onReceiveModelString(event.data ?: "")
                    }
                }
            }
        }

        viewModel.config.observe(this, Observer {
            onConfigChanged(it);
        })
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

    override fun toLogin(code: Int, msg: String?) {
        super.toLogin(code, msg)
    }

    protected open fun loginSuccess() {
        SLog.d(TAG, "loginSuccess,time:${systemTime()}")
    }

    protected open fun loginFail() {
        SLog.d(TAG, "loginFail,time:${systemTime()}")
    }

    protected open fun onConfigChanged(config : MiraConfig){
        SLog.d(TAG,"onConfigChanged");
    }

    companion object {
        protected const val TAG = "Mirror_Main_MirrorActivity=>"
        private const val ROUTER_LOGIN = "mirror_router_to_login"
    }
}
