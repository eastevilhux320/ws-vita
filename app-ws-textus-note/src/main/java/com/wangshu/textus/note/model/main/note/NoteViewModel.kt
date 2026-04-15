package com.wangshu.textus.note.model.main.note

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.textus.note.common.NoteViewModel
import com.wangshu.textus.note.entity.INoteTime
import com.wangshu.textus.note.entity.NoteTimeEntity
import com.wangshu.textus.note.entity.weather.WeatherNow
import com.wangshu.textus.note.entity.weather.YiyuanWeather
import com.wangshu.textus.note.model.main.NoteMainViewModel
import com.wangshu.textus.note.network.NoteModel
import com.wangshu.textus.note.network.reponse.YearlyDetailReponse
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.core.entity.domain.DateTimeEntity
import com.wsvita.core.entity.time.IDateTime
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : NoteMainViewModel(application) {

    private val _time = MutableLiveData<INoteTime>();
    val time : LiveData<INoteTime>
        get() = _time;

    private val _weather = MutableLiveData<WeatherNow>();
    val weather : LiveData<WeatherNow>
        get() = _weather;

    val billingDetail = MutableLiveData<YearlyDetailReponse>();

    override fun initModel() {
        super.initModel()
        startTimeTick();
        loadData();
    }


    override fun receiveLocation(location: BizLocation) {
        super.receiveLocation(location)
        SLog.d(TAG,"receiveLocation:${location.toJson()}");
        val lng = location.longitude;
        val lat = location.latitude;
        if(lng != null && lat != null){
            queryWeather(lng,lat);
        }
    }

    private fun loadData(){
        viewModelScope.launch {
            launch {
                billingDetail();
            }
        }
    }

    private fun startTimeTick() {
        // 使用 viewModelScope 确保在 ViewModel 销毁时自动取消协程
        viewModelScope.launch {
            while (isActive) {
                var t = _time.value;
                if(t == null){
                    t = NoteTimeEntity();
                }
                t.setTime(systemTime());
                _time.value = t!!;
                kotlinx.coroutines.delay(1000L) // 挂起 1 秒
            }
        }
    }

    fun queryWeather(langit : String,latit : String) = viewModelScope.launch{
        val result = request(showLoading = false, requestCode = REQ_CODE_WEATHER){
            NoteModel.instance.weatherByLngAndLat(langit,latit)
        }
        result?.let {
            withMain {
                _weather.value = it.now;
            }
        }
    }

    private suspend fun billingDetail(){
        val result = request(showLoading = false, requestCode = REQ_CODE_BILLING_DETAIL){
            NoteModel.instance.homeBillingDetail();
        }
        result?.let {
            withMain {
                billingDetail.value = it;
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
        private const val REQ_CODE_WEATHER = 0x01;
        private const val REQ_CODE_BILLING_DETAIL = 0x02;
    }
}
