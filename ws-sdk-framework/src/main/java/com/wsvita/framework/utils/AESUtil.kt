package com.wsvita.framework.utils

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.min

/**
 * 最终版 AESUtil - 严格对齐后端 io.renren.common.utils.AESUtil 逻辑
 */
class AESUtil {

    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val IV_LENGTH = 16

        /**
         * 1. 密钥派生：根据 ID 生成 256 位固定密钥的 Base64 字符串
         * 对齐后端 generateFixedKey256
         */
        @JvmStatic
        fun getKeyByPass(psdkey: String, keysize: Int): String? {
            return try {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(psdkey.toByteArray(StandardCharsets.UTF_8))
                // 注意：后端 generateFixedKey256 实际上是取了完整的 SHA-256 (32字节/256位)
                // 这里我们直接返回 Base64，与后端保持一致
                Base64.encodeToString(hash, Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 2. 解密逻辑：对齐后端 decrypt 方法
         * 处理 Base64(IV + Ciphertext) 格式
         */
        @JvmStatic
        fun aesDecrypt(encryptedData: String?, rawKey: String?): String {
            if (encryptedData.isNullOrEmpty() || rawKey.isNullOrEmpty()) return ""
            return try {
                // A. 准备密钥：将 Base64 格式的密钥解码为字节
                val keyBytes = Base64.decode(rawKey, Base64.DEFAULT)
                val secretKey = SecretKeySpec(keyBytes, ALGORITHM)

                // B. 解码密文数据
                val combined = Base64.decode(encryptedData, Base64.DEFAULT)
                if (combined.size < IV_LENGTH) return ""

                // C. 分离 IV (前 16 字节) 和 密文 (剩余字节)
                val ivBytes = combined.sliceArray(0 until IV_LENGTH)
                val cipherText = combined.sliceArray(IV_LENGTH until combined.size)

                // D. 初始化 Cipher
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(ivBytes))

                // E. 执行解密
                val original = cipher.doFinal(cipherText)
                String(original, StandardCharsets.UTF_8)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        /**
         * 3. 加密逻辑：对齐后端 encrypt 方法
         * 输出格式为 Base64(随机IV + 密文)
         */
        @JvmStatic
        fun aesEncrypt(data: String?, rawKey: String?): String {
            if (data.isNullOrEmpty() || rawKey.isNullOrEmpty()) return ""
            return try {
                val keyBytes = Base64.decode(rawKey, Base64.DEFAULT)
                val secretKey = SecretKeySpec(keyBytes, ALGORITHM)

                // 生成随机 IV
                val ivBytes = ByteArray(IV_LENGTH)
                java.security.SecureRandom().nextBytes(ivBytes)

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(ivBytes))

                val encrypted = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))

                // 拼接 IV + 密文
                val combined = ByteBuffer.allocate(ivBytes.size + encrypted.size)
                    .put(ivBytes)
                    .put(encrypted)
                    .array()

                Base64.encodeToString(combined, Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        // --- 存量方法适配 ---
        @JvmStatic
        fun byteToHexString(bytes: ByteArray): String {
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
