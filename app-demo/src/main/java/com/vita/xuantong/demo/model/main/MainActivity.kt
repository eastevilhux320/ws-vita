package com.vita.xuantong.demo.model.main

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vita.xuantong.demo.commons.KYActivity
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.ActivityMainBinding
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.sender.IntSendRouterContract
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : KYActivity<ActivityMainBinding, MainViewModel>() {

    private var currentFragment: Fragment? = null
    private var currentTabId: Long = -1L // 保持与 Spec 一致

    // 用于解决 Long ID 无法传入 Menu 的问题
    private val menuIdMap = mutableMapOf<Int, Long>()

    override fun layoutId(): Int {
        return R.layout.activity_main;
    }

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java;
    }

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        dataBinding.bottomNav.setOnItemSelectedListener { item ->
            // 从映射中找回原始 Long ID
            val originalId = menuIdMap[item.itemId]
            val spec = viewModel.tabConfigs.value.find { it.id == originalId }
            spec?.let {
                switchTab(it)
                return@setOnItemSelectedListener true
            }
            false
        }

        // 必须禁用 Tint，否则远程图片的原始颜色无法显示
        dataBinding.bottomNav.itemIconTintList = null
    }

    override fun addObserve() {
        super.addObserve()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tabConfigs.collect { configs ->
                    if (configs.isNotEmpty()) {
                        renderBottomMenu(configs)

                        // 初始选中逻辑
                        if (currentTabId == -1L) {
                            val firstTab = configs[0]
                            val menuId = menuIdMap.filterValues { it == firstTab.id }.keys.firstOrNull()
                            menuId?.let { dataBinding.bottomNav.selectedItemId = it }
                            switchTab(firstTab)
                        }
                    }
                }
            }
        }
    }

    private fun renderBottomMenu(configs: List<MainTabSpec>) {
        val menu = dataBinding.bottomNav.menu
        menu.clear()
        menuIdMap.clear()

        configs.forEachIndexed { index, spec ->
            // 使用索引作为 Int ID，避开 Long 转换风险
            val tempMenuId = index + 1
            menuIdMap[tempMenuId] = spec.id

            val menuItem = menu.add(Menu.NONE, tempMenuId, Menu.NONE, spec.title)

            // 处理图标
            if (spec.iconType == 1) {
                // 本地资源：直接创建 Selector
                menuItem.icon = createSelector(
                    ContextCompat.getDrawable(this, spec.iconSelRes),
                    ContextCompat.getDrawable(this, spec.iconNorRes)
                )
            } else if (spec.iconType == 2) {
                // 远程图片：使用 Glide 异步合成 Selector
                loadRemoteIcon(menuItem, spec)
            }
        }
    }

    /**
     * 使用 Glide 加载远程图片
     * 这里使用 SimpleTarget（或直接用 submit 获取 Drawable）以确保编译通过
     */
    private fun loadRemoteIcon(menuItem: MenuItem, spec: MainTabSpec) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // 在协程中使用 Glide 的 submit() 方法，避免回调泛型报错
                val norDrawable = withContext(Dispatchers.IO) {
                    //GlideApp.with(this@MainActivity).asDrawable().load(spec.iconNorUrl).submit().get()
                }
                val selDrawable = withContext(Dispatchers.IO) {
                    //GlideApp.with(this@MainActivity).asDrawable().load(spec.iconSelUrl).submit().get()
                }

                // 下载完成后合成 Selector 并赋值
                //menuItem.icon = createSelector(selDrawable, norDrawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createSelector(sel: Drawable?, nor: Drawable?): StateListDrawable {
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_checked), sel)
            addState(intArrayOf(), nor)
        }
    }

    private fun switchTab(spec: MainTabSpec) {
        if (currentTabId == spec.id) return

        // --- 新增：动态更新导航栏颜色 ---
        val colorStateList = createTabColorStateList(spec)
        dataBinding.bottomNav.itemTextColor = colorStateList

        // 如果图标也需要根据 Spec 的颜色值进行着色（Tint），而非使用图片原色，可以开启下面这行：
        // dataBinding.bottomNav.itemIconTintList = colorStateList
        // ----------------------------

        val transaction = supportFragmentManager.beginTransaction()
        currentFragment?.let { transaction.hide(it) }

        var target = supportFragmentManager.findFragmentByTag(spec.tag)
        if (target == null) {
            target = spec.createFragment()
            transaction.add(R.id.fragment_container, target, spec.tag)
        } else {
            transaction.show(target)
        }

        currentFragment = target
        currentTabId = spec.id
        transaction.commitAllowingStateLoss()
    }

    /**
     * 根据 Spec 创建颜色状态列表
     */
    private fun createTabColorStateList(spec: MainTabSpec): android.content.res.ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked), // 选中
            intArrayOf()                             // 默认
        )
        val colors = intArrayOf(
            spec.selColor(), // 调用您定义的获取选中色方法
            spec.norColor()  // 调用您定义的获取非选中色方法
        )
        return android.content.res.ColorStateList(states, colors)
    }

    companion object {
        private const val TAG = "WSVita_App_Main_MainActivity=>";
    }
}
