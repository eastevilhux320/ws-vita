package com.vita.xuantong.demo.commons

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.wangshu.vita.demo.R
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.entity.IAccount
import com.wsvita.account.entity.appenums.FromType
import com.wsvita.biz.core.commons.BizcoreActivity
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.commons.ModelConstants
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.full.BooleanFullRouterContract
import com.wsvita.framework.utils.SLog

abstract class KYActivity<D : ViewDataBinding,V : KYViewModel> : BizcoreActivity<D, V>(){

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
        configurator.register(
            ROUTER_LOGIN, BooleanFullRouterContract(
                Action.ACTION_LOGIN,
            ModelConstants.ResultKey.RESULT_LOGIN)){
            SLog.d(TAG,"prepareRouters_login_result:${it}");
            if(it){
                //登录成功，
                loginSuccess();
            }else{
                //登录失败
                loginFail();
            }
        }
    }

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig();
        c.isFullScreen = false;
        c.statusBarColor = getColor(R.color.color_main_theme);
        c.lightIcons = false;
        return c;
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

    override fun toLogin(code: Int, msg: String?) {
        super.toLogin(code, msg)
        val bundle = Bundle();
        bundle.putInt(AccountConstants.IntentExtra.JUMP_TYPE,FromType.NORMAL.getType())
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

    companion object{
        private const val TAG = "Mirror_Main_MirrorActivity=>";
        private const val ROUTER_LOGIN = "mirror_router_to_login";
    }
}
