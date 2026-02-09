package com.wsvita.biz.core.configure

/**
 * [全局路由 Action 映射表]
 * * 【设计职责】
 * 1. 唯一性：每个字符串代表项目中一个唯一的页面或功能动作。
 * 2. 契约化：作为组件间“对暗号”的依据，发起端和接收端必须引用同一个常量。
 * 3. 易查性：通过 IDE 的“Find Usages”功能，可以瞬间定位该 Action 的所有触发点和响应 Activity。
 */
object Action {

    /**
     * 【启动页：协议授权页面】
     * * - 业务场景：App 首次启动时弹出的隐私协议详情页。
     * - 目标页面：[com.wsvita.biz.core.model.protocol.ProtocolActivity]
     * - 匹配方式：AndroidManifest.xml 中 Activity 的 <action android:name="..." />
     * - 路由协议：通常配合 BooleanRouterContract 使用。
     * - 输入参数 (Input): 协议的 Web URL 地址 (String)。
     * - 输出参数 (Output): 用户是否点击了“同意”按钮 (Boolean)。
     */
    const val ACTIN_SPLASH_PROTOCOL = "com.wsvita.biz.core.splash.protocol";

    /**
     * 【城市选择页】
     * * - 业务场景：App 所有涉及到城市三级选择的页面
     * - 目标页面：[com.wsvita.biz.core.model.region.RegionActivity]
     * - 匹配方式：AndroidManifest.xml 中 Activity 的 <action android:name="..." />
     * - 路由协议：通常配合 BooleanRouterContract 使用。
     * - 输入参数 (Input): 是否启用热门城市
     * - 输出参数 (Output): 选择的具体的城市
     */
    const val ACTION_REGION = "com.wsvita.biz.core.region"

    /**
     * 【地址管理/录入页】
     * - 业务场景：用户新增收货地址、编辑现有地址或从地址薄选择地址。
     * - 目标页面：[com.wsvita.biz.core.model.address.AddressActivity] (建议对应路径)
     * - 匹配方式：AndroidManifest.xml 中 Activity 的 <action android:name="..." />
     * - 路由协议：通常配合 AddressRouterContract 使用。
     * - 输入参数 (Input):
     * 1. 模式选择 (Int): 新增模式或编辑模式。
     * 2. 初始数据 (Parcelable): 编辑模式下传入的现有地址对象。
     * - 输出参数 (Output):
     * 1. 操作结果 (Boolean): 是否保存成功。
     * 2. 地址数据 (Parcelable): 用户最终保存或选中的地址实体。
     */
    const val ACTION_ADDRESS = "com.wsvita.biz.core.address";
}
