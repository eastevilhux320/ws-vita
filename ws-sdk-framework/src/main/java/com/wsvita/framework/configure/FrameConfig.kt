package com.wsvita.framework.configure

import com.wsvita.framework.commons.BaseConfigBuilder
import com.wsvita.framework.commons.CommonBaseConfig

/**
 * 框架通用配置实现类
 * 继承自 CommonBaseConfig，用于统一管理框架层级的各项基础配置参数
 * @author Eastevil
 * @createTime 2025/12/24
 * @see CommonBaseConfig
 */
class FrameConfig : CommonBaseConfig<FrameConfig.Builder> {

    /**
     * 私有构造方法，强制通过 Builder 模式进行实例化
     * @param builder 配置构建器
     * @author Eastevil
     * @createTime 2025/12/24
     */
    private constructor(builder: Builder) : super(builder)

    /**
     * FrameConfig 专用的构建器类
     * 继承自 BaseConfigBuilder，负责具体配置项的收集与 FrameConfig 实例的生成
     * * @param appId 应用唯一标识 ID
     * @author Eastevil
     * @createTime 2025/12/24
     */
    class Builder(appId: Long) : BaseConfigBuilder<FrameConfig>(appId) {

        /**
         * 实现父类的构建方法，实例化 FrameConfig
         * * @return 返回初始化完毕的 FrameConfig 对象
         * @author Eastevil
         * @createTime 2025/12/24
         */
        override fun builder(): FrameConfig {
            return FrameConfig(this)
        }
    }
}
