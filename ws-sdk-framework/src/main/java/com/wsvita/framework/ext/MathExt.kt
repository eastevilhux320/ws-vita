package com.wsvita.framework.ext

import kotlin.random.Random

object MathExt {

    /**
     * 生成 [min, max] 范围内的随机整数（包含边界值）
     * create by Eastevil at 2025/12/26 15:31
     * @param min
     *      最小值（默认 0）
     * @param max
     *      最大值（默认 10）
     * @author Eastevil
     * @return
     *      范围内的随机数
     */
    fun randomNumber(min: Int = 0, max: Int = 10): Int {
        // 参数防错：如果 min 大于 max，交换它们或返回 min
        if (min >= max) return min

        // 方案 1：使用 Kotlin 标准库的区间函数 (推荐，语义清晰)
        // (min..max).random() 会返回 [min, max] 之间的值
        return (min..max).random()
    }

    /**
     * 生成指定范围内的随机浮点数
     */
    fun randomDouble(min: Double, max: Double): Double {
        if (min >= max) return min
        return Random.nextDouble(min, max)
    }
}
