package com.wsvita.account.commons

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.wsvita.account.configure.AccountConfig
import com.wsvita.core.common.AppActivity
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog

abstract class AccountActivity<D : ViewDataBinding,V : AccountViewModel> : AppActivity<D, V>(){

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig();
        c.isFullScreen = false;
        c.statusBarColor = Color.BLACK;
        c.lightIcons = false;
        return c;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.config.observe(this, Observer {
            onConfigChanged(it);
        })
    }

    protected open fun onConfigChanged(config : AccountConfig){
        SLog.d(TAG,"onConfigChanged");
    }

    companion object{
        private const val TAG = "WS_AC_AccountActivity=>"
    }
}
