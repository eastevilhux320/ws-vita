package com.wsvita.biz.core.startup

/**
 * 配置接收器接口
 */
interface AppStartupConfigReceiver {

    fun onStartupConfigReady(provider: IStartupConfigProvider)
}
