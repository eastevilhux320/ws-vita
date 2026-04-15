package com.wangshu.textus.note.network.request

import com.wsvita.network.entity.BaseRequest

class WeatherLngWlatRequest : BaseRequest() {
    var  type : Int? = null;

    var lng : String? = null;

    var lat : String? = null;
}
