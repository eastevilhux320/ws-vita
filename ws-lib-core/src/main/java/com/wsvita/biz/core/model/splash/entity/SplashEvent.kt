package com.wsvita.biz.core.model.splash.entity

/**
 * Splash 启动流程事件实体
 * 满足组件化中通过名称或 Code 获取具体值的需求
 */
class SplashEvent {

    var code: Int = -1
    var name: String? = null
    var value: String? = null

    /**
     * 提供一个便捷的初始化方法（链式调用）
     */
    fun set(code: Int, name: String? = null, value: String? = null): SplashEvent {
        this.code = code
        this.name = name
        this.value = value
        return this
    }

    override fun toString(): String {
        return "SplashEvent(code=$code, name=$name, value=$value)"
    }

    companion object {

        /** * [100] 启动流程正式开始
         * 触发时机：SplashActivity onCreate 或 ViewModel init
         */
        const val START = 100

        /** * [200] App 前置校验/预请求成功
         * 业务含义：设备校验、临时 Token 获取等前置接口已完成
         * Value 含义：通常存放返回的 Token 字符串
         */
        const val APP_BEFOREHAND_SUCCESS = 200

        /** * [201] App 前置校验失败
         * 业务含义：关键初始化接口请求失败，可能导致无法继续后续流程
         */
        const val APP_BEFOREHAND_ERROR = 201

        /** * [202] 从预请求接口获取到当前用户处于登录中
         * 业务含义：当前的app中的token可用，在后端能被识别，属于登录的状态。
         */
        const val ACCOUNT_STATE_ON = 202;

        /** * [300] 启动配置加载完成
         * 业务含义：远程配置（SplashConfigEntity）获取成功，决定了是否展示广告、协议版本等
         * Value 含义：可选，可存放配置版本的唯一标识
         */
        const val CONFIG_LOADED = 300

        /** * [301] 启动配置加载失败
         * 业务含义：无法获取广告策略或启动策略，通常走本地兜底逻辑或中断
         */
        const val CONFIG_ERROR = 301

        /** * [400] 需要展示隐私协议弹窗
         * 业务含义：本地校验发现用户未同意协议或协议版本已更新
         */
        const val NEED_PRIVACY = 400

        /** * [401] 用户在弹窗中点击「同意」
         * 业务含义：用户授权成功，流程可继续向下流转（如进入广告或首页）
         */
        const val PRIVACY_AGREED = 401

        /** * [402] 用户在弹窗中点击「拒绝」
         * 业务含义：用户不同意核心协议，通常 UI 层执行 finish() 退出应用
         */
        const val PRIVACY_DENY = 402

        /** * [403] 隐私协议已校验通过
         * **业务含义:**
         * 隐私协议已校验通过,用户已同意协议说明，可以进行各种IMEI,OAID等获取
         *
         * **使用场景:**
         * 1-启动页中，用户已同意协议，直接校验通过,在[com.wsvita.biz.core.model.splash.SplashViewModel.disposeProtocol]方法中发送该事件
         * 2-在隐私协议展示页面[com.wsvita.biz.core.model.protocol.ProtocolActivity]点击同意后，标识校验通过。回到启动页，在
         * 在[com.wsvita.biz.core.model.splash.SplashViewModel.privacyAlreadyAccepted]方法中发送该事件
         */
        const val PRIVACY_ACCEPTED = 403

        /** * [500] 检测到存在可展示的启动广告
         * 业务含义：广告组件准备就绪，可以切换 UI 展示开屏广告
         * Value 含义：广告的唯一标识符 AdId
         */
        const val AD_AVAILABLE = 500

        /**
         * /** [501] 跳转至主界面 */
         */
        const val TO_MAIN = 501;

        /** * [600] 启动流程全部结束
         * 业务含义：所有前置任务、协议校验、广告展示均已完成
         * 触发动作：UI 层应当执行跳转 MainActivity 的操作
         */
        const val FINISHED = 600

        const val SPLASH_KEY = "ws_vita_splash_event_key";

        // --- 工厂方法定义 ---

        fun start(): SplashEvent = SplashEvent().apply {
            code = START
            name = "START"
        }

        fun appBeforehandSuccess(token: String?): SplashEvent = SplashEvent().apply {
            code = APP_BEFOREHAND_SUCCESS
            name = "APP_BEFOREHAND_SUCCESS"
            value = token
        }

        fun appBeforehandError(): SplashEvent = SplashEvent().apply {
            code = APP_BEFOREHAND_ERROR
            name = "APP_BEFOREHAND_ERROR"
        }

        fun accountStateOn(): SplashEvent = SplashEvent().apply {
            code = ACCOUNT_STATE_ON
            name = "ACCOUNT_STATE_ON";
        }

        fun configLoaded(valStr: String? = null): SplashEvent = SplashEvent().apply {
            code = CONFIG_LOADED
            name = "CONFIG_LOADED"
            value = valStr
        }

        fun configError(): SplashEvent = SplashEvent().apply {
            code = CONFIG_ERROR
            name = "CONFIG_ERROR"
        }

        fun needPrivacyPolicy(): SplashEvent = SplashEvent().apply {
            code = NEED_PRIVACY
            name = "NEED_PRIVACY"
        }

        fun privacyPolicyAgreed(): SplashEvent = SplashEvent().apply {
            code = PRIVACY_AGREED
            name = "PRIVACY_AGREED"
        }

        fun denyPrivacyPolicy(): SplashEvent = SplashEvent().apply {
            code = PRIVACY_DENY
            name = "PRIVACY_DENY"
        }

        fun privacyAlreadyAccepted(): SplashEvent = SplashEvent().apply {
            code = PRIVACY_ACCEPTED
            name = "PRIVACY_ACCEPTED"
        }

        fun adAvailable(adId: String?): SplashEvent = SplashEvent().apply {
            code = AD_AVAILABLE
            name = "AD_AVAILABLE"
            value = adId
        }

        fun toMain(): SplashEvent = SplashEvent().apply {
            code = TO_MAIN
            name = "TO_MAIN"
        }

        fun finished(): SplashEvent = SplashEvent().apply {
            code = FINISHED
            name = "FINISHED"
        }
    }
}
