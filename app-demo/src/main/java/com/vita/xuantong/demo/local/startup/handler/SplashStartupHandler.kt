package com.wsvita.app.local.startup.handler

import com.vita.xuantong.demo.commons.KYApp
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.app.local.startup.BaseAppStartupHandlerImpl
import com.wsvita.biz.core.model.splash.entity.SplashEvent
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.framework.ext.JsonExt.parseGson
import com.wsvita.framework.local.manager.device.DeviceManager
import com.wsvita.framework.utils.SLog

class SplashStartupHandler : BaseAppStartupHandlerImpl() {

    companion object{
        private const val TAG = "Mirror_Startup_Splash=>";
        private const val SPLASH_KEY = "ws_vita_splash_event_key";
    }

    override fun onHandle(provider: IStartupConfigProvider) {
        val eventJson = provider.getString(SPLASH_KEY);
        SLog.d(TAG,"onHandle，eventJson:${eventJson}");
        val splashEvent = eventJson?.parseGson<SplashEvent>();
        splashEvent?.let {
            val code = it.code;
            SLog.d(TAG,"parse splash event success,code:${code}")
            when (code) {
                // [100] Startup sequence initiated
                SplashEvent.START -> {
                    SLog.d(TAG, "Splash process started.")
                }

                // [200] Pre-check or token request successful
                SplashEvent.APP_BEFOREHAND_SUCCESS -> {
                    SLog.i(TAG, "Pre-check success. Token received: ${splashEvent.value}")
                }

                // [201] Critical pre-check failed
                SplashEvent.APP_BEFOREHAND_ERROR -> {
                    SLog.e(TAG, "Pre-check failed. Core initialization may be compromised.")
                }

                SplashEvent.ACCOUNT_STATE_ON -> {
                    SLog.e(TAG, "ACCOUNT_STATE_ON.")
                    //接收到启动页发送的账号可用，处于登录状态的事务，更新用户信息
                    AccountManager.instance.notifyMember();
                }

                // [300] Remote configuration loaded successfully
                SplashEvent.CONFIG_LOADED -> {
                    SLog.i(TAG, "Config loaded. Version/ID: ${splashEvent.value}")
                }

                // [301] Remote configuration failed, usually falls back to local assets
                SplashEvent.CONFIG_ERROR -> {
                    SLog.w(TAG, "Config load error. Using local fallback strategy.")
                }

                // [400] UI needs to display privacy policy dialog
                SplashEvent.NEED_PRIVACY -> {
                    SLog.d(TAG, "Privacy policy interaction required.")
                }

                // [401] User clicked 'Agree' - Crucial point for SDK initialization
                SplashEvent.PRIVACY_AGREED -> {
                    SLog.i(TAG, "Privacy agreed by user. Triggering delayed SDK initialization.")
                    // Recommendation: Invoke lazy-load modules here
                }

                // [402] User clicked 'Deny' - App usually exits
                SplashEvent.PRIVACY_DENY -> {
                    SLog.w(TAG, "Privacy denied. App startup interrupted.")
                }

                // [403] Privacy already accepted previously (returning user)
                SplashEvent.PRIVACY_ACCEPTED -> {
                    SLog.d(TAG, "Privacy already accepted. Proceeding with normal flow.")
                    DeviceManager.instance.init(KYApp.app);
                }

                // [500] Advertisement data is ready for display
                SplashEvent.AD_AVAILABLE -> {
                    SLog.i(TAG, "Splash AD available. AdID: ${splashEvent.value}")
                }

                // [600] Splash sequence finished, navigating to Main/Home
                SplashEvent.FINISHED -> {
                    SLog.d(TAG, "Splash flow finished. Clearing startup cache.")
                }

                else -> {
                    SLog.e(TAG, "Unknown event code received: ${splashEvent.code}")
                }
            }
        }

    }
}
