package com.wsvita.framework.utils

import android.util.Base64
import java.io.*

/**
 * Base64 编解码及文件转换工具类
 * create by Eastevil at 2025/12/29 10:28
 * @author Eastevil
 */
class Base64Util private constructor() {

    companion object {
        private const val CACHE_SIZE = 1024

        /**
         * BASE64 字符串解码为二进制数据
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param base64 BASE64 编码的字符串
         * @return ByteArray 解码后的二进制字节数组
         */
        @JvmStatic
        fun decode(base64: String?): ByteArray {
            if (base64.isNullOrEmpty()) return byteArrayOf()
            return Base64.decode(base64, Base64.DEFAULT)
        }

        /**
         * 二进制数据编码为 BASE64 字符串
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param bytes 待编码的二进制数据
         * @return String? 编码后的 BASE64 字符串
         */
        @JvmStatic
        fun encode(bytes: ByteArray?): String? {
            if (bytes == null || bytes.isEmpty()) return ""
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }

        /**
         * 将文件内容编码为 BASE64 字符串
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param filePath 文件的绝对路径
         * @return String? 文件内容的 BASE64 编码字符串
         */
        @Throws(IOException::class)
        @JvmStatic
        fun encodeFile(filePath: String?): String? {
            val bytes = fileToByte(filePath)
            return if (bytes.isNotEmpty()) encode(bytes) else ""
        }

        /**
         * 将 BASE64 字符串解码并保存为文件
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param filePath 目标保存路径
         * @param base64 BASE64 编码的源字符串
         */
        @Throws(IOException::class)
        @JvmStatic
        fun decodeToFile(filePath: String?, base64: String?) {
            if (base64.isNullOrEmpty() || filePath.isNullOrEmpty()) return
            val bytes = decode(base64)
            byteArrayToFile(bytes, filePath)
        }

        /**
         * 文件读取为二进制数组
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param filePath 文件绝对路径
         * @return ByteArray 文件二进制数据
         */
        @Throws(IOException::class)
        @JvmStatic
        fun fileToByte(filePath: String?): ByteArray {
            if (filePath.isNullOrEmpty()) return byteArrayOf()
            val file = File(filePath)
            return if (file.exists() && file.isFile) {
                file.readBytes()
            } else {
                byteArrayOf()
            }
        }

        /**
         * 二进制数据写入指定文件路径
         * create by Eastevil at 2025/12/29 10:28
         * @author Eastevil
         * @param bytes 待写入的二进制数据
         * @param filePath 文件绝对路径
         */
        @Throws(IOException::class)
        @JvmStatic
        fun byteArrayToFile(bytes: ByteArray?, filePath: String?) {
            if (bytes == null || filePath.isNullOrEmpty()) return
            val destFile = File(filePath)
            destFile.parentFile?.let {
                if (!it.exists()) it.mkdirs()
            }
            destFile.writeBytes(bytes)
        }
    }
}
