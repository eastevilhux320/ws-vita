package com.wangshu.mira.model.detail

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.mira.R
import com.wangshu.mira.commons.MiraViewModel
import com.wangshu.mira.entity.InputContentEntity
import com.wangshu.mira.entity.StepOperationEvent
import com.wangshu.mira.entity.TaskAppEntity
import com.wangshu.mira.entity.TaskEntity
import com.wangshu.mira.entity.TaskOperationEvent
import com.wangshu.mira.entity.TaskStepEntity
import com.wangshu.mira.entity.TaskStepParamEntity
import com.wangshu.mira.entity.enums.MiraOperatetion
import com.wangshu.mira.entity.enums.UserTaskState
import com.wangshu.mira.network.model.MiraModel
import com.wangshu.mira.network.response.TaskDetailReponse
import com.wangshu.mira.network.response.TaskOperationBaseReponse
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.entity.VError
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class TaskDetailViewModel(application: Application) : MiraViewModel(application) {
    private val _taskDetail = MutableLiveData<TaskDetailReponse>();
    val taskDetail : LiveData<TaskDetailReponse>
        get() = _taskDetail;

    private val _task = MutableLiveData<TaskEntity>();
    val task : LiveData<TaskEntity>
        get() = _task;

    private val _stepList = MutableLiveData<MutableList<TaskStepEntity>>();
    val stepList : LiveData<MutableList<TaskStepEntity>>
        get() = _stepList;

    private val _app = MutableLiveData<TaskAppEntity>();
    val targetApp : LiveData<TaskAppEntity>
        get() = _app;
    /**
     * 1-已申请未开始(对应开始任务)
     * 2-进行中(对应继续任务/提交任务/取消任务)
     * 3-已取消
     * 4-已完成
     */
    private val _userTaskState = MutableLiveData<UserTaskState>();
    val userTaskState : LiveData<UserTaskState>
        get() = _userTaskState;

    private val _taskOperation = MutableLiveData<TaskOperationEvent>();
    val taskOperation : LiveData<TaskOperationEvent>
        get() = _taskOperation;


    // 1. 底部主按钮文本 (如：开始任务、提交任务)
    private val _mainButtonText = MutableLiveData<String>()
    val mainButtonText: LiveData<String> get() = _mainButtonText

    // 2. 底部副按钮文本 (如：取消任务)
    private val _subButtonText = MutableLiveData<String>()
    val subButtonText: LiveData<String> get() = _subButtonText

    // 3. 时间提醒文本 (如：请在 15:30 前完成)
    private val _expirationTimeText = MutableLiveData<String>()
    val expirationTimeText: LiveData<String> get() = _expirationTimeText

    /**
     * 当前正准备处理的步骤，由UI的事件传入，
     * 在用户点击步骤中的事件触发时候，记录步骤下标及步骤，下标对应的是步骤列表的下标
     * 比如：点击的第一个步骤的，则记录0.并且记录处理的第一个的步骤对象
     */
    private var currentImageIndex : Int = -1;
    private var currentStep : TaskStepEntity? = null;

    /**
     * 操作的具体步骤对象发生变化
     * 例如用户选择图片完成，需要通过该对象进行文件填充，或者处理其他音视频等，最终都会通过这个对象统治UI更新
     */
    private val _operationStep = MutableLiveData<StepOperationEvent>()
    val operationStep : LiveData<StepOperationEvent> = _operationStep;


    override fun initModel() {
        super.initModel()
    }

    fun initTask(taskId : Long){
        SLog.d(TAG,"init task_taskId:${taskId}");
        viewModelScope.launch {
            val detail = request(REQ_TASK_DETAIL,true){
                MiraModel.instance.taskDetail(taskId,getApplication());
            }
            detail?.let {
                withMain {
                    _taskDetail.value = it;
                    _task.value = it.task;
                    _stepList.value = it.stepList;
                    _app.value = it.app;
                    _mainButtonText.value = it.mainButtonText;
                    _subButtonText.value = it.subButtonText;
                    _expirationTimeText.value = it.expirationTimeText;
                    it.state?.let { state->
                        SLog.d(TAG,"initTask_userTaskState:${state}");
                        _userTaskState.value = UserTaskState.from(state);
                    }
                }
            }
        }
    }

    fun operationTask(inputList : MutableList<InputContentEntity>?){
        val userTaskState = this.userTaskState.value;
        when(userTaskState){
            UserTaskState.NONE->{
                //申请任务
                applyTask();
            }
            UserTaskState.IN_PROGRESS->{
                //提交任务
                submitTask(inputList);
            }
            UserTaskState.APPLIED->{
                //开始任务
                startTask();
            }
            UserTaskState.CANCELLED->{
                //查看详情
            }
            UserTaskState.COMPLETED->{
                //查看详情
            }
            else->{
                error(ERROR_OPERAT_TASK,getString(R.string.task_not_find));
                return;
            }
        }
    }

    fun subOperationTask(){
        val userTaskState = this.userTaskState.value;
        when(userTaskState){
            UserTaskState.NONE->{
                //申请任务
                error()
            }
            UserTaskState.APPLIED,
            UserTaskState.IN_PROGRESS->{
                /**
                 * 状态：申请中/进行中
                 * 操作：取消任务
                 */
                cancelTask();
            }
            UserTaskState.CANCELLED->{
                //查看详情
            }
            UserTaskState.COMPLETED->{
                //查看详情
            }
            else->{
                error(ERROR_OPERAT_TASK,getString(R.string.task_not_find));
                return;
            }
        }
    }

    fun continueTask(){
        if(UserTaskState.IN_PROGRESS == userTaskState.value){
            updateTaskOperation(MiraOperatetion.CONTINUE_TASK);
        }else{
            //以后继续优化，这里理论上需要增加提示,但其实如果不是正在进行中的，也不会看到继续任务的按钮
        }
    }

    fun setCurrentImageIndex(currentImageIndex : Int,step : TaskStepEntity){
        this.currentImageIndex = currentImageIndex;
        this.currentStep = step;
    }

    fun selectImage(uri : Uri){
        getFilePathFromUri(uri)?.let {
            val file = File(it);
            val operationStep = StepOperationEvent();
            operationStep.step = currentStep;
            operationStep.position = currentImageIndex;
            operationStep.fillingFile = file;
            _operationStep.value = operationStep;
            stepList.value?.let {
                //修改文件列表中的列表文件
                val position = operationStep.position;
                position?.let {p->
                    if(p >= 0 && p < it.size){
                        it.get(p).stepFile = file;
                    }
                }
            }
        }
    }

    /**
     * 是否允许被操作步骤
     * create by Eastevil at 2026/3/20 17:05
     * @author Eastevil
     * @param
     * @return
     */
    fun isAllowOperationStep(): Boolean {
        val userTaskState = this.userTaskState.value;
        //目前只有在进行中的任务允许被操作步骤
        return when(userTaskState){
            UserTaskState.IN_PROGRESS-> true;
            else-> false;
        }
    }

    fun stepNotAllowTips(): String {
        val userTaskState = this.userTaskState.value;
        return when(userTaskState){
            UserTaskState.NONE-> getString(R.string.mira_task_step_operation_not_allow_none_tips)
            UserTaskState.APPLIED-> getString(R.string.mira_task_step_operation_not_allow_applied_tips)
            UserTaskState.CANCELLED-> getString(R.string.mira_task_step_operation_not_allow_cancelled_tips)
            UserTaskState.COMPLETED-> getString(R.string.mira_task_step_operation_not_allow_completed_tips)
            else-> ""
        }
    }

    private fun applyTask(){
        val taskId = task.value?.id;
        if(taskId == null){
            return;
        }
        viewModelScope.launch {
            val result = request(REQ_TASK_APPLY,true){
                MiraModel.instance.applyTask(taskId,getApplication());
            }
            if(result != null){
                dispatchOperationResult(result);
                startTask();
            }
        }
    }

    private fun cancelTask(){
        val taskId = task.value?.id;
        if(taskId == null){
            return;
        }
        viewModelScope.launch {
            val result = request(REQ_TASK_CANCEL,true){
                MiraModel.instance.cancelTask(taskId,getApplication());
            }
            //任务取消成功
            result?.let { dispatchOperationResult(it) };
            updateTaskOperation(MiraOperatetion.CANCEL);
        }
    }

    private fun startTask(){
        val taskId = task.value?.id;
        if(taskId == null){
            return;
        }
        viewModelScope.launch {
            val result = request(REQ_TASK_START,true){
                MiraModel.instance.startTask(taskId,getApplication());
            }
            if(result != null){
                dispatchOperationResult(result);
                //任务开始成功，开始任务
                updateTaskOperation(MiraOperatetion.START);
            }
        }
    }

    /**
     * 提交任务
     * create by Eastevil at 2026/3/20 17:16
     * @author Eastevil
     * @param
     * @return
     */
    private fun submitTask(inputList : MutableList<InputContentEntity>?){
        val taskId = task.value?.id;
        if(taskId == null){
            return;
        }
        if(UserTaskState.IN_PROGRESS != userTaskState.value){
            //未来增加验证
            return;
        }
        //判断步骤中需要的信息是否都已完成
        val stepList = stepList.value;
        val stepParamsList = mutableListOf<TaskStepParamEntity>();
        stepList?.let {list->
            val size = list.size;
            var isValidated = true;
            for(i in 0 until  size){
                val step  = list.get(i);
                if(!step.isValidated()){
                    isValidated = false;
                    break;
                }

                val stepParam = TaskStepParamEntity();
                stepParam.stepId = step.id;
                stepParam.file = step.stepFile;
                stepParam.stepFileName = generateStepFileName(taskId,step.id,step.stepFile)

                stepParamsList.add(stepParam);
            }
            if(!isValidated){
                error(-1,getString(R.string.mira_task_tips_completed_step))
                return;
            }
        }
        //提交任务,构建提交请求参数
        viewModelScope.launch {
            val submitResult = request(REQ_TASK_SUBMIT,true){
                MiraModel.instance.submitTask(taskId,getApplication(),stepParamsList,inputList)
            }
            submitResult?.let {result->
                dispatchOperationResult(result);
                updateTaskOperation(MiraOperatetion.SUBMIT);
            }
        }
    }

    /**
     * 修改任务操作，会触发UI的操作
     * create by Eastevil at 2026/3/19 16:13
     * @author Eastevil
     * @param
     * @return
     */
    private fun updateTaskOperation(operation : MiraOperatetion) = viewModelScope.launch{
        val op = TaskOperationEvent.create(operation);
        task.value?.let {
            op.setTask(it);
        }
        op.setApp(targetApp.value);
        if(isMainThread()){
            _taskOperation.value = op;
        }else{
            withMain {
                _taskOperation.value = op;
            }
        }
    }

    private fun dispatchOperationResult(response : TaskOperationBaseReponse) = viewModelScope.launch{
        withMain {
            _mainButtonText.value = response.mainButtonText;
            _subButtonText.value = response.subButtonText;
            _expirationTimeText.value = response.expirationTimeText;
            response.state?.let { state->
                SLog.d(TAG,"initTask_userTaskState:${state}");
                _userTaskState.value = UserTaskState.from(state);
            }
        }
    }

    override fun isDataEmptyAsError(requestCode: Int): Boolean {
        return when(requestCode){
            REQ_TASK_APPLY-> false;
            REQ_TASK_START-> false;
            else-> super.isDataEmptyAsError(requestCode)
        }
    }

    fun getFilePathFromUri(uri: Uri): String? {
        if (uri.scheme == "file") return uri.path

        val context = getApplication<Application>();

        // 创建临时文件，避免权限问题
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return tempFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 生成符合业务规范的唯一文件名称
     * 格式：任务ID_步骤ID_毫秒时间戳.扩展名
     */
    private fun generateStepFileName(taskId: Long, stepId: Long, originalFile: File?): String {
        val extension = originalFile?.extension ?: "jpg" // 默认扩展名
        val timestamp = System.currentTimeMillis()
        return "${taskId}_${stepId}_${timestamp}.${extension}"
    }

    override suspend fun handleBusinessError(config: ModelRequestConfig, error: VError) {
        super.handleBusinessError(config, error)
        when(config.requestCode){
            REQ_TASK_SUBMIT->{
                //提交任务错误返回
                SLog.d(TAG,"handleBusinessError,code:${error.code},${error.msg}")
                val taskId = task.value?.id;
                taskId?.let {
                    initTask(taskId);
                }
            }
        }
    }

    companion object {
        private const val TAG = "Mira_Main_TaskDetailViewModel=>"
        private const val REQ_TASK_DETAIL = 0x01;
        private const val REQ_TASK_APPLY = 0x02;
        private const val REQ_TASK_START = 0x03;
        private const val REQ_TASK_CANCEL = 0x04;
        private const val REQ_TASK_SUBMIT = 0x05;
        private const val ERROR_OPERAT_TASK = 0x80;
    }

}
