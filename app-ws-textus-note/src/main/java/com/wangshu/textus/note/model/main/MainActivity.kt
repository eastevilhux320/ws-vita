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
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteActivity
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.databinding.ActivityMainBinding
import com.wsvita.biz.core.adapter.MainTabAdapter
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog
import ext.ViewExt.dip2px

class MainActivity : NoteActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var mainTabAdapter : MainTabAdapter;

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
            viewModel.selectTabIndex(position);
            dataBinding.viewpaer.currentItem = position;
        }


        dataBinding.viewpaer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                SLog.d(TAG,"onPageSelected==>${position}");
                viewModel.selectTabIndex(position);
            }
        })

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val mainPosition = intent?.getIntExtra(MainContants.MAIN_POSITION_INDEX,0)?:dataBinding.viewpaer.currentItem;
        SLog.d(TAG,"onNewIntent_mainPosition=>${mainPosition}")
        val pageCurrentPos = dataBinding.viewpaer.currentItem;
        if(pageCurrentPos != mainPosition){
            viewModel.selectTabIndex(mainPosition);
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
            initViewpaer(it);
        })
    }

    override fun needLocation(): Boolean {
        return true;
    }

    override fun onLocationChanged(location: BizLocation) {
        super.onLocationChanged(location)
        val currentIndex = dataBinding.viewpaer.currentItem;
        val f = viewModel.fragmentList.value?.get(currentIndex);
        if(f is com.wangshu.textus.note.model.main.note.NoteFragment){
            f.receiveContainerLocation(location);
        }
    }

    override fun baiduScanSpan(): Int {
        //return 1 * 60 * 60 * 1000;
        return 5000;
    }

    private fun initViewpaer(fragmentList : MutableList<NoteFragment<*, *>>){
        dataBinding.viewpaer.adapter = object : FragmentStatePagerAdapter(supportFragmentManager,
            BEHAVIOR_SET_USER_VISIBLE_HINT
        ) {
            /**
             * Return the Fragment associated with a specified position.
             */
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            /**
             * Return the number of views available.
             */
            override fun getCount(): Int {
                return fragmentList.size;
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                //super.destroyItem(container, position, `object`)
            }
        }
    }

    companion object{
        private const val TAG = "Note_Main_MainViewModel==>";

        fun showFragment(context : Context, position : Int, isFinishe : Boolean){

        }
    }
}
