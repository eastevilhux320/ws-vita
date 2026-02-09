package com.wsvita.network.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.startup.Initializer
import com.wsvita.framework.utils.SLog
import ext.StringExt.isNotInvalid

/**
 * Token 管理类 (合二为一方案)
 * 既是业务单例，也是 App Startup 的初始化器
 */
class TokenManager : Initializer<TokenManager> {

    private var _prefs: SharedPreferences? = null

    // 内部使用的属性，确保已初始化
    private val prefs: SharedPreferences?
        get() = _prefs

    companion object {
        private const val TAG = "WSVita_Network_Manager_TokenManager=>"
        private const val PREF_FILE = "wsvita_secure_prefs"
        private const val KEY_TOKEN = "access_token"

        private var token : String? = null;

        // 业务层调用的唯一出口
        val instance: TokenManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TokenManager()
        }
    }

    // --- 实现 Initializer 接口的方法 ---

    override fun create(context: Context): TokenManager {
        SLog.d(TAG,"onCreate");
        // App Startup 会在启动时自动调用此方法
        // 我们将 context 注入单例中
        instance.initInternal(context)
        token = getToken();
        return instance
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        // 可以在这里打日志，但必须返回列表
        // SLog.d(TAG, "TokenManager dependencies called")
        return mutableListOf()
    }

    // --- 内部初始化逻辑 ---

    private fun initInternal(context: Context) {
        if (_prefs != null) return

        try {
            // 适配 security-crypto:1.0.0
            val keyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            _prefs = EncryptedSharedPreferences.create(
                PREF_FILE,
                keyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            Log.d(TAG, "initInternal success")
        } catch (e: Exception) {
            Log.e(TAG, "initInternal error，used normal SP: ${e.message}")
            context.deleteSharedPreferences(PREF_FILE)
            _prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        }
    }

    // --- 业务方法 ---

    fun getToken(): String?{
        if(token != null && token.isNotInvalid()){
            return token;
        }
        token = prefs?.getString(KEY_TOKEN, null);
        return token;
    }

    fun resetToken(newToken: String?) {
        token = newToken;
        prefs?.edit()?.putString(KEY_TOKEN, newToken)?.apply()
    }

    fun clear() {
        prefs?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}
