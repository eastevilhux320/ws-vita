package com.wangshu.textus.note.entity.plan

import com.wangshu.note.app.entity.appenums.note.PlanType
import com.wangshu.textus.note.R
import java.io.Serializable


/**
 * 计划实体类
 * 支持时间计划、目标计划和代办计划
 */
open class PlanEntity : BasePlanEntity(),Serializable, IPlan {

    override fun customLayoutId(): Int {
        val pType = PlanType.fromType(planType);
        return when(pType){
            PlanType.ONCE-> R.layout.recycler_item_plan_single;
            PlanType.TIME_DAILY,
            PlanType.TIME_WEEKLY,
            PlanType.TIME_MONTHLY,
            PlanType.TIME_QUARTER,
            PlanType.TIME_YEARLY->{
                R.layout.recycler_item_plan_time;
            }
            PlanType.TODO-> R.layout.recycler_item_plan_todo;
            PlanType.GOAL-> R.layout.recycler_item_plan_goal;
            else-> R.layout.recycler_item_plan_single;
        }
    }

}
