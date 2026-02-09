package com.wsvita.framework.router.contract.full

import android.content.Intent
import com.wsvita.framework.router.contract.FullRouterContract

class BooleanFullRouterContract(action: String, resultKey: String) : FullRouterContract<Boolean>(action, resultKey) {

    override fun convertToOutput(data: Intent): Boolean {

        return data.getBooleanExtra(resultKey, false)
    }
}
