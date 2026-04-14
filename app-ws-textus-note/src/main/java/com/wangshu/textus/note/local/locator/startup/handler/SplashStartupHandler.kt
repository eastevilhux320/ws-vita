package com.wangshu.textus.note.local.locator.startup.handler

import com.wangshu.textus.note.common.NoteApp
import com.wangshu.textus.note.local.locator.startup.BaseAppStartupHandlerImpl
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.biz.core.model.splash.entity.LaunchEvent
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.biz.core.startup.StartupScope
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
        val launchEvent = eventJson?.parseGson<LaunchEvent>();
        launchEvent?.let {
            val code = it.code;
            SLog.d(TAG,"parse splash event success,code:${code}")
            when (code) {
                // [100] Startup sequence initiated
                LaunchEvent.START -> {
                    SLog.d(TAG, "Splash process started.")
                }

                // [200] Pre-check or token request successful
                LaunchEvent.APP_BEFOREHAND_SUCCESS -> {
                    SLog.i(TAG, "Pre-check success. Token received: ${launchEvent.value}")
                }

                // [201] Critical pre-check failed
                LaunchEvent.APP_BEFOREHAND_ERROR -> {
                    SLog.e(TAG, "Pre-check failed. Core initialization may be compromised.")
                }

                LaunchEvent.ACCOUNT_STATE_ON -> {
                    SLog.d(TAG, "ACCOUNT_STATE_ON.")
                    val state = provider.getInt("beforehandState",-1);
                    if(1 == state){
                        //接收到启动页发送的账号可用，处于登录状态的事务，更新用户信息
                        AccountManager.instance.notifyMember();
                    }
                }

                // [300] Remote configuration loaded successfully
                LaunchEvent.CONFIG_LOADED -> {
                    SLog.i(TAG, "Config loaded. Version/ID: ${launchEvent.value}")
                }

                // [301] Remote configuration failed, usually falls back to local assets
                LaunchEvent.CONFIG_ERROR -> {
                    SLog.w(TAG, "Config load error. Using local fallback strategy.")
                }

                // [400] UI needs to display privacy policy dialog
                LaunchEvent.NEED_PRIVACY -> {
                    SLog.d(TAG, "Privacy policy interaction required.")
                }

                // [401] User clicked 'Agree' - Crucial point for SDK initialization
                LaunchEvent.PRIVACY_AGREED -> {
                    SLog.i(TAG, "Privacy agreed by user. Triggering delayed SDK initialization.")
                    // Recommendation: Invoke lazy-load modules here
                }

                // [402] User clicked 'Deny' - App usually exits
                LaunchEvent.PRIVACY_DENY -> {
                    SLog.w(TAG, "Privacy denied. App startup interrupted.")
                }

                // [403] Privacy already accepted previously (returning user)
                LaunchEvent.PRIVACY_ACCEPTED -> {
                    SLog.d(TAG, "Privacy already accepted. Proceeding with normal flow.")
                    DeviceManager.instance.init(NoteApp.app);
                }

                // [500] Advertisement data is ready for display
                LaunchEvent.AD_AVAILABLE -> {
                    SLog.i(TAG, "Splash AD available. AdID: ${launchEvent.value}")
                }

                // [600] Splash sequence finished, navigating to Main/Home
                LaunchEvent.FINISHED -> {
                    SLog.d(TAG, "Splash flow finished. Clearing startup cache.")
                }
                else -> {
                    SLog.e(TAG, "Unknown event code received: ${launchEvent.code}")
                }
            }
        }

    }

    override fun getScope(): String {
        return StartupScope.STARTUP_SCOPE_APPBEFORE;
    }
}
