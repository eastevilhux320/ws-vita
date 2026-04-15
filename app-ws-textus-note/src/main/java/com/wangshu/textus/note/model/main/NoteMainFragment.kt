package com.wangshu.textus.note.model.main

import androidx.databinding.ViewDataBinding
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.common.NoteViewModel
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.framework.utils.SLog

abstract class NoteMainFragment<D : ViewDataBinding, V : NoteMainViewModel> : NoteFragment<D, V>(){

    open fun onMainHidden(){
        SLog.d(TAG,"onMainHidden");
    }

    open fun onMainShow(){
        SLog.d(TAG,"onMainShow");
    }

    fun mainActivity(): MainActivity? {
        return getCurrentActivity(MainActivity::class.java);
    }

    companion object {
        private const val TAG = "Note_Main_NoteMainFragment=>"
    }
}
