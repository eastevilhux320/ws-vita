package com.vita.xuantong.demo.model.main.default

import android.os.Bundle
import com.vita.xuantong.demo.commons.KYFragment
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.FragmentMainDefaultBinding

class DefaultFragment : KYFragment<FragmentMainDefaultBinding, DefaultViewModel>() {
    override fun layoutId(): Int {
        return R.layout.fragment_main_default;
    }

    override fun getVMClass(): Class<DefaultViewModel> {
        return DefaultViewModel::class.java;
    }

    override fun navigationId(): Int {
        return R.id.nav_f_main_default;
    }

    companion object{
        private const val TAG = "WS_App_Main_DefaultFragment==>";

        fun newInstance(): DefaultFragment {
            val args = Bundle()
            val fragment = DefaultFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
