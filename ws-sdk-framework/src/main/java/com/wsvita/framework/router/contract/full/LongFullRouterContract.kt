package com.wsvita.framework.router.contract.full

import android.content.Intent
import com.wsvita.framework.router.contract.FullRouterContract

class LongFullRouterContract(action: String, resultKey: String) : FullRouterContract<Long>(action, resultKey) {
    override fun convertToOutput(data: Intent): Long {
        return data.getLongExtra(resultKey, 0L)
    }
}
