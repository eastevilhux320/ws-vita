package com.wangshu.mira.network.model

import android.content.Context
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.entity.DeviceInfoEntity
import com.wangshu.mira.entity.InputContentEntity
import com.wangshu.mira.entity.TaskStepParamEntity
import com.wangshu.mira.local.manager.DeviceManager
import com.wangshu.mira.local.manager.MiraManager
import com.wangshu.mira.network.convert.MiraNetworkAdapter
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
import com.wangshu.mira.network.service.MiraService
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.network.model.SDKModel
import com.wsvita.core.network.service.SDKService
import com.wsvita.framework.local.manager.device.OAIDManager
import com.wsvita.framework.utils.SLog
import com.wsvita.network.NetworkClient
import com.wsvita.network.configure.NetworkConfigure
import ext.JsonExt.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MiraModel private constructor(){

    companion object {
        private const val TAG = "Mira_MiraModel=>"

        val instance : MiraModel by lazy(mode=  LazyThreadSafetyMode.SYNCHRONIZED){
            MiraModel();
        }

        /**
         * 基础服务实例依然通过单例获取，保证连接池和协议复用
         */
        private val miraService: MiraService by lazy {
            val baseUrl = NetworkConfigure.instance.baseUrl() ?: ""
            NetworkClient.instance.createService(MiraService::class.java,baseUrl,MiraNetworkAdapter());
        }

        val appId : Long = CoreConfigure.instance.appId();
    }

    suspend fun miraInit() = withContext(Dispatchers.IO){
        val request = MiraInitRequest();
        request.miraUserId = MiraManager.instance.getMiraUserId();
        request.appId = appId;
        return@withContext miraService.miraInit(request);
    }

    suspend fun connection(context: Context) = withContext(Dispatchers.IO){
        val request = MiraConnectionRequest();
        val device = DeviceManager.instance.device(context);
        device.oaid = OAIDManager.instance.oaid();
        request.device = device;
        request.userId = MiraManager.instance.getUserId();
        SLog.i(TAG,"connection params:${request.toJson()}");
        return@withContext miraService.connection(request);
    }

    suspend fun taskPage(context: Context,taskType : Int?,keyword : String?) = withContext(Dispatchers.IO){
        val request = TaskPageRequest();
        request.userId = MiraManager.instance.getUserId();
        request.keyword = keyword;
        request.taskType = taskType;
        request.device = DeviceInfoEntity.build(context);
        return@withContext miraService.taskPage(request);
    }

    suspend fun taskDetail(taskId : Long,context: Context) = withContext(Dispatchers.IO){
        val request = TaskDetailRequest();
        request.taskId = taskId;
        request.userId = MiraManager.instance.getUserId();
        request.userDeviceId = DeviceManager.instance.getUserDeviceId();
        request.device = DeviceInfoEntity.build(context);

        return@withContext miraService.taskDetail(request);

    }

    /**
     * 申请任务
     * create by Eastevil at 2026/3/17 16:13
     * @author Eastevil
     * @param
     * @return
     */
    suspend fun applyTask(taskId : Long,context: Context) = withContext(Dispatchers.IO){
        val request = TaskApplyReqeust();
        request.taskId = taskId;
        request.userId = MiraManager.instance.getUserId();
        request.userDeviceId = DeviceManager.instance.getUserDeviceId();
        request.device = DeviceInfoEntity.build(context);
        return@withContext miraService.applyTask(request);
    }

    /**
     * 开始任务
     * create by Eastevil at 2026/3/17 16:18
     * @author Eastevil
     * @param
     * @return
     */
    suspend fun startTask(taskId : Long,context: Context) = withContext(Dispatchers.IO){
        val request = TaskStartReqeust();
        request.taskId = taskId;
        request.userId = MiraManager.instance.getUserId();
        request.userDeviceId = DeviceManager.instance.getUserDeviceId();
        request.device = DeviceInfoEntity.build(context);
        return@withContext miraService.startTask(request)
    }

    suspend fun cancelTask(taskId : Long,context: Context) = withContext(Dispatchers.IO){
        val request = TaskCancelReqeust();
        request.taskId = taskId;
        request.userId = MiraManager.instance.getUserId();
        request.userDeviceId = DeviceManager.instance.getUserDeviceId();
        request.device = DeviceInfoEntity.build(context);
        return@withContext miraService.cancelTask(request)
    }

    suspend fun submitTask(taskId: Long, context: Context, stepList: MutableList<TaskStepParamEntity>,inputList : MutableList<InputContentEntity>?) = withContext(Dispatchers.IO) {
        val request = TaskSubmitReqeust()
        // ... 基础参数填充 ...
        request.taskId = taskId
        request.userId = MiraManager.instance.getUserId()
        request.userDeviceId = DeviceManager.instance.getUserDeviceId()
        request.device = DeviceInfoEntity.build(context)
        request.inputList = inputList

        val fileParts = mutableListOf<MultipartBody.Part>()

        // 遍历步骤列表，统一重命名文件
        stepList.forEach { entity ->
            val originFile = entity.file
            if (originFile != null && entity.stepId != null) {
                // 2. 将这个唯一名同步给 Entity，这样它会被序列化进 JSON 发给后端
                val uniqueName = entity.stepFileName;
                // 3. 构建 Part，这里的第二个参数必须传 uniqueName
                val fileBody = originFile.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("files", uniqueName, fileBody)
                fileParts.add(part)
            }
        }
        // 此时 request.stepList 里的每个 entity 都有了对应的 uniqueName
        request.stepList = stepList

        // 提交给 service
        return@withContext miraService.submitTask(request, fileParts)
    }

    /**
     * 获取历史提交页面的菜单列表
     * create by Eastevil at 2026/3/26 13:36
     * @author Eastevil
     * @param
     * @return
     */
    suspend fun chronoMenuList() = withContext(Dispatchers.IO){
        val request = ChronoMenuListReqeust();
        return@withContext miraService.chronoMenuList(request);
    }

    suspend fun submitRecords(page : Long,type : Int?) = withContext(Dispatchers.IO){
        val request = SubmitRecordReqeust();
        request.page = page;
        request.type = type;
        return@withContext miraService.submitRecords(request);
    }
}
