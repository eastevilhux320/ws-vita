package com.wangshu.textus.note.network.service

import com.wangshu.textus.note.entity.main.MonthBillDetail
import com.wangshu.textus.note.network.reponse.BillTypePercentResponse
import com.wangshu.textus.note.network.reponse.YearlyDetailReponse
import com.wangshu.textus.note.network.request.BillTypePercentRequest
import com.wsvita.network.entity.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoteService {

    /**
     * 查询月份资产订单详情
     * create by Administrator at 2023/4/20 19:40
     * @author Administrator
     * @param month
     *      月份
     * @return
     *      资产详情
     */
    @GET("api/bill/billing/detail/{month}")
    fun monthBillDeail(@Path("month") month : Int) : Result<MonthBillDetail>;

    /**
     * 首页账单记录详情接口
     * create by Administrator at 2023/4/23 22:16
     * @author Administrator
     * @param
     * @return
     */
    @GET("api/bill/billing/detail/home")
    fun homeBillingDetail() : Result<YearlyDetailReponse>;


    /**
     * 账单类型比例
     * create by Administrator at 2025/9/12 23:09
     * @author Administrator
     * @param
     * @return
     */
    @POST("api/bill/type/percentdetail")
    fun percentDetail(@Body request : BillTypePercentRequest) : Result<BillTypePercentResponse>;
}
