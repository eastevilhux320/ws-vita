package com.wangshu.textus.note.network

import com.wangshu.textus.note.common.AppConfigure
import com.wangshu.textus.note.entity.AppConfig
import com.wangshu.textus.note.local.NoteConstants
import com.wangshu.textus.note.network.request.BillTypePercentRequest
import com.wangshu.textus.note.network.service.NoteService
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.network.service.BizcoreService
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
}
