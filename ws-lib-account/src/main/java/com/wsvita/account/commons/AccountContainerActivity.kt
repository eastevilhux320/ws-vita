package com.wsvita.account.commons

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import com.wsvita.core.common.AppContainerActivity
import com.wsvita.framework.widget.view.VitaTitleBar

/**
 * 业务层通用导航容器基类。
 * 继承自 [AppContainerActivity]，专门用于支撑各业务模块（如 Account、Ad、Setting 等）
 * 实现“单 Activity + 多 Fragment”的组件化结构。
 * * [核心职责]
 * 1. UI 统筹：统一处理业务模块的 [VitaTitleBar] 样式及基础交互。
 * 2. 状态映射：将 [AccountViewModel] 中的业务状态映射为具体的导航容器行为。
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
abstract class AccountContainerActivity<V : AccountContainerViewModel> : AppContainerActivity<V>() {

    override fun initTitle(titleBar: VitaTitleBar) {
        super.initTitle(titleBar)
        titleBar.setBackgroundColor(viewModel.themeColor());
        titleBar.setTitleColor(Color.WHITE);
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
        if(fragment is AccountFragment<*,*>){
            val f = fragment as AccountFragment<*,*>;
            f.onMenuClick(view);
        }
    }
}
