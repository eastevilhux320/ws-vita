package ext

import java.math.BigDecimal
import java.math.RoundingMode

object BigDecimalExt {

    fun BigDecimal.scale(newScale : Int, roundingMode : RoundingMode = RoundingMode.HALF_UP): String {
        return this.setScale(newScale,roundingMode).toString();
    }

    fun String?.toBigDecimal(defaultBig : BigDecimal): BigDecimal {
        if(this == null){
            return defaultBig;
        }
        return BigDecimal(this);
    }

    /**
     * 判断[BigDecimal]是否为整数
     * create by Eastevil at 2023/11/1 10:19
     * @author Eastevil
     * @return
     *      BigDecimal是否为整数
     */
    fun BigDecimal.isIntegerValue(): Boolean {
        return this.stripTrailingZeros().scale() <= 0;
    }

    /**
     * 判断 BigDecimal 是否为整数
     * @author Eastevil
     * @createTime 2025/7/15 10:40
     * @param 
     * @since 
     * @see 
     * @return
     *      BigDecimal 是否为整数
     */
    fun BigDecimal.isInteger(): Boolean {
        return this.stripTrailingZeros().scale() <= 0
    }

    /**
     * 如果是整数则返回 Int，否则保留两位小数返回 BigDecimal
     * @author Eastevil
     * @createTime 2025/7/15 10:44
     * @param
     * @since
     * @see
     * @return
     */
    fun BigDecimal.formatSmart(): String {
        if(this.isInteger()){
            return this.toInt().toString();
        }else{
            return scale(2);
        }
    }

    /**
     * 通过调用[BigDecimal.setScale]方法转换[BigDecimal]为字符串类型
     * create by Eastevil at 2024/4/17 14:41
     * @author Eastevil
     * @param scale
     *      保留小数位
     * @return
     *
     */
    fun BigDecimal.scale(scale : Int): String {
        return this.setScale(scale,BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 将 BigDecimal 转换为带有符号的字符串，并保留指定小数位
     * @author Eastevil
     * @createTime 2025/7/15 10:50
     * @param scale
     * 保留小数位
     * @return
     * 如果 BigDecimal 小于 0，则返回 "-{scale(scale)的结果}"，否则返回 "{scale(scale)的结果}"
     * 例如: -1.234.withSymbol(2) -> "-1.23"; 1.234.withSymbol(2) -> "1.23"
     */
    fun BigDecimal.withSymbol(scale : Int): String {
        return if(this.compareTo(BigDecimal.ZERO)  < 0){
            "-${this.scale(scale)}";
        }else{
            this.scale(scale)
        }
    }
}
