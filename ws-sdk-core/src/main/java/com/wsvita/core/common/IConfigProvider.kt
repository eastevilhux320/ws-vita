package com.wsvita.core.common

interface IConfigProvider {

    fun getString(key: String): String?
    fun getInt(key: String, defaultValue: Int): Int
    fun getLong(key: String, defaultValue: Long): Long
}
