package com.wangshu.mira.entity

import java.io.File

/**
 * 步骤操作实际实体类
 */
class StepOperationEvent {

    /**
     * 操作的下标
     */
    var position : Int? = null;

    /**
     *  操作的步骤
     */
    var step : TaskStepEntity? = null;

    /**
     * 填充的文件
     */
    var fillingFile : File? = null;
}
