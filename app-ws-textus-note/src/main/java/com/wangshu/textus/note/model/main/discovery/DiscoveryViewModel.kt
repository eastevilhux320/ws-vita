package com.wangshu.note.app.model.main.discovery

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.starlight.dot.framework.utils.SLog
import com.starlight.dot.framework.utils.mainThread
import com.starlight.dot.framework.widget.popup.ListItem
import com.wangshu.note.app.common.NoteViewModel
import com.wangshu.note.app.entity.ShujiStatisticsEntity
import com.wangshu.note.app.entity.health.DailyTimeEntity
import com.wangshu.note.app.entity.health.HealthScienceEntity
import com.wangshu.note.app.entity.health.ScheduleTypeEntity
import com.wangshu.note.app.entity.health.UserDailyEntity
import com.wangshu.note.app.model.main.MainData
import com.wangshu.note.app.network.model.NoteModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DiscoveryViewModel(application: Application) : NoteViewModel<MainData>(application) {
    private var scheduleTypeList : MutableList<ScheduleTypeEntity>? = null;

    val todayTime = MutableLiveData<Long>();

    /**
     * 用户日常健康时间数据
     */
    val userDaily = MutableLiveData<UserDailyEntity>();

    /**
     * 每日健康日程安排时间列表
     */
    val dailyTimeList = MutableLiveData<MutableList<DailyTimeEntity>>();


    /**
     * 用户舒记数据记录
     */
    val shujiStatistics = MutableLiveData<ShujiStatisticsEntity>();

    /**
     * 健康科普列表
     */
    val healthScienceList = MutableLiveData<MutableList<HealthScienceEntity>>();

    override fun initData(): MainData {
        return MainData();
    }

    override fun initModel() {
        super.initModel()
        todayTime.value = System.currentTimeMillis();
        loadData();
    }

    override fun onResume() {
        super.onResume()
        refreshData();
    }

    fun loadData(){
        GlobalScope.launch {
            launch {
                shujiStatistics();
            }
            launch {
                userDaily();
            }
            launch {
                scheduleTypeList();
            }
            launch {
                healthScienceList();
            }
        }
    }

    fun getScheduleTypeList(): MutableList<ScheduleTypeEntity>? {
        return scheduleTypeList;
    }

    fun bindDaily(typeId : Long) = GlobalScope.launch{
        showLoading();
        val result = NoteModel.instance.bindDaily(typeId);
        dismissLoading();
        if(result.isSuccess){
            //绑定日程数据成功
            userDaily();
        }else{
            showError(result.code,result.msg,MainData.RequestCode.CODE_BIND_HEALTH_DAILY);
        }
    }

    fun refreshData(){
        GlobalScope.launch {
            launch {
                shujiStatistics();
            }
            launch {
                userDaily();
            }
            launch {
                scheduleTypeList();
            }
        }
    }

    private suspend fun userDaily(){
        val result = NoteModel.instance.userDailySchedule();
        if(result.isSuccess){
            mainThread {
                userDaily.value = result.data;
                dailyTimeList.value = result.data?.dailyTimeList;
            }
        }
    }

    private suspend fun scheduleTypeList(){
        val result = NoteModel.instance.scheduleTypeList();
        if(result.isSuccess){
            result.data?.let {
                SLog.d(TAG,"scheduleTypeList success");
                scheduleTypeList = result.data;
            }
        }
    }

    private suspend fun shujiStatistics(){
        val result = NoteModel.instance.shujiStatistics();
        if(result.isSuccess){
            mainThread {
                shujiStatistics.value = result.data?.shujiData;
            }
        }
    }

    private suspend fun healthScienceList(){
        val result = NoteModel.instance.healthSciencePage();
        if(result.isSuccess){
            mainThread {
                healthScienceList.value = result.data?.list;
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_Main_DiscoveryViewModel==>";
    }
}
