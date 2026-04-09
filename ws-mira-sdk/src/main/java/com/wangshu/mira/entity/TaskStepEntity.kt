package com.wangshu.mira.entity

import com.wangshu.mira.R
import com.wangshu.mira.entity.enums.StepFileType
import com.wsvita.core.common.BaseEntity
import java.io.File

class TaskStepEntity : BaseEntity() {

    /** 关联的任务ID */
    var taskId: Long? = null

    /** 步骤序号 (1, 2, 3...) */
    var stepOrder: Int = 0

    /** 步骤标题 */
    var stepTitle: String? = null

    /** 步骤详细操作描述 */
    var stepDescription: String? = null

    /** * 步骤类型 (TEXT-DISPLAY, JUMP_APP, WATCH_VIDEO, FOLLOW)
     * 对应不同的布局 ID
     */
    var stepType: String? = null

    /** 业务配置参数 (JSON格式: 存储博主UID、包名等) */
    var configData: String? = null

    /** 示例图片URL */
    var exampleImageUrl: String? = null

    /** 是否有示例图 (0-无, 1-有) */
    var hasExample: Int = 0

    /** 示例图展示类型 (1-横向, 2-纵向) */
    var exampleDisplayType: Int = 1

    /** 是否需要提交文件 (1-是, 0-否) */
    var isSubmitRequired: Int = 0

    /** 提交的文件类型 (1-mp3, 2-mp4, 3-jpg) */
    var submitFileType: Int? = null

    /** 限制总大小 (Byte) */
    var fileMaxSize: Long = 0

    /**
     * 需要上传的文件，这个文件由前端产生，在展示的时候，具体根据类型进行展示
     */
    var stepFile : File? = null;

    /**
     * 对应的文件是否已经填充,用来再UI展示的时候进行使用的
     */
    var isFilling : Boolean = false;

    var inputList : MutableList<StepInputEntity>? = null;

    override fun customLayoutId(): Int {
        val submitType = submitFileType?.let { StepFileType.getByType(it) }?:StepFileType.UNKNOWN;
        if(submitType.isImage()){
            //图片类型，判断是否存在示例图，及各种图片的方向
            if(hasExample == 1){
                if(exampleDisplayType == 1){
                    //展示的是横向示例图
                    return R.layout.recycler_item_mira_taskstep_img_h;
                }else{
                    //展示的是纵向实例图
                    return R.layout.recycler_item_mira_taskstep_img_v;
                }
            }else{
                //没有示例图
                return R.layout.recycler_item_mira_taskstep_img_noexample;
            }
        }else if(submitType.isVideo()){
            //视频
            return R.layout.recycler_item_mira_taskstep_video
        }else if(submitType.isAudio()){
            //音频
            return R.layout.recycler_item_mira_taskstep_audio
        }else if(submitType.isDoc()){
            //文档
            return R.layout.recycler_item_mira_taskstep_document;
        }
        //其他类型可以继续根据实际业务进行判断
        return R.layout.recycler_item_mira_task;
    }

    /**
     * 校验当前步骤是否符合提交要求的标准（输入合法性检查）
     * 1. 检查是否必须提交
     * 2. 检查文件是否填充
     * 3. 检查文件类型与业务要求是否匹配
     * * create by Eastevil at 2026/3/20 17:17
     */
    fun isValidated(): Boolean {
        // 如果该步骤不需要提交任何附件（如仅展示或仅跳转），则视为校验通过
        if (isSubmitRequired == 0) {
            return true
        }

        // 必须提交的情况下，检查填充状态和实体文件是否存在
        if (!isFilling || stepFile == null) {
            return false
        }

        // 获取预定义的业务文件类型
        val submitType = submitFileType?.let { StepFileType.getByType(it) } ?: StepFileType.UNKNOWN

        // 获取实际填充的文件后缀进行二次校验
        val fileName = stepFile?.name?.lowercase() ?: ""

        // 根据业务协议进行匹配
        return when {
            submitType.isImage() -> {
                // 常见的图片格式校验
                fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".png") || fileName.endsWith(".webp")
            }
            submitType.isVideo() -> {
                fileName.endsWith(".mp4") || fileName.endsWith(".mov")
            }
            submitType.isAudio() -> {
                fileName.endsWith(".mp3") || fileName.endsWith(".wav")
            }
            submitType.isDoc() -> {
                fileName.endsWith(".pdf") || fileName.endsWith(".txt") || fileName.endsWith(".doc")
            }
            else -> false
        }
    }
}
