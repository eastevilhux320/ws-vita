package com.wsvita.account.commons

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.configure.AccountConfigure
import com.wsvita.core.common.AppContainerViewModel

abstract class AccountContainerViewModel(application: Application) : AppContainerViewModel(application) {

    /**
     * 1. 保持 LiveData 观察能力，适应 Builder 模式的异步/延迟初始化
     * 设为只读 LiveData，防止 View 层篡改数据
     */
    private val _config = MutableLiveData<AccountConfig>()
    val config: LiveData<AccountConfig> = _config

    override fun initModel() {
        super.initModel()
        _config.value = AccountConfigure.instance.getConfig();
    }

    override fun themeColor(): Int {
        return _config.value?.mainThemeColor?: Color.BLACK;
    }
}
