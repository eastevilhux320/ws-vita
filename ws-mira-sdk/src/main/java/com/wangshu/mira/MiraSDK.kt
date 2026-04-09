package com.wangshu.mira

import android.app.Application
import android.content.Context
import android.content.Intent
import com.wangshu.mira.configure.MiraConfig
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.ext.ConfigExt.applyFrom
import com.wangshu.mira.configure.MiraContants
import com.wangshu.mira.local.manager.MiraManager
import com.wangshu.mira.model.main.MiraMainActivity
import com.wangshu.mira.network.MiraDataSecurity
import com.wangshu.mira.network.interceptor.MiraMultipartInterceptor
import com.wangshu.mira.network.interceptor.MiraReponseInterceptor
import com.wsvita.core.configure.CoreConfig
import com.wsvita.core.configure.CoreConfigure
import com.wsvita.core.local.manager.SystemBarManager
import com.wsvita.framework.configure.FrameConfig
import com.wsvita.framework.configure.FrameConfigure
import com.wsvita.framework.local.manager.device.DeviceManager
import com.wsvita.framework.local.manager.device.OAIDManager
import com.wsvita.network.NetworkClient
import com.wsvita.network.NetworkOptions
import com.wsvita.network.configure.NetworkConfig
import com.wsvita.network.configure.NetworkConfigure

class MiraSDK private constructor(){

    companion object{
        private var miraSDKStatus : Int = -1;

        /**
         * 线程安全的单例对象
         */
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MiraSDK() }


        /*private val baseUrl : String
            get() =
                when (BuildConfig.VERSION_TYPE) {
                    BuildConfig.VERSION_DEV,
                    BuildConfig.VERSION_SIT -> "http://m.swangshu.com/wangshu/appservice/"
                    BuildConfig.VERSION_UAT -> "http://m.swangshu.com/wangshu/appservice/"
                    BuildConfig.VERSION_RELEASE -> "http://m.swangshu.com/wangshu/appservice/"
                    else -> throw IllegalStateException("version type error")
                }*/

        private val baseUrl : String = "http://m.swangshu.com/wangshu/appservice/";

        /**
         * 初始化SDK
         * create by Eastevil at 2026/2/28 11:41
         * @author Eastevil
         * @param
         * @return
         */
        fun initSDK(config : MiraConfig){
            //填充url
            config.networkUrl = baseUrl;
            MiraConfigure.instance.init(config);
            MiraManager.instance.init();

            val networkOption = NetworkOptions.Builder(config.appId.toString())
                .security(false,-1)
                .successCode(MiraContants.SUCCESS_CODE)
                .addInterceptor(MiraReponseInterceptor())
                .addInterceptor(MiraMultipartInterceptor())
                .build();
            NetworkClient.instance.init(networkOption);

            NetworkClient.instance.addDataSecurity(MiraDataSecurity());

            val nc = NetworkConfig.Builder(config.appId, baseUrl)
                .applyFrom(config)
                .builder();
            NetworkConfigure.instance.init(nc,networkOption);

            val fc = FrameConfig.Builder(config.appId)
                .applyFrom(config)
                .builder()
            FrameConfigure.instance.init(fc);

            SystemBarManager.instance.init();

            val cc = CoreConfig.Builder(config.appId)
                .applyFrom(config)
                .builder();
            CoreConfigure.instance.init(cc);

            miraSDKStatus = 1;
        }
    }

    /**
     * 启动SDK
     * create by Eastevil at 2026/3/2 16:02
     * @author Eastevil
     * @param
     * @return
     */
    fun mira(context: Context){
        if(miraSDKStatus == 1){
            //进入SDK
            val intent = Intent(context,MiraMainActivity::class.java);
            context.startActivity(intent);
        }else{
            throw IllegalStateException("Mira SDK not init,init sdk first");
        }
    }

    /**
     * 启动SDK
     * create by Eastevil at 2026/3/2 16:02
     * @author Eastevil
     * @param
     * @return
     */
    fun mira(context: Context,userId : String){
        if(miraSDKStatus == 1){
            MiraManager.instance.setUserId(userId);
            //进入SDK
            val intent = Intent(context,MiraMainActivity::class.java);
            context.startActivity(intent);
        }else{
            throw IllegalStateException("Mira SDK not init,init sdk first");
        }
    }

    fun readySDK(app: Application){
        DeviceManager.instance.init(app);
        OAIDManager.instance.register(app);
        com.wangshu.mira.local.manager.DeviceManager.instance.init(app);
    }

}
