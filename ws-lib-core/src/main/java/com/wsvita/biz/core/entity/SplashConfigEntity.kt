package com.wsvita.biz.core.entity

import com.wsvita.core.common.BaseEntity

/**
 * 启动页配置实体类
 *
 * create by Eastevil at 2025/12/31 11:22
 * @author Eastevil
 */
class SplashConfigEntity : BaseEntity() {

    /**
     * 欢迎页关联的活动 ID
     */
    var eventId: Long = 0

    /**
     * 背景图片地址 (URL)
     */
    var bgimageUrl: String? = null

    /**
     * 标题文本
     */
    var title: String? = null

    /**
     * 展示内容文本（副标题或详细描述）
     */
    var content: String? = null

    /**
     * 创建时间 (时间戳)
     * 注意：Java 端使用的是 Date 类型，此处建议确认是毫秒值还是秒值
     */
    var createDate: Long = 0

    override fun customLayoutId(): Int {
        return 0;
    }

}
