package com.wsvita.account.entity.account

import com.wsvita.account.entity.account.impl.AccountImpl
import ext.StringExt.mobileAsterisk

abstract class BaseAccountEntity : AccountImpl() {

    /**
     * 用户唯一 ID
     * 设为 public 确保 Gson/FastJson 能解析。
     * 使用 internal set：确保只有在账号模块内部（如网络层解析后注入）可以修改。
     */
    var id: Long = 0
        internal set

    /**
     * 原始手机号
     * 同样使用 internal set。外部模块只能看到，但无法在代码中调用 setMobile(...)。
     */
    var mobile: String? = null
        internal set

    /**
     * 敏感信息：AES 密钥
     * 这种字段甚至可以不放在接口 IAccount 中。
     */
    var aeskey: String? = null
        protected set // 仅允许子类和内部修改

    var state: Int = 0
        internal set

    // --- 非敏感信息，允许子类随意操作 ---
    var nickName: String? = null
    var userIcon: String? = null
    var userNo: String? = null

    // --- 实现 IAccount 接口的只读方法 ---
    override fun getAccountId(): Long = id
    override fun getNickname(): String? = nickName
    override fun getMaskedMobileNo(): String? = mobile?.mobileAsterisk()
    override fun getHeaderIcon(): String? = userIcon
}
