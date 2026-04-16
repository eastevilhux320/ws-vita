package com.wangshu.textus.note.entity.plan

import com.wangshu.textus.note.entity.plan.PlanEntity


class PlanImageEntity : PlanEntity() {

    /**
     * 计划id
     */
    var planId : Long = 0;

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
