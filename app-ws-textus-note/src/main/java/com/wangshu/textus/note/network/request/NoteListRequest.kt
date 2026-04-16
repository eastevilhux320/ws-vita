package com.wangshu.textus.note.network.request

import com.wsvita.network.entity.BasePageRequest


class NoteListRequest : BasePageRequest() {
    var startTime : Long? = null;
    var endTime : Long? = null;
}
