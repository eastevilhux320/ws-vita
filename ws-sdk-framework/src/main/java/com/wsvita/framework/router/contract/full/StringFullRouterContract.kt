package com.wsvita.framework.router.contract.full

import android.content.Intent
import com.wsvita.framework.router.contract.FullRouterContract

class StringFullRouterContract(action: String, resultKey: String) : FullRouterContract<String>(action, resultKey) {

    override fun convertToOutput(data: Intent): String? {
        return data.getStringExtra(resultKey)
    }
}
