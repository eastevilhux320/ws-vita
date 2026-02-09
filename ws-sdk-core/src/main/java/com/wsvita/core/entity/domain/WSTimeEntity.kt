package com.wsvita.core.entity.domain

import com.wsvita.core.common.BaseEntity

class WSTimeEntity : IWheelPickerItem, BaseEntity() {

    var time : Int = 0;

    /**
     * 1-小时，2-分钟，3-秒
     */
    var type : Int = 1;

    override fun getPickerViewText(): String {
        return time.toString();
    }

    override fun customLayoutId(): Int {
        return 0;
    }
}
