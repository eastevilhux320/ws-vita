package com.vita.xuantong.demo.model.main.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vita.xuantong.demo.commons.KYViewModel
import com.wsvita.biz.core.entity.AppHomeConfigEntity
import com.wsvita.biz.core.entity.HomeBannerEntity
import com.wsvita.biz.core.network.model.AppModel
import com.wsvita.biz.core.network.model.BizcoreModel
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : KYViewModel(application) {

    private val _homeConfig = MutableLiveData<AppHomeConfigEntity>();
    val homeConfig : LiveData<AppHomeConfigEntity>
        get() = _homeConfig;

    private val _bannerList = MutableLiveData<MutableList<HomeBannerEntity>>();
    val bannerList : LiveData<MutableList<HomeBannerEntity>>
        get() = _bannerList;

    override fun initModel() {
        super.initModel()
        viewModelScope.launch {
            launch {
                homeConfig();
            }
            launch {
                homeBannerList();
            }
        }
    }

    private suspend fun homeConfig(){
        val homeConfig = request(showLoading = false, requestCode = REQUEST_HOME_CONFIG){
            AppModel.instance.appHomeConfig();
        }
        homeConfig?.let {
            withMain {
                _homeConfig.value = it;
            }
        }
    }

    private suspend fun homeBannerList(){
        val bannerList = request(requestCode = REQUEST_BANNER_LIST){
            BizcoreModel.instance.homeBannerList();
        }
        bannerList?.let {
            withMain {
                _bannerList.value = it;
            }
        }
    }

    companion object{
        private const val REQUEST_HOME_CONFIG = 0x01;
        private const val REQUEST_BANNER_LIST = 0x02;
    }
}
