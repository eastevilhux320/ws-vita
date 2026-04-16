package com.wangshu.textus.note.entity

import com.wsvita.core.common.BaseEntity

class QuoteEntity : BaseEntity() {

    /**
     * 名言正文
     */
    var content: String? = null

    /**
     * 作者/出处 (不填默认为 '佚名')
     */
    var author: String? = null

    override fun customLayoutId(): Int {
        return 0;
    }
}
