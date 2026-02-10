package com.vita.xuantong.demo.commons

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wsvita.account.entity.IAccount
import com.wsvita.app.local.manager.ContainerManager
import com.wsvita.biz.core.commons.BizcoreContainerViewModel
import com.wsvita.framework.utils.SLog

abstract class KYContainerViewModel(application: Application) : BizcoreContainerViewModel(application) {

    private val _account = MutableLiveData<IAccount?>();
    val account: LiveData<IAccount?> = _account;


    override fun initModel() {
        super.initModel();
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
            is Int -> ContainerManager.instance.putInt(key, data)
            is Long -> ContainerManager.instance.putLong(key, data)
            is Double -> ContainerManager.instance.putDouble(key, data)
            is String -> ContainerManager.instance.putString(key, data)
            else -> {
                // 如果是复杂对象，直接存入 Json 镜像，确保数据安全
                if(data != null){
                    ContainerManager.instance.putJson(key, data)
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
                ContainerManager.instance.getInt(key, 0) as? T
            }
            java.lang.Long::class.java, Long::class.java -> {
                ContainerManager.instance.getLong(key, 0L) as? T
            }
            java.lang.Double::class.java, Double::class.java -> {
                ContainerManager.instance.getDouble(key, 0.0) as? T
            }
            java.lang.String::class.java, String::class.java -> {
                ContainerManager.instance.getString(key, "") as? T
            }
            java.lang.Boolean::class.java, Boolean::class.java -> {
                // 如果 Manager 没提供 getBoolean，可以按需补充或通过 getString 解析
                ContainerManager.instance.getBoolean(key, false) as? T
            }
            else -> {
                // 3. 自定义实体类 (BizLocation, UserInfo等) 走 Json 反序列化
                ContainerManager.instance.getJsonEntity(key, clazz)
            }
        }
    }

    protected open fun isAccountObserverEnabled(): Boolean {
        return false;
    }

    companion object{
        private const val TAG = "Mirror_MirrorViewModel==>";
    }
}
