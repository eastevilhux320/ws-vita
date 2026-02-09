package com.wsvita.core.entity.domain

import com.wsvita.core.common.BaseEntity
import java.util.Calendar

class DateTimeEntity : BaseEntity() {

    /**
     * 1-星期
     * 2-日期
     */
    var timeType : Int = 0;

    // 完整日期时间戳 (用于点击后业务跳转或逻辑比对)
    var timestamp: Long = System.currentTimeMillis();

    var weekDayText : String? = null;

    /**
     * 是否可用,星期不作为计算。
     */
    var isUsed : Boolean = false;

    // 阳历日期数字 (通过 timestamp 实时转换)
    val dayText: String?
        get() {
            if(timeType == 1){
                return weekDayText;
            }else{
                val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
                return calendar.get(Calendar.DAY_OF_MONTH).toString()
            }
        }

    // 是否是今天：动态比对当前系统时间
    val isToday: Boolean
        get() {
            val now = Calendar.getInstance()
            val item = Calendar.getInstance().apply { timeInMillis = timestamp }
            return now.get(Calendar.YEAR) == item.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == item.get(Calendar.DAY_OF_YEAR)
        }

    companion object{
        private const val TAG = "WSVita_App_DateTimeEntity=>";
    }

    override fun customLayoutId(): Int {
        return 0;
    }

}
