package com.wsvita.biz.core.model.address.enrollment

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wsvita.biz.core.R
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.entity.region.CityEntity
import com.wsvita.biz.core.entity.region.DistrictEntity
import com.wsvita.biz.core.entity.region.ProvinceEntity
import com.wsvita.biz.core.network.model.BizcoreModel
import com.wsvita.core.common.NavigationViewModel
import com.wsvita.framework.ext.JsonExt.parseGson
import kotlinx.coroutines.launch

class EnrollmentViewModel(application: Application) : BizcoreViewModel(application) {
    private var  province : ProvinceEntity? = null;
    private var  city : CityEntity? = null;
    private var  district : DistrictEntity? = null;

    val cityText = MutableLiveData<String>();

    override fun initModel() {
        super.initModel()
        cityText.value = getString(R.string.bizcore_address_city_selected);
    }

    fun selectCity(province : String?,city : String?,district : String?){
        this.province = province?.parseGson();
        this.city = city?.parseGson();
        this.district = district?.parseGson();
        cityText.value = "${this.province?.name}${this.city?.name}${this.district?.name}"
    }

    fun enrollment(address : String){
        if(province == null || city == null || district == null){
            toast(R.string.bizcore_address_city_selected)
            return;
        }
        viewModelScope.launch {
            request(showLoading = true, requestCode = 1){
                BizcoreModel.instance.enrollmentAddress(province!!,city!!,district!!,address)
            }
            success();
        }
    }

    override fun isDataEmptyAsError(requestCode: Int): Boolean {
        return false;
    }
}
