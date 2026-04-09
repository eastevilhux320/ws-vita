package com.wangshu.mira.network.service

import com.wangshu.mira.entity.ChronoMenuEntity
import com.wangshu.mira.network.MiraResult
import com.wangshu.mira.network.request.ChronoMenuListReqeust
import com.wangshu.mira.network.request.MiraConnectionRequest
import com.wangshu.mira.network.request.MiraInitRequest
import com.wangshu.mira.network.request.SubmitRecordReqeust
import com.wangshu.mira.network.request.TaskApplyReqeust
import com.wangshu.mira.network.request.TaskCancelReqeust
import com.wangshu.mira.network.request.TaskDetailRequest
import com.wangshu.mira.network.request.TaskPageRequest
import com.wangshu.mira.network.request.TaskStartReqeust
import com.wangshu.mira.network.request.TaskSubmitReqeust
import com.wangshu.mira.network.response.MiraConnectionResponse
import com.wangshu.mira.network.response.MiraInitResponse
import com.wangshu.mira.network.response.SubmitRecordResponse
import com.wangshu.mira.network.response.TaskApplyResponse
import com.wangshu.mira.network.response.TaskCancelResponse
import com.wangshu.mira.network.response.TaskDetailReponse
import com.wangshu.mira.network.response.TaskPageResponse
import com.wangshu.mira.network.response.TaskStartResponse
import com.wangshu.mira.network.response.TaskSubmitResponse
import com.wsvita.network.entity.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MiraService {

    @POST("mira/init")
    fun miraInit(@Body request : MiraInitRequest) : Result<MiraInitResponse>;

    @POST("mira/connection")
    fun connection(@Body request : MiraConnectionRequest) : Result<MiraConnectionResponse>;

    /**
     * 玄映任务分页
     * create by Administrator at 2026/3/3 23:46
     * @author Administrator
     * @param 
     * @return  
     */ 
    @POST("mira/task/page")
    fun taskPage(@Body request : TaskPageRequest) : Result<TaskPageResponse>;

    @POST("mira/task/detail")
    fun taskDetail(@Body request : TaskDetailRequest) : Result<TaskDetailReponse>;

    /**
     * 申请任务
     * create by Eastevil at 2026/3/17 16:12
     * @author Eastevil
     * @param
     * @return
     */
    @POST("mira/task/apply")
    fun applyTask(@Body reqeust : TaskApplyReqeust) : Result<TaskApplyResponse>;

    /**
     * 开始任务
     */
    @POST("mira/task/start")
    fun startTask(@Body request : TaskStartReqeust) : Result<TaskStartResponse>;

    /**
     * 取消任务
     */
    @POST("mira/task/cancel")
    fun cancelTask(@Body requst : TaskCancelReqeust) : Result<TaskCancelResponse>;

    /**
     * 提交任务（含文件附件）
     * @param params 加密后的 MriaParamsDTO 对应的 RequestBody
     * @param files 文件列表 Part
     */
    @Multipart
    @POST("mira/task/submit")
    fun submitTask(
        // 必须供应一个 name，这里用 "params" 对应后端 @RequestPart MriaParamsDTO params
        @Part("params") request: TaskSubmitReqeust,
        @Part files: List<MultipartBody.Part>
    ): Result<TaskSubmitResponse>

    /**
     * 获取用户历史提交页面菜单列表
     * create by Eastevil at 2026/3/26 13:35
     * @author Eastevil
     * @param
     * @return
     */
    @POST("mira/chrono/menu/list")
    fun chronoMenuList(@Body request : ChronoMenuListReqeust) : Result<MutableList<ChronoMenuEntity>>;

    /**
     * 用户历史提交数据页面菜单列表
     * create by Eastevil at 2026/3/26 16:43
     * @author Eastevil
     * @param
     * @return
     */
    @POST("mira/chrono/record/page")
    fun submitRecords(@Body request : SubmitRecordReqeust) : Result<SubmitRecordResponse>;
}
