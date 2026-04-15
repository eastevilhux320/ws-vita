package com.wangshu.textus.note.entity.weather

import com.wangshu.textus.note.R
import com.wangshu.textus.note.common.NoteApp

class WeatherNow {

    /**
     * 空气指数，越小越好
     */
    var aqi: String? = null

    /**
     * 空气湿度
     */
    var sd: String? = null

    /**
     * 气温
     */
    var temperature: String? = null

    /**
     * 获得气温的时间
     */
    var temperatureTime: String? = null

    /**
     * 天气
     */
    var weather: String? = null

    /**
     * 天气小图标
     */
    var weatherPic: String? = null

    /**
     * 风向
     */
    var windDirection: String? = null

    /**
     * 风力
     */
    var windPower: String? = null

    /**
     * aqi明细数据
     */
    var aqiDetail: WeatherNowDetail? = null

    val temperatureText : String
    get() {
        return "${temperature}${NoteApp.app.getString(R.string.app_unit_temperature)}";
    }
}
