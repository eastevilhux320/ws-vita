package com.wsvita.biz.core.network.request

import com.wsvita.biz.core.entity.AppUrlEntity
import com.wsvita.biz.core.entity.SplashConfigEntity
import com.wsvita.network.entity.BaseRequest

/**
 * 启动配置
 */
class LaunchConfigRequest : BaseRequest() {

    /**
     * 欢迎页配置
     */
    var splash : SplashConfigEntity? = null;

    /**
     * 用户协议
     */
    var protocol : AppUrlEntity? = null;
}
