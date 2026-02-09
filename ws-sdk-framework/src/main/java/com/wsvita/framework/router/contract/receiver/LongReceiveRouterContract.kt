package com.wsvita.framework.router.contract.receiver

import android.content.Intent
import com.wsvita.framework.router.contract.ReceiveRouterContract

class LongReceiveRouterContract(action: String, resultKey: String) : ReceiveRouterContract<Long>(action, resultKey) {
    override fun convertToOutput(data: Intent): Long {
        return data.getLongExtra(resultKey, 0L)
    }
}
