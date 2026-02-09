package com.wsvita.biz.core.model.address.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.biz.core.entity.region.AddressEntity
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BizcoreViewModel(application) {

    private val _addressList = MutableLiveData<MutableList<AddressEntity>>();
    val addressList : LiveData<MutableList<AddressEntity>>
        get() = _addressList;

    val adress = MutableLiveData<BizLocation>();

    override fun initModel() {
        super.initModel()
        queryAddressList();
    }

    override fun receiveLocation(location: BizLocation) {
        super.receiveLocation(location)
        SLog.d(TAG,"receiveLocation,time:${systemTime()}");
        adress.value = location;
    }

    private fun queryAddressList(){
        viewModelScope.launch {
            val list = request(showLoading = true, requestCode = 1){
                BizcoreModel.instance.addressList();
            }
            if(list != null){
                _addressList.value = list!!;
            }
        }
    }

    companion object {
        private const val TAG = "WSV_Core_Address_MainViewModel=>"
    }
}
