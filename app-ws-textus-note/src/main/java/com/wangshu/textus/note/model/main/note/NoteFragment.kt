package com.wangshu.textus.note.model.main.note

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.databinding.FragmentMainNoteBinding

class NoteFragment :
    com.wangshu.textus.note.common.NoteFragment<FragmentMainNoteBinding, NoteViewModel>() {

    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_note;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_note;
    }

    override fun getVMClass(): Class<NoteViewModel> {
        return NoteViewModel::class.java;
    }

    companion object{
        private const val TAG = "WS_Note_Main_NoteFragment==>";
        private const val REQUEST_SELECT_PARENT_TYPE = 100;
        private const val REQUEST_SELECT_CHILD_TYPE = 101;

        fun newInstance(): NoteFragment {
            val args = Bundle()
            val fragment = NoteFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
