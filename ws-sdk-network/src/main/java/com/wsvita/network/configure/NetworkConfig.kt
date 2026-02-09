package com.wsvita.network.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig

class NetworkConfig : CommonBaseConfig<NetworkConfig.Builder> {
    var baseUrl : String? = null;

    private constructor(builder: Builder) : super(builder){
        this.baseUrl = builder.baseUrl;
    }

    class Builder : BaseConfigBuilder<NetworkConfig>{
        var baseUrl : String;

        constructor(appId: Long,baseUrl: String) : super(appId) {
            this.baseUrl = baseUrl;
        }

        override fun builder(): NetworkConfig {
            return NetworkConfig(this);
        }
    }
}
