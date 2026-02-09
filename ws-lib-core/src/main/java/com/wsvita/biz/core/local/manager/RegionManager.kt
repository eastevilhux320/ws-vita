package com.wsvita.biz.core.local.manager

import com.wsvita.biz.core.entity.region.*
import com.wsvita.framework.local.BaseManager
import java.util.concurrent.ConcurrentHashMap

/**
 * 区域数据管理类 - 负责省市区数据的临时内存缓存
 */
class RegionManager : BaseManager {

    companion object {
        private const val TAG = "WSV_Core_Manager_RegionManager=>"

        @Volatile
        private var instance: RegionManager? = null

        fun getInstance(): RegionManager {
            return instance ?: synchronized(this) {
                instance ?: RegionManager().also { instance = it }
            }
        }
    }

    private lateinit var mRegionCacheCity : ConcurrentHashMap<Int, MutableList<CityEntity>>
    private lateinit var mRegionCacheDistrict : ConcurrentHashMap<Int, MutableList<DistrictEntity>>;

    private lateinit var mRegionHotList : MutableList<HotCityEntity>;
    private lateinit var mProvinceList : MutableList<ProvinceEntity>;

    constructor() : super() {

    }

    override fun onInit() {
        // 初始化逻辑
        mRegionHotList = mutableListOf();
        mProvinceList = mutableListOf();
        mRegionCacheCity = ConcurrentHashMap()
        mRegionCacheDistrict = ConcurrentHashMap()
    }

    fun saveHotList(list: MutableList<HotCityEntity>) {
        mRegionHotList.clear()
        mRegionHotList.addAll(list)
    }

    fun getHotList(): MutableList<HotCityEntity>? {
        if(mRegionHotList.isEmpty()){
            return null;
        }
        return mRegionHotList;
    }

    /**
     * 设置省份列表缓存
     * 适用于模块初始化时从服务端获取数据并缓存 [cite: 2026-01-16]
     */
    fun saveProvinceList(list: MutableList<ProvinceEntity>) {
        mProvinceList.clear()
        mProvinceList.addAll(list)
    }

    /**
     * 获取缓存的省份列表
     */
    fun getProvinceList(): MutableList<ProvinceEntity>? {
        if (mProvinceList.isEmpty()) return null
        return mProvinceList
    }

    /**
     * 检查是否有省份缓存
     */
    fun hasProvince(): Boolean = mProvinceList.isNotEmpty()

    fun hasHotCity(): Boolean {
        return mRegionHotList.isNotEmpty();
    }

    fun putCityList(provinceCode : Int,cityList : MutableList<CityEntity>){
        mRegionCacheCity.put(provinceCode,cityList);
    }

    fun putDistrictList(cityCode : Int,districtList : MutableList<DistrictEntity>){
        mRegionCacheDistrict.put(cityCode,districtList);
    }

    fun getCityList(provinceCode : Int): MutableList<CityEntity>? {
        return mRegionCacheCity.get(provinceCode);
    }

    fun getDistrictList(cityCode : Int): MutableList<DistrictEntity>? {
        return mRegionCacheDistrict.get(cityCode);
    }

    fun clearHot() {
        mRegionHotList.clear()
    }

    fun clearProvince(){
        mProvinceList.clear();
    }

    fun clearAll(){
        mRegionHotList.clear()
        mProvinceList.clear();
        mRegionCacheCity.clear()
        mRegionCacheDistrict.clear();
    }
}
