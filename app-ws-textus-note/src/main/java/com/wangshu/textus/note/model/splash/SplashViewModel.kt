package com.wangshu.textus.note.model.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.wangshu.textus.note.common.NoteViewModel

class SplashViewModel(application: Application) : NoteViewModel(application) {
    val toSplash = MutableLiveData<Boolean>();

    override fun initModel() {
        super.initModel()
        delay(3000){
            toSplash.value = true;
        }
    }
}
