package com.wsvita.framework.local.manager.device

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.wsvita.framework.local.BaseManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

/**
 * ### 网络信息管理组件
 * * **1. 主要职责**
 * - 获取设备 IP 地址、网络类型及连接状态。
 *
 * **2. 设计原则**
 * - **实时性**：网络状态变化频繁，不进行磁盘持久化缓存。
 * - **简洁性**：采用主动获取模式，不维护复杂的广播监听。
 *
 * @author Administrator
 * @createTime 2026/1/12 11:50
 */
class NetworkInfoManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_NetworkInfoManager=>"
        val instance: NetworkInfoManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { NetworkInfoManager() }
    }

    override fun onInit() {}

    /**
     * 获取当前网络类型
     * @return 0: 未知/断开, 1: WiFi, 2: 蜂窝数据, 3: 以太网
     */
    @SuppressLint("MissingPermission")
    fun getNetworkType(context: Context): Int {
        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return 0

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork ?: return 0
            val actNw = cm.getNetworkCapabilities(nw) ?: return 0
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> 1
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> 2
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> 3
                else -> 0
            }
        } else {
            // 旧版本兼容逻辑
            @Suppress("DEPRECATION")
            val info = cm.activeNetworkInfo
            if (info == null || !info.isConnected) return 0
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> 1
                ConnectivityManager.TYPE_MOBILE -> 2
                ConnectivityManager.TYPE_ETHERNET -> 3
                else -> 0
            }
        }
    }

    /**
     * 获取本地 IPv4 地址
     * @return 示例: "192.168.1.105"
     */
    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in Collections.list(interfaces)) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: ""
                    }
                }
            }
        } catch (e: Exception) {
            // 忽略异常
        }
        return "0.0.0.0"
    }
}
