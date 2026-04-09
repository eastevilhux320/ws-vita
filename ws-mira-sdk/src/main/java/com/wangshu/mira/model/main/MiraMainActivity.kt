package com.wangshu.mira.model.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.wangshu.mira.R
import com.wangshu.mira.adapter.TaskAdapter
import com.wangshu.mira.commons.MiraAction
import com.wangshu.mira.commons.MiraActivity
import com.wangshu.mira.commons.MiraIntentKey
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.configure.MiraContants
import com.wangshu.mira.databinding.ActivityMiraMainBinding
import com.wangshu.mira.local.manager.PermissionManager
import com.wangshu.mira.model.detail.TaskDetailActivity
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.EmptyRouterContract
import com.wsvita.framework.router.contract.SendRouterContract
import com.wsvita.framework.router.contract.full.ComplexFullRouterContract
import com.wsvita.framework.router.contract.sender.LongSendRouterContract
import com.wsvita.framework.utils.SLog
import pub.devrel.easypermissions.EasyPermissions

/**
 * 玄映 (ws-mira) 主界面
 * 负责处理权限准入及业务初始化分发
 */
class MiraMainActivity : MiraActivity<ActivityMiraMainBinding, MiraMainViewModel>(), EasyPermissions.PermissionCallbacks {
    private lateinit var taskAdapter : TaskAdapter;

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
        val c = ComplexFullRouterContract(MiraAction.ACTION_TASK_DETAIL);
        configurator.register(TASK_DETAIL_ROUTER,c){s->
            val result = s.getBoolean(MiraIntentKey.SUBMIT_RESULT);
            val msg = s.getString("message");
            SLog.d(TAG,"result:${result},message:${msg}");
            if(result){
                viewModel.refreshTaskPage();
            }
        }

        val m = ComplexFullRouterContract(MiraAction.ACTION_CHRONO);
        configurator.register(CHRONO,m){s->

        }
    }

    override fun getVMClass(): Class<MiraMainViewModel> {
        return MiraMainViewModel::class.java
    }

    override fun layoutId(): Int {
        return R.layout.activity_mira_main
    }

    override fun initScreenConfig(): ScreenConfig {
        return super.initScreenConfig()
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        taskAdapter = TaskAdapter(this,null);
        dataBinding.taskAdapter = taskAdapter;


        taskAdapter.onTaskClick {
            /*val intent = Intent(this,TaskDetailActivity::class.java);
            intent.putExtra("task_id",it.id);
            startActivityForResult(intent,1);*/
            router(TASK_DETAIL_ROUTER,MiraIntentKey.TASK_ID,it.id);
        }

        dataBinding.tvMiraMainMenu.setOnClickListener {
            //进入我参与的列表
            router(CHRONO);
        }
    }

    /**
     * 将系统回调分发给 PermissionManager 内部处理
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.instance.handleRequestResult(requestCode, permissions, grantResults, this)
    }

    /**
     * EasyPermissions 成功回调
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // 再次进行全量权限最终校验，确保业务合规
        if (PermissionManager.instance.hasPermissions(this)) {
            viewModel.permissionOk()
        }
    }

    /**
     * EasyPermissions 失败回调
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // 权限被拒绝，按照业务协议返回结果并销毁页面
        setResult(MiraContants.MIRA_RESULT_CODE)
        finish()
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.initResult.observe(this, Observer {
            //初始化完成后才开启主页面的权限判断
            doMiraPermission();
        })

        viewModel.connectionResult.observe(this, Observer {
            //开始进行主页面的UI逻辑处理
            viewModel.refreshTaskPage();
        })

        viewModel.taskList.observe(this, Observer {
            taskAdapter.setList(it);
            taskAdapter.notifyDataSetChanged();
        })
    }

    override fun onConfigChanged(config: MiraConfig) {
        super.onConfigChanged(config)
        dataBinding.clMainTop.setBackgroundColor(config.mainThemeColor);
        dataBinding.tvMiraMainTitle.setText(config.titleText);
    }

    /**
     * 开启主页面的权限判断
     * create by Eastevil at 2026/3/3 15:12
     * @author Eastevil
     * @param
     * @return
     */
    private fun doMiraPermission(){
        SLog.d(TAG,"doMiraMain start,time:${systemTime()}");
        // 1. 判断是否拥有所需要的权限
        if (!PermissionManager.instance.hasPermissions(this)) {
            val isAuto = MiraConfigure.instance.getConfig()?.isAutoPermission ?: false
            if (isAuto) {
                // 调用 PermissionManager 内聚的请求逻辑
                PermissionManager.instance.requestPermissions(this)
            } else {
                // 非自动模式直接返回并关闭
                setResult(MiraContants.MIRA_RESULT_CODE)
                finish()
            }
            return
        }
        // 2. 权限校验通过，驱动 ViewModel 执行初始化逻辑
        viewModel.permissionOk()
    }

    companion object {
        private const val TAG = "Mira_Main_MiraMainActivity=>"
        private const val TASK_DETAIL_ROUTER = "mira_task_to_detail";
        private const val CHRONO = "mira_to_user_chrono";
    }
}
