package com.wangshu.mira.ext

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig

object ConfigExt {

    /**
     * 通用的配置属性同步扩展函数
     * * [功能描述]:
     * 1. 身份对齐：同步 appId 与 channelCode，确保望舒 APP 系统网络请求的验签合法性。
     * 2. 安全降级/灰度：同步 version 字段，作为底层处理 AppResult 时选择 AES 加密算法（老版本 vs 灰度版本）的判断依据。
     * 3. 视觉穿透：同步 mainThemeColor、submitColor 等 UI 颜色，驱动 wsui 五层继承 Adapter 实现组件皮肤的自动适配。
     * 4. 业务下发：同步协议地址与网络 BaseUrl，确保全站业务逻辑链条闭环。
     * * @param B 目标 Builder 类型，必须继承自 BaseConfigBuilder
     * @param source 源配置对象，必须继承自 CommonBaseConfig，提供原始业务参数
     * @return 返回 Builder 自身实例，支持流式 DSL 链式调用
     * * @sample
     * val fc = FrameConfig.Builder(config.appId).run {
     * with(ConfigExt) { applyFrom(config) }
     * builder()
     * }
     */
    fun <B : BaseConfigBuilder<*>> B.applyFrom(source: CommonBaseConfig<*>): B = apply {
        // 必须调用 Builder 提供的 Public 方法（即你代码中那些返回 BaseConfigBuilder<C> 的方法）
        this.setVersion(source.version)
        this.setVersionName(source.versionName ?: "")

        // 注意：这里调用的是方法，不是直接给 internal 变量赋值
        this.mainThemeColor(source.mainThemeColor)
        this.titleBackgroundColor(source.titleBackgroundColor)
        this.titleTextColor(source.titleTextColor)
        this.titleBackResId(source.titleBackResId)
        this.submitColor(source.submitColor)
        this.cancelColor(source.cancelColor)

        this.networkUrl(source.networkUrl ?: "")
        this.setChannelCode(source.channelCode ?: "")
        this.userAgreementUrl(source.userAgreementUrl ?: "")
        this.privacyAgreementUrl(source.privacyAgreementUrl ?: "")
    }
}
