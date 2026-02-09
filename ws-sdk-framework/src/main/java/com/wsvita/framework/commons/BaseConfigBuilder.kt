package com.wsvita.framework.commons

import android.graphics.Color
import com.wsvita.framework.R

/**
 * 组件基础配置顶级父类的 Builder 模式定义
 * @author Eastevil
 * @createTime 2025/12/24
 */
abstract class BaseConfigBuilder<C : BaseConfig> {

    /**
     * 应用ID
     */
    internal var appId: Long = 0

    /**
     * 标题背景颜色
     */
    internal var titleBackgroundColor: Int = Color.BLACK

    /**
     * 标题返回图片资源ID
     */
    internal var titleBackResId: Int = R.drawable.ic_ws_base_back

    /**
     * 标题文本
     */
    internal var titleText: String? = null

    /**
     * 标题文本颜色
     */
    internal var titleTextColor: Int = Color.WHITE

    /**
     * 进入工具模块的主题颜色
     */
    internal var mainThemeColor: Int = Color.BLACK

    /**
     * 渠道标识
     */
    internal var channelCode: String? = null

    /**
     * 组件网络请求url
     */
    internal var networkUrl: String? = null

    /**
     * 版本号
     */
    internal var version: Int = 0

    /**
     * 版本名称
     */
    internal var versionName: String? = null

    /**
     * 用户协议地址
     */
    internal var userAgreementUrl: String? = null

    /**
     * 隐私协议地址
     */
    internal var privacyAgreementUrl: String? = null

    /**
     * 提交按钮的颜色
     */
    internal var submitColor : Int = Color.BLACK;

    /**
     * 取消按钮的颜色
     */
    internal var cancelColor : Int = Color.GRAY;

    /**
     * 构造方法
     * @param appId 应用ID
     * @author Eastevil
     * @createTime 2025/12/24
     */
    constructor(appId: Long) {
        this.appId = appId
    }

    /**
     * 设置标题背景颜色
     * @param titleBackgroundColor 颜色值
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun titleBackgroundColor(titleBackgroundColor: Int): BaseConfigBuilder<C> = apply {
        this.titleBackgroundColor = titleBackgroundColor
    }

    /**
     * 设置标题返回图片资源ID
     * @param titleBackResId 图片资源ID
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun titleBackResId(titleBackResId: Int): BaseConfigBuilder<C> = apply {
        this.titleBackResId = titleBackResId
    }

    /**
     * 设置标题文本
     * @param titleText 标题字符串
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun titleText(titleText: String): BaseConfigBuilder<C> = apply {
        this.titleText = titleText
    }

    /**
     * 设置标题文本颜色
     * @param titleTextColor 颜色值
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun titleTextColor(titleTextColor: Int): BaseConfigBuilder<C> = apply {
        this.titleTextColor = titleTextColor
    }

    /**
     * 设置进入工具模块的主题颜色
     * @param mainThemeColor 主题色
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun mainThemeColor(mainThemeColor: Int): BaseConfigBuilder<C> = apply {
        this.mainThemeColor = mainThemeColor
    }

    /**
     * 设置渠道标识
     * @param channelCode 渠道Code
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun setChannelCode(channelCode: String): BaseConfigBuilder<C> = apply {
        this.channelCode = channelCode
    }

    /**
     * 设置组件网络请求URL
     * @param networkUrl 请求地址
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun networkUrl(networkUrl: String): BaseConfigBuilder<C> = apply {
        this.networkUrl = networkUrl
    }

    /**
     * 设置版本号
     * @param version 版本数字
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun setVersion(version: Int): BaseConfigBuilder<C> = apply {
        this.version = version
    }

    /**
     * 设置版本名称
     * @param versionName 版本名（如 1.0.0）
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun setVersionName(versionName: String): BaseConfigBuilder<C> = apply {
        this.versionName = versionName
    }

    /**
     * 设置用户协议地址
     * @param url 协议H5链接
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun userAgreementUrl(url: String): BaseConfigBuilder<C> = apply {
        this.userAgreementUrl = url
    }

    /**
     * 设置隐私协议地址
     * @param url 协议H5链接
     * @author Eastevil
     * @createTime 2025/12/24
     */
    fun privacyAgreementUrl(url: String): BaseConfigBuilder<C> = apply {
        this.privacyAgreementUrl = url
    }

    /**
     * 设置组件内“提交/确定/下一步”等正面意图按钮的背景颜色。
     * * 该配置将直接影响组件内关键操作按钮的视觉呈现，建议设置为接入方 App 的主色调以保持视觉一致性。
     * * @param submitColor 颜色值（例如：Color.BLUE 或 0xFF0000FF）
     * @return 当前 Builder 实例，支持链式调用
     * @author Eastevil
     */
    fun submitColor(submitColor: Int): BaseConfigBuilder<C> = apply {
        this.submitColor = submitColor
    }

    /**
     * 设置组件内“取消/返回/重置”等负面意图或辅助按钮的背景颜色。
     * * 该配置通常用于次要操作按钮，建议使用较弱的视觉颜色（如灰色系）或与主色调形成对比的辅助色。
     * * @param cancelColor 颜色值（例如：Color.GRAY 或 0xFF999999）
     * @return 当前 Builder 实例，支持链式调用
     * @author Eastevil
     */
    fun cancelColor(cancelColor: Int): BaseConfigBuilder<C> = apply {
        this.cancelColor = cancelColor
    }

    /**
     * 抽象构建方法，由子类实现具体配置对象的实例化
     * @return 具体配置类实例
     * @author Eastevil
     * @createTime 2025/12/24
     */
    abstract fun builder(): C
}
