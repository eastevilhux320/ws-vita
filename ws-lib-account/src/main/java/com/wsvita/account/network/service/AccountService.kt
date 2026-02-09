package com.wsvita.account.network.service

import com.wsvita.account.network.request.PhoneLoginRequest
import com.wsvita.account.network.request.UsernameLoginRequest
import com.wsvita.account.network.response.AccountCenterReponse
import com.wsvita.account.network.response.LoginResultResponse
import com.wsvita.account.network.response.MemberInfoResponse
import com.wsvita.account.network.response.UsernameLoginResponse
import com.wsvita.network.entity.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountService {

    @GET("api/account/accountinfo/{appId}")
    fun accountCenter(@Path("appId") appId : Long) : Result<AccountCenterReponse>;

    /**
     * 验证码登录
     * create by Administrator at 2026/1/11 22:33
     * @author Administrator
     * @param 
     * @return  
     */ 
    @POST("api/account/phonelogin")
    fun phoneLogin(@Body request : PhoneLoginRequest) : Result<LoginResultResponse>;

    /**
     * 账号密码登录
     * @author Eastevil
     * @param
     * @since
     * @see
     * @return
     */
    @POST("api/account/login")
    fun usernameLogin(@Body request : UsernameLoginRequest) : Result<UsernameLoginResponse>;

    /**
     * 用户信息查询
     * create by Eastevil at 2026/1/12 11:11
     * @author Eastevil
     * @param 
     * @return  
     */ 
    @GET("api/account/member/detail/{appId}")
    fun memberInfo(@Path("appId") appId: Long) : Result<MemberInfoResponse>;
}
