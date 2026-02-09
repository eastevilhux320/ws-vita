package com.wsvita.core.network.service

import com.wsvita.core.entity.domain.PlateLicenseEntity
import com.wsvita.core.network.request.PlateAlphabeticsRequest
import com.wsvita.network.entity.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SDKService {

    /**
     * 获取所有的车牌城市
     * create by Eastevil at 2026/1/13 15:49
     * @author Eastevil
     * @param 
     * @return  
     */ 
    @GET("api/getway/plates/all/{appId}")
    fun allPlateList(@Path("appId") appId : Long) : Result<MutableList<PlateLicenseEntity>>;

    /**
     * 获取车牌城市字母列表
     * create by Eastevil at 2026/1/13 15:51
     * @author Eastevil
     * @param 
     * @return  
     */ 
    @POST("api/getway/plates/all/{appId}")
    fun plateAlphabeticList(@Body request : PlateAlphabeticsRequest) : Result<MutableList<String>>;
}
