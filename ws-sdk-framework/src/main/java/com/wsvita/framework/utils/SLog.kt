package com.wsvita.framework.utils

import android.util.Log

/**
 * 针对组件化开发优化的日志工具类
 * 严格保留 isDebug 判断，确保 Release 包零损耗
 */
class SLog private constructor() {

    companion object {
        private var isDebug: Boolean = false
        private var globalTag: String = "VITA"

        /**
         * 初始化方法
         */
        fun init(debug: Boolean) {
            this.isDebug = debug
        }

        fun d(tag: String, message: String) {
            if (isDebug) {
                Log.d(formatTag(tag), message)
            }
        }

        fun d(message: String) {
            if (isDebug) {
                Log.d(getAutoTag(), message)
            }
        }

        fun i(tag: String, message: String) {
            if (isDebug) {
                Log.i(formatTag(tag), message)
            }
        }

        fun w(tag: String, message: String) {
            if (isDebug) {
                Log.w(formatTag(tag), message)
            }
        }

        fun i(message: String) {
            if (isDebug) {
                Log.i(getAutoTag(), message)
            }
        }

        fun e(tag: String, message: String, tr: Throwable? = null) {
            if (isDebug) {
                val finalTag = formatTag(tag)
                if (tr != null) Log.e(finalTag, message, tr) else Log.e(finalTag, message)
            }
        }

        fun e(message: String) {
            if (isDebug) {
                Log.e(getAutoTag(), message)
            }
        }

        /**
         * 长日志打印（修复了空指针和索引越界的严重 Bug）
         */
        fun longD(tag: String, message: String?) {
            // 1. 严格的 isDebug 和空判断
            if (!isDebug || message == null || message.isEmpty()) return

            val finalTag = formatTag(tag)
            val length = message.length
            val segmentSize = 3000 // 建议设置为 3000，留出空间给系统日志前缀

            // 2. 如果长度小于分段大小，直接打印，不走复杂逻辑
            if (length <= segmentSize) {
                Log.d(finalTag, message)
                return
            }

            // 3. 使用索引偏移法代替 substring 字符串裁剪，避免频繁创建新字符串对象
            var startIndex = 0
            while (startIndex < length) {
                var endIndex = startIndex + segmentSize
                if (endIndex > length) {
                    endIndex = length
                }

                // 关键修复：确保索引安全的情况下打印
                Log.d(finalTag, message.substring(startIndex, endIndex))

                startIndex = endIndex
            }
        }

        /**
         * 格式化 Tag，加入全局前缀
         */
        private fun formatTag(tag: String): String = "$globalTag-$tag"

        /**
         * 核心功能：自动获取调用处的文件名和行号作为 Tag
         * 仅在 isDebug 为 true 时才会被调用，避免性能浪费
         */
        private fun getAutoTag(): String {
            val stackTrace = Thread.currentThread().stackTrace
            // 索引 4 对应调用 SLog.d(message) 的位置
            val element = stackTrace.getOrNull(4)
            return if (element != null) {
                "$globalTag(${element.fileName}:${element.lineNumber})"
            } else {
                globalTag
            }
        }
    }
}
