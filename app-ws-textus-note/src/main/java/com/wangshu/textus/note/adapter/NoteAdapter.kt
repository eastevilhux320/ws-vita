package com.wangshu.textus.note.adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import com.wangshu.textus.note.entity.note.NoteEntity
import com.wsvita.core.common.adapter.AppAdapter
import com.wangshu.textus.note.R
import com.wangshu.textus.note.databinding.RecyclerItemNoteBinding
import com.wsvita.ui.ext.ViewExt.context

class NoteAdapter : AppAdapter<NoteEntity>{

    constructor(context: Context,dataList : MutableList<NoteEntity>?) : super(context,dataList){

    }

    override fun getLayoutId(): Int {
        return R.layout.recycler_item_note
    }

    override fun onBindingView(root: View, item: NoteEntity?, position: Int) {
        super.onBindingView(root, item, position)
    }

    override fun onBindItem(binding: ViewDataBinding, item: NoteEntity?, position: Int) {
        super.onBindItem(binding, item, position)
        if(binding is RecyclerItemNoteBinding){

            val lineDivider = DividerItemDecoration(binding.root.context, DividerItemDecoration.VERTICAL)
            lineDivider.setDrawable(ContextCompat.getDrawable(binding.root.context,  R.drawable.line_default)!!)
            binding.rvNoteText.addItemDecoration(lineDivider)

            var adapter = binding.noteTextAdapter;
            if(adapter == null){
                adapter = NoteTextAdapter(binding.root.context,item?.noteTextList);
                binding.noteTextAdapter = adapter;
            }else{
                adapter.setList(item?.noteTextList)
                adapter.notifyDataSetChanged();
            }
        }
    }
}
