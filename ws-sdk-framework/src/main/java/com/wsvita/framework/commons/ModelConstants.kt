package com.wsvita.framework.commons

object ModelConstants {

    /**
     * 定义系统的模块code标识
     * ws-sdk-framework-10000
     * ws-sdk-network-20000;
     * ws-sdk-ui-30000
     * ws-sdk-core-40000;
     * ws-lib-core-50000
     * ws-lib-advertise-60000;
     * ws-lib-account-70000;
     */
    object ModelCode{
        // SDK 系列 (10000 - 40000)
        const val SDK_FRAMEWORK = 10000
        const val SDK_NETWORK = 20000
        const val SDK_UI = 30000
        const val SDK_CORE = 40000

        //SDK 模块子code
        /**
         * 在ws-sdk-core中的默认请求标识
         */
        const val SDK_CORE_REQUEST_DEFAULT = 400010;
        const val SDK_CORE_REQEUST_EMPTY_ERROR_CODE = 400050;

        // Lib 系列 (50000 - 70000)
        const val LIB_CORE = 50000
        const val LIB_ADVERTISE = 60000
        const val LIB_ACCOUNT = 70000
    }

    /**
     * 定义系统级别的错误
     */
    object ModelError{

    }
    /**
     * 系统级 Intent 传递参数常量池
     * * [设计规范]
     * 1. 公共 Key 命名建议：wsv_sdk_[模块名]_[功能名]
     * 2. 新增 Key 必须包含：物理意义、数据类型、流向说明
     */
    object IntentExtra{
        /**
         * **容器跳转目的地标识**
         * * **[1. 物理意义]**
         * 该常量存储在启动容器 Activity (如 AppContainerActivity) 的 Intent 中。
         * * **[2. 工作流逻辑]**
         * - **传入端**：通过 `routerContainer` 方法族注入目的地 [fragmentId]。
         * - **解析端**：[AppContainerActivity.onPrepareNavGraph] 会优先读取此 ID 并覆盖默认起始页。
         * * **[3. 数据类型]**：[Int] (资源 ID)
         */
        const val EXTRA_TARGET_FRAGMENT_ID = "wsv_sdk_base_target_fragment_id"
        /**
         * **目标页面透传参数包**
         * * **[1. 物理意义]**
         * 一个嵌套在 Intent 根部的 [Bundle]，专门承载需要传递给目标 Fragment 的业务数据。
         * * **[2. 工作流逻辑]**
         * - **封装**：`routerContainer` 自动将业务参数放入此 Bundle 中。
         * - **中转**：[SDKNavContainerActivity] 在 `setGraph` 时，将此 Bundle 提取并设置为 Fragment 的 arguments。
         * - **接收**：目标 Fragment 在 [initFromArguments] 中通过此 Key 获取完整的参数集。
         * * **[3. 数据类型]**：[android.os.Bundle]
         */
        const val EXTRA_TARGET_FRAGMENT_ARGS = "wsv_sdk_base_target_fragment_args"

        /**
         * **整型路由通用传递 Key**
         * [数据类型]：Int
         */
        const val EXTRA_ROUTER_INT_KEY = "wsv_sdk_base_router_int_key"

        /**
         * **长整型路由通用传递 Key**
         * [数据类型]：Long
         */
        const val EXTRA_ROUTER_LONG_KEY = "wsv_sdk_base_router_long_key"

        /**
         * **字符串路由通用传递 Key**
         * [数据类型]：String
         */
        const val EXTRA_ROUTER_STRING_KEY = "wsv_sdk_base_router_string_key"

        /**
         * **布尔路由通用传递 Key**
         * [数据类型]：Boolean
         */
        const val EXTRA_ROUTER_BOOLEAN_KEY = "wsv_sdk_base_router_boolean_key"

        /**
         * **双精度浮点数路由通用传递 Key**
         * [数据类型]：Double
         */
        const val EXTRA_ROUTER_DOUBLE_KEY = "wsv_sdk_base_router_double_key"

        /**
         * **单精度浮点数路由通用传递 Key**
         * [数据类型]：Float
         */
        const val EXTRA_ROUTER_FLOAT_KEY = "wsv_sdk_base_router_float_key"

        /**
         * **序列化对象路由通用传递 Key**
         * [数据类型]：Serializable
         */
        const val EXTRA_ROUTER_SERIALIZABLE_KEY = "wsv_sdk_base_router_serializable_key"

        /**
         * **Parcelable 对象路由通用传递 Key**
         * [数据类型]：Parcelable
         */
        const val EXTRA_ROUTER_PARCELABLE_KEY = "wsv_sdk_base_router_parcelable_key"
    }

    /**
     * 系统级 Activity Result 返回值常量池
     * [设计规范]
     * 1. 命名建议：wsv_res_[模块名]_[功能名]
     * 2. 必须明确：返回值的具体数据类型及业务含义
     */
    object ResultKey {
        /**
         * **登录结果返回 Key**
         * * **[1. 物理意义]**：登录流程结束后返回给调用方的结果。
         * * **[2. 工作流逻辑]**：
         * - **设置端**：[LoginActivity] 登录成功或失败后，通过 `setResult` 将状态存入此 Key。
         * - **接收端**：调用方在 `onActivityResult` 或 `ActivityResultCallback` 中解析。
         * * **[3. 数据类型]**：[Boolean] (true: 登录成功, false: 取消或失败)
         */
        const val RESULT_LOGIN = "wsv_res_account_login_result";

        /**
         * **登录用户信息对象 Key**
         * * **[1. 物理意义]**：登录成功后透传的用户基本信息实体。
         * * **[2. 数据类型]**：[Parcelable] (UserEntity)
         */
        const val RESULT_LOGIN_USER_INFO = "wsv_res_account_user_info"

        /**
         * **通用操作结果描述**
         * * **[物理意义]**：返回操作失败时的错误文案或状态描述。
         * * **[数据类型]**：[String]
         */
        const val RESULT_COMMON_MESSAGE = "wsv_res_base_common_message"

        /**
         * **通用操作结果 Code**
         * * **[物理意义]**：对应 [ModelCode] 中的细分状态。
         * * **[数据类型]**：[Int]
         */
        const val RESULT_COMMON_CODE = "wsv_res_base_common_code"

        /**
         * **通用复杂数据返回 Key**
         * * **[物理意义]**：用于透传复杂的业务数据 Bundle。
         * * **[数据类型]**：[android.os.Bundle]
         */
        const val RESULT_COMMON_DATA_BUNDLE = "wsv_res_base_common_data_bundle"
    }
}
