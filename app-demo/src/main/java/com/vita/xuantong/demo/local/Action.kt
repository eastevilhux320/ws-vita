package com.wsvita.app.local

/**
 * 内部路由/动作常量定义表
 * 用于跨组件 Intent 跳转、广播发送或深度链接（DeepLink）匹配
 * * @author Eastevil
 * @createTime 2026/01/05
 */
object Action {

    /**
     * 主界面动作标识
     * 目标：指向应用的主入口（Mirror Main Activity）
     * 场景：常用于从外部启动页、通知栏点击回跳或插件化组件回调
     */
    const val ACTIN_MAIN = "com.wsvita.app.mirror.main";
}
