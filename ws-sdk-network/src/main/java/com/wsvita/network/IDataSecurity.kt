package com.wsvita.network

/**
 * 数据安全处理器接口。
 * 在组件化架构中，由安全业务组件实现，通过 NetworkClient 注入网络层。
 * create by Administrator at 2025/12/28 1:47
 * @author Administrator
 */
interface IDataSecurity {
    /**
     * 数据解密处理
     * @param data 需要解密的加密字符串
     * @return 解密后的明文数据。若解密失败，建议返回 null 或抛出 SecurityException
     */
    fun decrypt(data: String?): String?

    /**
     * 数据加密处理
     * @param data 需要加密的原始明文
     * @return 加密后的字符串
     */
    fun encrypt(data: String?): String?

    /**
     * 数据验签
     * create by Eastevil at 2026/1/6 15:51
     * @author Eastevil
     * @param
     * @return
     */
    fun signData(data : String) :String?
}
