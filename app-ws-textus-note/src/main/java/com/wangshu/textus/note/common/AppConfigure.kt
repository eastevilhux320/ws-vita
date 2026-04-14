package com.wangshu.textus.note.common

import java.io.BufferedReader
import com.wangshu.textus.note.entity.AppConfig
import com.wsvita.framework.ext.JsonExt.parseGson
import com.wangshu.textus.note.BuildConfig

/**
 * app初始化配置
 */
class AppConfigure private constructor(){

    companion object{

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AppConfigure() }

        /**
         * 本地配置数据
         */
        val appConfig: AppConfig by lazy {
            val assets = NoteApp.app.assets
            val inputStream = assets.open("config/app_config.json")
            val configureList: Map<String, AppConfig> = inputStream.bufferedReader().use(
                BufferedReader::readText).parseGson();
            val configure = configureList[BuildConfig.APPLICATION_ID] ?: error("no app configure")
            configure
        }

        val baseUrl : String
            get() =
                when (BuildConfig.VERSION_TYPE) {
                    BuildConfig.VERSION_DEV -> "http://192.168.1.142:8800/wangshu/note/"
                    BuildConfig.VERSION_PERSONAL-> "http://192.168.1.14:8800/wangshu/note/"
                    BuildConfig.VERSION_SIT -> "http://192.168.1.142:8800/wangshu/note/"
                    BuildConfig.VERSION_UAT -> "http://47.118.26.100:8800/wangshu/note/"
                    BuildConfig.VERSION_RELEASE -> "http://47.118.26.100:8800/wangshu/note/"
                    else -> throw IllegalStateException("version type error")
                }


    }

    fun refreshToken(token : String){

    }


    /**
     * 设置加密使用的key
     * create by Administrator at 2022/10/11 23:47
     * @author Administrator
     * @param keyType
     *      加密类型，1：RSA类型，2：AES类型
     * @param secretKey
     *      加密的key
     * @return
     *      void
     */
    fun setSecret(keyType : Int,secretKey : String){

    }

}
