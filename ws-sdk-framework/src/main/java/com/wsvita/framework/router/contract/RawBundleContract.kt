package com.wsvita.framework.router.contract

import android.content.Intent
import android.os.Bundle
import com.wsvita.framework.router.BaseComponentResult

/**
 * 万能快捷协议 B：输入 Bundle -> 返回 Bundle
 * 适合：完全不想定义对象，直接透传参数的场景
 */
/*
class RawBundleContract(action: String) : BaseComponentResult<Bundle, Bundle>(action) {
    override fun createInput(input: android.os.Bundle) = input
    override fun convertToOutput(data: Intent) = data.extras
}
*/
