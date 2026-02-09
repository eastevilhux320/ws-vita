package com.wsvita.biz.core.model.splash

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreActivity
import com.wsvita.biz.core.configure.Action
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.databinding.ActivitySplashBinding
import com.wsvita.biz.core.model.splash.entity.SplashEvent
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.EmptyRouterContract
import com.wsvita.framework.router.contract.receiver.BooleanReceiveRouterContract
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import kotlinx.coroutines.launch

class SplashActivity : BizcoreActivity<ActivitySplashBinding, SplashViewModel>() {

    override fun layoutId(): Int {
        return R.layout.activity_splash;
    }

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
        configurator.register(
            ROUTER_PROTOCOL,
            // 这里的协议定义了：返回结果时，从 Intent 中寻找名为 PROTOCOL_RESULT_FLAG 的 Boolean 值
            BooleanReceiveRouterContract(Action.ACTIN_SPLASH_PROTOCOL, BizConstants.IntentKey.PROTOCOL_RESULT_FLAG)
        ) { isAgreed->
            SLog.d(TAG,"prepareRouters_register_ROUTER_PROTOCOL,isAgree:${isAgreed}");
            viewModel.protocolResult(isAgreed)
        }
        val mainAction = BizcoreConfigure.instance.getConfig()?.mainAction;
        mainAction?.let {
            configurator.register(LAUNCH_TO_MAIN, EmptyRouterContract(it)){
                SLog.d(TAG,"prepareRouters_register_LAUNCH_TO_MAIN");
            }
        }

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun getVMClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java;
    }

    override fun onConfigChanged(config: BizcoreConfig) {
        super.onConfigChanged(config)
    }

    override fun addObserve() {
        super.addObserve()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.splashEvent.collect { event ->
                    SLog.d(TAG,"lifecycleScope_repeatOnLifecycle_splashEvent_event");
                    handleSplashEvent(event)
                }
            }
        }

        viewModel.splashConfig.observe(this, Observer {
            SLog.d(TAG,"splash config changed");
            SLog.i(TAG,"config:${it.toJson()}");
        })

        viewModel.protocolUrl.observe(this, Observer {
            router(ROUTER_PROTOCOL,BizConstants.IntentKey.PROTOCOL_URL_KEY,it.url);
        })
    }

    private fun handleSplashEvent(event: SplashEvent) {
        SLog.d(TAG, "handleSplashEvent: $event")
        when (event.code) {
            SplashEvent.PRIVACY_DENY -> {
                //拒绝协议，直接退出app
                SLog.w(TAG, "User denied privacy policy, exiting app")
                finish()
            }
            SplashEvent.PRIVACY_AGREED -> {
                SLog.i(TAG, "User agreed to privacy policy")
                //用户同意协议
                viewModel.privacyAlreadyAccepted();
            }
            SplashEvent.APP_BEFOREHAND_SUCCESS -> {
                val token = event.value
                SLog.d(TAG, "App beforehand success, token: $token")
            }
            SplashEvent.CONFIG_LOADED -> {
                val configValue = event.value
                SLog.d(TAG, "Config loaded successfully: $configValue")
            }

            SplashEvent.AD_AVAILABLE -> {
                val adId = event.value
                SLog.i(TAG, "Ad available to show, adId: $adId")
            }
            SplashEvent.FINISHED -> {
                SLog.i(TAG, "Splash process finished, navigating to Main")
                // Perform navigation to main component here
            }
            SplashEvent.CONFIG_ERROR, SplashEvent.APP_BEFOREHAND_ERROR -> {
                SLog.e(TAG, "Splash flow error occurred: ${event.name}")
            }
            SplashEvent.TO_MAIN->{
                viewModel.notifyToMain();
                //进入主页
                router(LAUNCH_TO_MAIN);
                //结束
                finish();
            }
            else -> {
                SLog.d(TAG, "Received unhandled event code: ${event.code}")
            }
        }
    }

    companion object{
        private const val TAG = "WS_Biz_Splash_SplashActivity=>"
        private const val ROUTER_PROTOCOL = "biz_splash_protocol";
        private const val LAUNCH_TO_MAIN = "biz_splash_to_main";
    }

}
