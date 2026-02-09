package com.wsvita.core.local.manager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.wsvita.core.media.OnMediaResultListener
import com.wsvita.framework.local.BaseManager
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

/**
 * 媒体选择管理类
 * * 职责：
 * 1. 封装系统相册选择器与相机拍照逻辑。
 * 2. 统一管理媒体请求的声明周期注册。
 * 3. 通过 requestCode 机制实现单页面多业务场景的结果分发。
 * create by Eastevil at 2026/1/26 15:00
 * @author Eastevil
 */
class MediaPickerManager : BaseManager {

    private constructor() : super()

    companion object {
        private const val TAG = "WSVita_F_M_MediaPickerManager=>"
        /**
         * 权限常量定义
         */
        private val PERMS_CAMERA = arrayOf(Manifest.permission.CAMERA)

        val instance: MediaPickerManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            MediaPickerManager()
        }
    }

    private var pickLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var captureLauncher: ActivityResultLauncher<Uri>? = null
    private var tempUri: Uri? = null

    // 当前正在进行的请求码
    private var currentRequestCode: Int = -1

    override fun onInit() { }

    /**
     * 权限检查抽象方法
     * 供外部在 openCamera 前置判断，避免 SecurityException
     */
    fun hasCameraPermission(context: Context): Boolean {
        return EasyPermissions.hasPermissions(context, *PERMS_CAMERA)
    }

    /**
     * 注册媒体请求
     * 利用 ActivityResultRegistry 实现生命周期安全的注册
     */
    fun register(
        registryOwner: ActivityResultRegistryOwner,
        lifecycleOwner: LifecycleOwner,
        listener: OnMediaResultListener
    ) {
        val registry = registryOwner.activityResultRegistry

        // 相册回调：显式实现，通过 currentRequestCode 传回给接口
        val pickCallback = object : ActivityResultCallback<Uri?> {
            override fun onActivityResult(result: Uri?) {
                listener.onMediaResult(currentRequestCode, result)
                currentRequestCode = -1 // 结束后重置
            }
        }

        // 拍照回调
        val captureCallback = object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(result: Boolean) {
                if (result) {
                    listener.onMediaResult(currentRequestCode, tempUri)
                } else {
                    listener.onMediaResult(currentRequestCode, null)
                }
                currentRequestCode = -1
            }
        }

        pickLauncher = registry.register(
            "wsui_picker_${lifecycleOwner.hashCode()}",
            lifecycleOwner,
            ActivityResultContracts.PickVisualMedia(),
            pickCallback
        )

        captureLauncher = registry.register(
            "wsui_camera_${lifecycleOwner.hashCode()}",
            lifecycleOwner,
            ActivityResultContracts.TakePicture(),
            captureCallback
        )
    }

    /**
     * 触发时必须带入 requestCode
     */
    fun openGallery(tag: Int) {
        this.currentRequestCode = tag
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        pickLauncher?.launch(request)
    }

    /**
     * 开启相机逻辑
     * 内部加入权限拦截与异常捕获
     */
    fun openCamera(context: Context, authority: String, tag: Int) {
        // 1. 权限拦截：防止未授权导致的 SecurityException
        if (!hasCameraPermission(context)) {
            android.util.Log.e(TAG, "Missing CAMERA permission, operation cancelled.")
            return
        }

        this.currentRequestCode = tag
        try {
            tempUri = generateWsuiTempUri(context, authority)
            tempUri?.let {
                captureLauncher?.launch(it)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to launch camera: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * 生成临时 URI
     * 适配 Android 9/10：建议使用外部私有路径以绕过分区存储限制
     */
    private fun generateWsuiTempUri(context: Context, authority: String): Uri? {
        return try {
            val fileName = "wsui_img_${System.currentTimeMillis()}.jpg"
            // 改用 getExternalFilesDir，配合 paths.xml 的 external-files-path 完美适配 root
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, fileName)

            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "generateWsuiTempUri error: ${e.message}")
            null
        }
    }
}
