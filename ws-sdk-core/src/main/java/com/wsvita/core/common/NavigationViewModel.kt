package com.wsvita.core.common

import android.app.Application

/**
 * 导航业务逻辑基类 ViewModel。
 *
 * 该类专为 Navigation 架构设计，用于处理 Fragment 碎片化场景下的业务逻辑。
 * 配合 [NavigationFragment] 使用，实现组件化模式下的数据流转与页面状态管理。
 *
 * create by Eastevil at 2026/1/20 15:30
 * @author Eastevil
 */
abstract class NavigationViewModel(application: Application) : AppViewModel(application) {

}
