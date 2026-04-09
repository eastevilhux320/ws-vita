package com.wangshu.textus.note.local

import android.Manifest
import android.os.Build

class NoteConstants private constructor(){

    companion object{
        const val HTTP_SUCCESS_CODE = 0

        /**
         * 经纬度权限
         */
        val PERMISSION_LOCATION = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    object PermissionCode{
        const val CODE_PERMISSION_LOCATION = 10000;
    }

    /**
     * 舒记应用下的一些服务端通用返回code标识。
     */
    object ErrorCode{
        /**
         * 数据为空标识
         */
        const val CODE_EMPTY_DATA = 10030

        /**
         * 未绑定支付宝账号
         */
        const val ACCOUNT_ALIPAY_ERROR =10057
    }

    /**
     * app中使用的请求code标识
     */
    object RequestCode{
        const val ACCOUNT_REQUEST_LOGIN_QUICK = 1000;
    }

    /**
     * app中使用的返回code定义
     */
    object ResultCode{
        /**
         * 账单类型返回标识
         */
        const val CODE_BILL_TYPE = 1000;
    }

    object ServiceKey{
        const val SERVICE_NOTE = "wangshu_app_note_service_key"
        const val SERVICE_BILL = "wangshu_app_note_bill_service_key";
    }


    /**
     * 底部导航栏标识
     */
    object TabCode{
        /**
         * 首页标识
         */
        const val FRAGMENT_HOME = "NOTE_MAIN_TAB_HOME";

        const val FRAGMENT_NOTE = "NOTE_MAIN_TAB_NOTE"


        /**
         * 发现标识
         */
        const val FRAGMENT_DISCOVERY = "NOTE_MAIN_TAB_DISCOVERY";

        /**
         * 我的标识
         */
        const val FRAGMENT_MINE = "NOTE_MAIN_TAB_MINE";
    }

    /**
     * 整个app内设计到的数据穿透数据
     */
    object IntentKey{
        /**
         * 账单上级类型
         */
        const val BILL_TYPE_PARENT = "ws_app_bill_type_parent_key";

        /**
         * 账单子类型
         */
        const val BILL_TYPE_CHILD = "ws_app_bill_type_child_key";
    }
}
