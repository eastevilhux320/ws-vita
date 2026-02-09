package com.wsvita.network.entity

/**
 * 网络请求分页响应基础类，继承自 [BaseResponse]，用于统一封装列表数据及分页元数据
 * create by Eastevil at 2025/12/25 10:20
 * @author Eastevil
 */
abstract class BasePageResponse<T> : BaseResponse() {

    /**
     * 满足当前查询条件的总记录数，用于 UI 层计算分页总数
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var total : Int = 0;

    /**
     * 当前页面返回的业务数据列表
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var list : MutableList<T>? = null;

    /**
     * 判断当前返回的数据列表是否为空
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     * @return Boolean 如果列表为 null 或长度为 0，则返回 true
     */
    fun isEmpty(): Boolean {
        list?.let {
            return it.isNotEmpty();
        }?:let {
            return true;
        }
    }
}
