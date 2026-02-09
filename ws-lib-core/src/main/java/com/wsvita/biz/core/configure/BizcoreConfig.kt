package com.wsvita.biz.core.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig

class BizcoreConfig : CommonBaseConfig<BizcoreConfig.Builder>{
    /**
     * 启动页展示的默认背景图片，默认将会是一张白色的shape背景
     */
    var splashDefaultId : Int = com.wsvita.ui.R.drawable.shape_white;
    var mainAction : String;

    private constructor(builder: Builder) : super(builder) {
        this.splashDefaultId = builder.splashDefaultId;
        this.mainAction = builder.mainAction;
    }

    class Builder : BaseConfigBuilder<BizcoreConfig> {
        /**
         * 启动页展示的默认背景图片，默认将会是一张白色的shape背景
         */
        internal var splashDefaultId : Int = com.wsvita.ui.R.drawable.shape_white;
        internal var mainAction : String;

        constructor(appId: Long,mainAction : String) : super(appId) {
            this.mainAction = mainAction;
        }

        fun splashDefaultId(splashDefaultId : Int): Builder {
            this.splashDefaultId = splashDefaultId;
            return this;
        }

        override fun builder(): BizcoreConfig {
            return BizcoreConfig(this);
        }
    }
}
