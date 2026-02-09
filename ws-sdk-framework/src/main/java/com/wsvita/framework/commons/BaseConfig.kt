package com.wsvita.framework.commons

import android.graphics.Color
import com.wsvita.framework.R

/**
 * 组件基础配置顶级父类
 */
abstract class BaseConfig {

    var appId : Long = 0;

    constructor(){

    }

    /**
     * 标题背景颜色
     */
    var titleBackgroundColor : Int = Color.BLACK;

    /**
     * 标题返回图片资源ID
     */
    var titleBackResId : Int = R.drawable.ic_ws_base_back;

    /**
     * 标题文本
     */
    var titleText : String? = null;

    /**
     * 标题文本颜色
     */
    var titleTextColor : Int = Color.WHITE;

    /**
     * 进入工具模块的主题颜色
     */
    var mainThemeColor : Int = Color.BLACK;

    /**
     * 渠道标识
     */
    var channelCode : String? = null;

    /**
     * 组件网络请求url
     */
    var networkUrl : String? = null;

    /**
     * 版本号
     */
    var version : Int = 0;

    /**
     * 版本名称
     */
    var versionName : String? = null;

    /**
     * 用户协议地址
     */
    var userAgreementUrl : String? = null;

    /**
     * 隐私协议地址
     */
    var privacyAgreementUrl : String? = null;

    /**
     * 提交按钮的颜色
     */
    var submitColor : Int = Color.BLACK;

    /**
     * 取消按钮的颜色
     */
    var cancelColor : Int = Color.GRAY;
}
