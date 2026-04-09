package com.wangshu.mira.entity

import com.wangshu.mira.R
import com.wsvita.core.common.BaseEntity

class ChronoMenuEntity : BaseEntity() {

    var name : String? = null;
    var type : Int = 0;

    override fun customLayoutId(): Int {
        return R.layout.recycler_item_mira_chrono_menu;
    }

}
