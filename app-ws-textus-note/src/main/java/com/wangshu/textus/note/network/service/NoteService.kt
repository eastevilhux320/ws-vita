package com.wangshu.textus.note.network.service

import com.wangshu.textus.note.entity.main.MonthBillDetail
import com.wangshu.textus.note.entity.note.MemoEntity
import com.wangshu.textus.note.entity.weather.YiyuanWeather
import com.wangshu.textus.note.network.reponse.BillTypePercentResponse
import com.wangshu.textus.note.network.reponse.BudgetDetailResponse
import com.wangshu.textus.note.network.reponse.NoteListResponse
import com.wangshu.textus.note.network.reponse.PlanListReponse
import com.wangshu.textus.note.network.reponse.YearlyDetailReponse
import com.wangshu.textus.note.network.request.BillTypePercentRequest
import com.wangshu.textus.note.network.request.BudgetDetailRequest
import com.wangshu.textus.note.network.request.NoteListRequest
import com.wangshu.textus.note.network.request.UserPlanListRequest
import com.wangshu.textus.note.network.request.WeatherLngWlatRequest
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

    @POST("api/assets/budget/mine")
    fun budgetDetail(@Body request : BudgetDetailRequest) : Result<BudgetDetailResponse>;

    /**
     * 根据经纬度查询天气
     * @author Eastevil
     * @createTime 2025/9/2 16:29
     * @param
     * @since
     * @see
     * @return
     */
    @POST("api/weather/lngwlat")
    fun weatherByLngAndLat(@Body request : WeatherLngWlatRequest) : Result<YiyuanWeather>;

    /**
     * 今日备忘录列表接口
     */
    @GET("api/memo/today")
    fun todayMemoList() : Result<MutableList<MemoEntity>>;

    /**
     * 用户计划列表
     * create by Administrator at 2024/7/28 21:41
     * @author Administrator
     * @param request
     *      [UserPlanListRequest]
     * @return
     */
    @POST("api/plan/page")
    fun userPlanList(@Body request : UserPlanListRequest) : Result<PlanListReponse>;

    /**
     * 查询日记列表接口
     * create by Administrator at 2023/4/24 23:32
     * @author Administrator
     * @param
     * @return
     */
    @POST("api/note/page/list")
    fun noteList(@Body request : NoteListRequest) : Result<NoteListResponse>;

}
