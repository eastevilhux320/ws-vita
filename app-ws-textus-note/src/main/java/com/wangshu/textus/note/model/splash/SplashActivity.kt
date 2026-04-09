package com.wangshu.textus.note.model.splash

import androidx.lifecycle.Observer
import com.wangshu.textus.note.common.NoteActivity
import com.wangshu.textus.note.R
import com.wangshu.textus.note.databinding.ActivitySplashBinding
import com.wangshu.textus.note.local.Action
import com.wangshu.textus.note.local.RouterName
import com.wsvita.framework.router.RouterConfigurator
import com.wsvita.framework.router.contract.EmptyRouterContract

class SplashActivity : NoteActivity<ActivitySplashBinding, SplashViewModel>() {

    override fun prepareRouters(configurator: RouterConfigurator) {
        super.prepareRouters(configurator)

        val splash = EmptyRouterContract(Action.ACTION_VITA_LAUNCH);
        configurator.register(RouterName.ROUTER_LAUNCH,splash){

        }
    }

    override fun getVMClass(): Class<SplashViewModel> {
        return SplashViewModel::class.java;
    }

    override fun layoutId(): Int {
        return R.layout.activity_splash;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.toSplash.observe(this, Observer {
            router(RouterName.ROUTER_LAUNCH);
            finish();
        })
    }

}
