package com.wsvita.biz.core.model.region.citylist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.entity.region.DistrictEntity
import com.wsvita.biz.core.entity.region.HotCityEntity
import com.wsvita.biz.core.entity.region.ProvinceEntity
import com.wsvita.biz.core.entity.region.RegionResult
import com.wsvita.biz.core.local.manager.RegionManager
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.biz.core.network.request.HotCityListRequest
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.http.POST

class CityListViewModel(application: Application) : BizcoreViewModel(application) {
    private var isFinish : Boolean = true;

    /**
     * 是否展示区域
     */
    private var showDistrict : Boolean = true;

    val showHot = MutableLiveData<Boolean>();


    /**
     * 当前的主布局展示的列表类型，
     * 1-市级，2-区级
     */
    val showType = MutableLiveData<Int>();
    //热门成功
    private val _hotCityList = MutableLiveData<MutableList<HotCityEntity>>();
    val hotCityList : LiveData<MutableList<HotCityEntity>>
        get() = _hotCityList;

    //省份
    private val _provinceList = MutableLiveData<MutableList<ProvinceEntity>>();
    val provinceList : LiveData<MutableList<ProvinceEntity>>
        get() = _provinceList;

    //城市
    private val _cityList = MutableLiveData<MutableList<CityEntity>>();
    val cityList : LiveData<MutableList<CityEntity>>
        get() = _cityList;

    private val _distraceList = MutableLiveData<MutableList<DistrictEntity>>();
    val distraceList : LiveData<MutableList<DistrictEntity>>
        get() = _distraceList;

    /**
     * 选择的省份
     */
    private val _province = MutableLiveData<ProvinceEntity>();
    val province : LiveData<ProvinceEntity>
        get() = _province;

    /**
     * 选择的城市
     */
    private val _city = MutableLiveData<CityEntity?>();
    val city : LiveData<CityEntity?>
        get() = _city;

    /**
     * 选择的区域
     */
    private val _district = MutableLiveData<DistrictEntity?>();
    val district : LiveData<DistrictEntity?>
        get() = _district;

    val cityResult = MutableLiveData<RegionResult>();

    override fun initModel() {
        super.initModel()
        val provinceList = RegionManager.getInstance().getProvinceList();
        if(provinceList.isNullOrEmpty()){
            viewModelScope.launch {
                provinceList();
            }
        }else{
            _provinceList.value = provinceList!!;
        }
    }

    override fun receiveBoolean(key: String, value: Boolean) {
        super.receiveBoolean(key, value)
        when(key){
            BizConstants.IntentKey.REGION_CITY_HOT_FLAG->{
                showHot.value = value;
                if(value){
                    SLog.d(TAG,"hotCityList");
                    viewModelScope.launch {
                        hotCityList();
                    }
                }
            }
            BizConstants.IntentKey.REGION_SELECTED_FINISH->{
                isFinish = value;
            }
            BizConstants.IntentKey.REGION_CITY_SHOW_DISTRICT->{
                showDistrict = value;
            }
        }
    }

    /**
     * 选择省份
     */
    fun selectProvince(position : Int){
        val province = provinceList.value?.get(position);
        showType.value = 1;
        //清除选择的城市和区域
        _city.value = null;
        _district.value = null;
        _cityList.value?.clear();
        _distraceList.value?.clear();
        province?.let {
            _province.value = it;
            val cityList = it.code?.let { it1 -> RegionManager.getInstance().getCityList(it1) };
            if(cityList.isNullOrEmpty()){
                it.code?.let {code->
                    viewModelScope.launch {
                        cityList(code);
                    }
                }?:let {
                    //理论上这里是有问题的,这里暂时不处理了
                    SLog.e(TAG,"province code is null");
                }
            }else{
                _cityList.value = cityList!!;
            }
        }
    }

    /**
     * 选择城市
     * create by Eastevil at 2026/1/21 15:25
     * @author Eastevil
     * @param
     * @return
     */
    fun selectCity(city : CityEntity){
        showType.value = 2;
        _city.value = city;
        city.code?.let {code->
            if(showDistrict){
                val districtList = RegionManager.getInstance().getDistrictList(code) ;
                if(districtList.isNullOrEmpty()){
                    viewModelScope.launch {
                        districtList(code);
                    }
                }else{
                    //直接刷新列表
                    _distraceList.value = districtList!!;
                }
            }else{
                //不展示城下的区域，直接返回到上一层
                val result = RegionResult();
                result.isFinish = isFinish;
                result.province = province.value;
                result.city = city;
                cityResult.value = result;
            }
        }
    }

    /**
     * 选择区域
     * create by Eastevil at 2026/1/21 16:39
     * @author Eastevil
     * @param
     * @return
     */
    fun selectDistrict(district:DistrictEntity){
        _district.value = district;
        val result = RegionResult();
        result.isFinish = isFinish;
        result.province = province.value;
        result.city = city.value;
        result.district = district;
        cityResult.value = result;
    }

    fun changeShowType(type : Int){
        if(1 == type){
            //选择展示城市，清除已经选择的区域
            _district.value = null;
        }
        showType.value = type;
    }

    /**
     * 是否展示城市下的区域
     * create by Administrator at 2026/1/25 23:06
     * @author Administrator
     * @return
     *      是否展示城市下的区域
     */
    fun showDistrict(): Boolean {
        return showDistrict;
    }

    private suspend fun provinceList(){
        val list = request(requestCode = PROVINCE_LIST, showLoading = true) {
            BizcoreModel.instance.provinceList();
        }
        list?.let {
            withMain {
                _provinceList.value = it;
            }
        }
    }

    private suspend fun cityList(provinceCode : Int){
        val cList = request(showLoading = true, requestCode = CODE_CITY_LIST){
            BizcoreModel.instance.cityList(provinceCode);
        }
        if(!cList.isNullOrEmpty()){
            RegionManager.getInstance().putCityList(provinceCode,cList);
            withMain {
                _cityList.value = cList!!;
            }
        }
    }

    private suspend fun districtList(cityCode : Int){
        val dList = request(showLoading = true, requestCode = CODE_DISTRICT_LIST) {
            BizcoreModel.instance.distractList(cityCode);
        }
        if(!dList.isNullOrEmpty()){
            RegionManager.getInstance().putDistrictList(cityCode,dList);
            withMain {
                _distraceList.value = dList!!;
            }
        }
    }

    private suspend fun hotCityList(){
        val list = request(requestCode = CITY_HOT_LIST, showLoading = false) {
            BizcoreModel.instance.hotCityList(null)
        }
        list?.let {
            withMain {
                _hotCityList.value = it;
            }
        }
    }

    companion object{
        private const val TAG = "WSV_Core_Region_CityListViewModel=>"
        private const val PROVINCE_LIST = 0x01;
        private const val CITY_HOT_LIST = 0x02;
        private const val CODE_CITY_LIST = 0x03;
        private const val CODE_DISTRICT_LIST = 0x04;
    }
}
