package com.wangshu.note.app.model.main.default

import android.os.Bundle
import com.wangshu.note.app.R
import com.wangshu.note.app.common.NoteFragment
import com.wangshu.note.app.databinding.FragmentMainDefaultBinding

class DefaultFragment : NoteFragment<FragmentMainDefaultBinding, DefaultViewModel>() {

    override fun getLayoutRes(): Int {
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
