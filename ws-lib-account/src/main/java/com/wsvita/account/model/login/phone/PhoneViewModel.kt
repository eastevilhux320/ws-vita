package com.wsvita.account.model.login.phone

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.account.accountup.AccountConfigLocator
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.commons.AccountViewModel
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.account.network.model.AccountModel
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.commons.FrameTimer
import com.wsvita.module.account.R
import com.wsvita.network.entity.Result
import com.wsvita.network.model.NetworkModel
import ext.StringExt.isInvalid
import kotlinx.coroutines.launch

class PhoneViewModel(application: Application) : AccountViewModel(application) {
    private var optTimeText : String? = null;

    /**
     * 当前的倒记时是否正处于倒记时中
     */
    private var optIsTimeing : Boolean = false;

    private val _optTime = MutableLiveData<String>();
    val optTime : LiveData<String>
        get() = _optTime;

    override fun initModel() {
        super.initModel()
        optIsTimeing = false;
        optTimeText = getString(R.string.account_send_opt_timer)
    }

    fun sendOPT(phone : String){
        if(optIsTimeing){
            //正处于倒记时中
            return;
        }
        if(phone.isInvalid()){
            toast(getString(R.string.account_ac_loginphone_hint_phone));
            return;
        }
        viewModelScope.launch {
            request(requestCode = REQ_SEND_OPT, showLoading = true){
                NetworkModel.instance.sendOPT(SEND_OPT_TYPE,phone)
            }
        }
    }

    fun login(phone : String,optCode : String){
        viewModelScope.launch {
            val result = request(showLoading = true, requestCode = REQ_LOGIN){
                AccountModel.instance.phoneLogin(phone,optCode);
            }
            result?.let {
                //登录成功，需要修改token及key
                val locator = AccountConfigLocator.instance;
                it.token?.let { it1 -> locator.put(AccountConstants.AccountKeys.TOKEN, it1) };
                it.secretKey?.let { it1 -> locator.put(AccountConstants.AccountKeys.SERVICE_KEY, it1) };
                it.keyType?.let { it1 -> locator.put(AccountConstants.AccountKeys.KEY_TYPE, it1) }

                locator.dispatchLoginReady()
                AccountManager.instance.notifyMember();
                //需要重置账号中的数据
                withMain {
                    success();
                }
            }
        }
    }

    override suspend fun requestBegin(config: ModelRequestConfig) {
        super.requestBegin(config)
        when(config.requestCode){
            REQ_SEND_OPT->{
                //发送验证码开始
                optIsTimeing = true;
            }
        }
    }

    override fun isDataEmptyAsError(requestCode: Int): Boolean {
        return when(requestCode){
            REQ_SEND_OPT-> false;
            else-> return super.isDataEmptyAsError(requestCode)
        }
    }

    override suspend fun handleBusinessEmpty(config: ModelRequestConfig, code: Int, msg: String?) {
        super.handleBusinessEmpty(config, code, msg)
        if(REQ_SEND_OPT == config.requestCode){
            //开启倒记时
            optIsTimeing = true;
            FrameTimer.build(60 * 1000L,1000)
                .onTick {
                    _optTime.value = "${(it/1000L)}$optTimeText"
                }
                .onFinish {
                    //倒记时结束
                    val text = getString(R.string.account_send_opt);
                    _optTime.value = text;
                    optIsTimeing = false;
                }
                .start();
        }
    }

    companion object{
        private const val TAG = "WS_AC_Login_PhoneViewModel=>";
        //当前模块，固定类型，值为1
        private const val SEND_OPT_TYPE = 1;

        private const val REQ_SEND_OPT = 0x01;
        private const val REQ_LOGIN = 0x02;
    }
}
