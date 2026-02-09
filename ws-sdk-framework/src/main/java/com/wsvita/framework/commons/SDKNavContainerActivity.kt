package com.wsvita.framework.commons

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.wsvita.framework.R
import com.wsvita.framework.databinding.ActivityWsvitaFrameworkContainerBinding
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.widget.view.VitaTitleBar

/**
 * 通用的导航容器 Activity
 * 适用于 account 或 ad 模块通过 Navigation 实现单 Activity 多 Fragment 结构
 */
abstract class SDKNavContainerActivity<V : SDKViewModel> : SDKActivity<ActivityWsvitaFrameworkContainerBinding, V>() {

    protected lateinit var navController: NavController

    /**
     * 让子类指定具体业务模块的导航图 (例如 R.navigation.nav_account)
     */
    abstract fun getNavGraphResId(): Int

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupNavigation()
        initTitle(dataBinding.vitaTitleBar);
    }

    private fun setupNavigation() {
        // 1. 获取 NavHostFragment (此处 ID 必须与 XML 中的 container_fragment 一致)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.container_fragment) as? NavHostFragment
            ?: throw IllegalStateException("Activity 布局中必须包含 ID 为 container_fragment 的 NavHostFragment")

        // 2. 初始化 NavController
        navController = navHostFragment.navController

        // 3. 动态配置导航图
        val navInflater = navController.navInflater
        val graph = navInflater.inflate(getNavGraphResId())
        val i = intent;
        // 4. 提供 Hook 钩子，允许业务模块在设置 graph 前修改逻辑（如动态起始页）
        onPrepareNavGraph(graph)
        // 5. 设置图并监听
        navController.graph = graph
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            onDestinationChanged(destination)
        }
    }

    /**
     * 子类可重写：在设置 Graph 之前，允许根据 Intent 传参动态修改起始目的地
     */
    open fun onPrepareNavGraph(graph: NavGraph) {
        SLog.d(TAG,"onPrepareNavGraph invoke,time:${systemTime()}");
    }

    /**
     * 子类可重写：目的地切换回调，自动同步 TitleBar 的显示
     */
    open fun onDestinationChanged(destination: NavDestination) {
        // 默认行为：将 Navigation 里的 Label 同步给 TitleBar
        dataBinding.vitaTitleBar.setTitleText(destination.label?.toString() ?: "")
    }

    /**
     * 默认指向 SDK 提供的容器布局
     */
    override fun layoutId(): Int = R.layout.activity_wsvita_framework_container

    /**
     * 处理回退栈：优先交由 Navigation 处理
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    open fun initTitle(titleBar : VitaTitleBar){
        SLog.d(TAG,"initTitle")
    }

    fun goneTitle(){
        dataBinding.vitaTitleBar.visibility = View.GONE;
    }

    fun visibileTitle(){
        dataBinding.vitaTitleBar.visibility = View.VISIBLE;
    }

    fun setTitleText(titleText : String){
        dataBinding.vitaTitleBar.setTitleText(titleText);
    }

    fun setTitleText(resId : Int){
        val title = getString(resId);
        setTitleText(title);
    }

    override fun onIntentValueReceived(key: String, value: Any?) {
        super.onIntentValueReceived(key, value)
    }

    companion object{
        private const val TAG = "WSV_F_SDKNavContainerActivity==>";
    }
}
