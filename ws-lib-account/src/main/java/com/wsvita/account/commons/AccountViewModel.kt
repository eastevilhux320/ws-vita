package com.wsvita.account.commons

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.configure.AccountConfigure
import com.wsvita.core.common.AppViewModel
import com.wsvita.framework.utils.SLog

abstract class AccountViewModel(application: Application) : AppViewModel(application) {

    /**
     * 1. 保持 LiveData 观察能力，适应 Builder 模式的异步/延迟初始化
     * 设为只读 LiveData，防止 View 层篡改数据
     */
    private val _config = MutableLiveData<AccountConfig>()
    val config: LiveData<AccountConfig> = _config

    fun appId(): Long {
        return AccountConfigure.instance.appId();
    }

    override fun initModel() {
        super.initModel()
        SLog.d(TAG,"initModel");
        _config.value = AccountConfigure.instance.getConfig();
    }

    override fun themeColor(): Int {
        return _config.value?.mainThemeColor?: Color.WHITE;
    }

    override fun submitColor(): Int {
        return _config.value?.submitColor?: Color.BLACK;
    }

    override fun cancelColor(): Int {
        return _config.value?.cancelColor?: Color.GRAY;
    }

    companion object{
        private const val TAG = "WS_AC_AccountViewModel=>"
    }
}
