package com.wangshu.textus.note.local

class WSConstants private constructor(){

    companion object{
        /**
         * 跳转进入activity时，获取跳转进入的类型intent传递参数的key
         */
        const val ACTIVITY_FROM_TYPE = "wangshu_app_activity_from_type_key";

        /**
         * 显示fragment时，用来获取上层显示的标识
         */
        const val FRAGMENT_FROM_TYPE = "wangshu_app_fragment_from_type_key";

        /**
         * 账号模块用户信息发生变更广播的action
         */
        const val ACTION_ACCOUNT_UPDATE = "com.wangshu.account.account.update";

        /**
         * 账号模块发送广播的时候，intent携带的加密后的账号数据的key
         */
        const val INTENT_ACCOUNT_DATA = "ws_account_account_data_key";

        /**
         * 发送修改应用语言的广播
         */
        const val ACTION_WS_LANGUAGE = "com.wangshu.common.language.action"

        val CAMERA_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO);
    }

    object ServiceKey{
        const val SERVICE_APP = "wangshu_common_service_app_key"
        const val SERVICE_WANGSHU = "wangshu_common_service_wangshu_key"
        const val SERVICE_ASSETS = "wangshu_common_service_assets_key";
        const val SERVICE_FULU = "wangshu_common_service_fulu_key";
        const val SERVICE_ORDER = "wangshu_common_service_order_key";
        const val SERVICE_AI = "wangshu_common_service_ai_key";
    }

    /**
     * 底部导航栏标识
     */
    object TabCode{
        /**
         * 首页标识
         */
        const val FRAGMENT_HOME = "BOX_TAB_MAIN_HOME";

        const val FRAMGNET_SHORTPLAY = "FRAGMENT_SHORT_PLAY";

        const val FRAGMENT_VIDOE = "FRAGMENT_VIDEO";

        const val FRAGMENT_BOX = "WS_NOTE_BOX"

        /**
         * 发现标识
         */
        const val FRAGMENT_DISCOVERY = "FRAGMENT_DISCOVERY";

        /**
         * 我的标识
         */
        const val FRAGMENT_MINE = "FRAGMENT_MINE";

        /**
         * 云闪盒我的标识
         */
        const val FRAGMENT_BOX_MAIN_MINE = "BOX_MAIN_MINE";

        const val FRAGMENT_WEBVIEW = "FRAGMENT_WEBVIEW"

        /**
         * 排名标识
         */
        const val FRAGMENT_RANKING = "BOX_MAIN_RANKING";

    }

    /**
     * 底部导航栏类型
     */
    object TabType{
        const val TYPE_WEBVIEW = 0;
        const val TYPE_HOME = 1;
        const val TYPE_BOX = 2;
        const val TYPE_DISCOVERY = 3;
        const val TYPE_MINE = 4;
        const val TYPE_RANKING = 5;
    }

    /**
     * 定义系统请求标识,所有公共的系统标识都在6000以上
     */
    object RequestCode{
        /**
         * 发布文章请求相册标识
         */
        const val CODE_ISSUE_ARITCLE_IMAGE = 6000;

        /**
         * 选择文章封面标识
         */
        const val CODE_ARTICLE_COVER = 6001;

        /**
         * 请求用户图像时的请求标识
         */
        const val CODE_ACCOUNT_ICON = 6002;

        /**
         * 请求进入选择视频标识
         */
        const val CODE_ISSUE_VIDEO = 6003;

        /**
         * 视频封面
         */
        const val CODE_VIDEO_COVER = 6004;

        /**
         * 选择精彩瞬间图片
         */
        const val CODE_MOMENT_IMAGE = 6005;

        /**
         * 选择话题图片
         */
        const val CODE_TOPIC_ICON = 6006;

        /**
         * 请求动态顶部背景图片标识
         */
        const val CODE_MOMENT_HEADER = 6007;

        const val CODE_MOMENT_VIDEO = 6008;

        /**
         * 选择视频封面文件
         */
        const val CODE_THUMBNAIL_FILE = 6009;

        /**
         * 选择图片集图片
         */
        const val CODE_PICTURE_IMAGE = 6010;


        /**
         * 选择话题
         */
        const val CODE_SELECT_TOPIC = 9000;

        /**
         * 请求登录code标识
         */
        const val CODE_ACCOUNT_LOGIN = 9001;

        /**
         * 请求相机标识
         */
        const val CODE_CAMERA = 9002;
    }

    object ResultCode{
        /**
         * 话题列表返回标识
         */
        const val CODE_TOPIC_SELECT = 8000;

        /**
         * 从登录页面返回标识
         */
        const val CODE_ACCOUNT_LOGIN = 8001;

        /**
         * 从账号详情返回
         */
        const val CODE_ACCOUNT_INFO = 8002;

        /**
         * 从城市列表返回
         */
        const val CODE_CITY = 8003;

        /**
         * 录像页面返回code标识
         */
        const val CODE_CAMERA_VIDEO = 8004;

        /**
         * 选择时间返回code标识
         */
        const val CODE_DATA_TIME = 8005;

        /**
         * 退出登录
         */
        const val CODE_ACCOUNT_EXIT = 8006
    }

    /**
     * app上传文件类型,当前App中使用此变量
     * @version 1.0.0
     * @since 1.0.0
     */
    object UploadFileType{

        /**
         * 问卷调查题目选项icon图片类型
         */
        const val TYPE_SUBJECT_OPTION_ICON = 10000;

        /**
         * 问卷题目附加图片
         */
        const val TYPE_daipet_SUBJECT_IMAGE = 10001;

        /**
         * 问卷调研背景图片
         */
        const val TYPE_daipet_BACKGROUND_IMAGE = 10002;

        /**
         * 个人图像
         */
        const val TYPE_ACCOUNT_ICON = 10005;

        /**
         * 我的页面用户头部背景
         */
        const val TYPE_ACCOUNT_BGHEADER = 10006;

        /**
         * 用户我的栏头部背景图片
         */
        const val TYPE_MINE_HEADER = 10009;

        /**
         * 问卷调研图标
         */
        const val TYPE_daipet_ICON = 10010;

        /**
         * 动态图片地址
         */
        const val TYPE_MOMENT_ICON = 10011;

        /**
         * 动态视频地址
         */
        const val TYPE_MOMENT_VIDEO = 10012;

        /**
         * 上传动态视频封面文件
         */
        const val TYPE_MOMENT_THUMBNAIL = 10013;

        /**
         * 图片集封面文件
         */
        const val TYPE_PICTURE_THUMBNAIL = 10015;

        /**
         * 话题图标
         */
        const val TYPE_TOPIC_ICON = 10016;

        /**
         * 话题头部背景
         */
        const val TYPE_TOPIC_HEADER = 10017;

        /**
         * 账单类型icon图标类型
         */
        const val TYPE_BILLTYPE_ICON = 10018;
    }

    /**
     * 公共intent传递参数定义的key
     */
    object IntentKey{
        /**
         * 登录返回code标识key
         */
        const val KEY_ACCOUNT_RESULTCODE = "app_ws_account_login_code";

        /**
         * 选择时间的传递时间的key
         */
        const val DATE_TIME_KEY = "ws_common_date_time_key"

        /**
         * 设置语言广播携带语言数据的key
         */
        const val LANGUAGE_DATA = "ws_commons_setting_language_key";
        /**
         * 资产数据传递的key
         * 用于跨组件或广播中传递资产数据。
         * 例如，在资产更新时，可能通过广播携带该数据，以便其他组件（如电商模块）可以接收到并更新相关信息。
         */
        const val ASSETS_DATA = "ws_common_assets_data_key"
    }

    /**
     * 公共组件下的接口公共错误
     * create by Eastevil at 2024/9/3 14:44
     * @author Eastevil
     * @param
     * @return
     */
    object NetworkRequestCode{

        /**
         * 数据为空
         */
        const val EMPTY_DATA = 10030;

        /**
         * 数据为空
         */
        const val EMPTY_ERROR = 5005;

        /**
         * 前后端加密规则同步失败，需要前端清空数据重新处理
         */
        const val PARAMS_CIPHER_ERROR = 5007;


        const val TOKEN_ERROR_EXPIRED = 10021;

        /**
         * 京东转链请求标识
         */
        const val ERROR_JDPROMOTION = 100000;

        /**
         * 淘宝高效转链请求标识
         */
        const val ERROR_TBPRIVILEGE_LINK = 100001;

        /**
         * 查询系统弹框
         */
        const val ERROR_APP_DIALOG = 100002;

        /**
         * 绑定支付宝
         */
        const val REQUEST_BIND_ALIPAY = 100003;

        /**
         * 商品组件中淘宝高效转链请求标识
         */
        const val REQUEST_ECOMMERECE_TBPRIVILEGE_LINK = 100004;
    }

    /**
     * 全局action定义
     */
    object Action{
        /**
         * 资产数据更新广播Action
         * 用途：
         * 该广播用于在资产数据发生更新时通知其他模块。主要用于如积分、余额等资产信息的变更，
         * 以便其他模块（如 `ws-lib-ecommerce`）能够及时收到变更通知并做相应的业务处理。
         *
         * 使用场景：
         * - 在 `east-evil-account` 模块中，资产数据发生变化时（如积分变动），通过发送此广播
         *   来通知其他模块更新相关数据。
         * - 在 `ws-lib-ecommerce` 等模块中，接收该广播并根据资产数据进行相关业务处理，
         *   如积分兑换商品时，确保商品兑换逻辑和资产数据一致。
         *
         * 注意事项：
         * - 该广播只会在同一应用内有效，适用于同一个应用中的不同模块之间的通信。
         * - 广播发送和接收应尽量简洁，避免复杂的数据传递，适合用于轻量级的事件通知。
         */
        const val ACTION_ASSETS_UPDATE = "com.wangshu.common.ACTION_ASSETS_UPDATE"
    }
}
