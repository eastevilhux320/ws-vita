package com.wangshu.textus.note.model.main.discovery

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainDiscoveryBinding
import com.wangshu.textus.note.model.main.NoteMainFragment

class DiscoveryFragment : NoteMainFragment<FragmentMainDiscoveryBinding, DiscoveryViewModel>() {


    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_discovery;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_discovery;
    }

    override fun getVMClass(): Class<DiscoveryViewModel> {
        return DiscoveryViewModel::class.java;
    }

    companion object{
        private const val TAG = "WS_Note_DiscoveryFragment==>";

        fun newInstance(): DiscoveryFragment {
            val args = Bundle()
            val fragment = DiscoveryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
