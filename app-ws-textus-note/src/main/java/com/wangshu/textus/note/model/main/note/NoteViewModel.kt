package com.wangshu.textus.note.model.main.note

import android.app.Application
import com.wangshu.textus.note.common.NoteViewModel
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson

class NoteViewModel(application: Application) : com.wangshu.textus.note.common.NoteViewModel(application) {

    override fun receiveLocation(location: BizLocation) {
        super.receiveLocation(location)
        SLog.d(TAG,"receiveLocation:${location?.toJson()}");
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
    }
}
