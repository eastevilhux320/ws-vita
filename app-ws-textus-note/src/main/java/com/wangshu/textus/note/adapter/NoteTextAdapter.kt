package com.wangshu.textus.note.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.wangshu.textus.note.entity.note.NoteTextEntity
import com.wsvita.core.common.adapter.AppAdapter
import com.wangshu.textus.note.R;
import com.wangshu.textus.note.BR;

class NoteTextAdapter : AppAdapter<NoteTextEntity>{

    constructor(context: Context,dataList : MutableList<NoteTextEntity>?) : super(context,dataList){

    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_note_text
    }

    override fun setBean(dataBinding: ViewDataBinding, entity: NoteTextEntity, position: Int) {
        super.setBean(dataBinding, entity, position)
        dataBinding.setVariable(BR.noteText,entity);
        dataBinding.setVariable(BR.position,position);
    }
}
