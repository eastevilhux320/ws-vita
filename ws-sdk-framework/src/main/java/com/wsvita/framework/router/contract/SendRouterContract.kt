package com.wsvita.framework.router.contract

import android.content.Intent
import android.os.Bundle

abstract class SendRouterContract : CommonRouterContract<Unit> {

    constructor(action: String) : super(action) {

    }

    override fun createInput(input: Bundle): Bundle? {
        // 显式返回 input，确保 routerContainer 的参数不丢失
        return input
    }

    override fun convertToOutput(data: Intent): Unit {
        return Unit
    }
}
