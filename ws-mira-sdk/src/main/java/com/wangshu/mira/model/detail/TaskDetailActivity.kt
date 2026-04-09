package com.wangshu.mira.model.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.wangshu.mira.R
import com.wangshu.mira.adapter.TaskStepAdapter
import com.wangshu.mira.commons.MiraActivity
import com.wangshu.mira.commons.MiraIntentKey
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.mira.databinding.ActivityMiraTaskDetailBinding
import com.wangshu.mira.entity.TaskStepEntity
import com.wangshu.mira.entity.enums.MiraOperatetion
import com.wangshu.mira.entity.enums.OperationCode
import com.wangshu.mira.entity.enums.UserTaskState
import com.wangshu.mira.ext.MiraExt.openApp
import com.wangshu.mira.view.dialog.ImagePreviewDialog
import com.wangshu.mira.view.dialog.MiraTaskSubmitResultDialog
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.GlideApp
import com.wsvita.framework.entity.ErrorHolder
import com.wsvita.framework.router.FinishParam
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson
import ext.ViewExt.createComplexRectDrawable


class TaskDetailActivity : MiraActivity<ActivityMiraTaskDetailBinding, TaskDetailViewModel>() {
    private lateinit var stepAdapter : TaskStepAdapter;
    private lateinit var previewDialog : ImagePreviewDialog;
    private lateinit var submitResultDialog : MiraTaskSubmitResultDialog;

    override fun getVMClass(): Class<TaskDetailViewModel> {
        return TaskDetailViewModel::class.java;
    }

    override fun layoutId(): Int {
        return R.layout.activity_mira_task_detail;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.activity = this;
        dataBinding.viewModel = viewModel;

        stepAdapter = TaskStepAdapter(this,null);
        dataBinding.stepAdapter = stepAdapter;

        previewDialog = ImagePreviewDialog.Builder(this)
            .builder();

        stepAdapter.onWatchExampleImage {
            //查看示例图，需要直接放大展示一张图片
            previewDialog.updateImageUrl(it);
            previewDialog.show();
        }

        stepAdapter.onImageSeleted { position, step ->
            //判断是否拥有权限
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                SLog.d(TAG,"checkSelfPermission false,request permissions");
                // 调用你基类或框架中的统一权限申请逻辑
                ActivityCompat.requestPermissions(this, arrayOf(permission), 1001)
            } else {
                SLog.d(TAG,"checkSelfPermission true,do it");
                //判断当前的状态是否允许被操作步骤
                if(viewModel.isAllowOperationStep()){
                    openGalleryIntent(position,step);
                }else{
                    val tipsText = viewModel.stepNotAllowTips();
                    toast(tipsText)
                }
            }
        }

        submitResultDialog = MiraTaskSubmitResultDialog.Builder(this)
            .onBackTaskList {
                val intent = Intent();
                intent.putExtra(MiraIntentKey.SUBMIT_RESULT,true);
                intent.putExtra("message","success");
                val p = FinishParam.create()
                    .putBoolean(MiraIntentKey.SUBMIT_RESULT,true)
                    .putString("message","success");
                finishWithResult(p);
            }
            .isCancelable(false)
            .builder();
    }

    override fun onIntentReceivedLong(key: String, value: Long) {
        super.onIntentReceivedLong(key, value)
        SLog.d(TAG,"onIntentReceivedLong,key:${key},value:${value}");
        if(MiraIntentKey.TASK_ID.equals(key)){
            viewModel.initTask(value);
        }
    }


    override fun autoIntentValue(): MutableList<String>? {
        return mutableListOf(MiraIntentKey.TASK_ID);
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.task.observe(this, Observer {
            GlideApp.with(dataBinding.ivMiraTaskIcon)
                .load(it.iconUrl)
                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .into(dataBinding.ivMiraTaskIcon);
            //获取任务操作名称
            val operationText = OperationCode.from(it.operationCode).getName(this);
            dataBinding.tvMiraTaskOperation.setText(operationText);
        })

        viewModel.targetApp.observe(this, Observer {
            GlideApp.with(dataBinding.ivMiraTaskAppIcon)
                .load(it.appIcon)
                .error(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .placeholder(com.wsvita.ui.R.drawable.ui_list_item_no_data_default)
                .into(dataBinding.ivMiraTaskAppIcon);
        })

        viewModel.stepList.observe(this, Observer {
            stepAdapter.setList(it);
            stepAdapter.notifyDataSetChanged();
        })

        viewModel.userTaskState.observe(this, Observer { state ->
            // 统一圆角半径 5dp
            val radiusDp = 5f

            when(state) {
                UserTaskState.NONE -> {
                    // 暂无记录逻辑保持不变
                    dataBinding.tvTaskOperationSub.visibility = View.GONE
                    dataBinding.tvMiraTaskTime.visibility = View.GONE
                    dataBinding.tvTaskContinueTask.visibility = View.GONE

                    // 动态设置背景：利用你 ViewExt 里的 Int 扩展方法
                    val bgColor = getColor(R.color.mira_task_operation_main_none)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_none))
                }

                UserTaskState.APPLIED -> {
                    // 已申请未开始逻辑
                    dataBinding.tvTaskOperationSub.visibility = View.GONE
                    dataBinding.tvMiraTaskTime.visibility = View.GONE
                    dataBinding.tvTaskContinueTask.visibility = View.GONE

                    val bgColor = getColor(R.color.mira_task_operation_main_applied)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_applied))
                }

                UserTaskState.IN_PROGRESS -> {
                    // 进行中逻辑
                    dataBinding.tvTaskOperationSub.visibility = View.VISIBLE
                    dataBinding.tvMiraTaskTime.visibility = View.VISIBLE
                    dataBinding.tvTaskContinueTask.visibility = View.VISIBLE

                    val bgColor = getColor(R.color.mira_task_operation_main_in_progress)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_in_progress))
                }

                UserTaskState.SUBMIT -> {
                    //已提交
                    dataBinding.tvTaskOperationSub.visibility = View.GONE;
                    dataBinding.tvMiraTaskTime.visibility = View.GONE;
                    dataBinding.tvTaskContinueTask.visibility = View.GONE;

                    val bgColor = getColor(R.color.mira_task_operation_main_submit)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_submit))
                }

                UserTaskState.COMPLETED -> {
                    // 已完成逻辑
                    dataBinding.tvTaskOperationSub.visibility = View.VISIBLE
                    dataBinding.tvMiraTaskTime.visibility = View.GONE
                    dataBinding.tvTaskContinueTask.visibility = View.GONE

                    val bgColor = getColor(R.color.mira_task_operation_main_completed)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_completed))
                }

                UserTaskState.CANCELLED -> {
                    // 已取消逻辑
                    dataBinding.tvTaskOperationSub.visibility = View.VISIBLE
                    dataBinding.tvMiraTaskTime.visibility = View.GONE
                    dataBinding.tvTaskContinueTask.visibility = View.GONE

                    val bgColor = getColor(R.color.mira_task_operation_main_canceled)
                    dataBinding.llTaskOperationMain.background = bgColor.createComplexRectDrawable(radius = radiusDp)

                    dataBinding.tvMiraTaskOpeationMain.setTextColor(getColor(R.color.mira_task_operation_main_text_canceled))
                }
                else -> {}
            }
        })
        viewModel.taskOperation.observe(this, Observer {
            when(it.operatetion()){
                MiraOperatetion.CONTINUE_TASK,
                MiraOperatetion.START->{
                    //打开指定的应用包名
                    it.targetApp?.packageName?.let {
                        this.openApp(it);
                    }
                }
                MiraOperatetion.SUBMIT->{
                    //任务提交成功，显示提交成功的弹框
                    submitResultDialog.show();
                }
                else->{}
            }
        })

        viewModel.operationStep.observe(this, Observer {
            val file = it.fillingFile;
            val position = it.position;
            if(file != null && position != null){
                //更新步骤展示
                stepAdapter.fillingSubmitFile(file,position);
            }
        })
    }

    override fun onConfigChanged(config: MiraConfig) {
        super.onConfigChanged(config)
        dataBinding.clMainTop.setBackgroundColor(config.mainThemeColor);
        dataBinding.tvMiraTaskDetailTitle.setText(config.titleText);
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.ll_task_operation_main->{
                val inputList = stepAdapter.getInputList();
                SLog.d(TAG,"inputList:${inputList.toJson()}");
                viewModel.operationTask(inputList);
            }
            R.id.tv_task_operation_sub->{
                viewModel.subOperationTask();
            }
            R.id.tv_task_continue_task->{
                viewModel.continueTask();
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == TASK_REQUEST_IMAGE_CODE) {
            data?.data?.let {uri->
                viewModel.selectImage(uri);
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == TASK_REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SLog.d(TAG, "Permissions granted by user")
                toast(R.string.mira_task_detail_permission_success)
            } else {
                SLog.d(TAG, "Permissions denied by user")
                // 可以在这里接入你 wsvita 框架的统一 Toast 提示用户
                toast(R.string.mira_task_detail_permission_fail)
            }
        }
    }

    override fun onRequestStageChanged() {
        super.onRequestStageChanged()
    }

    override fun onRequestStageChanged(config: ModelRequestConfig, active: Boolean) {
        super.onRequestStageChanged(config, active)

    }

    override fun onError(error: ErrorHolder) {
        super.onError(error)
        toast(error.msg);
    }

    private fun openGalleryIntent(position : Int,taskStepEntity: TaskStepEntity){
        viewModel.setCurrentImageIndex(position,taskStepEntity);
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(intent, TASK_REQUEST_IMAGE_CODE)
        } catch (e: Exception) {
            Log.d(TAG, "no activity found")
        }
    }

    companion object {
        private const val TAG = "Mira_Main_TaskDetailActivity=>"
        private const val TASK_REQUEST_IMAGE_CODE = 10000;
        private const val TASK_REQUEST_PERMISSION = 10001;
    }

}
