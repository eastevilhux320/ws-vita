package com.wsvita.biz.core.model.protocol

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.entity.AppUrlEntity
import com.wsvita.biz.core.model.splash.SplashViewModel
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.core.common.AppViewModel
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.network.entity.Result
import com.wsvita.network.model.NetworkModel
import kotlinx.coroutines.flow.MutableStateFlow

class ProtocolViewModel(application: Application) : BizcoreViewModel(application) {

    val protocolUrl = MutableLiveData<String>();

    val isAgree = MutableLiveData<Boolean>();

    override fun initModel() {
        super.initModel()
        isAgree.value = false;
    }

    fun resetUrl(url :String?){
        url?.let {
            protocolUrl.value = it;
        }
    }

    fun setAgreeFlag(){
        val v = isAgree.value?:false;
        isAgree.value = !v;
    }

}
