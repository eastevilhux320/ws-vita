package com.wangshu.mira.local.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import com.wangshu.mira.R
import com.wsvita.framework.local.BaseManager
import pub.devrel.easypermissions.EasyPermissions

/**
 * 玄映 (ws-mira) 权限中枢管理器
 * 遵循 ws-vita 底层框架协议，所有权限逻辑在此内聚
 */
class PermissionManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_PermissionManager=>"
        const val RC_MIRA_PERMISSIONS = 1001

        val instance: PermissionManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PermissionManager()
        }

        // --- 静态权限定义 ---

        private val PERMISSIONS_TIRAMISU = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        private val PERMISSIONS_LEGACY = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        /**
         * 获取当前系统所需的权限数组
         */
        private fun getRequiredPermissions(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PERMISSIONS_TIRAMISU
            } else {
                PERMISSIONS_LEGACY
            }
        }
    }

    override fun onInit() {
        // 初始化
    }

    /**
     * 校验权限状态
     */
    fun hasPermissions(context: Context): Boolean {
        return EasyPermissions.hasPermissions(context, *getRequiredPermissions())
    }

    /**
     * 执行权限请求
     * @param host 只能是 Activity 或 Fragment
     */
    fun requestPermissions(host: Activity) {
        val rationale = host.getString(R.string.mira_permission_rationale)
        EasyPermissions.requestPermissions(
            host,
            rationale,
            RC_MIRA_PERMISSIONS,
            *getRequiredPermissions()
        )
    }

    /**
     * 在 Activity 的 onRequestPermissionsResult 中调用此方法进行逻辑分发
     */
    fun handleRequestResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray, receiver: Any) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, receiver)
    }
}
