package com.wsvita.framework.utils

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Description RSA 加密工具类
 * create by Eastevil at 2025/12/29 11:45
 * @author Eastevil
 */
class RSAUtil {

    companion object {
        /**
         * Description 加密算法 RSA
         */
        private const val KEY_ALGORITHM = "RSA"

        /**
         * Description 获取公钥的 key
         */
        @JvmField
        val PUBLIC_KEY = "RSAPublicKey"

        /**
         * Description 获取私钥的 key
         */
        @JvmField
        val PRIVATE_KEY = "RSAPrivateKey"

        /**
         * Description 签名算法
         */
        private const val SIGNATURE_ALGORITHM = "MD5withRSA"

        /**
         * Description 常量 0
         */
        private const val ZERO = 0

        /**
         * Description RSA 最大加密明文最大大小
         */
        private const val MAX_ENCRYPT_BLOCK = 117

        /**
         * Description RSA 最大解密密文最大大小
         * 当密钥位数为 1024 时为 128；为 2048 时需改为 256
         */
        private const val MAX_DECRYPT_BLOCK = 128

        /**
         * Description 默认 key 大小
         */
        private const val DEFAULT_KEY_SIZE = 1024

        /**
         * Description 生成密钥对 (公钥和私钥)
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @return Map<String, String>? 包含 Base64 编码后的公私钥对
         */
        @JvmStatic
        @Throws(Exception::class)
        fun genKeyPair(): Map<String, String> {
            val keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM)
            keyPairGen.initialize(DEFAULT_KEY_SIZE)
            val keyPair = keyPairGen.generateKeyPair()
            val publicKey = keyPair.public as RSAPublicKey
            val privateKey = keyPair.private as RSAPrivateKey
            val keyMap = HashMap<String, String>(2)
            keyMap[PUBLIC_KEY] = Base64Util.encode(publicKey.encoded) ?: ""
            keyMap[PRIVATE_KEY] = Base64Util.encode(privateKey.encoded) ?: ""
            return keyMap
        }

        /**
         * Description 公钥加密
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 待加密的数据
         * @param publicKey Base64 编码的 RSA 公钥
         * @return String? 加密后进行 Base64 编码的数据
         */
        @JvmStatic
        @Throws(Exception::class)
        fun encryptByPublicKey(data: String, publicKey: String): String? {
            val keyBytes = Base64Util.decode(publicKey)
            val x509KeySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val entryByte = data.toByteArray(StandardCharsets.UTF_8)
            val encryptedByte = encrypt(entryByte, keyFactory, keyFactory.generatePublic(x509KeySpec))
            return Base64Util.encode(encryptedByte)
        }

        /**
         * Description 私钥加密
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 源数据
         * @param privateKey 私钥 (BASE64 编码)
         * @return String? 加密后的结果
         */
        @JvmStatic
        fun encryptByPrivateKey(data: String, privateKey: String): String? {
            return try {
                val keyBytes = Base64Util.decode(privateKey)
                val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
                val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
                val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
                val entryByte = data.toByteArray(StandardCharsets.UTF_8)
                Base64Util.encode(encrypt(entryByte, keyFactory, privateK))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Description 私钥解密
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 需要解密的数据 (Base64 字符串)
         * @param privateKey RSA 私钥 (Base64 编码)
         * @return String 解密后的明文
         */
        @JvmStatic
        @Throws(Exception::class)
        fun decryptByPrivateKey(data: String, privateKey: String): String {
            val keyBytes = Base64Util.decode(privateKey)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val encryptedData = Base64Util.decode(data)
            val decryptedData = decrypt(encryptedData, keyFactory, keyFactory.generatePrivate(pkcs8KeySpec))
            return String(decryptedData, StandardCharsets.UTF_8)
        }

        /**
         * Description 公钥解密
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 已加密数据 (Base64 字符串)
         * @param publicKey 公钥 (Base64 编码)
         * @return String 解密后的明文
         */
        @JvmStatic
        @Throws(Exception::class)
        fun decryptByPublicKey(data: String, publicKey: String): String {
            val keyBytes = Base64Util.decode(publicKey)
            val x509KeySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val publicK = keyFactory.generatePublic(x509KeySpec)
            val encryptedData = Base64Util.decode(data)
            val decryptedData = decrypt(encryptedData, keyFactory, publicK)
            return String(decryptedData, StandardCharsets.UTF_8)
        }

        /**
         * Description 用私钥对信息生成数字签名
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 已加密数据字节数组
         * @param privateKey 私钥 (Base64 编码)
         * @return String? 数字签名
         */
        @JvmStatic
        @Throws(Exception::class)
        fun sign(data: ByteArray, privateKey: String): String? {
            val keyBytes = Base64Util.decode(privateKey)
            val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
            val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
            signature.initSign(privateK)
            signature.update(data)
            return Base64Util.encode(signature.sign())
        }

        /**
         * Description 校验数字签名
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         * @param data 已加密数据字节数组
         * @param publicKey 公钥 (Base64 编码)
         * @param sign 数字签名
         * @return Boolean 是否验证成功
         */
        @JvmStatic
        @Throws(Exception::class)
        fun verify(data: ByteArray, publicKey: String, sign: String): Boolean {
            val keyBytes = Base64Util.decode(publicKey)
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
            val publicK = keyFactory.generatePublic(keySpec)
            val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
            signature.initVerify(publicK)
            signature.update(data)
            return signature.verify(Base64Util.decode(sign))
        }

        /**
         * Description 解密公共方法
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         */
        private fun decrypt(data: ByteArray, keyFactory: KeyFactory, key: Key): ByteArray {
            val cipher = Cipher.getInstance(keyFactory.algorithm)
            cipher.init(Cipher.DECRYPT_MODE, key)
            return encryptAndDecrypt(data, cipher, MAX_DECRYPT_BLOCK)
        }

        /**
         * Description 加密公共方法
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         */
        private fun encrypt(data: ByteArray, keyFactory: KeyFactory, key: Key): ByteArray {
            val cipher = Cipher.getInstance(keyFactory.algorithm)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return encryptAndDecrypt(data, cipher, MAX_ENCRYPT_BLOCK)
        }

        /**
         * Description 加密解密分段处理公共方法
         * create by Eastevil at 2025/12/29 11:45
         * @author Eastevil
         */
        private fun encryptAndDecrypt(data: ByteArray, cipher: Cipher, maxSize: Int): ByteArray {
            val inputLen = data.size
            val out = ByteArrayOutputStream()
            var offSet = ZERO
            var i = ZERO
            while (inputLen - offSet > ZERO) {
                val cache = if (inputLen - offSet > maxSize) {
                    cipher.doFinal(data, offSet, maxSize)
                } else {
                    cipher.doFinal(data, offSet, inputLen - offSet)
                }
                out.write(cache, ZERO, cache.size)
                i++
                offSet = i * maxSize
            }
            val outputData = out.toByteArray()
            out.close()
            return outputData
        }
    }
}
