package com.wangshu.mira.ext

import java.security.MessageDigest

/**
 * 配合玄同(ws-vita)验签逻辑的 MD5 工具类
 */
object MD5Util {

    /**
     * 对数据进行MD5加密处理
     * 用于 AppResult 的验签以及 secret_key 相关逻辑
     */
    @JvmStatic
    fun encryptDate(data: String?): String? {
        if (data.isNullOrEmpty()) {
            return null
        }
        return try {
            val messageDigest = MessageDigest.getInstance("MD5")
            val inputByteArray = data.toByteArray(Charsets.UTF_8)
            messageDigest.update(inputByteArray)
            val resultByteArray = messageDigest.digest()
            byteArrayToHex(resultByteArray)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 将字节数组转为大写十六进制字符串
     */
    private fun byteArrayToHex(byteArray: ByteArray): String {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
        val resultCharArray = CharArray(byteArray.size * 2)
        var index = 0
        for (b in byteArray) {
            val i = b.toInt()
            resultCharArray[index++] = hexDigits[i ushr 4 and 0xf]
            resultCharArray[index++] = hexDigits[i and 0xf]
        }
        return String(resultCharArray)
    }
}

// 快速测试脚本逻辑
fun main() {
    println(MD5Util.encryptDate("V10001996623831252299777"))
    println(MD5Util.encryptDate("S10001996772103270920193"))
    println(MD5Util.encryptDate("M10002001125663660011521"))
}
