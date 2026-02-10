package ext

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import com.wsvita.framework.local.WsContext
import ext.TimeExt.format

object ViewExt {

    /**
     * 获取屏幕分辨率扩展方法 (Pair 包装)
     * create by Eastevil at 2025/12/22 15:08
     * @author Eastevil
     * @return Pair<Int, Int> first: 宽度(px), second: 高度(px)
     */
    fun Context.getScreenPair(): Pair<Int, Int> {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+ 推荐做法
            val metrics = wm.currentWindowMetrics
            val bounds = metrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            // API 30 以下兼容做法
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }

        return Pair(width, height)
    }

    /**
     * 获取屏幕分辨率扩展方法
     * create by Eastevil at 2025/12/22 15:05
     * @author Eastevil
     * @return Array<IntArray> 索引说明：[0][0] 宽度(px), [0][1] 高度(px)
     */
    fun Context.getScreenSize(): Array<Int> {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 获取方式
            val metrics = wm.currentWindowMetrics
            val bounds = metrics.bounds
            width = bounds.width()
            height = bounds.height()
        } else {
            // Android 11 以下获取方式
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }

        // 返回一个包含宽和高的 Int 数组
        return arrayOf(width, height)
    }

    /**
     * DP 转换为 PX
     * create by Eastevil at 2025/12/22 15:10
     * @author Eastevil
     * @return Int 转换后的像素值
     */
    fun Int.dip2px(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    /**
     * SP 转换为 PX (推荐使用官方标准 API 实现)
     * create by Eastevil at 2025/12/22 15:10
     * @author Eastevil
     * @return Int 转换后的像素值
     */
    fun Int.sp2px(): Int {
        // 使用 TypedValue 转换更符合 Android 官方标准，且精度更高
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    /**
     * PX 转换为 DP
     * create by Eastevil at 2025/12/22 15:10
     * @author Eastevil
     * @return Int 转换后的 DP 值
     */
    fun Int.px2dip(): Int {
        val scale = Resources.getSystem().displayMetrics.density
        // 修正：除法运算应先转为 float 避免精度丢失
        return (this.toFloat() / scale + 0.5f).toInt()
    }

    /**
     * 动态配置 GradientDrawable 的核心工厂方法。
     *
     * 该方法封装了 [GradientDrawable] 的创建逻辑，支持设置填充色、圆角半径（统一圆角或特定四角）以及边框样式。
     * 在组件化开发中，此函数作为基础能力层提供给 UI 组件使用，以减少 XML 背景文件的创建。
     *
     * @param color
     *      背景填充颜色，使用 @ColorInt 定义，默认为白色。
     * @param radius
     *      统一圆角半径（单位：dp）。如果 [radii] 不为空，则此参数将被忽略。
     * @param radii 特定圆角半径数组。长度必须为 8，包含 4 个角的 (X, Y) 坐标对：
     *      [左上X, 左上Y, 右上X, 右上Y, 右下X, 右下Y, 左下X, 左下Y]。单位：dp。
     * @param strokeWidth
     *      边框宽度（单位：dp），默认为 0（无边框）。
     * @param strokeColor
     *      边框颜色，使用 @ColorInt 定义，默认为透明。
     * @param context
     *      运行上下文，用于将 dp 转换为 px。
     * @return 配置完成的 [GradientDrawable] 对象。
     */
    fun createRectangle(
        @ColorInt color: Int = Color.WHITE,
        @Dimension(unit = Dimension.DP) radius: Float = 0f,
        radii: FloatArray? = null,
        @Dimension(unit = Dimension.DP) strokeWidth: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT,
        context: Context
    ): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(color)

        // 处理圆角逻辑：优先判断 radii 数组，其次判断单一 radius
        if (radii != null && radii.size == 8) {
            val pxRadii = FloatArray(8) { i -> dpToPx(radii[i], context) }
            cornerRadii = pxRadii
        } else if (radius > 0f) {
            cornerRadius = dpToPx(radius, context)
        }

        // 处理边框逻辑
        if (strokeWidth > 0f) {
            setStroke(dpToPx(strokeWidth, context).toInt(), strokeColor)
        }
    }

    /**
     * [Context] 的扩展函数，用于快速创建基础矩形背景。
     *
     * 适用于具有相同圆角半径的简单矩形背景需求。
     *
     * @param bgColor 背景颜色，默认为白色。
     * @param radius 圆角半径（单位：dp），默认为 0。
     * @param strokeDp 边框宽度（单位：dp），默认为 0。
     * @param strokeColor 边框颜色，默认为透明。
     * @return 初始化的 [GradientDrawable] 矩形。
     */
    fun Context.createRectDrawable(
        @ColorInt bgColor: Int = Color.WHITE,
        @Dimension(unit = Dimension.DP) radius: Float = 0f,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ) = createRectangle(bgColor, radius, null, strokeDp, strokeColor, this)

    /**
     * [Context] 的扩展函数，用于创建四个角半径各异的矩形背景。
     *
     * 常用于卡片顶部圆角、标签背景等特定 UI 场景。
     *
     * @param bgColor 背景颜色，默认为白色。
     * @param lt 左上角圆角半径 (Left-Top)（单位：dp）。
     * @param rt 右上角圆角半径 (Right-Top)（单位：dp）。
     * @param rb 右下角圆角半径 (Right-Bottom)（单位：dp）。
     * @param lb 左下角圆角半径 (Left-Bottom)（单位：dp）。
     * @param strokeDp 边框宽度（单位：dp），默认为 0。
     * @param strokeColor 边框颜色，默认为透明。
     * @return 初始化的 [GradientDrawable] 矩形。
     */
    fun Context.createComplexRectDrawable(
        @ColorInt bgColor: Int = Color.WHITE,
        @Dimension(unit = Dimension.DP) lt: Float = 0f,
        @Dimension(unit = Dimension.DP) rt: Float = 0f,
        @Dimension(unit = Dimension.DP) rb: Float = 0f,
        @Dimension(unit = Dimension.DP) lb: Float = 0f,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        val radii = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
        return createRectangle(bgColor, 0f, radii, strokeDp, strokeColor, this)
    }

    /**
     * 创建一个带圆角、背景色和边框的矩形 Drawable。
     *
     * 常用于在代码中动态生成 View 的背景，替代繁琐的 shape.xml 文件。
     *
     * @receiver [Context] 上下文环境。
     * @param bgColor 矩形的背景颜色，默认为白色。
     * @param stroke 边框宽度（单位：像素 px），默认为 0（无边框）。
     * @param strokeColor 边框颜色，默认为黑色。
     * @param radius 圆角半径（单位：像素 px）。
     * @return 配置好的 [GradientDrawable] 对象。
     *
     * 示例：
     * view.background = context.createRectangleShape(
     * bgColor = Color.BLUE,
     * radius = 16f.dpToPx() // 建议配合 dp 转 px 使用
     * )
     */
    fun Context.createComplexRectDrawable(bgColor : Int = Color.WHITE,stroke : Int = 0,
                                     strokeColor : Int = Color.BLACK,radius : Float): GradientDrawable {
        val drawable = GradientDrawable();
        drawable.setColor(bgColor);
        drawable.shape = GradientDrawable.RECTANGLE;
        if(stroke > 0){
            drawable.setStroke(stroke,strokeColor);
        }
        drawable.setCornerRadius(radius);
        return drawable;
    }

    /**
     * [Int] 色值的扩展函数：将颜色值转换为具有特定圆角的矩形 [GradientDrawable]。
     *
     * 此方法允许直接在颜色值上调用，快速生成 UI 元素的背景。
     * 常用于动态设置卡片、按钮等组件的背景色和圆角。
     *
     * @receiver 背景颜色值，使用 @ColorInt 定义。
     * @param lt 左上角圆角半径 (Left-Top) (单位: dp)。
     * @param rt 右上角圆角半径 (Right-Top) (单位: dp)。
     * @param rb 右下角圆角半径 (Right-Bottom) (单位: dp)。
     * @param lb 左下角圆角半径 (Left-Bottom) (单位: dp)。
     * @param strokeDp 边框宽度 (单位: dp)，默认为 0（无边框）。
     * @param strokeColor 边框颜色，使用 @ColorInt 定义，默认为透明。
     * @return 配置完成的 [GradientDrawable] 对象。
     *
     * 调用示例：
     * ```
     * val bg = Color.RED.toComplexRectDrawable(lt = 12f, rt = 12f)
     * view.background = bg
     * ```
     */
    fun Int.createComplexRectDrawable(
        @Dimension(unit = Dimension.DP) lt: Float = 0f,
        @Dimension(unit = Dimension.DP) rt: Float = 0f,
        @Dimension(unit = Dimension.DP) rb: Float = 0f,
        @Dimension(unit = Dimension.DP) lb: Float = 0f,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        // 构造原生 GradientDrawable 要求的 radii 数组 (4个角，每个角包含 X, Y 两个半径)
        val radii = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)

        // 调用核心工厂方法
        // 注意：建议这里确保 WsContext.context 已初始化，或考虑在此处增加 Context 参数
        return createRectangle(
            color = this,
            radius = 0f,
            radii = radii,
            strokeWidth = strokeDp,
            strokeColor = strokeColor,
            context = WsContext.context
        )
    }

    /**
     * [Context] 的扩展函数，用于创建圆形或椭圆形背景。
     *
     * 如果宿主 View 是正方形，则生成圆形；如果是长方形，则生成椭圆形。
     *
     * @param bgColor 背景颜色，默认为白色。
     * @param strokeDp 边框宽度（单位：dp），默认为 0。
     * @param strokeColor 边框颜色，默认为透明。
     * @return 初始化的 [GradientDrawable] 椭圆。
     */
    fun Context.createOvalDrawable(
        @ColorInt bgColor: Int = Color.WHITE,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(bgColor)
        if (strokeDp > 0f) {
            val strokePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                strokeDp,
                resources.displayMetrics
            ).toInt()
            setStroke(strokePx, strokeColor)
        }
    }

    /**
     * [Context] 的扩展函数：快速创建一个带边框和圆角的背景。
     * 常用于搜索框、表单输入框或辅助按钮。
     * * @param strokeColor 边框颜色
     * @param radius 圆角半径 (单位: dp)
     * @param strokeWidthDp 边框宽度 (单位: dp)，默认为 1dp
     * @param bgColor 背景填充色，默认为透明
     */
    fun Context.createStrokeDrawable(
        @ColorInt strokeColor: Int,
        @Dimension(unit = Dimension.DP) radius: Float,
        @Dimension(unit = Dimension.DP) strokeWidthDp: Float = 1f,
        @ColorInt bgColor: Int = Color.TRANSPARENT
    ): GradientDrawable = createRectangle(
        color = bgColor,
        radius = radius,
        strokeWidth = strokeWidthDp,
        strokeColor = strokeColor,
        context = this
    )

    /**
     * [Int] 色值的扩展函数：将颜色值转换为具有统一圆角的矩形 [GradientDrawable]。
     *
     * @receiver 背景颜色值。
     * @param radius 统一圆角半径 (单位: dp)。
     * @param strokeDp 边框宽度 (单位: dp)。
     * @param strokeColor 边框颜色。
     */
    fun Int.createComplexRectDrawable(
        @Dimension(unit = Dimension.DP) radius: Float = 0f,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        return createRectangle(
            color = this,
            radius = radius,
            radii = null,
            strokeWidth = strokeDp,
            strokeColor = strokeColor,
            context = WsContext.context
        )
    }

    /**
     * [Int] 色值的扩展函数：将颜色值转换为带水波纹点击反馈的矩形背景。
     *
     * 该方法结合了 [createRectangle] 和 [RippleDrawable]，生成一个在正常状态显示 [this] 颜色，
     * 在点击状态显示水波纹效果的背景。
     *
     * @receiver 基础背景颜色 (ThemeColor)。
     * @param radius 统一圆角半径 (单位: dp)。
     * @param rippleColor 水波纹颜色。如果不传，默认在主题色基础上叠加 20% 的黑色（变深）或白色（变浅）。
     * @param strokeDp 边框宽度 (单位: dp)。
     * @param strokeColor 边框颜色。
     * @return 一个 [RippleDrawable] 实例，可直接设置为 View 的 background。
     */
    fun Int.toRippleDrawable(
        @Dimension(unit = Dimension.DP) radius: Float = 0f,
        @ColorInt rippleColor: Int? = null,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): Drawable {
        // 1. 创建正常状态下的背景 (使用你之前的逻辑)
        val contentDrawable = this.toRectDrawable(radius, strokeDp, strokeColor)

        // 2. 确定水波纹颜色：如果没有指定，则根据主题色自动计算一个压感色
        val finalRippleColor = rippleColor ?: getDarkerColor(this)

        // 3. 构建 RippleDrawable
        // 参数1：ColorStateList 决定波纹颜色
        // 参数2：content 正常显示的 Drawable
        // 参数3：mask 决定波纹范围的范围（通常和 content 一致，确保波纹不超出圆角范围）
        return RippleDrawable(
            ColorStateList.valueOf(finalRippleColor),
            contentDrawable,
            this.toRectDrawable(radius) // mask 需要同样的圆角，否则点击时波纹会溢出成直角
        )
    }

    /**
     * [Int] 色值的扩展函数：将颜色值转换为具有统一圆角的矩形 [GradientDrawable]。
     *
     * @receiver 背景颜色值。
     * @param radius 统一圆角半径 (单位: dp)。
     * @param strokeDp 边框宽度 (单位: dp)。
     * @param strokeColor 边框颜色。
     */
    fun Int.toRectDrawable(
        @Dimension(unit = Dimension.DP) radius: Float = 0f,
        @Dimension(unit = Dimension.DP) strokeDp: Float = 0f,
        @ColorInt strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        return createRectangle(
            color = this,
            radius = radius,
            radii = null,
            strokeWidth = strokeDp,
            strokeColor = strokeColor,
            context = WsContext.context
        )
    }

    /**
     * **Context 资源检索扩展**
     * * 获取当前 View 上下文绑定的字符串资源。
     * * **注意：** * 由于框架在 BaseActivity 中拦截了 attachBaseContext，此方法返回的字符串
     * 会自动遵循 [com.wsvita.framework.local.manager.LanguageManager] 当前设置的语言。
     *
     * @param resId 字符串资源 ID (R.string.xxx)
     * @return 对应多语言环境下的字符串内容
     */
    fun View.getString(resId : Int): String {
        return this.context.getString(resId);
    }

    /**
     * **Context 资源检索扩展 (支持占位符格式化)**
     * 获取当前 View 上下文绑定的格式化字符串资源。
     *
     * @param resId 字符串资源 ID (R.string.xxx)。
     * @param formatArgs 动态参数，用于替换字符串中的占位符（如 %s, %d）。
     * @return 经过 [com.wsvita.framework.local.manager.LanguageManager] 语言环境处理并格式化后的字符串。
     */
    fun View.getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return this.context.getString(resId, *formatArgs)
    }

    /**
     * **TextView 时间显示扩展**
     * * 将时间戳格式化后直接设置到当前 [TextView]。
     * 内部通过 [Long.format] 扩展实现，确保格式化逻辑遵循 [com.wsvita.framework.local.manager.LanguageManager]
     * 设定的全局语言环境（Locale），保证年月日、时分秒的符号显示符合国际化要求。
     *
     * @param time 毫秒级时间戳。
     * @param format 格式化模式串，如 "yyyy-MM-dd HH:mm:ss"。
     */
    fun TextView.showTime(time : Long,format : String){
        val s = time.format(format);
        this.setText(s);
    }

    /**
     * **TextView 时分秒显示扩展**
     * * 将毫秒级时间戳格式化为固定的“时:分:秒”格式 ([HH:mm:ss]) 并设置到当前 [TextView]。
     * * **业务场景：**
     * 1. 适用于播放器进度显示、打卡时刻标记、或仅需展示精确时间点而不包含日期的 UI 组件。
     * 2. 内部依赖 [Long.format] 扩展，其格式化过程会自动适配 [com.wsvita.framework.local.manager.LanguageManager]
     * 中设定的 Locale 环境，确保数字符号符合国际化标准。
     * * @param time 毫秒级时间戳。
     */
    fun TextView.showTime(time : Long){
        val s = time.format("HH:mm:ss");
        this.setText(s);
    }

    /**
     * **TextView 时间显示扩展 (支持资源模板)**
     * * 从资源文件中获取格式化字符串模板，并将时间戳格式化后显示。
     * * **业务场景：**
     * 适用于 XML 中定义的动态时间格式，例如：R.string.date_format = "yyyy/MM/dd"。
     * 这样可以根据 [LanguageManager] 切换不同的 strings.xml 从而实现不同语言下完全不同的日期排版。
     *
     * @param time 毫秒级时间戳。
     * @param resId 字符串资源 ID，其内容应为合法的 SimpleDateFormat 模式串。
     */
    fun TextView.showTime(time : Long,resId: Int){
        val format = this.getString(resId);
        showTime(time,format);
    }
    /**
     * **TextView 时间显示扩展 (支持动态占位符模板)**
     * * 1. 先通过 [resId] 获取带占位符的字符串资源（如："最后更新于 %s"）。
     * * 2. 使用 [formatArgs] 填充占位符得到最终的时间模式串。
     * * 3. 将 [time] 按照该模式串格式化后设置到 [TextView]。
     *
     * **业务场景：**
     * 适用于需要动态拼接时间格式的国际化场景。例如不同语言下“发表于”和“更新于”的时间排版逻辑不一致时，
     * 可以通过 strings.xml 动态配置整个显示模式。
     *
     * @param time 毫秒级时间戳。
     * @param resId 带占位符的字符串资源 ID。
     * @param formatArgs 动态填充占位符的参数列表。
     */
    fun TextView.showTime(time: Long,resId: Int,vararg formatArgs: Any?){
        val format = this.getString(resId,formatArgs);
        showTime(time,format);
    }


    /**
     * 辅助函数：计算颜色的深色版本，用于默认的点击反馈
     */
    @ColorInt
    private fun getDarkerColor(@ColorInt color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.8f // 亮度降低 20%
        return Color.HSVToColor(hsv)
    }

    private fun dpToPx(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }
}

