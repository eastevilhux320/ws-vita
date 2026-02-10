package com.wsvita.app.local.startup.handler

import com.wsvita.app.local.startup.BaseAppStartupHandlerImpl
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.framework.utils.SLog
import com.wsvita.network.manager.TokenManager

class TokenStartupHandler : BaseAppStartupHandlerImpl() {
    companion object{
        private const val TAG = "Mirror_Startup_Token=>";
        private const val TOKEN_PARAMS_NAME = "token";
    }

    override fun onHandle(provider: IStartupConfigProvider) {
        val token = provider.getString(TOKEN_PARAMS_NAME);
        SLog.d(TAG,"token:${token}");
        TokenManager.instance.resetToken(token);
    }
}
