package com.wangshu.textus.note.model.main.discovery

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wangshu.textus.note.common.NoteViewModel
import com.wangshu.textus.note.entity.QuoteEntity
import com.wangshu.textus.note.model.main.NoteMainViewModel

class DiscoveryViewModel(application: Application) : NoteMainViewModel(application) {
    private val _quotes = MutableLiveData<QuoteEntity>();
    val quotes : LiveData<QuoteEntity>
        get() = _quotes

    override fun initModel() {
        super.initModel()

        val q = QuoteEntity();
        q.author = "-罗曼·罗兰";
        q.content = "真正的英雄主义，是看清生活真相后依然热爱它"
        _quotes.value = q;
    }

    companion object{
        private const val TAG = "WS_Note_Main_DiscoveryViewModel==>";
    }
}
