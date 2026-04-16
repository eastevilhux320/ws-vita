package com.wangshu.textus.note.entity.note

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp
import com.wsvita.core.common.BaseEntity
import ext.TimeExt.format

class NoteTextEntity : BaseEntity() {

    /**
     * 文本内容
     */
    var text: String? = null

    /**
     * 创建时间
     */
    var createDate: Long = 0;

    /**
     * 用户id
     */
    var userId: Long = 0

    /**
     * 排序
     */
    var sort: Long = 0

    /**
     * 所属记录id
     */
    var noteId: Long = 0

    /**
     * 是否处于编辑之中
     */
    var isEditing : Boolean = false;

    /**
     * 是否属于输入类型
     */
    var isInput : Boolean = false;

    var address : String? = null;
    var province : String? = null;
    var city : String? = null;
    var area : String? = null;

    var weather : String? = null;
    var weatherPic : String? = null;

    val indexText : String
    get() {
        return sort.toString();
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

    val createTimeText : String
    get() {
        return createDate.format(NoteApp.app.getString(R.string.app_datatime_format_chinse_nosecond))
    }
}
