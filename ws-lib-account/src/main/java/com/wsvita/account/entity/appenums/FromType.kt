package com.wsvita.account.entity.appenums

enum class FromType {
    UNKNOWN(0),
    SPLASH(1),
    MAIN(2),
    NORMAL(3);

    private var type : Int;

    private constructor(type: Int){
        this.type = type;
    }

    fun getType(): Int {
        return type;
    }

    /**
     * Companion object 相当于 Java 的静态方法块
     */
    companion object {
        /**
         * 显式地根据 type 值查找对应的枚举
         */
        fun from(type: Int): FromType {
            return values().find { it.type == type } ?: UNKNOWN
        }
    }
}
