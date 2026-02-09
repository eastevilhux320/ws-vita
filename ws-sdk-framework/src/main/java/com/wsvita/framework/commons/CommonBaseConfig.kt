package com.wsvita.framework.commons

/**
 * 组件基础配置类,其他组件中的所有开放配置类都需要继承次类
 */
abstract class CommonBaseConfig<B : BaseConfigBuilder<*>> : BaseConfig{

    constructor(builder : BaseConfigBuilder<*>){
        appId = builder.appId;
        titleBackgroundColor = builder.titleBackgroundColor;
        titleBackResId = builder.titleBackResId;
        titleText = builder.titleText;
        titleTextColor = builder.titleTextColor;
        mainThemeColor = builder.mainThemeColor;
        channelCode = builder.channelCode;
        networkUrl = builder.networkUrl;
        version = builder.version;
        versionName = builder.versionName;
        userAgreementUrl = builder.userAgreementUrl;
        privacyAgreementUrl = builder.privacyAgreementUrl;
        submitColor = builder.submitColor;
        cancelColor = builder.cancelColor;
    }
}
