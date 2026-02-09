package com.wsvita.account.entity

/**
 * 账号信息对外暴露契约接口
 *
 * 遵循组件化隔离原则，隐藏 [Account] 实体类的敏感实现，
 * 仅对外提供业务逻辑所需的只读数据方法。
 */
interface IAccount {
    fun getAccountId(): Long
    fun getHeaderIcon(): String?
    fun getHeaderBackgroundIcon(): String?
    fun getUserNoText(): String?
    fun getNickname(): String?
    fun getUserType(): Int
    fun getMobileNo(): String? // 原始手机号（根据业务决定是否保留）
    fun getMaskedMobileNo(): String?
    fun isCertified(): Boolean
}
