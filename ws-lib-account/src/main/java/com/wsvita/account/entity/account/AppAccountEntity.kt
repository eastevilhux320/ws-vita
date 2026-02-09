package com.wsvita.account.entity.account


import java.math.BigDecimal

/**
 * 应用层账号实体：在这里实现具体的业务逻辑
 */
open class AppAccountEntity : BaseAccountEntity() {

    /** 官职：用于判断认证状态或显示特殊标识 */
    var officialPosition: String? = null
        internal set

    /** 等级：用于映射 UserType */
    var level: Int = 1
        internal set

    var description: String? = null

    var totalFollowers: Long = 0
        internal set

    var totalAttentions: Long = 0
        internal set

    var birthday: Long = 0
        internal set

    var userHeight: BigDecimal = BigDecimal.ZERO
        internal set

    var userWeight: BigDecimal = BigDecimal.ZERO

    /** 动态页个人签名 */
    var momentLag: String? = null
        internal set

    /** 主页背景图字段（假设后端字段名为 backgroundUrl） */
    var backgroundUrl: String? = null
        internal set

    // ===========================================================================
    // 核心实现：补全 IAccount 接口中 Base 层无法确定的业务逻辑
    // ===========================================================================

    /**
     * 1. 实现用户类型映射
     * 逻辑：根据 level 映射为 1-普通, 2-VIP, 3-SVIP
     */
    override fun getUserType(): Int {
        return when {
            level >= 10 -> 3 // SVIP
            level >= 5 -> 2  // VIP
            else -> 1        // 普通
        }
    }

    override fun getMobileNo(): String? {
        return mobile;
    }

    /**
     * 2. 实现认证逻辑
     * 逻辑：如果有官职或者等级达到一定程度，视为已认证
     */
    override fun isCertified(): Boolean {
        return !officialPosition.isNullOrEmpty() || level >= 2
    }

    /**
     * 3. 实现用户编号展示文本
     * 逻辑：将原始 ID 或 userNo 格式化为 UI 显示的文本
     */
    override fun getUserNoText(): String? {
        val no = userNo ?: getAccountId().toString()
        return "ID: $no"
    }

    /**
     * 4. 实现背景图获取
     */
    override fun getHeaderBackgroundIcon(): String? {
        return backgroundUrl
    }

    // ===========================================================================
    // 敏感数据重写（如果需要根据 App 逻辑特殊处理）
    // ===========================================================================

    /**
     * 如果 App 层有特殊的昵称显示规则（例如：等级达到 10 级显示金色昵称标识等）
     * 可以在这里重写 Base 层的实现
     */
    override fun getNickname(): String? {
        val rawName = super.getNickname()
        return if (level >= 10) "[尊贵]$rawName" else rawName
    }
}
