package com.wsvita.core.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig


class CoreConfig : CommonBaseConfig<CoreConfig.Builder> {

    private constructor(builder: Builder) : super(builder) {

    }

    class Builder(appId: Long) : BaseConfigBuilder<CoreConfig>(appId) {
        //组件中日期展示的管理


        override fun builder(): CoreConfig {
            return CoreConfig(this);
        }
    }
}
