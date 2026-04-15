package com.wangshu.textus.note.model.main.note

import android.os.Bundle
import com.baidu.entity.pb.PoiResult.Contents.Ext.DetailInfo.Meishipaihao.Main
import com.wangshu.textus.note.R
import com.wangshu.textus.note.databinding.FragmentMainNoteBinding
import com.wangshu.textus.note.model.main.MainActivity
import com.wangshu.textus.note.model.main.NoteMainFragment
import com.wsvita.biz.core.BR
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreContainerActivity
import com.wsvita.core.common.adapter.CoreAdapter
import com.wsvita.core.common.adapter.CoreAdapterBuilder
import com.wsvita.framework.router.FinishParam
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson

class NoteFragment : NoteMainFragment<FragmentMainNoteBinding, NoteViewModel>() {
    private lateinit var memoAdapter : CoreAdapter;

    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_note;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_note;
    }

    override fun getVMClass(): Class<NoteViewModel> {
        return NoteViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        SLog.d(TAG,"initView");

        memoAdapter = CoreAdapterBuilder(requireContext(), R.layout.recycler_item_memo)
            .setVariableId(BR.address)
            .onBind { binding, item, position ->  }
            .onItemClick { root, position ->

            }
            .build();
        dataBinding.memoAdapter = memoAdapter;
    }

    override fun onMainShow() {
        super.onMainShow()
        SLog.d(TAG,"onMainShow");
        val ac = requireActivity();
        if(ac is MainActivity){
            val location = ac.currentLocation();
            location?.let { viewModel.receiveLocation(it) };
        }
    }

    override fun onResume() {
        super.onResume()
        SLog.d(TAG,"onResume");
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
