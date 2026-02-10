package com.vita.xuantong.demo.commons

import android.os.Bundle
import com.wangshu.vita.demo.R
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.entity.appenums.FromType
import com.wsvita.biz.core.commons.BizcoreContainerActivity
import com.wsvita.core.common.SDKContants
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.full.BooleanFullRouterContract
import com.wsvita.framework.router.contract.receiver.LongReceiveRouterContract
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.widget.view.VitaTitleBar

abstract class KYContainerActivity<V : KYContainerViewModel> : BizcoreContainerActivity<V>() {

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
        configurator.register(
            ROUTER_LOGIN, BooleanFullRouterContract(
                Action.ACTION_LOGIN,
            ModelConstants.ResultKey.RESULT_LOGIN)
        ){
            SLog.d(TAG,"prepareRouters_login_result:${it}");
            if(it){
                //登录成功，
                loginSuccess();
            }else{
                //登录失败
                loginFail();
            }
        }

        if(needJumpDateTime()){
            val dataTime = LongReceiveRouterContract(com.wsvita.core.common.Action.ACTION_DATETIME,SDKContants.IntentKey.RESULT_DATETIME);
            configurator.register(ROUTER_DATATIME,dataTime){
                onDateTime(ROUTER_DATATIME,dataTime.getAction(),it);
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initTitle(titleBar: VitaTitleBar) {
        super.initTitle(titleBar)
        titleBar.setBackgroundColor(getColor(R.color.color_main_theme))
    }

    override fun initScreenConfig(): ScreenConfig {
        val config = ScreenConfig.build(false,getColor(R.color.color_main_theme),false);
        return config;
    }

    override fun toLogin(code: Int, msg: String?) {
        super.toLogin(code, msg)
        val bundle = Bundle();
        bundle.putInt(AccountConstants.IntentExtra.JUMP_TYPE, FromType.NORMAL.getType())
        bundle.putString(AccountConstants.IntentExtra.LOGIN_TIPS,msg);
        bundle.putString(AccountConstants.IntentExtra.JUMP_NAME,"mirror");
        router(ROUTER_LOGIN,bundle);
    }

    /**
     * 【方法作用】
     * 登录成功后的业务回调钩子。
     * * 【调用说明】
     * 当 [ROUTER_LOGIN] 路由执行完毕，且返回值为 true (表示登录流程完整结束且校验通过) 时，
     * 由框架层在 [prepareRouters] 的结果回调中自动触发。
     *
     * 【子类使用说明】
     * 子类应重写此方法以处理登录后的逻辑，例如：
     * 1. 重新请求需要 Token 权限的敏感数据。
     * 2. 更新 UI 状态（如显示用户头像、名称）。
     * 3. 开启只有登录后才运行的后台长连接或轮询。
     *
     * 【注意事项】
     * - 该方法在主线程回调，请勿执行耗时操作。
     * - 重写时通常不需要调用 super.loginSuccess()，除非父类中有通用的埋点逻辑。
     * - 若当前 Activity 处于后台，回调仍会触发，注意异步 UI 更新的生命周期安全。
     */
    protected open fun loginSuccess(){
        SLog.d(TAG,"loginSuccess,time:${systemTime()}");
    }

    /**
     * 【方法作用】
     * 登录失败或取消的回调钩子。
     * * 【调用说明】
     * 当 [ROUTER_LOGIN] 路由返回值为 false，或用户中途点击返回键取消登录、
     * 或是登录接口返回特定错误码时触发。
     *
     * 【子类使用说明】
     * 子类可根据业务场景选择性重写：
     * 1. 弹出友好的失败提示（Toast 或 Dialog）。
     * 2. 如果当前页面强依赖登录态，可在此处调用 finish() 关闭页面。
     * 3. 重置登录前的临时状态位。
     *
     * 【注意事项】
     * - 需区分“用户主动取消”与“登录接口报错”两种逻辑（通常由路由契约内部处理）。
     * - 避免在此处进行无限循环的重试跳转，防止页面陷入死循环。
     */
    protected open fun loginFail(){
        SLog.d(TAG,"loginFail,time:${systemTime()}");
    }

    /**
     * 【统一获取入口】
     * 从容器缓存中获取数据。支持基础类型 (Int, Long, Double, String, Boolean)
     * 以及通过 Json 镜像存储的自定义实体类。
     *
     * @param key   缓存唯一标识
     * @param clazz 目标类型的 Class 对象
     * @return 匹配到的数据实例，找不到则返回 null
     */
    fun <T> getContainerData(key: String, clazz: Class<T>): T? {
        return viewModel.getContainerData(key, clazz)
    }


    /**
     * 【统一获取入口】
     * 从容器缓存中获取数据。支持基础类型 (Int, Long, Double, String, Boolean)
     * 以及通过 Json 镜像存储的自定义实体类。
     *
     * @param key   缓存唯一标识
     * @param clazz 目标类型的 Class 对象
     * @param defaultValue 默认值
     * @return 匹配到的数据实例，找不到则返回 null
     */
    fun <T> getContainerData(key: String, clazz: Class<T>, defaultValue: T): T {
        return getContainerData(key, clazz) ?: defaultValue
    }

    /**
     * 【方法作用】
     * 控制是否需要监听或跳转系统日期时间设置的开关。
     *
     * 【调用说明】
     * 在 [prepareRouters] 中被调用。如果返回 true，框架会自动注册 [ROUTER_DATATIME] 路由，
     * 用于拦截或接收来自系统/其他组件的日期时间变更信号。
     *
     * 【子类使用说明】
     * 如果你的页面（如镜像容器）强依赖于精确的系统时间（例如打卡、倒计时、限时任务），
     * 请重写此方法并返回 true。
     *
     * @return 默认返回 false，不开启时间路由监听。
     */
    open fun needJumpDateTime(): Boolean {
        return false;
    }

    /**
     * 【方法作用】
     * 执行日期时间设置的路由跳转或逻辑触发。
     *
     * 【设计意图】
     * 1. **容器封装**：作为 [KYContainerActivity] 暴露的公共接口，统一管理时间路由 [ROUTER_DATATIME] 的跳转行为。
     * 2. **解耦调用**：子类或内部组件无需关心路由注册的具体 key，通过此方法实现一键触达。
     *
     * 【调用说明】
     * 当业务层检测到系统时间异常、或者用户点击 UI 上的时钟组件需要进入校准/设置页面时，调用此方法。
     * 该方法内部通过 [routerContainer] 触发容器级路由分发。
     *
     * create by Eastevil at 2026/01/27
     * @author Eastevil
     */
    fun gotoDateTime(){
        routerContainer(ROUTER_DATATIME);
    }

    /**
     * 【方法作用】
     * 系统日期时间变更或校准后的业务回调钩子。
     *
     * 【调用说明】
     * 当 [needJumpDateTime] 为 true 且 [ROUTER_DATATIME] 路由收到合法的 Long 类型时间戳时触发。
     * 这里的 dateTime 通常是校准后的系统毫秒值。
     *
     * 【子类使用说明】
     * 子类重写此方法以处理时间敏感业务：
     * 1. 刷新 ViewModel 中的 [DataTimeViewModel.timeString] 逻辑。
     * 2. 重新同步本地时钟偏移量。
     * 3. 校对当前页面展示的业务倒计时。
     *
     * @param name     路由名称，固定为 [ROUTER_DATATIME]
     * @param action   触发动作标识，对应 [com.wsvita.core.common.Action.ACTION_DATETIME]
     * @param dateTime 接收到的最新系统时间戳 (ms)
     */
    protected open fun onDateTime(name : String,action : String,dateTime : Long){
        SLog.d(TAG,"onDateTime,name:${name},action:${action},dataTime:${dateTime}");
        val f = currentFragment(KYFragment::class.java);
        f?.onDateTime(name, action, dateTime)
    }

    companion object{
        private const val TAG = "Mirror_MirrorContainerActivity=>";
        private const val ROUTER_LOGIN = "mirror_router_to_login";
        private const val ROUTER_DATATIME = "mirror_router_to_jump_data_time";
    }

}
