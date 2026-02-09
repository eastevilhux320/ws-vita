package com.wsvita.core.common

import androidx.databinding.ViewDataBinding

/**
 * 导航架构碎片基类 Fragment。
 *
 * 该类是组件化开发中 Navigation 体系的核心支撑类。它通过泛型绑定特定的 [ViewDataBinding]
 * 与 [NavigationViewModel]，并支持从宿主容器 [AppContainerActivity] 获取预存数据。
 *
 * 特性说明：
 * 1. 支持多 Fragment 碎片在同一容器下的挂载管理。
 * 2. 核心数据通过 [getIntentData] 及其扩展方法实现安全提取。
 *
 * create by Eastevil at 2026/1/20 15:30
 * @author Eastevil
 */
abstract class NavigationFragment<D : ViewDataBinding, V : NavigationViewModel> :
    AppFragment<D, V>() {

    /**
     * 获取由宿主容器预存的业务数据（Integer类型）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentInt(key: String): Int? {
        return getNavIntentData(key) as? Int
    }

    /**
     * 获取由宿主容器预存的业务数据（Long类型）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentLong(key: String): Long? {
        return getNavIntentData(key) as? Long
    }

    /**
     * 获取由宿主容器预存的业务数据（String类型）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentString(key: String): String? {
        return getNavIntentData(key) as? String
    }

    /**
     * 获取由宿主容器预存的业务数据（Boolean类型）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentBoolean(key: String): Boolean? {
        return getNavIntentData(key) as? Boolean
    }

    /**
     * 获取由宿主容器预存的业务数据（Double类型）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    protected fun getIntentDouble(key: String): Double? {
        return getNavIntentData(key) as? Double
    }

    /**
     * 获取由宿主容器预存的业务数据（Serializable/Parcelable对象）。
     *
     * 该方法是组件化传参的核心入口。它通过当前 Fragment 的物理 [getId] 作为索引，
     * 从宿主容器的 ContainerManager 中提取数据。
     *
     * 调用场景：
     * 1. 在初始化阶段获取从上个页面传递过来的 Intent 参数。
     * 2. 获取在 AppContainerActivity 中自动挂载的全量业务数据。
     *
     * create by Eastevil at 2026/1/20 14:59
     * @author Eastevil
     *
     * @param key 业务参数对应的键名。
     * @return 存储的数据对象，需自行根据业务类型进行强制转换；若不存在则返回 null。
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <T> getIntentSerializable(key: String): T? {
        return getNavIntentData(key) as? T
    }

    private fun getNavIntentData(key : String): Any? {
        val id = navigationId();
        return getIntentData(id,key);
    }

    /**
     * 获取当前 Fragment 碎片在 Navigation 体系中的唯一业务标识。
     * * 【标注】：该 ID 对应的是 res/navigation/下的xml 资源文件中，
     * * 当前 Fragment 节点的 android:id 属性值（例如：R.id.plateLicenseFragment）
     *
     * * 该标识用于在宿主容器的 ContainerManager 中精确定位数据缓存空间。
     * * 子类必须实现此方法，并返回对应的 Navigation Destination ID。
     *
     * create by Eastevil at 2026/1/20 16:05
     * @author Eastevil
     *
     * @return 当前碎片的导航业务 ID (即 XML 中的 android:id)。
     */ 
    abstract fun navigationId() : Int;
}
