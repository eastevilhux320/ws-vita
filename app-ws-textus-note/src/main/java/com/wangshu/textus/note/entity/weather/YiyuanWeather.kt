package com.wangshu.textus.note.entity.weather

import com.wangshu.textus.note.entity.weather.WeatherCityInfo
import com.wangshu.textus.note.entity.weather.WeatherF
import com.wangshu.textus.note.entity.weather.WeatherNow

class YiyuanWeather {

    /**
     * 返回码
     */
    var retCode: Int = 0

    /**
     * 返回时间
     */
    var time: String? = null

    /**
     * 备注信息
     */
    var remark: String? = null

    /**
     * 现在实时的天气情况
     */
    var now: WeatherNow? = null

    /**
     * 查询的地区基本资料
     */
    var cityInfo: WeatherCityInfo? = null

    /**
     * 今天的天气预报
     */
    var f1: WeatherF? = null

    /**
     * 今天+1天后的天气预报
     */
    var f2: WeatherF? = null

    /**
     * 今天+2天后的天气预报
     */
    var f3: WeatherF? = null

    /**
     * 今天+3天后的天气预报
     */
    var f4: WeatherF? = null

    /**
     * 今天+4天后的天气预报
     */
    var f5: WeatherF? = null

    /**
     * 今天+5天后的天气预报
     */
    var f6: WeatherF? = null

    /**
     * 今天+6天后的天气预报
     */
    var f7: WeatherF? = null
}
