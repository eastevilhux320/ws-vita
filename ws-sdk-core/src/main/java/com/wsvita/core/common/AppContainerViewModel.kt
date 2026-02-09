package com.wsvita.core.common

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.wsvita.core.local.manager.ContainerManager

/**
 * 业务层容器ViewModel基类
 * * [功能描述]
 * 1. 负责管理Container下挂载的多个Fragment的业务逻辑与数据中转。
 * 2. 结合ContainerManager实现Fragment级别的数据隔离存储。
 * 3. 监听生命周期，在Fragment销毁时自动清理关联的内存缓存，防止内存泄露。
 *
 * create by Eastevil at 2026/1/20 13:56
 * @author Eastevil
 */
abstract class AppContainerViewModel(application: Application) : AppViewModel(application) {

    protected lateinit var containerManager : ContainerManager;

    override fun initModel() {
        super.initModel()
        containerManager = ContainerManager.instance;
    }

    /**
     * 物理缓存 Fragment 相关的业务数据。
     * * 通常用于在 Activity 接收到 Intent 传参后，将数据通过此方法分发给特定的 Fragment。存入见[AppContainerActivity.onPrepareNavGraph]
     * 方法中在获取到容器下的子类唯一id后进行缓存。是由上一层的Activity跳转到当前的Activity携带的数据，进行缓存后，提供给当前容器下的各个fragment使用
     *
     * * 存储的数据将由 [com.wsvita.core.local.manager.ContainerManager] 维护，直到该 fragmentId 被主动清理。
     *
     * @param fragmentId 目标 Fragment 的资源标识 ID
     * @param key 缓存数据的键值名
     * @param value 需要缓存的数据对象（Any 类型）
     *
     * create by Eastevil at 2026/1/20 13:59
     * @author Eastevil
     * @return
     *      void
     **/
    fun cacheFragmentData(fragmentId : Int,key : String,value : Any?){
        containerManager.put(fragmentId,key,value);
    }

    /**
     * 获取指定 Fragment 缓存的业务数据。
     * * Fragment 在初始化数据阶段调用此方法，根据对应的 Key 取回存储在 [com.wsvita.core.local.manager.ContainerManager]
     * 中的原始数据对象。
     *
     * @return 存储的数据对象，若 Key 不存在则返回 null
     * Description
     * create by Eastevil at 2026/1/20 13:59
     * @author Eastevil
     *
     * @param fragmentId 目标 Fragment 的资源标识 ID
     * @param key 缓存数据的键值名
     *
     * @return
     *      存储的数据对象，若 Key 不存在则返回 null
     */
    fun getFragmentCacheData(fragmentId: Int,key : String): Any? {
        return containerManager.getObj(fragmentId,key);
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }
}
