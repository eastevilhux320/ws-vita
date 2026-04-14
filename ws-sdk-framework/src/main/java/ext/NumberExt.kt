package ext

import java.text.DecimalFormat

object NumberExt {

    /**
     * Float类型格式化，接收一个位数参数，表示要保留的小数位数
     * create by Administrator at 2023/6/13 21:50
     * @author Administrator
     * @param decimalPlaces
     *      保留的小数位数
     * @return
     *      格式化后的字符串
     */
    fun Float.formatDecimalPlaces(decimalPlaces: Int): String {
        if(0F == this){
            return "0.00";
        }
        val pattern = StringBuilder("#.")
        repeat(decimalPlaces) {
            pattern.append("0")
        }
        val decimalFormat = DecimalFormat(pattern.toString())
        return decimalFormat.format(this)
    }

}
