package com.vita.xuantong.demo.commons

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.wsvita.account.entity.IAccount
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog

abstract class KYFragment<D : ViewDataBinding, V : KYViewModel> : BizcoreFragment<D, V>(){

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        goneMenu();
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.account.observe(this, Observer {
            if(it != null){
                onAccountChanged(it);
            }else{
                SLog.d(TAG,"account is null");
            }
        })
    }


    /**
     * 账号变更的钩子方法
     * * [设计意图 - 模板方法模式]
     * 1. **View层契约**：子类 Activity 或 Fragment 通过重写此方法，可直接在账号切换时执行业务初始化（如重置分页、刷新私有数据）。
     * 2. **屏蔽细节**：子类无需感知 EventBus 的存在，只需关注 [IAccount] 数据的变更。
     * 3. **线程安全**：此方法保证在主线程回调，可直接进行 UI 操作。
     *
     * create by Eastevil at 2026/1/9 14:12
     * @author Eastevil
     *
     * @param account- 最新的账号契约对象
     * @return
     *      void
     */
    protected open fun onAccountChanged(account : IAccount){
        SLog.d(TAG,"onAccountChanged");
    }

    override fun getScreenConfig(): ScreenConfig? {
        return super.getScreenConfig()
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
     * 【方法作用】
     * 触发宿主容器执行日期时间相关的路由跳转逻辑。
     *
     * [设计意图 - 跨层级协作]
     * 1. **容器通信**：Fragment 遵循“仅持有引用而不持有实现”的原则，通过获取 [KYContainerActivity] 实例来调用其暴露的 [gotoDateTime] 接口。
     * 2. **类型安全**：利用 [getCurrentActivity] 进行类型检查，确保当前 Fragment 的宿主环境符合 [KYContainerActivity] 契约，避免盲目跳转导致的空指针或逻辑异常。
     *
     * [调用说明]
     * 当 Fragment 内部业务（如点击时间展示区域、检测到时钟偏移过大）需要引导用户进入时间设置或执行校准路由时，直接调用此方法。
     *
     * [注意事项]
     * 需确保 Fragment 所在的 Activity 必须继承自 [KYContainerActivity]，否则跳转将失效（ac 为 null）。
     *
     * create by Eastevil at 2026/01/27
     * @author Eastevil
     */
    protected fun jumpDateTime(){
        val ac = getCurrentActivity(KYContainerActivity::class.java);
        ac?.gotoDateTime()
    }

    /**
     * 系统日期时间变更或校准后的业务回调钩子
     *
     * [设计意图 - 组件化通信契约]
     * 1. **时间感知能力**：子类 Fragment 无需自行监听系统广播，通过重写此方法即可感知全局时间偏移或重置。
     * 2. **数据同步一致性**：当容器层（Activity）收到 [ROUTER_DATATIME] 的路由信号时，会分发至当前可见的 Fragment，确保 UI 层的倒计时或打卡逻辑同步刷新。
     *
     * [调用说明]
     * 通常由所属的容器 Activity 在接收到路由通知后，遍历并分发给当前活跃的 [KYFragment]。
     *
     * [子类使用说明]
     * 1. 若 Fragment 包含时间敏感组件（如实时时钟、限时秒杀），应在此更新 [DataTimeViewModel] 的基准值。
     * 2. 避免在此处执行复杂的计算逻辑，仅建议做状态标记或数据流触发。
     *
     * create by Eastevil at 2026/1/27 14:38
     * @author Eastevil
     *
     * @param name     路由名称标识
     * @param action   触发动作类型，通常对应 [com.wsvita.core.common.Action.ACTION_DATETIME]
     * @param dateTime 接收到的最新系统时间戳 (ms)
     */
    open fun onDateTime(name : String,action : String,dateTime : Long){
        SLog.d(TAG,"onDateTime,name:${name},action:${action},dataTime:${dateTime}");
    }

    companion object{
        private const val TAG = "Mirror_MirrorFragment=>";
    }
}
