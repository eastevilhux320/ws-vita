package com.wangshu.mira.network

import android.os.Build
import com.wangshu.mira.configure.MiraConfigure
import com.wangshu.mira.ext.StringExt.md5
import com.wsvita.framework.utils.AESUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.IDataSecurity
import com.wsvita.network.NetworkClient
import java.lang.StringBuilder
import java.net.URLDecoder
import java.net.URLEncoder

class MiraDataSecurity : IDataSecurity {

    companion object{
        private const val TAG = "Mira_Security_MiraDataSecurity=>";
    }

    override fun decrypt(data: String?): String? {
        //数据解密
        val aesKey = MiraConfigure.instance.getConfig()?.appKey;
        //进行URLDecode
        var dataStr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            URLDecoder.decode(data, NetworkClient.instance.getOptions().httpCharset)
        }else{
            URLDecoder.decode(data);
        }
        //AES解密
        dataStr = AESUtil.aesDecrypt(dataStr,aesKey);
        SLog.d(TAG,"decrypt_dataStr_result:${dataStr}");
        return dataStr;
    }

    override fun encrypt(data: String?): String? {
        //数据加密
        val aesKey = MiraConfigure.instance.getConfig()?.appKey;
        var encryptData = AESUtil.aesEncrypt(data,aesKey);
        encryptData = URLEncoder.encode(encryptData,"UTF-8");
        return encryptData;
    }

    override fun signData(data: String): String? {
        val sb = StringBuilder();
        sb.append(MiraConfigure.instance.getConfig()?.merchantNo);
        sb.append(MiraConfigure.instance.getConfig()?.appId);
        sb.append(MiraConfigure.instance.getConfig()?.secretKey);
        sb.append(data);
        SLog.d(TAG,"signData_befor:${sb.toString()}");
        val md5 = sb.toString().md5();
        return md5;
    }
}
