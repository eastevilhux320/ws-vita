package com.wangshu.textus.note.model.main.note

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.starlight.dot.framework.utils.*
import com.wangshu.note.app.R
import com.wangshu.note.app.entity.NoteLocation
import com.wangshu.note.app.entity.note.MemoEntity
import com.wangshu.note.app.entity.note.NoteEntity
import com.wangshu.note.app.entity.note.plan.PlanEntity
import com.wangshu.note.app.entity.weather.WeatherNow
import com.wangshu.note.app.model.main.MainData
import com.wangshu.note.app.model.note.WSNoteData
import com.wangshu.note.app.network.model.BillModel
import com.wangshu.note.app.network.model.NoteModel
import com.wangshu.textus.note.network.reponse.YearlyDetailReponse
import com.wangshu.note.app.network.request.UserPlanListRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : com.wangshu.note.app.common.NoteViewModel<MainData>(application) {
    private var page : Long = 1;
    private lateinit var timeFormat : String;
    private lateinit var dateFormat : String;
    val noteList = MutableLiveData<MutableList<NoteEntity>>();
    val memoList = MutableLiveData<MutableList<MemoEntity>>();
    val planList = MutableLiveData<MutableList<PlanEntity>>();
    val billingDetail = MutableLiveData<YearlyDetailReponse>();

    val timeText = MutableLiveData<String>();
    val dateText = MutableLiveData<String>();

    val noteTimeText = MutableLiveData<String>();

    /**
     * 当前的天气
     */
    val weatherNow = MutableLiveData<WeatherNow>();

    override fun initData(): MainData {
        return MainData();
    }

    override fun initModel() {
        super.initModel()
        timeFormat = getString(R.string.sl_time_format_default);
        dateFormat = getString(R.string.main_note_date_format)
        noteTimeText.value = System.currentTimeMillis().format(dateFormat);
        startClock();
        GlobalScope.launch {
            launch {
                todayMemoList();
            }
            launch {
                noteList();
            }
            launch {
                planList();
            }
            launch {
                billingDetail();
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData();
    }

    fun refreshData(){
        GlobalScope.launch {
            launch {
                todayMemoList();
            }
            launch {
                noteList();
            }
            launch {
                planList();
            }
            launch {
                billingDetail();
            }
        }
    }

    /**
     * 收到主页的地址为变化数据
     * create by Administrator at 2025/10/18 3:17
     * @author Administrator
     * @param location
     *      地址位置
     * @return
     *      void
     */
    fun receiveMainLocation(location : NoteLocation){
        noteLocation.value = location;
        weatherNow(location.longitude,location.latitude);
    }

    override fun onNoteLocation(location: NoteLocation) {
        super.onNoteLocation(location)
        weatherNow(location.longitude,location.latitude);
    }

    private suspend fun noteList() = GlobalScope.launch{
        val sTime = startOfToday().time;
        val eTime = endOfToday().time;
        val result = NoteModel.instance.noteList(page,sTime,eTime);
        success(requestCode = MainData.RequestCode.CODE_REFRESH_WSNOTEList)
        if(result.isSuccess){
            mainThread {
                noteList.value = result.data?.list;
            }
        }
    }

    private suspend fun todayMemoList(){
        val result = NoteModel.instance.todayMemoList();
        if(result.isSuccess){
            mainThread {
                memoList.value = result.data;
            }
        }else{
            mainThread {
                memoList.value = null;
            }
        }
    }

    private suspend fun planList(){
        val request = UserPlanListRequest();
        val result = NoteModel.instance.userTodayPlanList(request);
        if(result.isSuccess){
            mainThread {
                planList.value = result.data?.list;
            }
        }else{
            mainThread {
                planList.value = null;
            }
        }
    }

    suspend fun billingDetail() = GlobalScope.launch{
        val result = BillModel.instance.homeBillingDetail();
        SLog.d(TAG,"homeBillingDetail=>${result.toJSON()}");
        if(result.isSuccess){
            mainThread {
                billingDetail.value = result.data;
            }
        }
    }

    private fun startClock() {
        dateText.value = System.currentTimeMillis().format(dateFormat);
        viewModelScope.launch {
            while (isActive) {
                timeText.postValue(System.currentTimeMillis().format(timeFormat));
                delay(1000L) // 每秒刷新一次
            }
        }
    }


    private fun weatherNow(longit : String?,latit : String?){
        SLog.d(TAG,"weatherNow invoke,longit:${longit},latit:${latit}");
        if(longit != null && latit != null){
            GlobalScope.launch {
                val result = NoteModel.instance.weatherByLngAndLat(longit,latit);
                if(result.isSuccess){
                    mainThread {
                        weatherNow.value = result.data?.now;
                    }
                }else{
                    showError(result.code,result.msg, MainData.RequestCode.CODE_WEATHER_NOW)
                }
            }
        }
    }

    fun addNoteText(noteText: String?){
        if(noteText == null || noteText.isEmpty()){
            showError(msg = getString(R.string.hint_note_text),requestCode = WSNoteData.RequestCode.CODE_INPUT_NOTETEXT);
            return;
        }
        addNoteText(noteText);
    }

    private fun addNoteText(noteText : String) = GlobalScope.launch{
        showLoading();
        val l = noteLocation.value;
        val result = NoteModel.instance.addNoteText(noteText,l?.longitude,l?.latitude,
            l?.address,l?.province,l?.city,l?.district);
        dismissLoading();
        if(result.isSuccess){
            noteList();
            success(requestCode = WSNoteData.RequestCode.CODE_ADD_NOTETEXT);
        }else{
            showError(result.code,result.msg, WSNoteData.RequestCode.CODE_ADD_NOTETEXT);
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
    }
}
