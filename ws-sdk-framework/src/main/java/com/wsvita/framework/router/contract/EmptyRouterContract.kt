package com.wsvita.framework.router.contract

import android.content.Intent
import android.os.Bundle

class EmptyRouterContract(action: String) : CommonRouterContract<Unit>(action) {

    override fun createInput(input: Bundle): Bundle? {
        // 显式返回 null，不将任何 Bundle 数据带入 Intent
        return null
    }

    override fun convertToOutput(data: Intent): Unit {
        // 显式返回 Unit，不从返回的 Intent 中取值
        return Unit
    }
}
