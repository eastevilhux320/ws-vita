package com.wangshu.textus.note.entity.note

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.TimeExt.format

class MemoEntity : BaseEntity() {
    /**
     * 备忘录文本
     */
    var memoText: String? = null

    /**
     * 标题
     */
    var title: String? = null


    /**
     * 经度
     */
    var latitude: String? = null

    /**
     * 纬度
     */
    var longitude: String? = null

    /**
     * 详细地址
     */
    var address: String? = null

    /**
     * 重要程度，(划分位1-4个等级，1：极度重要，2：重要，3：一般，4：可忽略)
     */
    var degree: Int? = null

    /**
     * 备忘录时间
     */
    var memoDate: Long = 0L;


    /**
     * 创建时间
     */
    var createDate: Long = 0L;

    /**
     * 排序
     */
    var sort: Int = 0;

    val degreeIcon : Int
    get() {
        return when(degree){
            1-> R.drawable.icon_memo_degree_1;
            2-> R.drawable.icon_memo_degree_2;
            3-> R.drawable.icon_memo_degree_3;
            4-> R.drawable.icon_memo_degree_4;
            else-> com.wsvita.ui.R.drawable.ui_camera_default;
        }
    }

    val degreeTextColor : Int
    get() {
        return when(degree){
            1-> NoteApp.app.getColor(R.color.color_degree_1);
            2-> NoteApp.app.getColor(R.color.color_degree_2);
            3-> NoteApp.app.getColor(R.color.color_degree_3);
            4-> NoteApp.app.getColor(R.color.color_degree_4);
            else-> NoteApp.app.getColor(com.wsvita.ui.R.color.base_app_text);
        }
    }

    val degreeText : String
    get() {
        return when(degree){
            1-> getString(R.string.memo_degree_1);
            2-> getString(R.string.memo_degree_2);
            3-> getString(R.string.memo_degree_3);
            4-> getString(R.string.memo_degree_4);
            else-> unknowText();
        }
    }

    val memoTimeText : String
    get() {
        val ft = NoteApp.app.getString(com.wsvita.core.R.string.ws_date_time_format_chinese)
        return memoDate.format(ft);
    }

    val memoTipsText : String
    get() {
        return memoTimeText;
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
        return R.layout.recycler_item_memo;
    }
}
