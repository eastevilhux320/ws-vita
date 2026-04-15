package com.wangshu.textus.note.entity.weather

class WeatherF {

    /**
     * 白天天气
     */
    var dayWeather: String? = null

    /**
     * 晚上天气
     */
    var nightWeather: String? = null

    /**
     * 白天天气温度(摄氏度)
     */
    var dayAirTemperature: String? = null

    /**
     * 晚上天气温度(摄氏度)
     */
    var nightAirTemperature: String? = null

    /**
     * 白天风向编号
     */
    var dayWindDirection: String? = null

    /**
     * 晚上风向编号
     */
    var nightWindDirection: String? = null

    /**
     * 白天风力编号
     */
    var dayWindPower: String? = null

    /**
     * 晚上风力编号
     */
    var nightWindPower: String? = null

    /**
     * 日出日落时间(中间用|分割)
     */
    var sunBeginEnd: String? = null

    /**
     * 指数对象
     */
    var index: WeathF1Index? = null

    /**
     * 当前天
     */
    var day: String? = null

    /**
     * 星期几
     */
    var weekday: String? = null

    /**
     * 白天天气图标
     */
    var dayWeatherPic: String? = null

    /**
     * 晚上天气图标
     */
    var nightWeatherPic: String? = null

    /**
     * 白天的天气编码
     */
    var dayWeatherCode: String? = null

    /**
     * 晚上的天气编码
     */
    var nightWeatherCode: String? = null

    /**
     * 大气压
     */
    var airPress: String? = null

    /**
     * 降水概率
     */
    var jiangshui: String? = null

    /**
     * 紫外线
     */
    var ziwaixian: String? = null
}
