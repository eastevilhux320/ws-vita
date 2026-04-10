package com.wangshu.textus.note.model.main.default

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainDefaultBinding

class DefaultFragment : NoteFragment<FragmentMainDefaultBinding, DefaultViewModel>() {


    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_default;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_default;
    }

    override fun getVMClass(): Class<DefaultViewModel> {
        return DefaultViewModel::class.java;
    }

    companion object{
        private const val TAG = "WS_AI_DefaultFragment==>";

        fun newInstance(): DefaultFragment {
            val args = Bundle()
            val fragment = DefaultFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
