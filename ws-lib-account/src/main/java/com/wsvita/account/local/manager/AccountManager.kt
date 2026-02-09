package com.wsvita.account.local.manager

import android.content.Intent
import com.wsvita.account.accountup.AccountConfigLocator
import com.wsvita.account.commons.AccountConstants
import com.wsvita.account.entity.AccountAction
import com.wsvita.account.entity.IAccount
import com.wsvita.account.local.event.AccountModifyEvent
import com.wsvita.account.network.model.AccountModel
import com.wsvita.core.configure.SDKManager
import com.wsvita.framework.GlideApp
import com.wsvita.framework.commons.BaseApplication
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.SLog
import com.wsvita.module.account.AccountEventIndex
import ext.JsonExt.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 核心账号管理类
 * * 设计核心：
 * 1. 对外隐藏具体的实体类（Account/AppAccountEntity），仅暴露 [IAccount] 接口。
 * 2. 兼容外部扩展：支持通过 Class 泛型获取具体的子类实现。
 */
class AccountManager private constructor() : SDKManager() {

    // 【修复】定义私有作用域，用于管理当前类发起的异步任务生命周期
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 【修复】增加请求状态锁，防止 onInit 和外部调用 notifyMember 同时发起多个重复网络请求
    private val isFetching = AtomicBoolean(false)

    companion object {
        private const val TAG = "WS_AC_Manager_AccountManager=>";
        @JvmStatic
        val instance: AccountManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AccountManager() }
    }

    /**
     * 内存中持有的账号实例。
     * 虽然内部是 Account 对象，但存储类型为接口，确保解耦。
     */
    @Volatile
    private var mCurrentAccount: IAccount? = null

    override fun onInit() {
        notifyMemberInfo();
    }

    private val _isLoginFlow = MutableStateFlow(false)
    val isLoginFlow = _isLoginFlow.asStateFlow()

    /**
     * 保存/更新账号信息
     * 接收 [IAccount] 接口，实现层可以传入 Account 或其子类
     */
    private fun updateAccount(account: IAccount?) {
        synchronized(this) {
            if (account == null) {
                logout()
                return
            }

            this.mCurrentAccount = account
            this._isLoginFlow.value = true

            //发送账号变更事件
            val accountJson = account.toJson();
            AccountConfigLocator.instance.put(AccountConstants.AccountKeys.ACCOUNT_KEY,accountJson);

            // 1. 使用显式构造函数创建事件对象
            val event = AccountModifyEvent(account)

            // 2. 调用 SDKManager 封装的粘性发送方法
            // 使用粘性事件确保后进入的页面也能通过 getAccount() 获取数据
            postStickyEvent(event)

            //发送账号更新广播
            // 修复：通过 context 发送标准系统广播，便于跨组件/跨进程监听
            try {
                val intent = Intent(AccountAction.ACTION_ACCOUNT_MODIFIED);
                intent.putExtra(AccountConstants.AccountKeys.ACCOUNT_ID, account.getAccountId());
                intent.putExtra(AccountConstants.AccountKeys.ACCOUNT_DETAIL,accountJson);
                BaseApplication.app.sendBroadcast(intent);
                SLog.d(TAG, "send account update broadcast success");
            } catch (e: Exception) {
                SLog.e(TAG, "send account update broadcast error: " + e.message);
            }
        }
    }

    /**
     * 【核心设计】按类型获取账号实例
     * * 场景：如果外部 App 继承了 Account 增加了 myField 字段，
     * 调用方式：AccountManager.instance.getAccount(MyExtAccount::class.java)?.myField
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IAccount> getAccount(clazz: Class<T>): T? {
        val current = mCurrentAccount
        return if (clazz.isInstance(current)) {
            current as T
        } else {
            null
        }
    }

    /**
     * 基础获取方法，仅暴露契约接口。
     * 满足 90% 的业务场景（如获取昵称、ID 等）。
     */
    fun getAccount(): IAccount? = mCurrentAccount

    fun getAccountId(): Long? {
        return getAccount()?.getAccountId();
    }

    /**
     * 判断登录状态
     */
    fun isLogin(): Boolean {
        return mCurrentAccount != null;
    }

    /**
     * 退出登录
     */
    fun logout() {
        synchronized(this) {
            mCurrentAccount = null
            _isLoginFlow.value = false
            // 清除持久化数据
        }
    }

    @org.greenrobot.eventbus.Subscribe
    fun onStubEvent(event: AccountModifyEvent) {

    }

    fun notifyMember(){
        notifyMemberInfo();
    }
    /**
     * 从远程服务器刷新当前会员信息
     */
    private fun notifyMemberInfo() {
        // 【修复】如果当前正在请求中，则不再发起新的请求
        if (isFetching.compareAndSet(false, true)) {
            managerScope.launch {
                try {
                    val result = AccountModel.instance.memberInfo();
                    if (result.isSuccess) {
                        val data = result.data;
                        if (data != null && data.account != null) {
                            updateAccount(data.account);
                        } else {
                            SLog.e(TAG, "notifyMemberInfo error: result data or account is null");
                        }
                    } else {
                        SLog.e(TAG, "notifyMemberInfo error，code:" + result.code + ",msg:" + result.msg);
                    }
                } catch (e: Exception) {
                    // 【修复】完善异常日志打印，保留堆栈信息
                    e.printStackTrace();
                    SLog.e(TAG, "notifyMemberInfo exception: " + e.message);
                } finally {
                    // 【修复】无论成功失败，请求结束必须重置标识位
                    isFetching.set(false);
                }
            }
        } else {
            SLog.d(TAG, "notifyMemberInfo is already in progress, ignore this request.");
        }
    }

    /**
     * 【修复】SDKManager/BaseManager 销毁时需要调用此方法释放协程，防止泄露
     */
    fun onDestroy() {
        if (managerScope != null) {
            managerScope.cancel();
        }
    }

    protected fun postEvent(event: Any, sticky: Boolean = false) {
        if (sticky) {
            EventBus.getDefault().postSticky(event)
        } else {
            EventBus.getDefault().post(event)
        }
    }

}
