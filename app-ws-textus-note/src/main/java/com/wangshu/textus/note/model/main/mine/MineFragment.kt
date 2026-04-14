package com.wangshu.textus.note.model.main.mine

import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainMineBinding
import ext.ColorExt.changeAlpha

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

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.viewModel = viewModel;
        dataBinding.fragment = this;
        dataBinding.appBarVip.addOnOffsetChangedListener(onOffsetChangedListener);
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
        private const val TAG = "WS_Note_MineFragment==>";

        fun newInstance(): MineFragment {
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
