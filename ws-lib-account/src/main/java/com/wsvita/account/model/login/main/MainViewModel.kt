package com.wsvita.account.model.login.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.wsvita.account.local.locator.AccountConfigLocator
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.commons.AccountViewModel
import com.wsvita.account.local.locator.AccountScope
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.account.network.model.AccountModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AccountViewModel(application) {


    fun usernameLogin(userName : String,passowrd : String){
        viewModelScope.launch {
            val result = request(requestCode = REQ_LOGIN, showLoading = true,) {
                AccountModel.instance.usernameLogin(userName,passowrd);
            }
            result?.let {
                //登录成功，需要修改token及key
                val locator = AccountConfigLocator.instance;
                it.token?.let { it1 -> locator.put(AccountConstants.AccountKeys.TOKEN, it1) };
                it.secretKey?.let { it1 -> locator.put(AccountConstants.AccountKeys.SERVICE_KEY, it1) };
                it.keyType?.let { it1 -> locator.put(AccountConstants.AccountKeys.KEY_TYPE, it1) }

                locator.dispatchAction(AccountScope.AC_SCOPE_LOGIN);
                locator.dispatchAction(AccountScope.AC_SCOPE_TOKEN);

                AccountManager.instance.notifyMember();
                //需要重置账号中的数据
                withMain {
                    success();
                }
            }
        }
    }


    companion object{
        private const val TAG = "WS_AC_Login_MainViewModel=>";

        private const val REQ_LOGIN = 0x02;
    }
}
