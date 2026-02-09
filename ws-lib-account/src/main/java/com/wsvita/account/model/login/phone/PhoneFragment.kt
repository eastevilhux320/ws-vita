package com.wsvita.account.model.login.phone

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.wsvita.account.commons.AccountFragment
import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.model.login.LoginActivity
import com.wsvita.account.model.login.main.MainFragment
import com.wsvita.framework.entity.SuccessHolder
import com.wsvita.module.account.R
import com.wsvita.module.account.databinding.FragmentLoginPhoneBinding
import ext.StringExt.isInvalid
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.toRippleDrawable

class PhoneFragment : AccountFragment<FragmentLoginPhoneBinding, PhoneViewModel>() {

    override fun layoutId(): Int {
        return R.layout.fragment_login_phone;
    }

    override fun getVMClass(): Class<PhoneViewModel> {
        return PhoneViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        showStatusBar();
        showTitle();
        setTitleText(R.string.account_ac_title_login_phone);
        dataBinding.fragment = this;

        dataBinding.inputPhoneNumber.onSuffixClick { suffixView, parent ->
            //发送验证码
            val phone = dataBinding.inputPhoneNumber.getInputText();
            viewModel.sendOPT(phone);
        }
    }

    override fun onConfigChanged(config: AccountConfig) {
        super.onConfigChanged(config)
        val btnBg = config.mainThemeColor.toRippleDrawable(25f);
        dataBinding.btnEnterLogin.background = btnBg;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.optTime.observe(this, Observer {
            dataBinding.inputPhoneNumber.setSuffixText(it);
        })
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.btn_enter_login->{
                //确定登录
                val phone = dataBinding.inputPhoneNumber.getInputText();
                if(phone.isInvalid()){
                    toast(R.string.account_ac_loginphone_hint_phone);
                    return;
                }
                val optCode = dataBinding.inputOtp.getInputText();
                if(optCode.isInvalid()){
                    toast(R.string.account_hint_ac_out_code);
                    return;
                }
                viewModel.login(phone,optCode);
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
        private const val TAG = "WS_AC_Loing_PhoneFragment==>";

        fun newInstance(): PhoneFragment {
            val args = Bundle()
            val fragment = PhoneFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
