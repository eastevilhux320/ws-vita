package com.wsvita.network.service

import com.wsvita.network.entity.Result
import com.wsvita.network.request.SendOPTRequest
import com.wsvita.network.response.AppBeforehandReponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkService {

    @GET("api/app/beforehand")
    fun appBeforehand() : Result<AppBeforehandReponse>;

    /**
     * 发送验证码
     * create by Administrator at 2026/1/11 17:48
     * @author Administrator
     * @param request
     *      [SendOPTRequest]
     * @return  
     */ 
    @POST("api/app/sendmsg")
    fun sendOPT(@Body request : SendOPTRequest) : Result<Any?>;
}
