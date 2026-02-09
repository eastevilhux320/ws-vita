package ext

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 文件与 Uri 处理扩展类
 * 适配 Android 6.0 - 15.0 全版本环境下的权限判定、Uri 转换与数据校验。
 * 针对 Android 13+ 媒体权限及 Android 10+ 分区存储进行了深度适配。
 * @author Administrator
 * @date 2025/12/28 1:00
 */
object FileExt {

    /**
     * 判断字符串是否为无效数据。
     * 该方法不仅检查 Kotlin 原生的 null 和空字符串，
     * 还针对业务中常见的 "null" 字符串及全空格字符串进行了拦截。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return
     * true-表示字符串为 null、空、仅包含空格或等于 "null"(不区分大小写)；否则返回 false。
     */
    fun String?.isInvalid(): Boolean {
        if (this == null) return true
        val trimmed = this.trim()
        return trimmed.isEmpty() || trimmed.equals("null", ignoreCase = true)
    }

    /**
     * 获取当前系统版本所需的媒体读取权限数组。
     * 适配 Android 6.0 到 Android 15.0。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return 对应版本的权限字符串数组
     */
    fun getRequiredMediaPermissions(): Array<String> {
        return when {
            // Android 13 (API 33) 及以上：细分媒体权限
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
            }
            // Android 6.0 到 Android 12
            else -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * 将 Uri 拷贝到 App 私有缓存目录下。
     * 解决 Android 10+ 分区存储限制，确保在 Android 15 等高版本下依然能获得 File 对象。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return 拷贝后的 File 对象，失败则返回 null。
     */
    fun Uri.copyToCache(context: Context, fileName: String = "wsui_temp_${System.currentTimeMillis()}.jpg"): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(this)
            val cacheFile = File(context.cacheDir, fileName)
            inputStream?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            cacheFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将 Uri 转换为可操作的 File 对象。
     * 内部适配 Android 6-15 全版本：
     * 1. 针对 file 协议直接返回；
     * 2. 针对 content 协议执行沙盒拷贝，规避全版本存储权限变更导致的无法读取问题。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return 转换后的 File 对象，失败则返回 null。
     */
    fun Uri.toFile(context: Context): File? {
        return when (this.scheme) {
            "file" -> this.path?.let { File(it) }
            "content" -> this.copyToCache(context)
            else -> null
        }
    }

    /**
     * 将 Uri 直接转换为 Bitmap 图像。
     * 采用 ContentResolver 流读取，支持 Android 15 存储架构。
     * @author Administrator
     * @date 2025/12/28 1:00
     * @return 解码后的 Bitmap，失败则返回 null。
     */
    fun Uri.toBitmap(context: Context): Bitmap? {
        return try {
            context.contentResolver.openInputStream(this)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
