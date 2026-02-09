package com.wsvita.biz.core.commons

import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import androidx.lifecycle.Observer
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.biz.core.location.SDKLocationListener
import com.wsvita.core.common.AppContainerActivity
import com.wsvita.core.common.AppFragment
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.widget.view.VitaTitleBar
import ext.JsonExt.toJson

/**
 * 业务层通用导航容器基类。
 * 继承自 [AppContainerActivity]，专门用于支撑各业务模块（如 Account、Ad、Setting 等）
 * 实现“单 Activity + 多 Fragment”的组件化结构。
 * * [核心职责]
 * 1. UI 统筹：统一处理业务模块的 [VitaTitleBar] 样式及基础交互。
 * 2. 状态映射：将 [BizcoreViewModel] 中的业务状态映射为具体的导航容器行为。
 * 3. 简化模板：为子类提供快捷操作方法（如动态修改标题栏图标）。
 *
 * [最佳实践]
 * - 适用于流程闭环的业务模块：例如登录注册流程、广告投放配置流等。
 * - 避免过度堆砌：若业务逻辑非全局通用，建议在具体的实现类中编写，保持此类精简。
 *
 * -------------------------------------------------------------------------------------
 * @Author: Eastevil
 * @Date: 2026/01/08 16:00
 * @Version: 1.1.0
 * @see AppContainerActivity
 */
abstract class BizcoreContainerActivity<V : BizcoreContainerViewModel> : AppContainerActivity<V>() {
    private var mLocClient: LocationClient? = null
    private var sdkLocationListener : SDKLocationListener? = null;

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig();
        c.isFullScreen = false;
        c.statusBarColor = BizcoreConfigure.instance.getConfig()?.mainThemeColor?:Color.BLACK;
        c.lightIcons = false;
        return c;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.location.observe(this, Observer {
            SLog.d(TAG,"onLocationChanged");
            onLocationChanged(it);
        })

        viewModel.config.observe(this, Observer {
            SLog.d(TAG,"onConfigChanged");
            onConfigChanged(it);
        })
    }

    /**
     * **动态更新标题栏菜单图标**
     * * [1. 物理意义]
     * 用于在运行时根据业务逻辑（如状态切换、权限变更）动态修改 VitaTitleBar 右侧菜单栏的图标资源。
     * * [2. 工作流逻辑]
     * - 访问 [dataBinding] 中的标题栏组件。
     * - 调用 [setMenuIconResource] 方法执行 UI 刷新。
     * * @author Eastevil
     * @param iconResId 目标图标的资源 ID（例如：R.drawable.ic_confirm），传入 0 或无效值可能导致图标隐藏。
     * @since 2026/1/8 16:00
     */
    fun setMenuIcon(iconResId: Int) {
        val t = dataBinding.vitaTitleBar
        t.setMenuIconResource(iconResId)
    }

    /**
     * 设置标题栏右侧菜单图标的显示状态。
     * <p>
     * <b>作用说明：</b> 动态控制 Activity 容器顶部标题栏 [VitaTitleBar] 的菜单区域可见性。<br>
     * <b>逻辑映射：</b> 内部通过 [setMenuType] 实现，1 映射为可见（标准菜单态），0 映射为隐藏（无菜单态）。<br>
     * <b>应用场景：</b> 用于业务 Fragment 根据自身逻辑（如：编辑模式或查看模式）动态切换容器 UI。
     * </p>
     * * create by Eastevil at 2026/1/8 16:09
     * @author Eastevil
     * @param visibility true 表示展示菜单图标，false 表示完全隐藏
     */
    fun setMenuIconVisibility(visibility : Boolean){
        val t = dataBinding.vitaTitleBar;
        if(visibility){
            t.setMenuType(1);
        }else{
            t.setMenuType(0);
        }
    }

    override fun onMenuClick(view: View) {
        super.onMenuClick(view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(com.wsvita.framework.R.id.container_fragment) as? androidx.navigation.fragment.NavHostFragment

        val fragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment;
        if(fragment is BizcoreFragment<*,*>){
            val f = fragment as BizcoreFragment<*,*>;
            f.onMenuClick(view);
        }
    }

    override fun performLocationAction() {
        super.performLocationAction()
        // 1. 业务开关检查
        if (!needLocation()) {
            SLog.i(TAG, "Location task ignored: needLocation() is false.")
            return
        }
        SLog.w(TAG, "Location permission status confirmed. Executing...")
        LocationClient.setAgreePrivacy(true)
        // 2. 初始化定位客户端 (单例检查，避免重复创建)
        if (mLocClient == null) {
            // 注意：百度 SDK 建议传递 ApplicationContext
            mLocClient = LocationClient(applicationContext)
        }
        val client = mLocClient ?: return
        sdkLocationListener = SDKLocationListener();
        sdkLocationListener?.onLocation { bdLocation, location ->
            SLog.d(TAG,"sdkLocationListener,onLocation");
            viewModel.receiveLocation(location);
        }
        client.registerLocationListener(sdkLocationListener)
        // 3. 配置参数 (调用私有方法)
        client.locOption = initLocationOption()
        client.start();
    }

    protected open fun onLocationChanged(location : BizLocation){
        SLog.d(TAG,"onLocationChanged");
        val f= currentFragment(BizcoreFragment::class.java);
        f?.receiveContainerLocation(location);
    }

    protected open fun onConfigChanged(config : BizcoreConfig){
        SLog.d(TAG,"onConfigChanged");
        dataBinding.vitaTitleBar.setBackgroundColor(config.mainThemeColor);
    }

    /**
     * ╔══════════════════════════════════════════════════════════════════════════════════╗
     * ║ 私有实现                                                                          ║
     * ╚══════════════════════════════════════════════════════════════════════════════════╝
     */

    /**
     * 初始化百度定位
     */
    private fun initLocationOption(): LocationClientOption {
        val option = LocationClientOption()

        // 显式赋值，禁止使用 apply 闭包
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy // 高精度模式
        option.coorType = "bd09ll"         // 百度经纬度坐标系 (GCJ02/BD09LL)
        option.setScanSpan(5000)              // 0表示单次定位
        option.isOpenGps = true            // 允许使用GPS
        option.setIsNeedAddress(true)      // 需要详细地址描述

        return option
    }

    companion object{
        private const val TAG = "WSV_Bizcore_BizcoreContainerActivity=>"
    }
}
