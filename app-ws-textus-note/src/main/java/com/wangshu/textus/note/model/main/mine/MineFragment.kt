package com.wangshu.textus.note.model.main.mine

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainMineBinding

class MineFragment : NoteFragment<FragmentMainMineBinding, MineViewModel>() {

    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_mine;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_mine;
    }

    override fun getVMClass(): Class<MineViewModel> {
        return MineViewModel::class.java;
    }

    companion object{
        private const val TAG = "WS_Note_MineFragment==>";

        fun newInstance(): MineFragment {
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
