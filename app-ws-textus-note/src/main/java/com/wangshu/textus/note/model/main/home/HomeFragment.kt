package com.wangshu.note.app.model.main.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.baidu.location.BDLocation
import com.star.light.common.adapter.NavigationAdapter
import com.star.light.common.entity.enums.NavigationCode
import com.star.light.common.ext.ApplicationExt.openQQChat
import com.star.light.common.local.manager.PermissionsManager
import com.starlight.dot.framework.utils.*
import com.wangshu.lunaris.model.customer.CustomerData
import com.wangshu.note.app.R
import com.wangshu.note.app.adapter.TypePercentAdapter
import com.wangshu.note.app.common.AppConfigure
import com.wangshu.note.app.common.NoteFragment
import com.wangshu.note.app.databinding.FragmentMainHomeBinding
import com.wangshu.note.app.entity.appenums.TimeType
import com.wangshu.note.app.entity.bill.BillTypeEntity
import com.wangshu.note.app.local.CommentPopupHelper.showCommentInput
import com.wangshu.note.app.local.manager.AppManager
import com.wangshu.note.app.model.finance.FinanceData
import com.wangshu.note.app.model.bill.BillData
import com.wangshu.note.app.model.budget.BudgetData
import com.wangshu.note.app.model.main.MainActivity
import com.wangshu.note.app.model.note.WSNoteData
import com.wangshu.note.app.model.plan.PlanData
import com.wangshu.note.app.model.search.SearchData
import com.wangshu.note.app.widget.popup.BillPopup
import com.wangshu.note.app.widget.popup.HomeAddPopup

class HomeFragment : NoteFragment<FragmentMainHomeBinding, HomeViewModel>() {
    private lateinit var navigationAdapter : NavigationAdapter;
    private lateinit var incomePercentAdapter : TypePercentAdapter;
    private lateinit var expenditurePercentAdapter : TypePercentAdapter;
    private lateinit var addPopup : HomeAddPopup;
    private lateinit var billPopup: BillPopup;

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main_home;
    }

    override fun getVMClass(): Class<HomeViewModel> {
        return HomeViewModel::class.java;
    }

    override fun initView() {
        super.initView()
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
            it.code?.let {
                val code = NavigationCode.getNavigationCode(it);
                SLog.d(TAG,"onNavigationClick,code:${code.code}")
                when(code){
                    NavigationCode.NOTE_MAIN_NAV_BILL->{
                        BillData.billList(requireContext(),false);
                    }
                    NavigationCode.NOTE_MAIN_NAV_BUDGET->{
                        BudgetData.toBudget(requireContext(),null,false);
                    }
                    NavigationCode.NOTE_MAIN_NAV_NOTE->{
                        BillData.billStatistics(requireContext(),TimeType.DAILY,false);
                    }
                    NavigationCode.NOTE_MAIN_NAV_INCOME->{
                        FinanceData.toMain(requireContext());
                    }
                    NavigationCode.NOTE_MAIN_NAV_SERVICE->{
                        CustomerData.customerWeb(requireContext());
                    }
                }
            }
        }

        dataBinding.swipeRefreshlayout.setOnRefreshListener {
            viewModel.refreshData();
        }

        incomePercentAdapter = TypePercentAdapter(requireContext(),null);
        expenditurePercentAdapter = TypePercentAdapter(requireContext(),null);
        dataBinding.incomePercentAdapter = incomePercentAdapter;
        dataBinding.expenditurePercentAdapter = expenditurePercentAdapter;

        addPopup = HomeAddPopup.Builder(requireContext())
            .onAdd {
                when(it){
                    HomeAddPopup.ADD_TYPE_MEMO->{
                        WSNoteData.memo(requireContext(),false);
                    }
                    HomeAddPopup.ADD_TYPE_NOTE->{
                        setAlpha(0.7f);
                        val notePopup = getAppActivity().showCommentInput(onLoading = {
                            viewModel.showLoading();
                        }, onDismissLoading = {
                            viewModel.dismissLoading();
                        }, onSuccess = {
                            //发布成功
                            SLog.d(TAG,"notePopup success");
                        })
                        viewModel.currentLocation()?.let { it1 -> notePopup.setLocation(it1) }
                        notePopup.setOnDismissListener {
                            setAlpha(1f);
                        }
                    }
                    HomeAddPopup.ADD_TYPE_PLAN->{
                        PlanData.publishPlan(requireContext(),false);
                    }
                }
            }
            .onBill {
                billPopup.showViewDown(dataBinding.clMainLayout);
            }
            .size(getScreenSize(requireContext())[0],220.dip2px())
            .builder();
        addPopup.setOnDismissListener {
            setAlpha(1f);
        }

        billPopup = BillPopup.Builder(requireContext())
            .onSelectTime {
                //选择时间
                AppManager.instance.selectDataTime(requireContext(),0);
            }
            .onSelectParentType {
                //选择父类型
                BillData.selectBillType(requireContext(),it,
                    REQUEST_SELECT_PARENT_TYPE,false);
            }
            .onSelectChildType {
                //选择子类型
                BillData.selectBillType(requireContext(), it,
                    REQUEST_SELECT_CHILD_TYPE,false);
            }
            .onLoading {
                viewModel.showLoading();
            }
            .onDismissLoading {
                viewModel.dismissLoading();
            }
            .onBillSuccess {
                viewModel.refreshData();
            }
            .size(getScreenSize(requireContext())[0],590.dip2px())
            .isTranslucent(true)
            .builder();

    }

    override fun addObserve() {
        super.addObserve()
        viewModel.homeConfig.observe(this, Observer {
            dataBinding.clMainTitle.setBackgroundColor(Color.parseColor(it.toolBarColor));
            val editBgcolor = it.searchBgcolor?.parseColor() ?: Color.WHITE;
            val searchStrokeColor = it.searchStrokeColor?.parseColor() ?: Color.WHITE;

            dataBinding.editHomeSearch.setBackground(
                requireContext().createRectangleShape(
                    editBgcolor,
                    it.searchStroke, searchStrokeColor, it.searchRadius
                )
            );
        })

        viewModel.monthBillDeail.observe(this, Observer {
            dataBinding.swipeRefreshlayout.isRefreshing = false;
        })

        viewModel.navigationList.observe(this, Observer {
            navigationAdapter.dataList = it;
            navigationAdapter.notifyDataSetChanged();
        })

        viewModel.incomePercentList.observe(this, Observer {
            incomePercentAdapter.dataList = it;
            incomePercentAdapter.notifyDataSetChanged();
        })

        viewModel.expenditurePercentList.observe(this, Observer {
            expenditurePercentAdapter.dataList = it;
            expenditurePercentAdapter.notifyDataSetChanged();
        })
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.iv_add_icon->{
                if(viewModel.locationSuccess()){
                    setAlpha(0.7f);
                    addPopup.showViewDown(dataBinding.clMainLayout);
                }else{
                    if(PermissionsManager.instance.hasLocationPermissions(getAppActivity())){
                        //已经有权限,请求定位
                        viewModel.showLoading();
                        val ac = getAppActivity(MainActivity::class.java);
                        ac?.requestLocation();
                        delay(800){
                            viewModel.dismissLoading();
                            setAlpha(0.7f);
                            addPopup.showViewDown(dataBinding.clMainLayout);
                        }
                    }else{
                        //没有权限，请求权限
                        val ac = getAppActivity(MainActivity::class.java);
                        ac?.requestLocationTips();
                    }
                }
            }
            R.id.cl_bill_detail_day->{
                BillData.billStatistics(requireContext(),TimeType.DAILY,false);
            }
            R.id.cl_bill_detail_week->{
                BillData.billStatistics(requireContext(),TimeType.WEEKLY,false);
            }
            R.id.cl_bill_detail_month->{
                BillData.billStatistics(requireContext(),TimeType.MONTHLY,false);
            }
            R.id.cl_bill_detail_year->{
                BillData.billStatistics(requireContext(),TimeType.YEARLY,false);
            }
            R.id.edit_home_search->{
                SearchData.searchMain(requireContext());
            }
        }
    }

    override fun onDateSelected(requestCode: Int, resultCode: Int, dateTime: Long) {
        super.onDateSelected(requestCode, resultCode, dateTime)
        SLog.d(TAG,"onDateSelected,requestCode:${requestCode},resultCode:${resultCode},dataTime:${dateTime}");
        //选择时间返回
        billPopup.setBilLTime(dateTime);
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
        setAlpha(0.7f);
        addPopup.showViewDown(dataBinding.clMainLayout);
    }

    fun receiveMainLocationChanged(location : BDLocation){
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
    }

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
