package com.wsvita.account.model.login

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.FullyDrawnReporter
import com.wsvita.account.commons.AccountContainerActivity
import com.wsvita.account.configure.AccountConfigure
import com.wsvita.account.model.login.main.MainFragment
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog
import com.wsvita.framework.widget.view.VitaTitleBar
import com.wsvita.module.account.R

class LoginActivity : AccountContainerActivity<LoginViewModel>() {
    override fun startDestinationId(): Int {
        return R.id.f_login_main;
    }

    override fun destinationIdList(): MutableList<Int> {
        return mutableListOf(R.id.f_login_main,R.id.f_login_phone);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun getNavGraphResId(): Int {
        return R.navigation.nav_login;
    }

    override fun getVMClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java;
    }

    override fun initTitle(titleBar: VitaTitleBar) {
        super.initTitle(titleBar)
        val navId = currentNavigateId();
        when(navId){
            R.id.f_login_main->{
                //titleBar.setTitleText(R.string.account_ac_title_login_main)
                goneTitle();
            }
            R.id.f_login_phone->{
                visibileTitle();
                titleBar.setTitleText(R.string.account_ac_title_login_phone)
            }
        }
    }

    override fun onInterceptBack(): Boolean {
        val navId = currentNavigateId();
        if(R.id.f_login_phone == navId){
            navigate(R.id.f_login_main);
            return true;
        }
        return super.onInterceptBack()
    }

    override fun initScreenConfig(): ScreenConfig {
        SLog.d(TAG,"initScreenConfig,time:${systemTime()}");
        val color = AccountConfigure.instance.getConfig()?.mainThemeColor?: Color.BLACK;
        val config = ScreenConfig.buildFull(color);
        return config;
    }

    override fun onIntentReceivedString(key: String, value: String) {
        super.onIntentReceivedString(key, value)
        SLog.d(TAG,"onIntentReceivedString,key:${key},value");
    }

    companion object{
        private const val TAG = "WS_AC_Login_LoginActivity==>";
    }
}
