package com.wangshu.note.app.model.main.note

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.baidu.location.BDLocation
import com.star.starlight.ui.view.commons.RichAdapter
import com.star.starlight.ui.view.commons.RichBuilder
import com.star.starlight.ui.view.entity.RecyclerItemEntity
import com.starlight.dot.framework.utils.*
import com.wangshu.note.app.BR
import com.wangshu.note.app.R
import com.wangshu.note.app.adapter.NoteAdapter
import com.wangshu.note.app.adapter.note.PlanAdapter
import com.wangshu.note.app.databinding.FragmentMainNoteBinding
import com.wangshu.textus.note.entity.bill.BillTypeEntity
import com.wangshu.note.app.local.CommentPopupHelper.showCommentInput
import com.wangshu.note.app.local.manager.AppManager
import com.wangshu.note.app.model.bill.BillData
import com.wangshu.note.app.model.main.MainActivity
import com.wangshu.note.app.model.note.WSNoteData
import com.wangshu.note.app.model.plan.PlanData
import com.wangshu.note.app.widget.popup.BillPopup
import com.wangshu.textus.note.model.main.note.NoteViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NoteFragment :
    com.wangshu.note.app.common.NoteFragment<FragmentMainNoteBinding, NoteViewModel>() {
    private lateinit var noteAdapter : NoteAdapter;
    private lateinit var memoAdapter : RichAdapter;
    private lateinit var billPopup: BillPopup;
    private lateinit var planAdapter : PlanAdapter;

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main_note;
    }

    override fun getVMClass(): Class<NoteViewModel> {
        return NoteViewModel::class.java;
    }

    override fun initView() {
        super.initView()
        SLog.d(TAG,"initView");

        dataBinding.viewModel = viewModel;
        dataBinding.fragment = this;

        noteAdapter = NoteAdapter(requireContext(),null);
        noteAdapter.onNote {
            setAlpha(0.7f);
            val notePopup = getAppActivity().showCommentInput {
                viewModel.addNoteText(it);
            }
            notePopup.setOnDismissListener {
                setAlpha(1f);
            }
        }
        dataBinding.noteAdapter = noteAdapter;

        memoAdapter = RichBuilder(requireContext())
            .addLayout(R.layout.recycler_item_memo, BR.memo)
            .builder();
        dataBinding.memoAdapter = memoAdapter;

        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refreshData();
        }

        billPopup = BillPopup.Builder(requireContext())
            .onSelectTime {
                //选择时间
                AppManager.instance.selectDataTime(requireContext(),0);
            }
            .onSelectParentType {
                //选择父类型
                BillData.selectBillType(requireContext(),it,REQUEST_SELECT_PARENT_TYPE,false);
            }
            .onSelectChildType {
                //选择子类型
                BillData.selectBillType(requireContext(), it,REQUEST_SELECT_CHILD_TYPE,false);
            }
            .onLoading {
                viewModel.showLoading();
            }
            .onDismissLoading {
                viewModel.dismissLoading();
            }
            .onBillSuccess {
                //viewModel.refreshData();
                GlobalScope.launch {
                    viewModel.billingDetail();
                }
            }
            .size(getScreenSize(requireContext())[0],590.dip2px())
            .isTranslucent(true)
            .builder();

        planAdapter = PlanAdapter(requireContext(),null);
        dataBinding.planAdapter = planAdapter;

        dataBinding.rvPlan.addLineDivider(R.drawable.recycler_line_mainbg_10)
    }

    override fun onResume() {
        super.onResume()
        SLog.d(TAG,"onResume");
        val ac = getAppActivity(MainActivity::class.java);
        val mainLocation = ac?.getMainLocation();
        if (mainLocation != null) {
            viewModel.receiveMainLocation(mainLocation)
        }
    }

    override fun onContainerShow() {
        super.onContainerShow()
        val ac = getAppActivity(MainActivity::class.java);
        val mainLocation = ac?.getMainLocation();
        if (mainLocation != null) {
            viewModel.receiveMainLocation(mainLocation)
        }
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.noteList.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
            noteAdapter.dataList = it;
            noteAdapter.notifyDataSetChanged();
        })

        viewModel.memoList.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
            memoAdapter.dataList = it as List<RecyclerItemEntity>?;
            memoAdapter.notifyDataSetChanged();
        })

        viewModel.planList.observe(this, Observer {
            planAdapter.dataList = it;
            planAdapter.notifyDataSetChanged();
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        SLog.d(TAG,"setUserVisibleHint")
        if(isVisibleToUser){
            val ac = getAppActivity(MainActivity::class.java);
            val mainLocation = ac?.getMainLocation();
            if (mainLocation != null) {
                viewModel.receiveMainLocation(mainLocation)
            }else{
                //调用首页的请求开始定位的方法
                ac?.requestLocationTips();
            }
        }
    }

    fun setBillType(parentTypeJson : String?,childTypeJson : String?){
        val pType = parentTypeJson?.parseJSON<BillTypeEntity>();
        if(pType != null){
            billPopup.setParentType(pType);
        }
        val cType = childTypeJson?.parseJSON<BillTypeEntity>();
        if(cType != null){
            billPopup.setChildType(cType);
        }
    }


    override fun onViewClick(view: View) {
        super.onViewClick(view)
        if(!viewModel.isLogin()){
            toLogin();
            return;
        }
        when(view.id){
            R.id.ll_note_note->{
                //WSNoteData.notes(requireContext(),false);
                setAlpha(0.7f);
                val notePopup = getAppActivity().showCommentInput {
                    viewModel.addNoteText(it);
                }
                notePopup.setOnDismissListener {
                    setAlpha(1f);
                }
            }
            R.id.ll_note_memo->{
                WSNoteData.memo(requireContext(),false);
            }
            R.id.tv_note_bill->{
                billPopup.showViewDown(dataBinding.clMainLayout);
            }
            R.id.ll_note_plan->{
                PlanData.publishPlan(requireContext(),false);
            }
        }
    }

    override fun onDateSelected(requestCode: Int, resultCode: Int, dateTime: Long) {
        super.onDateSelected(requestCode, resultCode, dateTime)
        SLog.d(TAG,"onDateSelected,requestCode:${requestCode},resultCode:${resultCode},dataTime:${dateTime}");
        //选择时间返回
        billPopup.setBilLTime(dateTime);
    }

    fun receiveMainLocationChanged(location : BDLocation){
        SLog.d(TAG,"receiveMainLocationChanged");
        viewModel.recevieLocation(location);
    }

    /**
     * 地址权限请求成功，在首页设计到的是右上角的增加按钮点击事件
     * @author Eastevil
     * @createTime 2025/10/17 17:31
     * @param permissionFlag
     *      权限是否申请成功
     * @since
     * @see
     * @return
     *      void
     */
    fun requestLocationResult(permissionFlag : Boolean){
        //地址请求成功，这里不需要再继续请求，在MainActity中已经开始请求地址定位了
        //如果有一些其他的交互，可以在这里进行
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
