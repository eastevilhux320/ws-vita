package com.wangshu.textus.note.model.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.wangshu.textus.note.R
import com.wangshu.textus.note.adapter.MainPagerAdapter
import com.wangshu.textus.note.common.NoteActivity
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.ActivityMainBinding
import com.wsvita.biz.core.adapter.MainTabAdapter
import com.wsvita.biz.core.commons.BizcoreFragment
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog
import ext.ViewExt.dip2px

class MainActivity : NoteActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mainTabAdapter : MainTabAdapter;
    private lateinit var pagerAdapter: MainPagerAdapter;

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig()
        c.isFullScreen = true
        c.statusBarColor = getColor(R.color.color_main_theme)
        c.lightIcons = false
        return c
    }

    override fun getVMClass(): Class<MainViewModel> {
        return MainViewModel::class.java;
    }

    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        val layoutManager = LinearLayoutManager(this);
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL;
        dataBinding.rvBottomTab.layoutManager = layoutManager;

        mainTabAdapter = MainTabAdapter(this,4,50.dip2px());
        dataBinding.mainTabAdapter = mainTabAdapter;

        mainTabAdapter.onMainTabClick { tab, position ->
            SLog.d(TAG,"onItemSelect==>${position}");
            selectIndex(position);
        }

        pagerAdapter = MainPagerAdapter(this)
        dataBinding.viewpaer.adapter = pagerAdapter
        dataBinding.viewpaer.isUserInputEnabled = false

        // 3. 页面切换监听
        dataBinding.viewpaer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectIndex(position)
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val mainPosition = intent?.getIntExtra(MainContants.MAIN_POSITION_INDEX,0)?:dataBinding.viewpaer.currentItem;
        SLog.d(TAG,"onNewIntent_mainPosition=>${mainPosition}")
        val pageCurrentPos = dataBinding.viewpaer.currentItem;
        if(pageCurrentPos != mainPosition){
            selectIndex(mainPosition);
            dataBinding.viewpaer.setCurrentItem(mainPosition);
        }
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.tabList.observe(this, Observer {
            mainTabAdapter.setList(it);
            mainTabAdapter.notifyDataSetChanged();
        })

        viewModel.fragmentList.observe(this, Observer {
            pagerAdapter.updateData(it)
        })
    }

    override fun needLocation(): Boolean {
        return true;
    }

    override fun baiduScanSpan(): Int {
        //return 1 * 60 * 60 * 1000;
        return 5000;
    }

    override fun onLocationChanged(location: BizLocation) {
        super.onLocationChanged(location)
        SLog.d(TAG,"onLocationChanged");
    }

    fun currentLocation(): BizLocation? {
        return viewModel.location.value;
    }

    private fun selectIndex(position: Int) {
        if (viewModel.isSameSelect(position)) {
            return
        }
        SLog.d(TAG, "selectIndex: $position")

        // 1. Hide the current one immediately
        currentFragment()?.onMainHidden()

        // 2. Update ViewModel and ViewPager
        viewModel.selectTabIndex(position)
        dataBinding.viewpaer.currentItem = position

        // 3. IMPORTANT: Wait for the ViewPager to finish attaching the fragment
        // posting to the view's queue ensures this runs after the transaction
        dataBinding.viewpaer.post {
            if (!isFinishing && !isDestroyed) {
                currentFragment()?.onMainShow()
            }
        }
    }

    private fun currentFragment(): NoteMainFragment<*, *>? {
        return viewModel.fragmentList.value?.get(dataBinding.viewpaer.currentItem);
    }

    companion object{
        private const val TAG = "Note_Main_MainViewModel==>";

        fun showFragment(context : Context, position : Int, isFinishe : Boolean){

        }
    }
}
