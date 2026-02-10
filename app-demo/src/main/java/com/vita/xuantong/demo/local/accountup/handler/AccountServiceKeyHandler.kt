package com.wsvita.app.local.accountup.handler

import com.wsvita.account.accountup.BaseAccountHandlerImpl
import com.wsvita.account.accountup.IAccountConfigProvider

/**
 * 专门处理 Service Key 修改的节点
 */
class AccountServiceKeyHandler : BaseAccountHandlerImpl() {
    companion object{
        private const val TAG = "Mirror_Accountup_ServiceKey=>";
    }

    override fun onHandle(provider: IAccountConfigProvider) {
        val secretKey = provider.getString("wsui_service_key")
        // 这里可以调用你的 SecurityManager 或其他加密逻辑
    }
}
