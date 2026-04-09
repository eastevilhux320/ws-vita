package com.wangshu.textus.note.model.main

import android.os.Bundle
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteActivity
import com.wangshu.textus.note.databinding.ActivityMainBinding
import com.wsvita.biz.core.adapter.MainTabAdapter

class MainActivity : NoteActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mainTabAdapter : MainTabAdapter;

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java;
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

}
