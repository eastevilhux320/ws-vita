package com.wsvita.biz.core.commons

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.core.common.AppContainerViewModel
import com.wsvita.framework.utils.SLog

abstract class BizcoreContainerViewModel(application: Application) : AppContainerViewModel(application) {

    /**
     * 1. 保持 LiveData 观察能力，适应 Builder 模式的异步/延迟初始化
     * 设为只读 LiveData，防止 View 层篡改数据
     */
    private val _config = MutableLiveData<BizcoreConfig>()
    val config: LiveData<BizcoreConfig> = _config

    private val _location = MutableLiveData<BizLocation>();
    val location : LiveData<BizLocation> = _location;

    fun appId(): Long {
        return BizcoreConfigure.instance.appId();
    }

    override fun initModel() {
        super.initModel()
        SLog.d(TAG,"initModel");
        _config.value = BizcoreConfigure.instance.getConfig();
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

    open fun isLogin(): Boolean {
        return false;
    }

    open fun receiveLocation(location : BizLocation){
        SLog.i(TAG,"receiveLocation");
        _location.value = location;
    }

    companion object{
        private const val TAG = "WSVita_Bizcore_BizcoreContainerViewModel=>"
    }
}
