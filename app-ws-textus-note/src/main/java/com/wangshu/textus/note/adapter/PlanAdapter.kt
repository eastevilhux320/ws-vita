package com.wangshu.textus.note.adapter

import android.content.Context
import com.wangshu.textus.note.entity.plan.PlanEntity
import com.wsvita.core.common.adapter.AppAdapter

class PlanAdapter : AppAdapter<PlanEntity>{


    constructor(context: Context, dataList : MutableList<PlanEntity>?) : super(context,dataList){

    }

    override fun isUsedAdapterLayout(): Boolean {
        return false;
    }

}
