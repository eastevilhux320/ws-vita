package com.wsvita.framework.local.manager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager as AndroidLocationManager
import androidx.core.content.ContextCompat
import com.wsvita.framework.commons.BaseApplication
import com.wsvita.framework.local.BaseManager

class LocationManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_LocationManager=>"

        val instance: LocationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LocationManager()
        }
    }

    override fun onInit() {
        // 初始化逻辑（如需要）
    }

    /**
     * 获取最后已知的经纬度
     * 返回 Pair(纬度, 经度)，无权限或获取失败时返回 null
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Pair<String, String>? {
        val context = BaseApplication.app ?: return null

        // 1. 权限前置检查
        if (!checkPermission(context)) {
            return null
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? AndroidLocationManager
            ?: return null

        // 2. 获取所有可用的 Provider (GPS, Network, Passive)
        // 按照精度从高到低尝试获取缓存位置
        val providers = manager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val l = manager.getLastKnownLocation(provider) ?: continue
            // 挑选最新的或者精度最高的位置
            if (bestLocation == null || l.accuracy < bestLocation!!.accuracy) {
                bestLocation = l
            }
        }

        return bestLocation?.let {
            Pair(it.latitude.toString(), it.longitude.toString())
        }
    }

    /**
     * 检查是否具备位置权限（wsui 相关业务通常至少需要粗略位置）
     */
    private fun checkPermission(context: Context): Boolean {
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        return coarse == PackageManager.PERMISSION_GRANTED || fine == PackageManager.PERMISSION_GRANTED
    }
}
