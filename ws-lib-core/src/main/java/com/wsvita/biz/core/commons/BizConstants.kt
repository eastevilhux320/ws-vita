package com.wsvita.biz.core.commons

/**
 * 业务核心层通用常量定义
 * * 设计规范：
 * 1. 采用组件化隔离前缀，业务逻辑 Key 统一以 "bizcore_" 开头。
 * 2. 配合 MVVM 架构中 ViewModel 的 SavedStateHandle 或 Intent 传参使用。
 * 3. 严格遵循自定义组件的属性命名规范。
 * ws-lib-core:50000
 */
object BizConstants {

    /**
     * 页面跳转及数据传递专用 Key
     */
    object IntentKey{
        const val PROTOCOL_URL_KEY = "bizcore_protocol_url_key";
        const val PROTOCOL_RESULT_FLAG = "protocol_result_flag_key";
        /**
         * 地区/城市热门状态标识
         * 常用于：城市选择组件中区分“热门城市”与“普通列表”的逻辑判断
         * 对应前缀：bizcore_region_
         * 类型：Boolean
         */
        const val REGION_CITY_HOT_FLAG = "bizcore_region_hot_city_flag";

        /**
         * 是否展示区/县级列表的控制标识
         * true-在选择完城市后，继续展示并要求选择对应的区/县 (District)
         * false-仅选择到城市 (City) 级别即停止逻辑，不展示区/县列表
         */
        const val REGION_CITY_SHOW_DISTRICT = "bizcore_region_show_district";

        /**
         * 城市选择页面选择完毕后是否直接返回的标识
         * true-表示选择完毕后立即返回到上一层页面
         * false-表示选择完毕后继续停留在城市页面
         */
        const val REGION_SELECTED_FINISH = "bizcore_region_city_selected_finish_flag"

        /**
         * 地址信息数据对象
         * - 业务场景：进入地址编辑页面时传入的初始实体类，或保存成功后回传给调用方的地址对象。
         * - 数据类型：Parcelable (通常对应 AddressBean 或 SiteEntity)
         * - 协作组件：AddressEnrollmentFragment, AddressActivity
         * - 对应前缀：bizcore_address_
         */
        const val ADDRESS = "bizcore_address_info_key";

        /**
         * 地区选择结果：省份 (Province)
         * 返回数据通常包含：省份名称、省份编码 (adcode)
         */
        const val RESULT_REGION_PROVINCE = "bizcore_region_result_province_key";
        /**
         * 地区选择结果：城市 (City)
         * 返回数据通常包含：城市名称、城市编码、所属省份信息
         */
        const val RESULT_REGION_CITY = "bizcore_region_result_city_key";
        /**
         * 地区选择结果：区/县 (District)
         * 返回数据通常包含：区县名称、街道信息、完整行政区划路径
         */
        const val RESULT_REGION_DISTRICT = "bizcore_region_result_district_key";
    }

    /**
     * 业务核心层 - 地区选择结果常量定义
     * * 用于 Fragment 间通信 (Fragment Result API) 或 ViewModel 消息传递。
     * 配合组件化架构中的多个 Fragment 容器使用，确保跨模块/跨页面的 Key 统一。
     */
    object ResultKey{

    }

    /**
     * 65535以下
     */
    object ResultCode{
        /**
         * 地区选择结果回传 Key
         * * [应用场景]
         * - 用于 [PlateLicensePopupwindow] 弹窗选择结果的回传。
         * - 用于 Fragment 之间通信（Fragment Result API）时的数据标识。
         * - 统一前缀：bizcore
         */
        const val RESULT_REGION = 500001;
    }
}
