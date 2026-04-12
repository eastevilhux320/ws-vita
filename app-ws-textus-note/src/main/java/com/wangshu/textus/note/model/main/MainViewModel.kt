package com.wangshu.textus.note.model.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteFragment
import com.wangshu.textus.note.common.NoteViewModel
import com.wangshu.textus.note.local.NoteConstants
import com.wangshu.textus.note.local.WSConstants
import com.wangshu.textus.note.local.manager.ChannelManager
import com.wangshu.textus.note.model.main.default.DefaultFragment
import com.wangshu.textus.note.model.main.discovery.DiscoveryFragment
import com.wangshu.textus.note.model.main.home.HomeFragment
import com.wangshu.textus.note.model.main.mine.MineFragment
import com.wangshu.textus.note.model.main.webview.WebviewFragment
import com.wsvita.biz.core.entity.MainTabEntity
import com.wsvita.biz.core.network.model.AppModel
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJSON
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : NoteViewModel(application) {

    private var lastSelectTab : Int = -1;


    val tabList = MutableLiveData<MutableList<MainTabEntity>>();
    val fragmentList = MutableLiveData<MutableList<NoteFragment<*, *>>>();

    override fun initModel() {
        super.initModel()
        tabList();
    }

    /**
     * 选择底部导航栏
     * create by Administrator at 2022/10/12 23:40
     * @author Administrator
     * @param position
     *      选择的下标
     * @return
     *      void
     */
    fun selectTabIndex(position : Int){
        if(position == lastSelectTab){
            return;
        }
        val list = tabList.value;
        list?.let {
            it[lastSelectTab].itemSelect = false;
            it[position].itemSelect = true;
            lastSelectTab = position;
            tabList.value = it;
        }
    }

    private fun tabList() = viewModelScope.launch{
        val result = AppModel.instance.mainTabList(ChannelManager.instance.getChannel());
        SLog.d(TAG,"tabList=>${result.toJSON()}");
        if(result.isSuccess){
            val tabList = result.data;
            if(tabList != null && tabList.isNotEmpty()){
                val list = mutableListOf<MainTabEntity>();
                val fList = mutableListOf<NoteFragment<*, *>>()
                var index = 0;
                tabList.forEach {
                    val tab = it;
                    tab?.itemSelect = index == 0;
                    lastSelectTab = 0;
                    index++;
                    if (tab != null) {
                        list.add(tab)
                    }
                    when(it.fragmentCode){
                        NoteConstants.TabCode.FRAGMENT_HOME->{
                            fList.add(HomeFragment.newInstance());
                        }
                        NoteConstants.TabCode.FRAGMENT_NOTE->{
                            fList.add(com.wangshu.textus.note.model.main.note.NoteFragment.newInstance());
                        }
                        NoteConstants.TabCode.FRAGMENT_DISCOVERY->{
                            fList.add(DiscoveryFragment.newInstance());
                        }
                        NoteConstants.TabCode.FRAGMENT_MINE->{
                            fList.add(MineFragment.newInstance());
                        }
                        WSConstants.TabCode.FRAGMENT_WEBVIEW->{
                            fList.add(WebviewFragment.newInstance());
                        }
                        else->{
                            fList.add(DefaultFragment.newInstance());
                        }
                    }
                }
                if(list.isNotEmpty()){
                    withMain {
                        this@MainViewModel.tabList.value = list;
                        fragmentList.value = fList;
                    }
                }else{
                    createLocalTabList();
                }
            }else{
                withMain {
                    createLocalTabList();
                }
            }
        }else{
            withMain {
                createLocalTabList();
            }
        }
    }

    /**
     * 创建默认底部导航栏
     * create by Eastevil at 2022/10/12 16:55
     * @author Eastevil
     * @return
     *      void
     */
    private fun createLocalTabList(){
        val list = mutableListOf<MainTabEntity>();
        val selColor = "#B92D1E"
        val norColor = "#8A8A8A"
        val homeTab = MainTabEntity();
        homeTab.fragmentType = WSConstants.TabType.TYPE_HOME;
        homeTab.fragmentCode = WSConstants.TabCode.FRAGMENT_HOME;
        homeTab.name = getString(R.string.tab_main_home)
        homeTab.selColor = selColor;
        homeTab.norColor = norColor;
        homeTab.itemSelect = false;
        list.add(homeTab);

        val mineTab = MainTabEntity();
        mineTab.fragmentType = WSConstants.TabType.TYPE_MINE;
        mineTab.fragmentCode = WSConstants.TabCode.FRAGMENT_MINE;
        mineTab.name = getString(R.string.tab_main_mine)
        mineTab.selColor = selColor;
        mineTab.norColor = norColor;
        mineTab.itemSelect = false;
        list.add(mineTab);
        tabList.value = list;
    }

    companion object{
        private const val TAG = "Vita_Note_Main_MainViewModel=>";
    }

}
