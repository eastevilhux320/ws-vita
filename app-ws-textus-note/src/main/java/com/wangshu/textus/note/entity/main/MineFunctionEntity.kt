package com.wangshu.note.app.entity.main

import com.wangshu.textus.note.R
import com.wsvita.core.common.BaseEntity

class MineFunctionEntity : BaseEntity() {
    var iconResId : Int = com.wsvita.ui.R.drawable.ui_list_item_no_data_default;

    var functionText : String? = null;

    override fun customLayoutId(): Int {
        return R.layout.rv_item_mine_function;
    }
}
