package com.wsvita.biz.core.model.address.enrollment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.databinding.FragmentAddressEnrollmentBinding
import com.wsvita.biz.core.model.address.RouterName
import com.wsvita.core.common.NavigationFragment
import com.wsvita.framework.entity.SuccessHolder
import com.wsvita.framework.router.contract.ComplexResult
import ext.StringExt.isInvalid
import ext.ViewExt.createComplexRectDrawable
import ext.ViewExt.toRippleDrawable

class EnrollmentFragment : BizcoreFragment<FragmentAddressEnrollmentBinding, EnrollmentViewModel>() {

    override fun navigationId(): Int {
        return R.id.f_bizcore_address_enrollment;
    }

    override fun layoutId(): Int {
        return R.layout.fragment_address_enrollment;
    }

    override fun getVMClass(): Class<EnrollmentViewModel> {
        return EnrollmentViewModel::class.java;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setTitleText(getString(R.string.bizcore_title_address_enrollment))

        dataBinding.fragment = this;
    }

    override fun receiveRouterContract(action: String, name: String, complexResult: ComplexResult) {
        super.receiveRouterContract(action, name, complexResult)
        val province = complexResult.getString(BizConstants.IntentKey.RESULT_REGION_PROVINCE);
        val city = complexResult.getString(BizConstants.IntentKey.RESULT_REGION_CITY);
        val district = complexResult.getString(BizConstants.IntentKey.RESULT_REGION_DISTRICT);
        viewModel.selectCity(province,city,district);
    }

    override fun onConfigChanged(config: BizcoreConfig) {
        super.onConfigChanged(config)
        dataBinding.tvBizcoreAddressAdd.background = config.mainThemeColor.toRippleDrawable(25f);
    }

    override fun onViewClick(view: View) {
        super.onViewClick(view)
        when(view.id){
            R.id.mete_city->{
                routerContainer(RouterName.ROUTER_REGION);
            }
            R.id.tv_bizcore_address_add->{
                //添加地址
                val address = dataBinding.editAddress.text.toString();
                if(address.isInvalid()){
                    toast(R.string.bizcore_hint_address_detail);
                    return;
                }
                viewModel.enrollment(address);
            }
        }
    }

    override fun onSuccess(success: SuccessHolder) {
        super.onSuccess(success)
        //地址添加成功
        navigate(R.id.f_bizcore_address_main);
    }

}
