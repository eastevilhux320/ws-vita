package com.wsvita.account.model.login.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.wsvita.account.commons.AccountFragment
import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.model.login.LoginActivity
import com.wsvita.framework.entity.SuccessHolder
import com.wsvita.module.account.R
import com.wsvita.module.account.databinding.FragmentLoginMainBinding
import ext.StringExt.isInvalid
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.toRippleDrawable

class MainFragment : AccountFragment<FragmentLoginMainBinding, MainViewModel>() {

    override fun layoutId(): Int {
        return R.layout.fragment_login_main;
    }

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java;
    }

    override fun onConfigChanged(config: AccountConfig) {
        super.onConfigChanged(config)
        val themeColor = viewModel.themeColor();
        dataBinding.tvAcLoginUsername.setTextColor(themeColor);
        dataBinding.tvLoginWechat.setTextColor(themeColor);
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding.fragment = this;
        hideStatusBar();
        goneTitle();
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.cl_ac_login_phone->{
                navigate(R.id.f_login_phone);
            }
            R.id.tv_ac_login_username->{
                //用户名密码登录
                val userName = dataBinding.accountEditUsername.text.toString();
                if(userName.isInvalid()){
                    toast(R.string.account_hint_account_login_username);
                    return;
                }
                val password = dataBinding.accountEditPassword.text.toString();
                if(password.isInvalid()){
                    toast(R.string.account_hint_account_login_password);
                    return;
                }
                viewModel.usernameLogin(userName,password);
            }
        }
    }

    override fun onSuccess(success: SuccessHolder) {
        super.onSuccess(success)
        //登录成功
        //成功，结束当前的activity，既：登录页面，返回上一层
        val ac = getCurrentActivity(LoginActivity::class.java);
        ac?.finish();
    }

    companion object{
        private const val TAG = "WS_AC_Login_MainFragment==>";

        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
