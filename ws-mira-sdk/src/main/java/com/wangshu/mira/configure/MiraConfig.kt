package com.wangshu.mira.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig

class MiraConfig : CommonBaseConfig<MiraConfig.Builder> {
    var appKey : String? = null;

    /**
     * 商户密钥
     */
    var secretKey : String? = null;

    /**
     * 商户号
     */
    var merchantNo : String? = null;

    /**
     * 权限分发模式：是否由系统（玄同中枢）自动接管权限申请
     * true:  中枢自动处理 (符合 ws-vita 标准化流程)
     * false: 业务手动接管 (交给 ws-vision/ws-mira 灵活实现)
     */
    var isAutoPermission : Boolean = false;

    private constructor(builder: Builder) : super(builder){
        this.appKey = builder.appKey;
        this.secretKey = builder.secretKey;
        this.merchantNo = builder.merchantNo;
        this.isAutoPermission = builder.isAutoPermission;
    }

    class Builder : BaseConfigBuilder<MiraConfig> {
        internal var appKey : String? = null;
        internal var secretKey : String? = null;
        /**
         * 商户号
         */
        internal var merchantNo : String? = null;

        /**
         * 权限分发模式：是否由系统（玄同中枢）自动接管权限申请
         * true:  中枢自动处理 (符合 ws-vita 标准化流程)
         * false: 业务手动接管 (交给 ws-vision/ws-mira 灵活实现)
         */
        internal var isAutoPermission : Boolean = false;


        constructor(appId: Long,appKey : String,secretKey : String) : super(appId) {
            this.appKey = appKey;
            this.secretKey = secretKey;
        }

        fun merchantNo(merchantNo : String): Builder {
            this.merchantNo = merchantNo;
            return this;
        }

        /**
         * 设置权限自动化处理策略
         * create by Eastevil at 2026/3/3 11:50
         * @author Eastevil
         * @param b
         *      true-中枢自动处理 (符合 ws-mira 标准化流程)
         *      false-业务手动接管
         * @return
         *      链式调用对象
         */
        fun isAutoPermission(b : Boolean): Builder {
            this.isAutoPermission = b;
            return this
        }

        override fun builder(): MiraConfig {
            return MiraConfig(this);
        }
    }
}
