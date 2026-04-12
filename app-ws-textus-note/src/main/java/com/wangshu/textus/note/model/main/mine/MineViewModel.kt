package com.wangshu.textus.note.model.main.mine

import android.app.Application
import com.wangshu.textus.note.common.NoteViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MineViewModel(application: Application) : NoteViewModel(application) {


    override fun initModel() {
        super.initModel()
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
    }
}
