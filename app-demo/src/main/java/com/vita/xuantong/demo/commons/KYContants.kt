package com.vita.xuantong.demo.commons

/**
 * 应用自身的配置
 */
object KYContants {

    /**
     * 容器数据总线缓存 Key
     * 对应 ContainerManager 中的存储键值
     */
    object ContainerKey {

        // 位置信息缓存（Json 镜像）
        const val BIZ_LOCATION = "mirror_container_location"

        // 账号/用户信息缓存
        const val ACCOUNT_INFO = "mirror_container_account"

        // 界面配置状态 (Int/Boolean)
        const val WSUI_THEME_MODE = "wsui_container_theme_mode"

        // 容器运行状态标记
        const val CONTAINER_STATUS = "mirror_container_run_status"
    }

}
