package com.wsvita.biz.core.entity

/**
 * ### 业务通用定位实体
 * * **设计意图**：解耦第三方地图 SDK，作为业务层传递位置信息的统一载体。
 * * **规范约束**：禁止使用主构造函数，显式展示构造方法。
 */
class BizLocation {

    /**
     * 详细地址信息
     */
    var address : String? = null;

    /**
     * 国家
     */
    var country : String? = null;

    /**
     * 获取省份
     */
    var province : String? = null;

    /**
     * 城市
     */
    var city : String? = null;

    /**
     * 区县
     */
    var district : String? = null;

    /**
     * 经度
     */
    var longitude : String? = null;

    /**
     * 纬度
     */
    var latitude : String? = null;

}
