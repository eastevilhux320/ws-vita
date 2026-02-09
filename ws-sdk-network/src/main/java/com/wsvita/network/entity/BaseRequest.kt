package com.wsvita.network.entity

import com.wsvita.network.configure.NetworkConfigure

/**
 * Description 网络请求基础抽象类，用于自动填充全局通用的请求参数（如 appId、版本信息、渠道等）
 * create by Eastevil at 2025/12/25 10:20
 * @author Eastevil
 */
abstract class BaseRequest {

    /**
     * Description 应用所属唯一标识 ID，默认从 NetworkConfigure 实例中获取
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var appId : Long? = NetworkConfigure.instance.appId();

    /**
     * Description 当前应用的版本 Code，用于后端进行版本兼容性逻辑处理
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var version : Int? = NetworkConfigure.instance.getConfig()?.version;

    /**
     * Description 当前应用的版本名称（如 1.0.0），用于展示或日志审计
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var versionName : String? = NetworkConfigure.instance.getConfig()?.versionName

    /**
     * Description 渠道名称/编码，用于统计不同应用市场的下载与使用数据
     * create by Eastevil at 2025/12/25 10:20
     * @author Eastevil
     */
    var channel : String? = NetworkConfigure.instance.getConfig()?.channelCode;
}
