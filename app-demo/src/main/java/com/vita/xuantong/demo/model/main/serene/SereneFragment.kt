package com.vita.xuantong.demo.model.main.serene

import android.os.Bundle
import com.vita.xuantong.demo.commons.KYFragment
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.FragmentMainDefaultBinding

class SereneFragment : KYFragment<FragmentMainDefaultBinding, SereneViewModel>() {
    override fun layoutId(): Int {
        return R.layout.fragment_main_serene;
    }

    override fun getVMClass(): Class<SereneViewModel> {
        return SereneViewModel::class.java;
    }

    override fun navigationId(): Int {
        return R.id.nav_f_main_serene;
    }

    companion object{
        private const val TAG = "WS_App_Main_DefaultFragment==>";

        fun newInstance(): SereneFragment {
            val args = Bundle()
            val fragment = SereneFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
