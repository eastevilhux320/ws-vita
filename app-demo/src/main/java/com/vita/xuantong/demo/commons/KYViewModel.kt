package com.vita.xuantong.demo.commons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.entity.Account
import com.wsvita.account.entity.AccountAction
import com.wsvita.account.entity.IAccount
import com.wsvita.account.local.event.AccountLogoutEvent
import com.wsvita.account.local.event.AccountModifyEvent
import com.wsvita.account.local.manager.AccountManager
import com.wsvita.app.local.manager.ContainerManager
import com.wsvita.biz.core.commons.BizcoreViewModel
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.framework.ext.JsonExt.parseGson
import com.wsvita.framework.utils.SLog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * App 业务基础 ViewModel
 * * 核心逻辑：完全基于事件驱动。
 * 通过 EventBus 的粘性事件机制，在注册时自动触发初始状态同步。
 */
abstract class KYViewModel(application: Application) : BizcoreViewModel(application) {
    private lateinit var containerManager : ContainerManager;

    private val _account = MutableLiveData<IAccount?>();
    val account: LiveData<IAccount?> = _account;

    // 1. 定义广播接收器成员变量（避免匿名内部类，方便注销）
    private val accountReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: android.content.Intent?) {
            val action = intent?.action
            SLog.d(TAG, "receive broadcast action: $action")
            when(action){
                AccountAction.ACTION_ACCOUNT_MODIFIED->{
                    SLog.d(TAG,"account changed")
                    // 执行更新逻辑
                    val accountJson = intent.getStringExtra(AccountConstants.AccountKeys.ACCOUNT_DETAIL)
                    SLog.i(TAG,"account changed,data:${accountJson}");
                    val account = accountJson?.parseGson<Account>()
                    _account.value = account
                }
            }
        }
    }

    override fun isLogin(): Boolean {
        return AccountManager.instance.isLogin();
    }

    override fun receiveLocation(location: BizLocation) {
        super.receiveLocation(location)
        containerManager.putJson(KYContants.ContainerKey.BIZ_LOCATION,location);
    }

    override fun initModel() {
        super.initModel();
        containerManager = ContainerManager.instance;
        // 仅执行注册。注册瞬间，如果有粘性事件，onAccountLogin 会被自动调用。
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if(isAccountObserverEnabled()){
            SLog.d(TAG,"register account");
            // 2. 注册广播（使用 ApplicationContext 确保安全）
            try {
                val filter = android.content.IntentFilter()
                filter.addAction(AccountAction.ACTION_ACCOUNT_MODIFIED)
                getApplication<Application>().registerReceiver(accountReceiver, filter)
            } catch (e: Exception) {
                SLog.e(TAG, "registerReceiver error: " + e.message)
            }
        }
    }

    /**
     * 接收退出登录事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAccountLogout(event: AccountLogoutEvent) {
        SLog.d(TAG,"eventbus,onAccountLogout")
        _account.value = null;
    }

    /**
     * 接收账号发生变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAccountModify(event : AccountModifyEvent){
        SLog.d(TAG,"eventbus,onAccountModify")
        _account.value = event.getAccount();
    }

    override fun onCleared() {
        try {
            getApplication<Application>().unregisterReceiver(accountReceiver)
            SLog.d(TAG, "unregisterReceiver success")
        } catch (e: Exception) {
            SLog.e(TAG, "unregisterReceiver error: " + e.message)
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onCleared();
    }

    protected open fun isAccountObserverEnabled(): Boolean {
        return false;
    }

    /**
     * 核心方法：向容器分发并缓存数据
     * @param key 缓存的唯一标识
     * @param data 数据实例
     * @param clazz 数据类型的 Class
     */
    fun <T> putContainerData(key: String, data: T, clazz: Class<T>) {
        // 1. 根据类型进行物理缓存
        when (data) {
            is Int -> containerManager.putInt(key, data)
            is Long -> containerManager.putLong(key, data)
            is Double -> containerManager.putDouble(key, data)
            is String -> containerManager.putString(key, data)
            else -> {
                // 如果是复杂对象，直接存入 Json 镜像，确保数据安全
                if(data != null){
                    containerManager.putJson(key, data)
                }
            }
        }
        SLog.d(TAG, "pubContainerData: key=$key, type=${clazz.simpleName}")
    }

    /**
     * 获取容器数据 (通用型)
     * 根据传入的 clazz 类型，自动从物理缓存中匹配对应的数据
     * * @param key 缓存唯一标识
     * @param clazz 目标类型的 Class 对象
     * @return 匹配到的数据实例，找不到则返回 null
     */
    fun <T> getContainerData(key: String, clazz: Class<T>): T? {
        // 1. 获取底层 Manager 实例
        // 2. 强类型分流判断
        // 利用 javaClass 与传入 clazz 的比对，确保基础类型能精准拉取
        return when (clazz) {
            java.lang.Integer::class.java, Int::class.java -> {
                containerManager.getInt(key, 0) as? T
            }
            java.lang.Long::class.java, Long::class.java -> {
                containerManager.getLong(key, 0L) as? T
            }
            java.lang.Double::class.java, Double::class.java -> {
                containerManager.getDouble(key, 0.0) as? T
            }
            java.lang.String::class.java, String::class.java -> {
                containerManager.getString(key, "") as? T
            }
            java.lang.Boolean::class.java, Boolean::class.java -> {
                // 如果 Manager 没提供 getBoolean，可以按需补充或通过 getString 解析
                containerManager.getBoolean(key, false) as? T
            }
            else -> {
                // 3. 自定义实体类 (BizLocation, UserInfo等) 走 Json 反序列化
                containerManager.getJsonEntity(key, clazz)
            }
        }
    }

    companion object{
        private const val TAG = "Mirror_MirrorViewModel==>";
    }
}
