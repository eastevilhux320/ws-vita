package com.wangshu.textus.note.entity.main

class AppHomeConfig {

    /**
     * id
     */
    var id: Long = 0


    /**
     * 所属应用id
     */
    var appId: Long = 0

    /**
     * 所属应用名称
     */
    var appName: String? = null

    var homeLogo: String? = null

    /**
     * 顶部状态栏
     */
    var toolBarColor: String? = null

    /**
     * 是否展示首页搜索框
     */
    var showSearch: Boolean = false

    /**
     * 首页搜索背景框颜色
     */
    var searchBgcolor: String? = null

    /**
     * 首页搜索边框宽度
     */
    var searchStroke: Int = 0

    /**
     * 首页搜索边框颜色
     */
    var searchStrokeColor: String? = null

    /**
     * 首页搜索边框圆角角度
     */
    var searchRadius: Float = 0F

    /**
     * 是否显示扫一扫图标
     */
    var showScan: Boolean = false

    /**
     * 是否显示添加图标
     */
    var showAdd: Boolean = false

    /**
     * 扫一扫图标地址
     */
    var scanIcon: String? = null

    /**
     * 添加图标地址
     */
    var addIcon: String? = null

    /**
     * 状态，1：上架，2：下架
     */
    var state: Int = 0

    /**
     * 搜索图标地址
     */
    var searchIcon: String? = null

    
    var searchHint: String? = null

    /**
     * 轮播图标识
     */
    var bannerCode : String? = null;

    /**
     * 首页列表id
     */
    var homeListId : Long = 0;
}
