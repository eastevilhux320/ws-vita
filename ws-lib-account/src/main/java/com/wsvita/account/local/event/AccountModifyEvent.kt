package com.wsvita.account.local.event

import com.wsvita.account.entity.IAccount
import com.wsvita.account.local.AccountEvent

class AccountModifyEvent : AccountEvent {

    /** 内部持有的账号信息实例 */
    private var account : IAccount;

    /**
     * 显式构造方法
     * @param account 登录成功的账号实体
     */
    constructor(account : IAccount){
        this.account = account;
    }

    /**
     * 获取当前事件携带的账号数据
     * @return 实现 IAccount 契约的实体对象
     */
    fun getAccount() : IAccount {
        return this.account;
    }
}
