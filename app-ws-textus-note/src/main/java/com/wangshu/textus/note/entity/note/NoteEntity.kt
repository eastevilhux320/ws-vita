package com.wangshu.textus.note.entity.note

import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.TimeExt.format
import java.util.*
import com.wangshu.textus.note.R
import ext.TimeExt.day
import ext.TimeExt.week

class NoteEntity : BaseEntity() {

    /**
     * 创建时间
     */
    var createDate: Long = 0;

    /**
     * 排序
     */
    var sort: Long = 0;

    var noteNum : Int = 0;

    var noteTextList : MutableList<NoteTextEntity>? = null;

    val noteTimeMonthText : String
    get() {
        return createDate.format(NoteApp.app.getString(R.string.main_note_date_format));
    }

    val noteNumText : String
    get() {
        return NoteApp.app.getString(R.string.note_num_format,noteNum.toString());
    }

    val dailyNoteText : String
    get() {
        return getString(R.string.daily_note_title,createDate.day().toString());
    }

    val dailyText : String
    get() {
        return getString(R.string.day_format,createDate.day().toString());
    }

    val noteWeekText : String
    get() {
        var w = createDate.week();
        return when (w) {
            Calendar.MONDAY -> getString(com.wsvita.ui.R.string.week_1)   // 星期一
            Calendar.TUESDAY -> getString(com.wsvita.ui.R.string.week_2)  // 星期二
            Calendar.WEDNESDAY -> getString(com.wsvita.ui.R.string.week_3)// 星期三
            Calendar.THURSDAY -> getString(com.wsvita.ui.R.string.week_4) // 星期四
            Calendar.FRIDAY -> getString(com.wsvita.ui.R.string.week_5)   // 星期五
            Calendar.SATURDAY -> getString(com.wsvita.ui.R.string.week_6) // 星期六
            Calendar.SUNDAY -> getString(com.wsvita.ui.R.string.week_7)   // 星期日
            else -> ""
        }
    }

    val noteDailyTimeText : String
    get() {
        val format = getString(R.string.chinese_time_week_format);
        return createDate.format(format);
    }

    val noteDailyNumText : String
    get() {
        return getString(R.string.note_daily_num_format,noteNum.toString());
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
        return 0;
    }
}
