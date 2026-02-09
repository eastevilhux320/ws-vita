package com.wsvita.biz.core.network.service

import com.wsvita.biz.core.entity.AppHomeConfigEntity
import com.wsvita.biz.core.entity.MainTabEntity
import com.wsvita.biz.core.network.request.MainTablistRequest
import com.wsvita.network.entity.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 所有和APP配置相关的服务接口处理
 */
interface AppService {

    @POST("api/app/tablist/v2")
    fun mainTabList(@Body request : MainTablistRequest) : Result<MutableList<MainTabEntity>>;

    @GET("api/app/homeconfig/{appId}")
    fun appHomeConfig(@Path("appId") appId : Long) : Result<AppHomeConfigEntity>;
}
