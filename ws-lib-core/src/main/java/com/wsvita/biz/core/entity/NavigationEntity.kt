package com.wsvita.biz.core.entity

import com.wsvita.core.common.BaseEntity
import java.util.*

/**
 * @author Eastevil
 * @version 1.0.0
 * @description : 应用导航实体类
 * @date 2024/7/15 10:26
 */
class NavigationEntity : BaseEntity
    () {

    /**
     * 名称
     */
    var name: String? = null

    /**
     * 副名称
     */
    var subName: String? = null

    /**
     * 标识编码
     */
    var code : String? = null;

    /**
     * 类型，1-首页导航菜单
     */
    var type: Int? = null

    /**
     * 连接url
     */
    var linkUrl: Int? = null

    /**
     * 点击跳转类型,0-web连接类型,1-页面跳转
     */
    var jumpType: Int? = null

    /**
     * 跳转页面路径
     */
    var jumpPage: String? = null

    /**
     * 如果[recyclerItemType]方法返回为[com.star.starlight.ui.view.commons.RecyclerItemType]中定义的自定义布局展示类型，
     * 展示的item资源布局将通过调用次方法获得
     * create by Eastevil at 2022/10/28 17:15
     * @author Eastevil
     * @param
     * @return
     */
    override fun customLayoutId(): Int {
        return 0;
    }
}
