package com.wsvita.framework.router.contract

import android.os.Bundle

abstract class FullRouterContract<O : Any> : CommonRouterContract<O> {

    constructor(action: String, resultKey: String) : super(action,resultKey) {

    }

    constructor(action: String) : super(action){

    }

    override fun createInput(input: Bundle): Bundle? {
        return input
    }

    // 子类需实现 convertToOutput 来定义具体取值逻辑
}
