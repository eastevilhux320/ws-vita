package com.vita.xuantong.demo.model.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.vita.xuantong.demo.commons.KYFragment
import com.vita.xuantong.demo.commons.KYViewModel
import com.wangshu.vita.demo.R
import com.wsvita.app.local.manager.ChannelManager
import com.vita.xuantong.demo.model.main.default.DefaultFragment
import com.vita.xuantong.demo.model.main.discovery.DiscoveryFragment
import com.vita.xuantong.demo.model.main.home.HomeFragment
import com.vita.xuantong.demo.model.main.mine.MineFragment
import com.vita.xuantong.demo.model.main.serene.SereneFragment
import com.wsvita.biz.core.network.model.AppModel
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.network.entity.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 主界面 ViewModel
 * 负责从后端获取动态 Tab 配置，并根据 fragmentCode 分发不同的组件
 */
class MainViewModel(application: Application) : KYViewModel(application) {

    // 暴露给 Activity 的观察流
    private val _tabConfigs = MutableStateFlow<List<MainTabSpec>>(emptyList())
    val tabConfigs = _tabConfigs.asStateFlow()

    override fun initModel() {
        super.initModel()
        // 初始化时发起请求
        mainTabList()
    }

    /**
     * 调用后端接口获取动态菜单列表
     */
    private fun mainTabList() {
        viewModelScope.launch {
            // 使用基类提供的 request 方法，处理 Loading 和异常
            val result = request(showLoading = true, requestCode = REQUEST_MAIN_TABS) {
                AppModel.instance.mainTabList(ChannelManager.instance.getChannel())
            }

            // 如果 result 不为空，则映射为 MainTabSpec 列表
            result?.let { list ->
                val specs = list.map { entity ->
                    MainTabSpec.Builder()
                        .setId(entity.id) // 这里是 Long 类型
                        .setTitle(entity.name)
                        .setTag("TAG_${entity.fragmentCode}")
                        // 根据后端返回的图标 URL 类型设置
                        .setIconUrl(entity.norIcon, entity.selIcon)
                        .setColors(entity.norColor,entity.selColor)
                        .setFragment { buildFragment(entity.fragmentCode) }
                        .build()
                }
                _tabConfigs.value = specs
            }
        }
    }

    /**
     * 当数据为空时的兜底策略 (基类回调)
     */
    override fun onEmptyData(config: ModelRequestConfig, result: Result<*>) {
        super.onEmptyData(config, result)
        if (config.requestCode == REQUEST_MAIN_TABS) {
            initDefaultTabConfig()
        }
    }

    /**
     * 设置为必传数据，如果请求失败会触发 error/empty 回调
     */
    override fun isDataEmptyAsError(requestCode: Int): Boolean {
        return requestCode == REQUEST_MAIN_TABS
    }

    override fun onRequestError(config: ModelRequestConfig, result: Result<*>): Boolean {
        when(config.requestCode){
            REQUEST_MAIN_TABS ->{
                initDefaultTabConfig();
                return true;
            }
        }
        return super.onRequestError(config, result)
    }

    /**
     * 默认的本地 Tab 配置（当接口不可用时的兜底）
     */
    private fun initDefaultTabConfig() {
        _tabConfigs.value = listOf(
            MainTabSpec.Builder()
                .setId(1L)
                .setTitle(getString(R.string.wsvita_main_tab_home))
                .setIconRes(R.drawable.ic_main_home_nor, R.drawable.ic_main_home_sel)
                .setTag("TAG_HOME")
                .setFragment { HomeFragment.newInstance() }
                .build(),
            MainTabSpec.Builder()
                .setId(2L)
                .setTitle(getString(R.string.wsvita_main_tab_discovery))
                .setIconRes(R.drawable.ic_main_discovery_nor, R.drawable.ic_main_discovery_nor)
                .setTag("TAG_DISCOVERY")
                .setFragment { DiscoveryFragment.newInstance() }
                .build(),
            MainTabSpec.Builder()
                .setId(3L)
                .setTitle(getString(R.string.wsvita_main_tab_mine))
                .setIconRes(R.drawable.ic_main_mine_nor, R.drawable.ic_main_mine_sel)
                .setTag("TAG_MINE")
                .setFragment { MineFragment.newInstance() }
                .build()
        )
    }

    /**
     * 根据后端定义的 fragmentCode 创建对应的 Fragment 实例
     * 体现了组件化开发的灵活性，解耦了业务 Code 与具体实现类
     */
    private fun buildFragment(fragmentCode: String?): KYFragment<*, *> {
        return when (fragmentCode) {
            MainConstants.FragmentCode.HUBER_MAIN_TAB_HOME -> HomeFragment.newInstance()
            MainConstants.FragmentCode.HUBER_MAIN_TAB_DISCOVERY -> DiscoveryFragment.newInstance()
            MainConstants.FragmentCode.HUBER_MAIN_TAB_CHULINK -> SereneFragment.newInstance()
            MainConstants.FragmentCode.HUBER_MAIN_TAB_MINE -> MineFragment.newInstance()
            else -> DefaultFragment.newInstance() // 无法识别的 Code 走兜底页面
        }
    }

    companion object {
        private const val REQUEST_MAIN_TABS = 0x01
    }
}
