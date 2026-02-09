package com.wsvita.biz.core.model.splash

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.configure.StartupConfigLocator
import com.wsvita.biz.core.entity.AppUrlEntity
import com.wsvita.biz.core.entity.SplashConfigEntity
import com.wsvita.biz.core.model.splash.entity.SplashEvent
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.entity.VError
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import com.wsvita.network.entity.Result
import com.wsvita.network.manager.TokenManager
import com.wsvita.network.model.NetworkModel
import com.wsvita.network.response.AppBeforehandReponse
import ext.JsonExt.toJson
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
class SplashViewModel(application: Application) : BizcoreViewModel(application) {

    val app = MutableLiveData<AppBeforehandReponse>()

    private val _splashEvent = MutableSharedFlow<SplashEvent>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val splashEvent = _splashEvent.asSharedFlow()

    private val _splashConfig = MutableLiveData<SplashConfigEntity>()
    val splashConfig: LiveData<SplashConfigEntity> get() = _splashConfig

    /**
     * 是否同意协议，默认是false
     */
    private var protocolFlag: Boolean = false

    /**
     * 隐私协议配置
     */
    private val _protocolUrl = MutableLiveData<AppUrlEntity>()
    val protocolUrl: LiveData<AppUrlEntity> get() = _protocolUrl

    override fun initModel() {
        super.initModel()
        // 使用工厂方法发送开始事件
        emitSplash(SplashEvent.start())
        disposeProtocol()
        viewModelScope.launch {
            getAppBeforehand()
        }
    }

    fun isAgreeProtocol(): Boolean {
        return protocolFlag
    }

    /**
     * 同意协议结果处理
     */
    fun protocolResult(flag: Boolean) {
        // 更新内存状态
        protocolFlag = flag
        if (flag) {
            emitSplash(SplashEvent.privacyPolicyAgreed())
            /**
             * 记录用户同意状态。
             * * 这里的 key 必须与 [SplashViewModel.disposeProtocol] 方法中生成的 key 保持一致。
             */
            val key = "WSV_Core_SplashViewModel_${appId()}";
            StorageManager.instance.put(key,true);
            doNext();
        } else {
            emitSplash(SplashEvent.denyPrivacyPolicy())
        }
    }

    /**
     * 隐私协议已校验通过，该方法用户为[SplashActivity]提供调用发送隐私协议已校验通过的事件，其他地方
     * create by Eastevil at 2026/1/5 10:41
     * @author Eastevil
     */
    fun privacyAlreadyAccepted(){
        emitSplash(SplashEvent.privacyAlreadyAccepted())
    }

    /**
     * 由view层发起，通知进入主页，发送启动页完成事件
     * create by Eastevil at 2026/1/5 13:40
     * @author Eastevil
     * @param
     * @return
     */
    fun notifyToMain(){
        emitSplash(SplashEvent.finished());
    }

    private suspend fun getAppBeforehand() {
        SLog.d(TAG, "getAppBeforehand")
        val reponse = request(REQ_APP_BEFOREHAND, ModelRequestConfig.SHOW_TYPE_DIALOG_TIPS) {
            NetworkModel.instance.appBeforehand()
        }
        reponse?.let {
            it.token?.let { it1 -> StartupConfigLocator.instance.put("token", it1) }
            it.secretKey?.let { it1 -> StartupConfigLocator.instance.put("secretKey", it1) }
            StartupConfigLocator.instance.put("keyType", it.keyType)
            StartupConfigLocator.instance.dispatchReady()

            SLog.d(TAG, "getAppBeforehand success")
            // 修复：使用 appBeforehandSuccess 工厂方法传递 token
            emitSplash(SplashEvent.appBeforehandSuccess(it.token))

            if(1 == it.state){
                //当前处于登录状态
                emitSplash(SplashEvent.accountStateOn());
            }
            launchConfig()
            withMain {
                app.value = it
            }
        }
    }

    private suspend fun launchConfig() {
        SLog.d(TAG, "launchConfig")
        val result = request(
            requestCode = REQ_LAUNCHE_CONFIG,
            showType = ModelRequestConfig.SHOW_TYPE_DIALOG_COFMIRT,
            showLoading = false,
            submitText = getString(R.string.bizcore_contact_customer_service)
        ) {
            BizcoreModel.instance.launchConfig()
        }
        if (result != null) {
            SLog.d(TAG, "launch success")
            // 修复：使用 configLoaded 工厂方法，这里可根据需要传递 ID 字符串
            emitSplash(SplashEvent.configLoaded(result.splash?.toJson()))

            withMain {
                _splashConfig.value = result.splash
                if (!isAgreeProtocol()) {
                    // 未同意协议，赋值协议对象，交给 UI 处理跳转
                    _protocolUrl.value = result.protocol
                }else{
                    // 用户已经同意协议
                    doNext();
                }
            }
        }
    }

    /**
     * 统一发送事件的方法
     * 接收由 SplashEvent 静态工厂创建的对象
     */
    private fun emitSplash(event: SplashEvent) {
        SLog.d(TAG, "emitSplash, time:${systemTime()}, event: $event")
        viewModelScope.launch {
            _splashEvent.emit(event)
            StartupConfigLocator.instance.put(SplashEvent.SPLASH_KEY,event.toJson());
        }
    }

    override fun onRequestError(config: ModelRequestConfig, result: Result<*>): Boolean {
        when (config.requestCode) {
            REQ_LAUNCHE_CONFIG -> {
                emitSplash(SplashEvent.configError())
            }
            REQ_APP_BEFOREHAND -> {
                emitSplash(SplashEvent.appBeforehandError())
            }
        }
        return super.onRequestError(config, result)
    }

    /**
     * 处理协议过程
     */
    private fun disposeProtocol() {
        val key = "WSV_Core_SplashViewModel_${appId()}"
        val isAgree = StorageManager.instance.getBoolean(key)
        SLog.d(TAG, "is agree protocol: $isAgree")
        protocolFlag = isAgree
        if (isAgree) {
            emitSplash(SplashEvent.privacyAlreadyAccepted())
        } else {
            emitSplash(SplashEvent.needPrivacyPolicy())
        }
    }

    private fun doNext(){
        /*
         * 用户同意协议，这里可以进行一些常规的用户同意协议后的操作
         * 例如：加载广告，展示广告等一些耗时的操作
         */
        //模拟一个加载
        delay(2000){
            emitSplash(SplashEvent.toMain())
        }
    }

    companion object {
        private const val TAG = "WSV_Core_SplashViewModel=>"
        private const val REQ_APP_BEFOREHAND = 0x01
        private const val REQ_LAUNCHE_CONFIG = 0x02
    }
}
