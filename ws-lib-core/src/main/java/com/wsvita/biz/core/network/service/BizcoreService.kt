package com.wsvita.biz.core.network.service

import com.wsvita.biz.core.entity.AppUrlEntity
import com.wsvita.biz.core.entity.HomeBannerEntity
import com.wsvita.biz.core.entity.MainTabEntity
import com.wsvita.biz.core.entity.region.AddressEntity
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.entity.region.DistrictEntity
import com.wsvita.biz.core.entity.region.HotCityEntity
import com.wsvita.biz.core.entity.region.ProvinceEntity
import com.wsvita.biz.core.network.reponse.AppBeforehandResponse
import com.wsvita.biz.core.network.request.CityListRequest
import com.wsvita.biz.core.network.request.DistractListRequest
import com.wsvita.biz.core.network.request.EnrollmentAddressRequest
import com.wsvita.biz.core.network.request.LaunchConfigRequest
import com.wsvita.biz.core.network.request.MainTablistRequest
import com.wsvita.network.entity.BaseRequest
import com.wsvita.network.entity.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BizcoreService {

    @GET("api/app/launch/config/{appId}")
    fun launchConfig(@Path("appId") appId : Long) : Result<LaunchConfigRequest>;

    /**
     * 首页Banner列表
     */
    @GET("api/app/banner/homelist/{appId}")
    fun homeBannerList(@Path("appId") appId : Long) : Result<MutableList<HomeBannerEntity>>;

    /**
     * 热门城市列表查询
     */
    @POST("api/region/city/hot/list")
    fun hotCityList(@Body request: @JvmSuppressWildcards Map<String, Any?>): Result<MutableList<HotCityEntity>>

    /**
     * 省份列表
     * create by Eastevil at 2026/1/19 10:27
     * @author Eastevil
     * @param
     * @return
     */
    @GET("api/region/city/provinces/{appId}")
    fun provinceList(@Path("appId") appId :Long) : Result<MutableList<ProvinceEntity>>;

    /**
     * 根据省份code获取该省份下所有的城市列表
     * create by Administrator at 2026/1/21 0:02
     * @author Administrator
     * @param
     * @return
     */
    @POST("api/region/city/list")
    fun cityList(@Body request : CityListRequest) : Result<MutableList<CityEntity>>;

    /**
     * 根据城市查询城市区域列表
     * create by Eastevil at 2026/1/21 15:20
     * @author Eastevil
     * @param 
     * @return  
     */ 
    @POST("api/region/city/distract/list")
    fun distractList(@Body request : DistractListRequest) : Result<MutableList<DistrictEntity>>;

    /**
     * 用户添加地址
     * create by Administrator at 2026/1/25 2:29
     * @author Administrator
     * @param 
     * @return  
     */ 
    @POST("api/address/enrollment")
    fun enrollmentAddress(@Body request : EnrollmentAddressRequest) : Result<Any?>;

    /**
     * 用户地址列表
     * create by Administrator at 2026/1/25 16:20
     * @author Administrator
     * @param 
     * @return  
     */ 
    @GET("api/address/list")
    fun addressList() : Result<MutableList<AddressEntity>>;
}
