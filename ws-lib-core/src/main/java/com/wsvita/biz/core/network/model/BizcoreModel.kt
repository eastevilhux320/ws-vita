package com.wsvita.biz.core.network.model

import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.entity.region.DistrictEntity
import com.wsvita.biz.core.entity.region.ProvinceEntity
import com.wsvita.biz.core.network.BizcoreRequestBuilder
import com.wsvita.biz.core.network.request.CityListRequest
import com.wsvita.biz.core.network.request.DistractListRequest
import com.wsvita.biz.core.network.request.EnrollmentAddressRequest
import com.wsvita.biz.core.network.request.MainTablistRequest
import com.wsvita.biz.core.network.service.BizcoreService
import com.wsvita.framework.utils.SLog
import com.wsvita.network.configure.NetworkConfigure
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BizcoreModel private constructor(){

    companion object {
        private const val TAG = "WSVita_Bizcore_Network_BizcoreModel=>"

        val instance : BizcoreModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            BizcoreModel();
        }

        /**
         * 基础服务实例依然通过单例获取，保证连接池和协议复用
         */
        private val service: BizcoreService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(BizcoreService::class.java, baseUrl)
        }

        val appId : Long = BizcoreConfigure.instance.appId();
    }

    private fun config(): BizcoreConfig? {
        return BizcoreConfigure.instance.getConfig();
    }

    suspend fun launchConfig() = withContext(Dispatchers.IO){
        return@withContext service.launchConfig(appId);
    }

    suspend fun homeBannerList() = withContext(Dispatchers.IO){
        return@withContext service.homeBannerList(appId)
    }

    suspend fun hotCityList(size : Int? = null) = withContext(Dispatchers.IO){
        // 直接通过 Builder 构建，无需创建 MainTablistRequest 类
        val requestMap = BizcoreRequestBuilder()
            .build()
        return@withContext service.hotCityList(requestMap);
    }

    suspend fun provinceList() = withContext(Dispatchers.IO){
        return@withContext service.provinceList(appId);
    }

    /**
     * 根据省份code查询该省份下所有的城市列表
     * create by Administrator at 2026/1/21 0:03
     * @author Administrator
     * @param provinceCode
     *      省份code
     * @return
     *      省份下所有的城市列表
     */
    suspend fun cityList(provinceCode : Int) = withContext(Dispatchers.IO){
        val request = CityListRequest();
        request.provinceCode = provinceCode;
        request.channel = BizcoreConfigure.instance.getConfig()?.channelCode;
        request.appId = BizcoreModel.appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.d(TAG,"cityList_params:${request.toJson()}");
        return@withContext service.cityList(request);
    }

    /**
     * 根据城市查询城市区域列表
     * create by Eastevil at 2026/1/21 15:21
     * @author Eastevil
     * @param cityCode
     *      城市code
     * @return
     */
    suspend fun distractList(cityCode : Int) = withContext(Dispatchers.IO){
        val request = DistractListRequest();
        request.cityCode = cityCode;
        request.channel = BizcoreConfigure.instance.getConfig()?.channelCode;
        request.appId = BizcoreModel.appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;
        SLog.d(TAG,"distractList_params:${request.toJson()}");
        return@withContext service.distractList(request);
    }

    suspend fun enrollmentAddress(province : ProvinceEntity,city : CityEntity,district : DistrictEntity,address : String) = withContext(Dispatchers.IO){
        val request = EnrollmentAddressRequest();
        request.provinceId = province.id;
        request.cityId = city.id;
        request.districtId = district.id;
        request.provinceName = province.name;
        request.cityName = city.name;
        request.districtName = district.name;
        request.address = address;
        request.channel = BizcoreConfigure.instance.getConfig()?.channelCode;
        request.appId = BizcoreModel.appId;
        request.version = config()?.version;
        request.versionName = config()?.versionName;

        return@withContext service.enrollmentAddress(request);
    }

    /**
     * 用户地址列表
     * create by Administrator at 2026/1/25 16:20
     * @author Administrator
     * @param 
     * @return  
     */ 
    suspend fun addressList() = withContext(Dispatchers.IO){
        return@withContext service.addressList();
    }
}
