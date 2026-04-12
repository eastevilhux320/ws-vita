package com.wangshu.textus.note.model.main.home

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.textus.note.entity.main.MonthBillDetail
import com.wangshu.textus.note.common.NoteViewModel
import com.wangshu.textus.note.entity.bill.BillTypePercentEntity
import com.wangshu.textus.note.network.NoteModel
import com.wangshu.textus.note.network.reponse.YearlyDetailReponse
import com.wsvita.biz.core.entity.AppHomeConfigEntity
import com.wsvita.biz.core.entity.NavigationEntity
import com.wsvita.biz.core.network.model.AppModel
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : NoteViewModel(application) {
    val homeConfig = MutableLiveData<AppHomeConfigEntity>();
    /**
     * 首页导航菜单
     */
    val navigationList = MutableLiveData<MutableList<NavigationEntity>>();

    val monthBillDeail = MutableLiveData<MonthBillDetail>();
    val homeBillingDetail = MutableLiveData<YearlyDetailReponse>();

    /**
     * 收入百分比
     */
    val incomePercentList = MutableLiveData<MutableList<BillTypePercentEntity>>();

    /**
     * 支出百分比
     */
    var expenditurePercentList = MutableLiveData<MutableList<BillTypePercentEntity>>();


    override fun initModel() {
        super.initModel()
        loadData();
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        refreshData();
    }

    fun refreshData(){
        SLog.d(TAG,"refreshData")
        viewModelScope.launch {
            launch {
                homeBillDetail();
            }
            launch {
                homeBillingDetail();
            }
            launch {
                monthTypePercent();
            }
        }
    }

    private fun loadData(){
        SLog.d(TAG,"loadData")
        viewModelScope.launch {
            launch {
                homeConfig();
            }
            launch {
                navigationList();
            }
            launch {
                homeBillDetail();
            }
            launch {
                homeBillingDetail();
            }
            launch {
                monthTypePercent();
            }
        }
    }

    private suspend fun homeConfig(){
        SLog.d(TAG,"homeConfig");
        val homeConfig = request(requestCode = REQ_CODE_HOME_CONFIG, showLoading = false){
            AppModel.instance.appHomeConfig();
        }
        homeConfig?.let {
            withMain {
                this.homeConfig.value = it;
            }
        }

    }

    private suspend fun navigationList(){
        val result = request(requestCode = REQ_CODE_NAVIGATION_LIST, showLoading = false){
            BizcoreModel.instance.appNavigationList(1);
        }
        result?.let {list->
            withMain {
                navigationList.value = list;
            }
        }
    }

    private suspend fun homeBillDetail(){
        val result = request(requestCode = REQ_CODE_MONTHBILLDEAIL, showLoading = false){
            NoteModel.instance.monthBillDeail();
        }
        result?.let {
            withMain {
                monthBillDeail.value = it;
            }
        }
    }

    private suspend fun homeBillingDetail(){
        val result = request(requestCode = REQ_CODE_HOME_BILLING_DETAIL, showLoading = false){
            NoteModel.instance.homeBillingDetail();
        }
        result?.let{
            SLog.d(TAG,"homeBillingDetail=>${it.toJson()}");
            withMain {
                homeBillingDetail.value = it;
            }
        }
    }

    private suspend fun monthTypePercent(){
        val result = request(requestCode = REQ_CODE_PERCENT_DETAIL, showLoading = true){
            NoteModel.instance.percentDetail(null,null);
        }
        result?.let {
            withMain {
                incomePercentList.value = it.incomePercentList;
                expenditurePercentList.value = it.expenditurePercentList;
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_HomeViewModel==>";
        private const val REQ_CODE_HOME_REFRESH = 0x01;
        private const val REQ_CODE_NAVIGATION_LIST = 0x02;
        private const val REQ_CODE_MONTHBILLDEAIL =  0x03;
        private const val REQ_CODE_HOME_BILLING_DETAIL = 0x04;
        private const val REQ_CODE_PERCENT_DETAIL = 0x05;
        private const val REQ_CODE_HOME_CONFIG = 0x06;
    }
}
