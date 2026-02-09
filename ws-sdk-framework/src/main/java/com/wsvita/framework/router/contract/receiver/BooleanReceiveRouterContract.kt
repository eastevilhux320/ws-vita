package com.wsvita.framework.router.contract.receiver

import android.content.Intent
import com.wsvita.framework.router.contract.ReceiveRouterContract

class BooleanReceiveRouterContract(action: String, resultKey: String) : ReceiveRouterContract<Boolean>(action, resultKey) {

    override fun convertToOutput(data: Intent): Boolean {
        return data.getBooleanExtra(resultKey, false)
    }
}
