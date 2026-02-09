package com.wsvita.framework.router.contract.full

import android.content.Intent
import com.wsvita.framework.router.contract.FullRouterContract

class IntFullRouterContract(action: String, resultKey: String) : FullRouterContract<Int>(action, resultKey) {
    override fun convertToOutput(data: Intent): Int {
        return data.getIntExtra(resultKey, 0)
    }
}
