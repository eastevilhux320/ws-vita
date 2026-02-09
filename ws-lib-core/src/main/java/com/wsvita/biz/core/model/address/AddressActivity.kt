package com.wsvita.biz.core.model.address

import androidx.navigation.NavGraph
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizConstants
import com.wsvita.biz.core.commons.BizcoreContainerActivity
import com.wsvita.biz.core.configure.Action
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.full.ComplexResultRouterContract

class AddressActivity : BizcoreContainerActivity<AddressViewModel>() {

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)
        val contrace = ComplexResultRouterContract(Action.ACTION_REGION)
            .addInput(BizConstants.IntentKey.REGION_SELECTED_FINISH,true)
            .addInput(BizConstants.IntentKey.REGION_CITY_HOT_FLAG,true);

        configurator.registerContract(RouterName.ROUTER_REGION, contrace) {action, name, data ->
            dispatchRouterContract(action,name,data);
        }
    }

    override fun onLocationChanged(location: BizLocation) {
        super.onLocationChanged(location)
    }

    override fun startDestinationId(): Int {
        return R.id.f_bizcore_address_main;
    }

    override fun destinationIdList(): MutableList<Int> {
        return mutableListOf(R.id.f_bizcore_address_main,R.id.f_bizcore_address_enrollment);
    }

    override fun getNavGraphResId(): Int {
        return R.navigation.nav_address;
    }

    override fun getVMClass(): Class<AddressViewModel> {
        return AddressViewModel::class.java;
    }

    override fun onBack() {
        super.onBack()
    }

    override fun onInterceptBack(): Boolean {
        val currentId = currentNavigateId();
        when(currentId){
            R.id.f_bizcore_address_enrollment-> {
                navigate(R.id.f_bizcore_address_main);
                return true;
            }
        }
        return super.onInterceptBack();
    }

    override fun needLocation(): Boolean {
        return true;
    }

    companion object{

    }
}
