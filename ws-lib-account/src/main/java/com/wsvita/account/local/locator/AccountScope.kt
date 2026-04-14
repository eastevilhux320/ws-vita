package com.wsvita.account.local.locator

/**
 * Account 组件定义的全局唯一 Scope 协议。
 * * 【重要规范】：
 * 1. 只有在此定义的常量才会被 ws-lib-account 组件识别和分发。
 * 2. App 或其他组件可以定义私有 Tag，但若未在此备案，ws-lib-account 系统将不予执行。
 * 3. 命名建议：vita_ac_scope_xxxx
 *
 * create by Eastevil at 2026/4/13 16:46
 * @author Eastevil
 */
object AccountScope {
    const val AC_SCOPE_LOGIN = "vita_ac_scope_login"
    const val AC_SCOPE_LOGOUT = "vita_ac_scope_logout"
    const val AC_SCOPE_TOKEN = "vita_ac_scope_token"
    const val AC_SCOPE_ALL = "vita_ac_scope_all"
    const val AC_SCOPE_UPDATE = "vita_ac_scope_update";
}
