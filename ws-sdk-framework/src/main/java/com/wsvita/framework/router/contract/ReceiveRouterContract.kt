package com.wsvita.framework.router.contract

import android.content.Intent
import android.os.Bundle

abstract class ReceiveRouterContract<O : Any>(action: String, resultKey: String) : CommonRouterContract<O>(action, resultKey) {

    override fun createInput(input: Bundle): Bundle? {
        return input
    }
}
