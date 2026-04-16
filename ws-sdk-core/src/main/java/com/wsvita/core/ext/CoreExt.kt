package com.wsvita.core.ext

import com.wsvita.framework.R
import com.wsvita.framework.local.WsContext

object CoreExt {

    fun Int.weekText() : String{
        return when(this){
            1-> WsContext.app.getString(com.wsvita.ui.R.string.week_1)
            2-> WsContext.app.getString(com.wsvita.ui.R.string.week_2)
            3-> WsContext.app.getString(com.wsvita.ui.R.string.week_3)
            4-> WsContext.app.getString(com.wsvita.ui.R.string.week_4)
            5-> WsContext.app.getString(com.wsvita.ui.R.string.week_5)
            6-> WsContext.app.getString(com.wsvita.ui.R.string.week_6)
            7-> WsContext.app.getString(com.wsvita.ui.R.string.week_7)
            else-> ""
        }
    }
}
