package com.wangshu.mira.model.chrono

import android.os.Bundle
import androidx.appcompat.view.menu.MenuAdapter
import androidx.lifecycle.Observer
import com.wangshu.mira.R
import com.wangshu.mira.adapter.ChornoMenuAdapter
import com.wangshu.mira.adapter.SubmitRecordAdapter
import com.wangshu.mira.commons.MiraActivity
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.mira.databinding.ActivityMiraChronoBinding
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.entity.IUIEvent
import com.wsvita.framework.utils.SLog
import ext.ViewExt.getScreenPair

class ChronoActivity : MiraActivity<ActivityMiraChronoBinding, ChronoViewModel>() {
    private lateinit var menuAdapter : ChornoMenuAdapter;
    private lateinit var submitAdapter : SubmitRecordAdapter;

    override fun getVMClass(): Class<ChronoViewModel> {
        return ChronoViewModel::class.java;
    }

    override fun layoutId(): Int {
        return R.layout.activity_mira_chrono;
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        menuAdapter = ChornoMenuAdapter(this,null);
        dataBinding.menuAdapter = menuAdapter;

        submitAdapter = SubmitRecordAdapter(this,null);
        dataBinding.submitAdapter = submitAdapter;

        menuAdapter.onMenuClick { memu, position ->
            //选中类型
            viewModel.queryTypeList(memu.type,position);
        }

        dataBinding.ivMiraTaskDetailBack.setOnClickListener {
            finish();
        }
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.menuList.observe(this, Observer {
            val screenWidth = this.getScreenPair().first;
            menuAdapter.countItemWidth(screenWidth,it.size);
            menuAdapter.setList(it);
            menuAdapter.notifyDataChangedSafe();
        })

        viewModel.submitRecordList.observe(this, Observer {
            submitAdapter.setList(it);
            submitAdapter.notifyDataSetChanged();
        })

        viewModel.typeSelectedIndex.observe(this, Observer {
            menuAdapter.setSelected(it);
        })
    }

    override fun handleUIEvent(event: IUIEvent) {
        super.handleUIEvent(event)
    }

    override fun onRequestStageChanged(config: ModelRequestConfig, active: Boolean) {
        super.onRequestStageChanged(config, active)
    }

    override fun onConfigChanged(config: MiraConfig) {
        super.onConfigChanged(config)
        dataBinding.clChronoTop.setBackgroundColor(config.mainThemeColor);
    }
}
