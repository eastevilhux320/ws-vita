package com.wsvita.account.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig
import com.wsvita.module.account.R

class AccountConfig : CommonBaseConfig<AccountConfig.Builder> {
    var appLogo : Int = 0;
    var appName : String? = null;
    var appSlogan : String? = null;

    private constructor(builder: Builder) : super(builder) {
        this.appLogo = builder.appLogo;
        this.appName = builder.appName;
        this.appSlogan = builder.appSlogan;
    }

    class Builder : BaseConfigBuilder<AccountConfig> {

        internal var appLogo : Int = 0;
        internal var appName : String? = null;
        internal var appSlogan : String? = null;


        constructor(appId: Long) : super(appId) {

        }

        fun appLogo(appLogo : Int): Builder {
            this.appLogo = appLogo;
            return this;
        }

        fun appName(appName: String?): Builder {
            this.appName = appName;
            return this;
        }

        fun appSlogan(appSlogan: String?): Builder {
            this.appSlogan = appSlogan;
            return this;
        }

        override fun builder(): AccountConfig {
            return AccountConfig(this);
        }
    }
}
