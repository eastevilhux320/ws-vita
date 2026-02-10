package com.vita.xuantong.demo.model.main.serene

import android.app.Application
import com.vita.xuantong.demo.commons.KYViewModel

class SereneViewModel(application: Application) : KYViewModel(application) {

    override fun isAccountObserverEnabled(): Boolean {
        return true;
    }
}
