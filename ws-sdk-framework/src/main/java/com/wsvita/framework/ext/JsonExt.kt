package com.wsvita.framework.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wsvita.framework.utils.JsonUtil

/**
 * JSON 转换扩展功能库。
 * 提供 Gson 与 Fastjson2 两种实现，区分命名以适配组件化项目中的不同场景。
 * 针对组件化项目优化：解决泛型擦除问题，支持复杂的 Bean 与 List 互转。
 * Create by Eastevil at 2025/12/24 10:18
 * @author Eastevil
 */
object JsonExt {

    // --- Gson 实现部分 ---

    /**
     * Description: String 扩展函数，将 JSON 字符串解析为指定的泛型对象 (Gson 版)
     * create by Eastevil at 2025/12/24 10:45
     * @author Eastevil
     * @return T?
     *      解析后的对象实例
     */
    inline fun <reified T> String.parseGson(): T = JsonUtil.getInstance().getGson().parseJSON(this)

    /**
     * Description: Gson 实例扩展函数，通过 TypeToken 保留泛型信息进行解析
     * create by Eastevil at 2025/12/24 10:45
     * @author Eastevil
     * @param json
     *      待解析的 JSON 字符串
     * @return T?
     *      解析后的对象实例
     */
    inline fun <reified T> Gson.parseJSON(json: String?): T {
        return this.fromJson<T>(json, object : TypeToken<T>() {}.type)
    }

}
