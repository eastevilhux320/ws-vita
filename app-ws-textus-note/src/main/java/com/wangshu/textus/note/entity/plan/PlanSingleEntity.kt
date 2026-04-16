package com.wangshu.textus.note.entity.plan

import com.wangshu.textus.note.R
import com.wangshu.textus.note.entity.plan.PlanEntity
import ext.TimeExt.format

/**
 * 单次计划实体类
 */
class PlanSingleEntity : PlanEntity() {

    override fun showCreateTimeText(): String? {
        val format = appDateFormat();
        val time = createTime.format(format);
        return getString(R.string.plan_single_time_create,time);
    }

    override fun showProgressText(): String {
        return getString(R.string.plan_complete_format,super.showProgressText());
    }

    /**
     * 如果[recyclerItemType]方法返回为[com.star.starlight.ui.view.commons.RecyclerItemType]中定义的自定义布局展示类型，
     * 展示的item资源布局将通过调用次方法获得
     * create by Eastevil at 2022/10/28 17:15
     * @author Eastevil
     * @param
     * @return
     */
    override fun customLayoutId(): Int {
        return R.layout.recycler_item_plan_single;
    }
}
