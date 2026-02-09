package com.wsvita.account.accountup

import com.wsvita.core.common.IConfigProvider

/**
 * 账号模块特有的配置提供者
 */
interface IAccountConfigProvider : IConfigProvider {
    // 可以在此扩展账号特有的方法，如获取当前的 UID 等
}
