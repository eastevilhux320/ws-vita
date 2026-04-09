package com.wangshu.note.app.model.main.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.star.light.common.entity.AppHomeConfig
import com.star.light.common.entity.NavigationEntity
import com.star.light.common.network.model.AppModel
import com.starlight.dot.framework.utils.SLog
import com.starlight.dot.framework.utils.mainThread
import com.starlight.dot.framework.utils.toJSON
import com.wangshu.note.app.common.NoteViewModel
import com.wangshu.note.app.entity.bill.BillTypePercentEntity
import com.wangshu.note.app.entity.main.MonthBillDetail
import com.wangshu.note.app.model.main.MainData
import com.wangshu.note.app.network.model.BillModel
import com.wangshu.note.app.network.reponse.YearlyDetailReponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : NoteViewModel<MainData>(application) {
    val homeConfig = MutableLiveData<AppHomeConfig>();
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

    override fun initData(): MainData {
        return MainData();
    }

    override fun initModel() {
        super.initModel()
        loadData();
    }

    override fun onResume() {
        super.onResume()
        refreshData();
    }

    fun refreshData(){
        SLog.d(TAG,"refreshData")
        GlobalScope.launch {
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
        GlobalScope.launch {
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
        val result = AppModel.instance.appHomeConfig();
        success(requestCode = MainData.RequestCode.CODE_HOME_REFRESH)
        if(result.isSuccess){
            mainThread {
                homeConfig.value = result.data;
            }
        }
    }

    private suspend fun navigationList(){
        val result = AppModel.instance.appNavigationList(1);
        if(result.isSuccess){
            mainThread {
                navigationList.value = result.data;
            }
        }
    }

    private fun homeBillDetail() = GlobalScope.launch{
        val result = BillModel.instance.monthBillDeail();
        if(result.isSuccess){
            mainThread {
                monthBillDeail.value = result.data;
            }
        }
    }

    private fun homeBillingDetail() = GlobalScope.launch{
        val result = BillModel.instance.homeBillingDetail();
        SLog.d(TAG,"homeBillingDetail=>${result.toJSON()}");
        if(result.isSuccess){
            mainThread {
                homeBillingDetail.value = result.data;
            }
        }
    }

    private suspend fun monthTypePercent(){
        val result = BillModel.instance.percentDetail(null,null);
        if(result.isSuccess){
            mainThread {
                incomePercentList.value = result.data?.incomePercentList;
                expenditurePercentList.value = result.data?.expenditurePercentList;
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_HomeViewModel==>";
    }
}
