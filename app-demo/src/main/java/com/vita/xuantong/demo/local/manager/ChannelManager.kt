package com.wsvita.app.local.manager

import com.meituan.android.walle.WalleChannelReader
import com.vita.xuantong.demo.commons.KYApp
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.local.manager.StorageManager
import com.wsvita.framework.utils.SLog
import ext.StringExt.isNotInvalid
import java.lang.Exception

class ChannelManager : BaseManager{

    private constructor(){

    }

    companion object{
        private const val TAG = "Mirror_M_ChannelManager=>";

        private const val APP_CHANNEL_NAME = "ws_app_mirror_sp_channel_name_key";
        private const val APP_CHANNEL_ID = "ws_app_mirror_sp_channel_id_key";

        //美团打包的配置文件中用来定义id的key
        private const val WALLE_EXTRAINFO_CHANNEL_ID = "id"

        private const val DEFAULT_CHANNEL = "wangshu"
        private const val DEFAULT_CHANNEL_ID = 1

        /**
         * 缓存的应用渠道信息
         */
        private var cacheChannelName : String? = null;
        private var cacheChannelId : Int = 0;

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ChannelManager() }

        private var isInit : Boolean = false;
    }

    override fun onInit() {
        doGet();
    }

    fun getChannel(): String {
        checkInit();
        if(cacheChannelName.isNotInvalid()){
            return cacheChannelName!!;
        }else{
            return DEFAULT_CHANNEL;
        }
    }

    private fun doGet(){
        if(!hasCache()){
            getFromLocalStore();
        }
        if(!hasCache()){
            getFromWalle()
        }
        if(!hasCache()){
            cacheChannelName = DEFAULT_CHANNEL;
            cacheChannelId = DEFAULT_CHANNEL_ID;
        }
    }

    private fun hasCache() : Boolean{
        if(cacheChannelName == null){
            return false;
        }
        if(cacheChannelName.isNullOrEmpty()){
            return false;
        }
        if(cacheChannelId <= 0){
            return false;
        }
        return true;
    }

    /**
     * 从本地的缓存数据中获取渠道信息
     * @author Eastevil
     * @createTime 2025/4/15 10:49
     * @since 1.0.0
     * @return
     *      void
     */
    private fun getFromLocalStore(){
        cacheChannelName = StorageManager.instance.get(APP_CHANNEL_NAME);
        cacheChannelId = StorageManager.instance.getInt(APP_CHANNEL_ID);
        SLog.d(TAG,"getFromLocalStore,cacheChannelName:${cacheChannelName},cacheChannelId:${cacheChannelId}");
    }

    private fun getFromWalle(){
        cacheChannelName = WalleChannelReader.getChannel(KYApp.app);
        val fid = WalleChannelReader.getChannelInfo(KYApp.app)?.extraInfo?.get(WALLE_EXTRAINFO_CHANNEL_ID);
        if(fid != null){
            try {
                cacheChannelId = fid.toInt();
            }catch (e : Exception){
                SLog.e(TAG,"getFromWalle,id parse int value error,id is :${fid}");
            }
        }
        SLog.d(TAG,"getFromWalle,cacheChannelName:${cacheChannelName},cacheChannelId:${cacheChannelId}");
    }
}
