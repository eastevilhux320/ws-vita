package com.wangshu.textus.note.entity.weather

import java.io.Serializable

/**
 * 天气指数实体类
 */
class WeathF1Index : Serializable {

    /**
     * 运动指数
     */
    var yh: YhBean? = null

    /**
     * 晾晒指数
     */
    var ls: LsBean? = null

    /**
     * 穿衣指数
     */
    var clothes: ClothesBean? = null

    /**
     * 钓鱼指数
     */
    var dy: DyBean? = null

    /**
     * 室内运动指数
     */
    var sports: SportsBean? = null

    /**
     * 心情指数
     */
    var xq: XqBean? = null

    /**
     * 旅游指数
     */
    var travel: TravelBean? = null

    /**
     * 美容指数
     */
    var beauty: BeautyBean? = null

    /**
     * 戏水指数
     */
    var hc: HcBean? = null

    /**
     * 感冒指数
     */
    var cold: ColdBean? = null

    /**
     * 逛街指数
     */
    var gj: GjBean? = null

    /**
     * 中暑指数
     */
    var zs: ZsBean? = null

    /**
     * 舒适度指数
     */
    var comfort: ComfortBean? = null

    /**
     * 紫外线指数
     */
    var uv: UvBean? = null

    /**
     * 空气清新指数
     */
    var cl: ClBean? = null

    /**
     * 洗车指数
     */
    var wash_car: WashCarBean? = null

    /**
     * 空调使用指数
     */
    var ac: AcBean? = null

    /**
     * 太阳镜指数
     */
    var glass: GlassBean? = null

    /**
     * 空气质量指数
     */
    var aqi: AqiBean? = null

    /**
     * 护发指数
     */
    var mf: MfBean? = null

    /**
     * 过敏指数
     */
    var ag: AgBean? = null

    /**
     * 啤酒指数
     */
    var pj: PjBean? = null

    /**
     * 夜生活指数
     */
    var nl: NlBean? = null

    /**
     * 放风筝指数
     */
    var pk: PkBean? = null

    class YhBean {
        /**
         * 指数标题
         */
        var title: String? = null

        /**
         * 指数描述
         */
        var desc: String? = null
    }

    class LsBean {
        var title: String? = null
        var desc: String? = null
    }

    class ClothesBean {
        var title: String? = null
        var desc: String? = null
    }

    class DyBean {
        var title: String? = null
        var desc: String? = null
    }

    class SportsBean {
        var title: String? = null
        var desc: String? = null
    }

    class XqBean {
        var title: String? = null
        var desc: String? = null
    }

    class TravelBean {
        var title: String? = null
        var desc: String? = null
    }

    class BeautyBean {
        var title: String? = null
        var desc: String? = null
    }

    class HcBean {
        var title: String? = null
        var desc: String? = null
    }

    class ColdBean {
        var title: String? = null
        var desc: String? = null
    }

    class GjBean {
        var title: String? = null
        var desc: String? = null
    }

    class ZsBean {
        var title: String? = null
        var desc: String? = null
    }

    class ComfortBean {
        var title: String? = null
        var desc: String? = null
    }

    class UvBean {
        var title: String? = null
        var desc: String? = null
    }

    class ClBean {
        var title: String? = null
        var desc: String? = null
    }

    class WashCarBean {
        var title: String? = null
        var desc: String? = null
    }

    class AcBean {
        var title: String? = null
        var desc: String? = null
    }

    class GlassBean {
        var title: String? = null
        var desc: String? = null
    }

    class AqiBean {
        var title: String? = null
        var desc: String? = null
    }

    class MfBean {
        var title: String? = null
        var desc: String? = null
    }

    class AgBean {
        var title: String? = null
        var desc: String? = null
    }

    class PjBean {
        var title: String? = null
        var desc: String? = null
    }

    class NlBean {
        var title: String? = null
        var desc: String? = null
    }

    class PkBean {
        var title: String? = null
        var desc: String? = null
    }
}
