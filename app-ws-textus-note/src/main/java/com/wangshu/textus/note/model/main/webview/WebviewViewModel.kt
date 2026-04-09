package com.wangshu.note.app.model.main.webview

import android.app.Application
import com.wangshu.note.app.common.NoteViewModel
import com.wangshu.note.app.model.main.MainData

class WebviewViewModel(application: Application) : NoteViewModel<MainData>(application) {

    override fun initData(): MainData {
        return MainData();
    }
}
