package com.wsvita.core.media

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File

/**
 * 媒体选择组件 - 负责拍照与相册选择逻辑
 * 遵循显式展示构造方法规范
 */
class MediaPickerComponent : LifecycleObserver {

    private var registry: ActivityResultRegistry
    private lateinit var getContent: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var takePhoto: ActivityResultLauncher<Uri>

    private var tempUri: Uri? = null
    var onMediaResult: ((Uri?) -> Unit)? = null

    // 显式展示构造方法
    constructor(registry: ActivityResultRegistry) {
        this.registry = registry
    }

    /**
     * 在 Activity/Fragment 创建时初始化 Launcher
     */
    fun onCreate(owner: LifecycleOwner) {
        // 相册选择 Launcher
        getContent = registry.register(
            "wsui_gallery_picker",
            owner,
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            onMediaResult?.invoke(uri)
        }

        // 拍照 Launcher
        takePhoto = registry.register(
            "wsui_camera_launcher",
            owner,
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) onMediaResult?.invoke(tempUri) else onMediaResult?.invoke(null)
        }
    }

    /**
     * 打开系统照片选择器 (Photo Picker)
     */
    fun openGallery() {
        getContent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * 启动拍照
     * @param context 上下文用于生成临时 Uri
     */
    fun openCamera(context: Context) {
        val fileName = "wsui_capture_${System.currentTimeMillis()}.jpg"
        // 这里建议使用 FileProvider 生成 Uri
        val file = File(context.cacheDir, fileName)
        tempUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        tempUri?.let { takePhoto.launch(it) }
    }
}
