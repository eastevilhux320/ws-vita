package com.vita.xuantong.demo.model.main.discovery

import android.os.Bundle
import com.vita.xuantong.demo.commons.KYFragment
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.FragmentMainDiscoveryBinding

class DiscoveryFragment : KYFragment<FragmentMainDiscoveryBinding, DiscoveryViewModel>() {

    override fun layoutId(): Int {
        return R.layout.fragment_main_discovery;
    }

    override fun getVMClass(): Class<DiscoveryViewModel> {
        return DiscoveryViewModel::class.java;
    }

    override fun navigationId(): Int {
        return R.id.nav_f_main_discovery;
    }

    companion object{
        private const val TAG = "WSVita_App_Main_DiscoveryFragment==>";

        fun newInstance(): DiscoveryFragment {
            val args = Bundle()
            val fragment = DiscoveryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
