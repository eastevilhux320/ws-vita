package com.vita.xuantong.demo.model.main

/**
 * 首页常量配置
 */
object MainConstants{

    /**
     * Fragment 唯一标识常量池
     * 用于匹配后端动态配置的底部导航栏标识
     */
    object FragmentCode {

        /** 首页 */
        const val HUBER_MAIN_TAB_HOME = "HUBER_MAIN_TAB_HOME"

        /** 发现 */
        const val HUBER_MAIN_TAB_DISCOVERY = "HUBER_MAIN_TAB_DISCOVERY"

        /** 荆楚行 (应用内定义功能) */
        const val HUBER_MAIN_TAB_CHULINK = "HUBER_MAIN_TAB_CHULINK"

        /** 我的 */
        const val HUBER_MAIN_TAB_MINE = "HUBER_MAIN_TAB_MINE"
    }

    /**
     * 首页的跳转配置定义
     */
    object RouterName{
        /**
         * 搭车/匹配中心
         */
        const val HITCH = "hitch";
    }

}
