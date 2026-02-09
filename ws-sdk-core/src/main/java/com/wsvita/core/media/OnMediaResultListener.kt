package com.wsvita.core.media

import android.net.Uri

interface OnMediaResultListener {
    fun onMediaResult(tag :Int,uri: Uri?)
}
