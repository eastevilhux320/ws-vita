package com.wangshu.textus.note.model.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.baidu.location.BDLocation
import com.wangshu.textus.note.R
import com.wangshu.textus.note.adapter.NavigationAdapter
import com.wangshu.textus.note.adapter.TypePercentAdapter
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.FragmentMainHomeBinding
import com.wangshu.textus.note.entity.bill.BillTypeEntity
import com.wangshu.textus.note.model.main.NoteMainFragment
import ext.StringExt.parseColor
import ext.ViewExt.createComplexRectDrawable

class HomeFragment : NoteMainFragment<FragmentMainHomeBinding, HomeViewModel>() {
    private lateinit var navigationAdapter : NavigationAdapter;
    private lateinit var incomePercentAdapter : TypePercentAdapter;
    private lateinit var expenditurePercentAdapter : TypePercentAdapter;


    override fun getVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.viewModel = viewModel;
        dataBinding.fragment = this;

        val navigationManager = GridLayoutManager(requireContext(),5);
        dataBinding.rvNavigation.layoutManager = navigationManager;

        navigationAdapter = NavigationAdapter(requireContext(),null);
        dataBinding.rvNavigation.adapter = navigationAdapter;

        navigationAdapter.onNavigationClick {
            if(!viewModel.isLogin()){
                toLogin();
                return@onNavigationClick;
            }
        }

        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refreshData();
        }

        incomePercentAdapter = TypePercentAdapter(requireContext(),null);
        expenditurePercentAdapter = TypePercentAdapter(requireContext(),null);
        dataBinding.incomePercentAdapter = incomePercentAdapter;
        dataBinding.expenditurePercentAdapter = expenditurePercentAdapter;

    }

    override fun addObserve() {
        super.addObserve()

        viewModel.homeConfig.observe(this, Observer {
            dataBinding.clMainTitle.setBackgroundColor(Color.parseColor(it.toolBarColor));
            val editBgcolor = it.searchBgcolor?.parseColor() ?: Color.WHITE;
            val searchStrokeColor = it.searchStrokeColor?.parseColor() ?: Color.WHITE;

            dataBinding.editHomeSearch.setBackground(
                requireContext().createComplexRectDrawable(editBgcolor, it.searchStroke, searchStrokeColor, it.searchRadius)
            );
        })

        viewModel.monthBillDeail.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
        })

        viewModel.navigationList.observe(this, Observer {
            navigationAdapter.setList(it);
            navigationAdapter.notifyDataSetChanged();
        })

        viewModel.incomePercentList.observe(this, Observer {
            incomePercentAdapter.setList(it)
            incomePercentAdapter.notifyDataSetChanged();
        })

        viewModel.expenditurePercentList.observe(this, Observer {
            expenditurePercentAdapter.setList(it)
            expenditurePercentAdapter.notifyDataSetChanged();
        })
    }

    override fun navigationId(): Int {
        return R.id.nav_textus_note_main_home;
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        if(!viewModel.isLogin()){
            toLogin();
            return;
        }
        when(view.id){

        }
    }

    override fun layoutId(): Int {
        return R.layout.fragment_main_home;
    }

   /* override fun onDateSelected(requestCode: Int, resultCode: Int, dateTime: Long) {
        super.onDateSelected(requestCode, resultCode, dateTime)
        SLog.d(TAG,"onDateSelected,requestCode:${requestCode},resultCode:${resultCode},dataTime:${dateTime}");
        //选择时间返回
        billPopup.setBilLTime(dateTime);
    }
*/
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
     *//*
    fun requestLocationResult(permissionFlag : Boolean){
        setAlpha(0.7f);
        addPopup.showViewDown(dataBinding.clMainLayout);
    }*/

  /*  fun receiveMainLocationChanged(location : BDLocation){
        SLog.d(TAG,"receiveMainLocationChanged");
        viewModel.recevieLocation(location);
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
    }*/

    companion object{
        private const val TAG = "WS_Note_HomeFragment==>";

        private const val REQUEST_SELECT_PARENT_TYPE = 80;
        private const val REQUEST_SELECT_CHILD_TYPE = 81;

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
