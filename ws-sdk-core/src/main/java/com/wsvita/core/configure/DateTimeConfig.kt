package com.wsvita.core.configure

import com.wsvita.framework.commons.BaseConfig
import com.wsvita.framework.commons.BaseConfigBuilder

/**
 * Description: 日期时间组件配置类，用于管理日历展示、时分秒进制、可选范围以及轮显行为。
 * * create by Eastevil at 2025/12/30 10:27
 * @author Eastevil
 */
class DateTimeConfig : BaseConfig {

    /**
     * 是否展示时分秒的选择
     */
    val isShowTime: Boolean

    /**
     * 小时进制：12还是24
     */
    val hourMode: Int

    /**
     * 能否选择当前时间之前的时间
     */
    val canSelectBefore: Boolean

    /**
     * 能否选择当前时间之后的时间
     */
    val canSelectAfter: Boolean

    /**
     * 精度控制：1-到时, 2-到分, 3-到秒
     */
    val precision: Int

    /**
     * 最小日期锚点（时间戳）
     */
    val minDate: Long

    /**
     * 最大日期锚点（时间戳）
     */
    val maxDate: Long

    private constructor(builder: Builder) : super() {
        this.isShowTime = builder.isShowTime
        this.hourMode = builder.hourMode
        this.canSelectBefore = builder.canSelectBefore
        this.canSelectAfter = builder.canSelectAfter
        this.precision = builder.precision
        this.minDate = builder.minDate
        this.maxDate = builder.maxDate
    }

    class Builder(appId: Long) : BaseConfigBuilder<DateTimeConfig>(appId) {

        internal var isShowTime: Boolean = true
        internal var hourMode: Int = 24
        internal var canSelectBefore: Boolean = true
        internal var canSelectAfter: Boolean = true
        internal var precision: Int = 3
        internal var minDate: Long = -1L
        internal var maxDate: Long = -1L

        /**
         * Description: 设置是否展示时分秒选择器。如果设为 false，则组件仅展示日期日历部分。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param isShowTime 是否展示
         * @return Builder 实例
         */
        fun setShowTime(isShowTime: Boolean): Builder {
            this.isShowTime = isShowTime
            return this
        }

        /**
         * Description: 设置小时的进制模式。通常可选 12 或 24。
         * 注意：设置为 12 时，数据源需配合 AM/PM 逻辑处理。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param hourMode 进制数 (12或24)
         * @return Builder 实例
         */
        fun setHourMode(hourMode: Int): Builder {
            this.hourMode = hourMode
            return this
        }

        /**
         * Description: 设置能否选择当前系统时间之前的时间。
         * 常用于约束如“预约日期”等业务场景，防止用户选择过去的时间。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param canSelectBefore 是否可选
         * @return Builder 实例
         */
        fun setCanSelectBefore(canSelectBefore: Boolean): Builder {
            this.canSelectBefore = canSelectBefore
            return this
        }

        /**
         * Description: 设置能否选择当前系统时间之后的时间。
         * 常用于约束如“出生日期”等业务场景，防止用户选择未来的时间。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param canSelectAfter 是否可选
         * @return Builder 实例
         */
        fun setCanSelectAfter(canSelectAfter: Boolean): Builder {
            this.canSelectAfter = canSelectAfter
            return this
        }

        /**
         * Description: 设置时间选择的精度。
         * 1 代表只选到小时，2 代表选到分钟，3 代表选到秒。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param precision 精度级别
         * @return Builder 实例
         */
        fun setPrecision(precision: Int): Builder {
            this.precision = precision
            return this
        }

        /**
         * Description: 设置可选日期的最小边界。
         * 传入时间戳，日历和滚轮将无法选中早于此日期的值。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param minDate 最小日期时间戳
         * @return Builder 实例
         */
        fun setMinDate(minDate: Long): Builder {
            this.minDate = minDate
            return this
        }

        /**
         * Description: 设置可选日期的最大边界。
         * 传入时间戳，日历和滚轮将无法选中晚于此日期的值。
         *
         * create by Eastevil at 2025/12/30 10:27
         * @author Eastevil
         * @param maxDate 最大日期时间戳
         * @return Builder 实例
         */
        fun setMaxDate(maxDate: Long): Builder {
            this.maxDate = maxDate
            return this
        }

        override fun builder(): DateTimeConfig {
            return DateTimeConfig(this)
        }
    }
}
