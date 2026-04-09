package com.wangshu.mira.model.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.mira.MiraSDK
import com.wangshu.mira.commons.MiraViewModel
import com.wangshu.mira.entity.TaskEntity
import com.wangshu.mira.local.manager.DeviceManager
import com.wangshu.mira.local.manager.MiraManager
import com.wangshu.mira.network.model.MiraModel
import com.wangshu.mira.network.response.MiraConnectionResponse
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.utils.SLog
import com.wsvita.network.entity.Result
import com.wsvita.network.manager.TokenManager
import com.wsvita.network.model.NetworkModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MiraMainViewModel(application: Application) : MiraViewModel(application) {
    /**
     * 初始化结果标识
     */
    val initResult = MutableLiveData<Boolean>();

    val connectionResult = MutableLiveData<MiraConnectionResponse>();

    private val _taskList = MutableLiveData<MutableList<TaskEntity>>()
    val taskList: LiveData<MutableList<TaskEntity>>
        get() = _taskList


    override fun initModel() {
        super.initModel()

        miraServiceInit();
    }

    /**
     * 已经获取到该有的权限，开始SDK逻辑处理
     * create by Eastevil at 2026/3/3 15:15
     * @author Eastevil
     * @param
     * @return
     *      void
     */
    fun permissionOk(){
        showLoading();
        SLog.d(TAG,"permissionOk,time:${systemTime()}");
        val app = getApplication<Application>();
        MiraSDK.instance.readySDK(app);
        //通常，第一次OAID的获取需要延迟一下
        delay(1000){
            connection();
        }
    }

    private fun miraServiceInit() = GlobalScope.launch{
        val initReponse = request(MIRA_INIT,true){
            MiraModel.instance.miraInit();
        }
        if(initReponse != null){
            TokenManager.instance.resetToken(initReponse.token);
            initReponse.userId?.let { MiraManager.instance.userId(it) }
            withMain {
                initResult.value = true;
            }
        }
    }

    private fun connection() = viewModelScope.launch{
        val connectionResult = request(MIRA_CONNECTION,true){
            MiraModel.instance.connection(getApplication())
        }
        if(connectionResult != null){
            connectionResult.userDeviceId?.let { DeviceManager.instance.setUserDeviceId(it) }
            withMain {
                this@MiraMainViewModel.connectionResult.value = connectionResult;
            }
        }
    }

    fun refreshTaskPage(){
        _taskList.value?.let {
            it.clear();
        }
        taskPage();
    }

    private fun taskPage() = viewModelScope.launch{
        val taskPage = request(requestCode = TASK_PAGE,showLoading = true){
            MiraModel.instance.taskPage(getApplication(),null,null);
        }
        if(taskPage != null){
            var list = _taskList.value;
            if(list == null){
                list = mutableListOf();
            }
            taskPage.list?.let { list.addAll(it) }
            withMain {
                _taskList.value = list!!;
            }
        }
    }

    companion object {
        private const val TAG = "Mira_Main_MiraMainViewModel=>"
        private const val MIRA_INIT = 0x01
        private const val MIRA_CONNECTION = 0x02;
        private const val TASK_PAGE = 0x03;
    }
}
