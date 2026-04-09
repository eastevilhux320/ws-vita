package com.wangshu.mira.entity

import java.io.File

/**
 * 任务步骤提交参数
 */
class TaskStepParamEntity {

    var stepId : Long? = null;

    /**
     * 步骤填充的文件
     */
    var file : File? = null;

    /**
     * 当前步骤的填充文件名称
     */
    var stepFileName : String? = null;
}
