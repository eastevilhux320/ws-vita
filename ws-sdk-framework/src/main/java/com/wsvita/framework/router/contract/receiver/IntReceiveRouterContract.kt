package com.wsvita.framework.router.contract.receiver

import android.content.Intent
import com.wsvita.framework.router.contract.ReceiveRouterContract

class IntReceiveRouterContract(action: String, resultKey: String) : ReceiveRouterContract<Int>(action, resultKey) {
    override fun convertToOutput(data: Intent): Int {
        return data.getIntExtra(resultKey, 0)
    }
}
