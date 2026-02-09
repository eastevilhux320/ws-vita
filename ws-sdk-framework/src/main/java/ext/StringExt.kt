package ext

import android.graphics.Color

object StringExt {

    /**
     * 判断字符串是否为无效数据。
     * 该方法不仅检查 Kotlin 原生的 null 和空字符串，
     * 还针对业务中常见的 "null" 字符串及全空格字符串进行了拦截。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return
     *      true-表示字符串为 null、空、仅包含空格或等于 "null"(不区分大小写)；否则返回 false。
     */
    fun String?.isInvalid(): Boolean {
        // 1. 处理 Kotlin 层的 null
        if (this == null) {
            return true
        }
        // 2. 去掉首尾空格后判断是否为空
        // 这一步代替了 isEmpty()，能识别 "  " 这种情况
        val trimmed = this.trim()
        if (trimmed.isEmpty()) {
            return true
        }
        // 3. 忽略大小写判断是否为 "null" 字符串
        // 使用 lowercase() 替代过时的 toLowerCase()
        if ("null".equals(trimmed.lowercase())) {
            return true
        }
        return false
    }

    /**
     * 判断字符串是否为有效数据。
     * 只有当字符串不为 null、非空字符、且排除掉纯空格及 "null" 字符串（不区分大小写）时，才判定为有效。
     * create by Administrator at 2025/12/28 1:07
     * @author Administrator
     * @return
     *      true-表示字符串包含实际业务内容；false-表示字符串为 null、全空格或特定无效占位符。
     */
    fun String?.isNotInvalid(): Boolean {
        return !this.isInvalid();
    }

    /**
     * 将十六进制颜色字符串解析为颜色整数（Color Int）。
     * * @receiver 颜色字符串，支持格式如: "#RRGGBB", "#AARRGGBB", "red", "blue" 等。
     * * 示例:
     * "#FF0000".parseColor() // 返回 Color.RED
     * create by Administrator at 2026/1/7 0:02
     * @author Administrator
     *
     * @throws IllegalArgumentException 如果字符串格式不正确，将抛出异常。
     * @return
     *      对应的颜色整数值（@ColorInt）。
     */
    fun String.parseColor(): Int {
        return Color.parseColor(this);
    }

    /**
     * 字符串脱敏扩展函数：将手机号转换为 "123 **** 8888" 格式
     * * 核心逻辑：
     * 1. 验证：检查字符串是否为空或长度是否足以进行脱敏。
     * 2. 切片：保留前 3 位和后 4 位。
     * 3. 组装：使用中间占位符重新拼接。
     *
     * @return 格式化后的字符串。
     * - 如果为 null 或 Empty，返回空字符串 ""
     * - 如果长度不足 7 位（无法满足前3后4），则原样返回，避免截断错误
     */
    fun String?.mobileAsterisk(): String {
        // 1. 判空处理：如果原始字符串为 null 或为空，直接返回空字符串，避免后续逻辑异常
        if (this.isNullOrEmpty()) {
            return ""
        }

        // 2. 边界检查：如果长度不足 7 位（无法保证前3后4的结构），
        // 则不进行脱敏处理，直接返回原字符串，防止 take/takeLast 函数逻辑冲突
        if (this.length < 7) {
            return this
        }

        // 3. 结构化处理：
        // take(3): 提取前 3 位数字（通常是区号或号段）
        // takeLast(4): 提取后 4 位数字（通常是用户尾号）
        val prefix = this.take(3)
        val suffix = this.takeLast(4)

        // 4. 使用字符串模板进行拼接，保持代码整洁
        return "$prefix **** $suffix"
    }

    /**
     * 判断字符串是否为纯数字（不包含小数点、负号或空格）
     * * 核心逻辑：
     * 1. 验证：调用 isInvalid 拦截 null、空串、全空格及 "null" 字符串。
     * 2. 匹配：使用正则表达式验证字符串是否完全由 0-9 的数字组成。
     *
     *
     * create by Eastevil at 2026/1/27 10:00
     * @author Eastevil
     * @return 校验结果
     * - true：字符串仅包含 0-9 的数字（如 "123456"）。
     * - false：包含小数点、负号、字母或处于 isInvalid 状态（如 "11.22"、"-1"、"a123"）。
     */
    fun String?.isNumber(): Boolean {
        // 复用你之前写的判空逻辑
        if (this.isInvalid()) {
            return false
        }
        // ^\d+$ 确保从头到尾每一位都是 0-9 的数字
        return this!!.matches(Regex("^\\d+$"))
    }
}
