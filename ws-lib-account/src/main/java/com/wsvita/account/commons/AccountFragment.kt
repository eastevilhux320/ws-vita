package com.wsvita.account.commons

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.wsvita.account.configure.AccountConfig
import com.wsvita.core.common.AppFragment
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog

abstract class AccountFragment<D : ViewDataBinding, V : AccountViewModel> : AppFragment<D, V>() {

    override fun addObserve() {
        super.addObserve()
        viewModel.config.observe(this, Observer {
            onConfigChanged(it);
        })
    }

    protected open fun onConfigChanged(config : AccountConfig){
        SLog.d(TAG,"onConfigChanged");
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【设置标题栏右侧菜单图标资源】
     * -------------------------------------------------------------------------------------
     * [1. 物理映射]
     * 直接操作 [VitaTitleBar] 组件的内部 ImageButton 资源。
     * 调用此方法会自动触发 UI 重绘，将传入的 [resId] 加载到菜单槽位。
     *
     * [2. 逻辑联动]
     * 为了确保图标设置后立即可见，内部通常会强制同步 [setMenuType(1)]，
     * 避免因之前调用过 [goneMenu] 导致图标设置后仍处于隐藏状态。
     *
     * [3. 调用约束]
     * 仅限在 Activity 及其承载的业务 Fragment 中调用。若资源 ID 无效，
     * 系统可能显示默认占位图或导致渲染空异常，调用前请确保资源已编译。
     *
     * @param menuIconResId 目标图标的 Drawable 资源 ID（如 R.drawable.ic_biz_save）。
     */
    protected fun setMenuIcon(menuIconResId : Int){
        val ac = getCurrentActivity(AccountContainerActivity::class.java);
        ac?.let {
            it.setMenuIcon(menuIconResId);
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【显示标题栏菜单】
     * -------------------------------------------------------------------------------------
     * [1. 物理路径]
     * 通过宿主 Activity 的 [setMenuIconVisibility] 接口，控制标题栏功能按键区域的可见性。
     * * [2. 内部行为]
     * 将菜单视图状态设置为 [View.VISIBLE]。此时标题栏右侧将展示由 [setMenuIcon] 指定的图标。
     * * [3. 关联对象]
     * 虽然方法内获取了 [dataBinding.root]，但这主要用于确保视图层次已初始化，核心操作仍
     * 作用于宿主 Activity 的标题栏。
     */
    protected fun showMenu(){
        val ac = getCurrentActivity(AccountContainerActivity::class.java);
        ac?.let {
            val s = dataBinding.root;
            it.setMenuIconVisibility(true);
        }
    }

    /**
     * -------------------------------------------------------------------------------------
     * 【隐藏标题栏菜单】
     * -------------------------------------------------------------------------------------
     * [1. 物理路径]
     * 通过宿主 Activity 接口将标题栏功能按键区域物理移除。
     * * [2. 业务逻辑]
     * 对应状态设置为 [View.GONE]。当该业务页面不需要额外操作项（如只读页面）时调用此方法。
     * * [3. 调用时机]
     * 建议在 [onViewCreated] 或业务状态机切换至非操作模式时调用。
     */
    protected fun goneMenu(){
        val ac = getCurrentActivity(AccountContainerActivity::class.java);
        ac?.let {
            it.setMenuIconVisibility(false);
        }
    }

    protected fun showStatusBar(){
        val sConfig = getScreenConfig();
        sConfig?.let {
            it.isFullScreen = false;
            setScreenConfig(it);
        }
    }

    protected fun hideStatusBar(){
        val sConfig = getScreenConfig();
        sConfig?.let {
            it.isFullScreen = true;
            setScreenConfig(it);
        }
    }


    /**
     * -------------------------------------------------------------------------------------
     * 【菜单点击事件分发入口】
     * -------------------------------------------------------------------------------------
     * [1. 事件溯源]
     * 当用户点击容器 Activity 顶部的菜单按钮时，由 Activity 通过接口反向分发至此。
     * * [2. 调试监控]
     * 默认打印 [TAG] 及系统毫秒级时间戳 [systemTime()]，方便在复杂业务流中追踪交互响应时效。
     * * [3. 子类覆盖约束]
     * 子类必须重写（Override）此方法来实现具体的业务点击逻辑（如保存、分享、跳转等）。
     * * @param view 被点击的菜单 View 实例（通常是图标所在的 ImageButton）。
     */
    open fun onMenuClick(view : View){
        SLog.d(TAG,"onMenuClick invoke,time:${systemTime()}");
    }

    companion object{
        private const val TAG = "WS_AC_AccountFragment=>"
    }
}
