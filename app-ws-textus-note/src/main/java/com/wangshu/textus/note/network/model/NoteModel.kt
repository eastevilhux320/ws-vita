package com.wangshu.textus.note.network.model

import com.wangshu.textus.note.BuildConfig
import com.wangshu.textus.note.common.AppConfigure
import com.wangshu.textus.note.local.ext.AppExt.appVersionCode
import com.wangshu.textus.note.local.ext.AppExt.appVersionName
import com.wangshu.textus.note.local.manager.ChannelManager
import com.wangshu.textus.note.network.request.BillTypePercentRequest
import com.wangshu.textus.note.network.request.BudgetDetailRequest
import com.wangshu.textus.note.network.request.NoteListRequest
import com.wangshu.textus.note.network.request.UserPlanListRequest
import com.wangshu.textus.note.network.request.WeatherLngWlatRequest
import com.wangshu.textus.note.network.service.NoteService
import com.wsvita.network.configure.NetworkConfigure
import ext.TimeExt.currentMonthEnd
import ext.TimeExt.currentMonthStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class NoteModel private constructor(){

    companion object {
        private const val TAG = "WS_Note_Network_NoteModel==>";

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NoteModel() }

        /**
         * 基础服务实例依然通过单例获取，保证连接池和协议复用
         */
        private val service: NoteService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkConfigure.instance.createService(NoteService::class.java, baseUrl)
        }

        private val appId : Long = AppConfigure.appConfig.appId;
    }

    /**
     * 获取月份资产详情
     * create by Administrator at 2023/4/20 19:42
     * @author Administrator
     * @param
     * @return
     */
    suspend fun monthBillDeail() = withContext(Dispatchers.IO){
        val month = Calendar.getInstance().get(Calendar.MONTH);
        return@withContext service.monthBillDeail(month);
    }

    /**
     *
     * create by Administrator at 2023/4/23 22:16
     * @author Administrator
     * @param
     * @return
     */
    suspend fun homeBillingDetail() = withContext(Dispatchers.IO){
        return@withContext service.homeBillingDetail();
    }

    /**
     * 账单类型比例
     * create by Administrator at 2025/9/12 23:34
     * @author Administrator
     * @param
     * @return
     */
    suspend fun percentDetail(startTime: Long?,endTime: Long?) = withContext(Dispatchers.IO){
        val sTime  = startTime?: currentMonthStart().time;
        val eTime = endTime?: currentMonthEnd().time;
        val request = BillTypePercentRequest();
        request.appId = appId;
        request.startTime = sTime;
        request.endTime = eTime;
        return@withContext service.percentDetail(request);
    }

    /**
     * Description
     * create by Eastevil at 2026/4/14 14:42
     * @author Eastevil
     * @param budgetType
     *       预算类型，1-个人预算，2-家庭预算，3-团队预算
     * @param timeType
     *       预算时间类型，1-每天预算，2-每周预算，3-每月预算，4-每季度预算，5-每年预算
     * @return
     */
    suspend fun budgetDetail(budgetType : Int?,timeType  : Int?) = withContext(Dispatchers.IO){
        val request = BudgetDetailRequest();
        request.appId = appId;
        request.budgetType = budgetType;
        request.timeType = timeType;
        return@withContext service.budgetDetail(request);
    }

    suspend fun weatherByLngAndLat(lng : String,lat : String) = withContext(Dispatchers.IO){
        val request = WeatherLngWlatRequest();
        request.type = 3;
        request.lng = lng;
        request.lat = lat;
        request.appId = appId;
        request.channel = ChannelManager.instance.getChannel();
        request.version = BuildConfig.VERSION_CODE
        request.versionName = BuildConfig.VERSION_NAME
        return@withContext service.weatherByLngAndLat(request);
    }

    suspend fun todayMemoList() = withContext(Dispatchers.IO){
        return@withContext service.todayMemoList();
    }

    suspend fun userTodayPlanList(request : UserPlanListRequest) = withContext(Dispatchers.IO){
        request.queryTime = System.currentTimeMillis();
        return@withContext service.userPlanList(request);
    }

    /**
     * 查询日记列表
     * create by Administrator at 2023/4/24 23:33
     * @author Administrator
     * @param startTime
     *      开始时间
     * @param endTime
     *      结束时间
     * @return
     *      日记列表
     */
    suspend fun noteList(page : Long,startTime : Long?, endTime :Long?,
                         orderFiled : String="create_date",order : String="desc") = withContext(Dispatchers.IO){
        val request = NoteListRequest();
        request.startTime = startTime;
        request.endTime = endTime;
        request.page = page;
        request.orderField = orderFiled;
        request.order = order
        request.appId = appId;
        request.channel = ChannelManager.instance.getChannel();
        request.version = appVersionCode();
        request.versionName = appVersionName();
        return@withContext service.noteList(request);
    }
}
