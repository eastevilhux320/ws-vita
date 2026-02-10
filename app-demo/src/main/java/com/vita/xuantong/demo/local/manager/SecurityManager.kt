package com.wsvita.app.local.manager

import com.wsvita.framework.utils.AESUtil
import com.wsvita.framework.utils.RSAUtil
import com.wsvita.framework.utils.SLog
import com.wsvita.network.IDataSecurity
import com.wsvita.network.manager.TokenManager
import ext.StringExt.isNotInvalid
import java.net.URLDecoder
import java.net.URLEncoder

class SecurityManager private constructor(){

    companion object {
        private const val TAG = "Mirror_M_SecurityManager=>"
        private const val KEY_TYPE_RSA = 1;
        private const val KEY_TYPE_AES = 2;
        private const val KEY_TYPE_UAES = 3;

        // 业务层调用的唯一出口
        val instance: SecurityManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SecurityManager() }

        private var dataSecret : String? = null;
        private var keyType : Int = 0;
    }

    /**
     * 重置当前数据加解密所使用的密钥配置。
     *
     * 该方法用于在 App 生命周期内动态切换加密方案，
     * 例如：
     * - 登录前使用 RSA 公钥进行密钥交换
     * - 登录后切换为 AES 对业务数据进行加解密
     *
     * 注意：
     * 1. 此方法会原子性地替换当前生效的密钥配置；
     * 2. 替换后，Network 层后续所有请求的 encrypt / decrypt
     *    都将使用新的密钥与加密类型；
     * 3. 调用方需保证在发起网络请求前完成初始化，
     *    否则可能触发 IllegalStateException。
     * create by Eastevil at 2025/12/31 16:10
     * @author Eastevil
     *
     * @param type  加密类型，标识当前密钥的使用算法（如 AES / RSA）,1-RSA,2-AES
     * @param secretKey 加解密所需的密钥内容（AES 密钥或 RSA 公钥）
     * @return
     */
    fun resetSecret(type : Int,secretKey : String){
        keyType = type;
        dataSecret = secretKey;
    }

    /**
     * 获取提供给 Network 层使用的数据安全处理器。
     *
     * Network 层仅依赖 IDataSecurity 接口，
     * 不感知具体的加密算法、密钥管理或安全策略。
     *
     * 返回的 IDataSecurity 实例是一个长期有效的代理对象：
     * - 其内部会在调用 encrypt / decrypt 时，
     *   动态读取当前最新的密钥配置；
     * - 即使在运行时调用了 resetSecret 切换密钥，
     *   Network 层也无需重新获取 IDataSecurity 实例。
     *
     * create by Eastevil at 2025/12/31 16:10
     * @author Eastevil
     * @param
     * @return [IDataSecurity] 数据加解密处理器实例
     */
    fun getDataSecurity(): IDataSecurity {
        return dataSecurity;
    }

    private val dataSecurity = object : IDataSecurity{

        override fun decrypt(data: String?): String? {
            SLog.d(TAG,"dataSecurity,decrypt data:${data}");
            if(dataSecret == null){
                return null;
            }
            if(KEY_TYPE_AES == keyType){
                return AESUtil.aesDecrypt(data, dataSecret);
            }else if(KEY_TYPE_RSA == keyType){
                return RSAUtil.decryptByPublicKey(data?:"", dataSecret!!);
            } else if(KEY_TYPE_UAES == keyType){
                return AESUtil.aesDecrypt(data, dataSecret);
            } else{
                throw IllegalAccessException("unknow data secret type");
            }
        }

        override fun encrypt(data: String?): String? {
            SLog.d(TAG,"dataSecurity,encrypt data:${data}");
            if(data == null){
                return data;
            }
            if(dataSecret == null){
                throw IllegalAccessException("dataSecret is null");
            }
            val s = if(KEY_TYPE_AES == keyType){
                var aesData = AESUtil.aesEncrypt(data, dataSecret!!);
                if(aesData.isNotInvalid()){
                    aesData = URLEncoder.encode(aesData,"UTF-8");
                }
                aesData;
            }else if(KEY_TYPE_RSA == keyType){
                RSAUtil.encryptByPublicKey(data, dataSecret!!);
            } else if(KEY_TYPE_UAES == keyType) {
                var aesData = AESUtil.aesEncrypt(data, dataSecret!!);
                if(aesData.isNotInvalid()){
                    aesData = URLEncoder.encode(aesData,"UTF-8");
                }
                aesData;
            }  else{
                throw IllegalAccessException("unknow data secret type");
            }
            return s;
        }

        override fun signData(data: String): String? {
            //暂时不设计具体的验签操作
            return data;
        }

    }
}
