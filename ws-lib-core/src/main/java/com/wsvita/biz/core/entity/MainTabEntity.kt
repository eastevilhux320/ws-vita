package com.wsvita.biz.core.entity

import com.wsvita.biz.core.R
import com.wsvita.core.common.BaseEntity

class MainTabEntity : BaseEntity() {

    /**
     * tab名称
     */
    var name: String? = null

    /**
     * 选中图标
     */
    var selIcon: String? = null

    /**
     * 非选中图标
     */
    var norIcon: String? = null

    /**
     * 选中文本颜色
     */
    var selColor: String? = null

    /**
     * 非选中文本颜色
     */
    var norColor: String? = null

    /**
     * 图标宽度
     */
    var iconWidth: Int? = null

    /**
     * 图标高度
     */
    var iconHeight: Int? = null

    /**
     * 底部导航对应的fragment类型
     * 1:首页, 2:精品, 3:发现, 4:我的
     */
    var fragmentType: Int? = null

    /**
     * tab栏类型
     * 1：主界面底部导航栏
     */
    var tabType: Int? = null

    /**
     * 底部导航对应的fragment code标识
     */
    var fragmentCode: String? = null

    /**
     * 网页嵌入url地址
     */
    var webviewUrl: String? = null

    /**
     * 排序
     */
    var sort: Int? = null

    override fun customLayoutId(): Int {
        return R.layout.rv_item_bizcore_main_tab;
    }

}
