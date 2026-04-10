package com.wangshu.textus.note.entity.bill

import com.wangshu.textus.note.R
import com.wsvita.core.common.BaseEntity
import java.io.Serializable
import java.util.*

/**
 * @author Eastevil
 * @version 1.0.0
 * @description : TODO
 * @date 2023/4/17 16:34
 */
class BillTypeEntity : Serializable, BaseEntity() {

    /**
     * 上级ID,一级pid为0
     */
    var pid: Long = 0
    var typeName: String? = null

    /**
     * 等级，1-1级，2-2级
     */
    var levelType: Int = 0;

    /**
     * 所属类型，1-平台(所有人可见),2-个人(个人所见)
     */
    var belongType: Int = 0;

    /**
     * 创建时间
     */
    var createDate: Long = System.currentTimeMillis();

    var creator: Long = 0

    /**
     * 账单类型，1-支出，2-收入
     */
    var billType: Int? = null

    /**
     * 如果[recyclerItemType]方法返回为[com.star.starlight.ui.view.commons.RecyclerItemType]中定义的自定义布局展示类型，
     * 展示的item资源布局将通过调用次方法获得
     * create by Eastevil at 2022/10/28 17:15
     * @author Eastevil
     * @param
     * @return
     */
    override fun customLayoutId(): Int {
        return  R.layout.recycler_item_billtype;
    }

    companion object {
        private const val serialVersionUID = -658775028424860289L
    }
}
