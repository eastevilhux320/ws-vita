package com.wsvita.network.entity

/**
 * 网络请求分页基础抽象类，继承自 [BaseRequest]，用于统一处理分页查询、排序等通用参数
 * create by Eastevil at 2025/12/25 10:20
 * @author Eastevil
 */
abstract class BasePageRequest : BaseRequest() {
    companion object {
        /**
         * Description 默认的单页记录条数
         */
        private const val PAGE_SIZE_DEFAULT = 15L;
    }

    /**
     * 当前查询的页码，默认为第 1 页
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var page : Long = 1;

    /**
     * 分页限制条数，与 [pageSize] 含义相同，兼容不同后端接口习惯
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var limit : Long = PAGE_SIZE_DEFAULT;

    /**
     * 单页显示的数量规模
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var pageSize : Long = PAGE_SIZE_DEFAULT;

    /**
     * 排序方式，如 "asc" (升序) 或 "desc" (降序)，默认为 "asc"
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var order : String = "asc";

    /**
     * 排序参考的数据库字段名称，默认为 "id"
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var orderField : String = "id";
}
