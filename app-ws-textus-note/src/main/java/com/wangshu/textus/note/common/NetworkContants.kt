package com.wangshu.textus.note.common

object NetworkContants {

    /**
     * 全局连接超时时间（秒）
     */
    const val TIME_OUT = 30L

    /**
     * 默认每页加载数量
     */
    const val PAGE_SIZE = 20


    object ServiceCode{
        /** 成功 */
        const val SUCCESS = 0;
        /** 服务器错误 */
        const val SERVER_ERROR = 500
        /** 数据为空 */
        const val EMPTY = 704

        // 鉴权相关
        const val TOKEN_ERROR = 401
        const val SIGN_ERROR = 700
        const val APPID_ERROR = 701
        const val CHANNEL_UNAUTHORIZED = 441

        // 业务相关
        const val ACCOUNT_ERROR = 400
        const val NOT_ENOUGH = 800
        const val DATA_EXISTS = 10002

        //提交任务相关的错误
        /**
         * 提交任务的时候，需要提交示例文件，但提交的文件为空或者没有提交文件
         */
        var TASK_SUBMIT_FILE_EMPTY: Int = 81100

        /**
         * 提交的文件类型不符合规范
         */
        var TASK_SUBMIT_FILE_SUFFIX_ERROR: Int = 81101

        /**
         * 提交任务的步骤输入内容错误
         */
        var TASK_SUBMIT_INPUT_ERROR_STEP: Int = 81102

        /**
         * 提交的任务输入内容错误
         */
        var TASK_SUBMIT_INPUT_ERROR_TASK: Int = 81103

        /**
         * 提交任务文件上传失败
         */
        var TASK_SUBMIT_OSS_UPLOAD_ERROR: Int = 81104

        /**
         * 已提交过该任务
         */
        var TASK_SUBMIT_ALREADY: Int = 81105

        /**
         * 该任务该用户已完成
         */
        var TASK_SUBMIT_COMPLETED: Int = 81106
    }
}
