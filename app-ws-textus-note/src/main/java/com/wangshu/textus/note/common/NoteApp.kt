package com.wangshu.textus.note.common

import android.graphics.Color
import com.wangshu.mira.MiraSDK
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.textus.note.local.manager.ChannelManager
import com.wangshu.textus.note.local.manager.SecurityManager
import com.wangshu.textus.note.local.startup.AppStartupConfigDispatcher
import com.wangshu.textus.note.R
import com.wangshu.textus.note.local.manager.ContainerManager
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.configure.BizcoreConfigure
import com.wsvita.biz.core.configure.StartupConfigLocator
import com.wsvita.core.configure.CoreConfig
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.configure.DateTimeConfig
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.framework.commons.BaseApplication
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import com.wangshu.textus.note.BuildConfig
import com.wsvita.account.configure.AccountConfig
import com.wsvita.account.configure.AccountConfigure
import com.wsvita.framework.ext.BaseApplicationExt.appName
import com.wsvita.network.configure.NetworkOptions
import com.wsvita.network.configure.NetworkConfig
import com.wsvita.network.configure.NetworkConfigure
import ext.TimeExt.systemTime

class NoteApp : BaseApplication() {

    companion object {
        private const val TAG = "KY_App==>";
        /**
         * 全局 Application 实例代理
         */
        private lateinit var instance: NoteApp

        @JvmStatic
        val app: NoteApp
            get() = instance
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onInit() {
        super.onInit()
        SLog.init(true);
        instance = this;
        //需要注意初始化的顺序
        initBaseServices();
    }

    override fun onInitialize() {
        super.onInitialize()

        val appConfig = AppConfigure.appConfig;
        val miriConfig = MiraConfig.Builder(appConfig.appId,appConfig.appKey, appConfig.secretKey)
            .merchantNo("V1000")
            .isAutoPermission(true)
            .mainThemeColor(Color.parseColor("#8B008B"))
            .titleText("note_app_title")
            .builder();
        MiraSDK.initSDK(miriConfig);

        //开启初始化
        initCommon();
        initComponent();
        initBusiness();
        //注册各种组件中的回调设置
        registerConfigs();
    }

    /**
     * 底座组件，必须要先进行初始化
     * create by Eastevil at 2026/1/8 17:49
     * @author Eastevil
     * @param
     * @return
     */
    private fun initBaseServices(){
        val appConfig = AppConfigure.appConfig;
        //存储管理工具类需要优先初始化
        StorageManager.instance.init(appConfig.appId)
        ChannelManager.instance.init();
    }

    /**
     * 基础组件，必须要进行初始化后才能进入app
     */
    private fun initCommon(){
        val themeColor = getColor(R.color.color_main_theme);
        val submitColor = getColor(R.color.color_dialog_submit);
        val cancelColor = getColor(R.color.color_dialog_cancel);
        val channelCode = ChannelManager.instance.getChannel();
        SLog.d(TAG,"initCommons invoke");
        SLog.d(TAG,"channelCode:${channelCode}")
        val cStartTime = systemTime();
        SLog.i(TAG,"initCommons start,time:${cStartTime}");
        val appConfig = AppConfigure.appConfig;
        //初始化基础组件
        SLog.i(TAG,"init framework start,time:${systemTime()}");
        val frmeConfig = FrameConfig.Builder(appConfig.appId)
            .setChannelCode(channelCode)
            .builder();
        FrameConfigure.instance.init(frmeConfig);
        SLog.i(TAG,"init framework end,time:${systemTime()}");

        //初始化网络组件
        SLog.i(TAG,"init network start,time:${systemTime()}");
        val baseUrl = AppConfigure.baseUrl;
        val netConfig = NetworkConfig.Builder(appConfig.appId,baseUrl)
            .mainThemeColor(themeColor)
            .setChannelCode(channelCode)
            .submitColor(submitColor)
            .cancelColor(cancelColor)
            .builder();

        val networkOpetion = NetworkOptions.Builder()
            .needUrlDecode()
            .timeout(NetworkContants.TIME_OUT)
            .successCode(NetworkContants.ServiceCode.SUCCESS)
            .security(true,-1)
            .build();
        NetworkConfigure.instance.init(netConfig,networkOpetion);
        //为网络层设置数据处理过程
        NetworkConfigure.instance.addDataSecurity(SecurityManager.instance.getDataSecurity())
        SLog.i(TAG,"init network end,time:${systemTime()}");

        //初始化公共组件
        SLog.i(TAG,"init core start,time:${systemTime()}");
        val coreConfig = CoreConfig.Builder(appConfig.appId)
            .mainThemeColor(themeColor)
            .submitColor(submitColor)
            .cancelColor(cancelColor)
            .setChannelCode(channelCode)
            .builder();
        CoreConfigure.instance.init(coreConfig);
        SLog.i(TAG,"init core end,time:${systemTime()}");

        //初始化桥梁组件
        SLog.i(TAG,"init bizcore start,time:${systemTime()}");
        val bizCoreConfig = BizcoreConfig.Builder(appConfig.appId, Action.ACTION_MAIN)
            .splashDefaultId(R.drawable.bg_app_splash)
            .mainThemeColor(themeColor)
            .submitColor(submitColor)
            .cancelColor(cancelColor)
            .setChannelCode(channelCode)
            .builder();
        BizcoreConfigure.instance.init(bizCoreConfig);
        SLog.i(TAG,"init bizcore end,time:${systemTime()}");

        //账号组件
        SLog.i(TAG,"init account start,time:${systemTime()}");
        val accountConfig = AccountConfig.Builder(appConfig.appId)
            .appLogo(R.mipmap.icon_app_logo)
            .appName(app.appName())
            .appSlogan(getString(R.string.app_note_slogan))
            .mainThemeColor(themeColor)
            .titleBackgroundColor(themeColor)
            .privacyAgreementUrl(appConfig.agreementPrivacyUrl)
            .userAgreementUrl(appConfig.agreementAccountUrl)
            .builder();
        AccountConfigure.instance.init(accountConfig);
        SLog.i(TAG,"init account end,time:${systemTime()}");


        val cEndTime = systemTime();
        SLog.i(TAG,"initCommons end,time:${cEndTime}");
        val initTime = cEndTime - cStartTime;
        SLog.d(TAG,"initCommons totalTime:${initTime}");
    }

    /**
     * 普通组件，可延迟初始化
     */
    private fun initComponent(){
        val sTime = systemTime();
        SLog.d(TAG,"initComponent start,time:${sTime}");

        //初始化
        SystemBarManager.instance.init();

        val eTime = systemTime();
        SLog.d(TAG,"initComponent end,time:${eTime}");

        if(BuildConfig.DEBUG){
            val initTime = eTime - sTime;
            SLog.d(TAG,"initCommons totalTime:${initTime}");
        }
    }

    /**
     * 业务组件，可延迟初始化
     */
    private fun initBusiness(){
        SLog.d(TAG,"initBusiness invoke");
        SLog.i(TAG,"initBusiness start,time:${systemTime()}");
        //初始化eventbus
        try {
            val appId = AppConfigure.appConfig.appId;

            ContainerManager.instance.init();

            //日期时间的处理展示
            val dtConfig = DateTimeConfig.Builder(appId)
                .builder();
            CoreConfigure.instance.setDateTimeConfig(dtConfig);


        } catch (e: Exception) {
            // 防止多次初始化报错
        }
        SLog.i(TAG,"initBusiness end,time:${systemTime()}");
    }


    private fun registerConfigs(){
        var dispatcher = AppStartupConfigDispatcher()
        dispatcher.initChain()
        StartupConfigLocator.instance.register(dispatcher)
    }
}
