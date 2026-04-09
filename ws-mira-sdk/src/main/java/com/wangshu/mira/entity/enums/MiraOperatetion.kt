package com.wangshu.mira.entity.enums

/**
 * 玄映SDK定义的用户操作枚举类
 * 10-初始化，20-建立连接，30-查看详情，40-申请，45-开始任务,50-提交，60-审核
 * * @author Eastevil
 * @version 1.0.0
 * @date 2026/3/18 11:38
 */
enum class MiraOperatetion(val operatetion: Int) {
    /** 10-初始化  */
    INITIALIZE(10),

    /** 20-建立连接  */
    CONNECT(20),

    /** 30-查看详情  */
    VIEW_DETAIL(30),

    /** 40-申请  */
    APPLY(40),

    /**
     * 开始任务
     */
    START(45),

    /** 50-提交  */
    SUBMIT(50),

    /** 60-审核  */
    AUDIT(60),

    /**
     * 取消任务
     */
    CANCEL(70),

    /**
     * 继续任务
     */
    CONTINUE_TASK(80);

    companion object {
        /**
         * 根据数值获取对应的操作枚举
         * 用于 AppResult 解密后，根据业务数值匹配具体操作逻辑
         *
         * @param value 对应 operatetion 的数值
         * @return 匹配的枚举实例，若未匹配返回 null
         */
        fun from(value: Int): MiraOperatetion? {
            for (type in values()) {
                if (type.operatetion == value) {
                    return type
                }
            }
            return null
        }
    }
}
