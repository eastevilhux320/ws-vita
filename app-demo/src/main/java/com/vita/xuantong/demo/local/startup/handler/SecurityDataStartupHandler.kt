package com.wsvita.app.local.startup.handler

import com.wsvita.app.local.manager.SecurityManager
import com.wsvita.app.local.startup.BaseAppStartupHandlerImpl
import com.wsvita.biz.core.startup.IStartupConfigProvider
import com.wsvita.framework.utils.SLog

class SecurityDataStartupHandler : BaseAppStartupHandlerImpl() {
    companion object{
        private const val TAG = "Mirror_Startup_Security=>";
        private const val SECRETKEY_PARAMS_NAME = "secretKey";
        private const val KEYTYPE_PARAMS_NAME = "keyType";
    }


    override fun onHandle(provider: IStartupConfigProvider) {
        val keyType = provider.getInt(KEYTYPE_PARAMS_NAME,1);
        val secretKey = provider.getString(SECRETKEY_PARAMS_NAME);
        SLog.d(TAG,"keyType:${keyType},secretKey:${secretKey}");
        SecurityManager.instance.resetSecret(keyType,secretKey?:"");
    }

}
