package com.vita.xuantong.demo.model.main.mine

import android.app.Application
import com.vita.xuantong.demo.commons.KYViewModel

class MineViewModel(application: Application) : KYViewModel(application) {

    override fun isAccountObserverEnabled(): Boolean {
        return true;
    }
}
