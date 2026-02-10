package com.vita.xuantong.demo.model.main.mine

import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.vita.xuantong.demo.commons.KYFragment
import com.wangshu.vita.demo.R
import com.wangshu.vita.demo.databinding.FragmentMainMineBinding
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.core.local.manager.SystemBarManager
import ext.ColorExt.changeAlpha

class MineFragment : KYFragment<FragmentMainMineBinding, MineViewModel>() {

    override fun layoutId(): Int {
        return R.layout.fragment_main_mine;
    }

    override fun getVMClass(): Class<MineViewModel> {
        return MineViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //我的不需要状态栏
        val config = ScreenConfig.buildFull(getColor(R.color.color_main_theme));
        SystemBarManager.instance.applyConfig(requireActivity(), config)

        dataBinding.fragment = this;
        dataBinding.appBarVip.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    override fun navigationId(): Int {
        return R.id.nav_f_main_mine;
    }

    private val onOffsetChangedListener = object : AppBarLayout.OnOffsetChangedListener{
        override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
            val offSet = Math.abs(verticalOffset);
            val range = appBarLayout?.totalScrollRange;
            if(range != null){
                val alph = Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange();
                dataBinding.toolbarVip.setBackgroundColor(
                    changeAlpha(getColor(R.color.color_main_theme),
                        Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange())
                );
                val viewAlpha = offSet.toFloat()/range.toFloat();
                dataBinding.ivMineMsg.alpha = viewAlpha;
                dataBinding.ivMineSetting.alpha = viewAlpha;
                dataBinding.tvMineAccountNickname.alpha = viewAlpha;

                if(alph == 0.0F){
                    dataBinding.toolbarVip.visibility = View.GONE;
                }else{
                    if(dataBinding.toolbarVip.visibility == View.GONE){
                        dataBinding.toolbarVip.visibility = View.VISIBLE;
                    }
                }
            }
        }
    }

    companion object{
        private const val TAG = "WSVita_App_Main_MineFragment==>";

        fun newInstance(): MineFragment {
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
