package com.wsvita.framework.router.contract.receiver

import android.content.Intent
import com.wsvita.framework.router.contract.ReceiveRouterContract

class StringReceiveRouterContract(action: String, resultKey: String) : ReceiveRouterContract<String>(action, resultKey) {
    override fun convertToOutput(data: Intent): String? {
        return data.getStringExtra(resultKey)
    }
}
