package com.wangshu.mira.entity.enums

/**
 * 文件类型枚举 - 业务全分类前端版
 * 严格对齐后端 io.renren.common.entity.enums.MiraFileType 逻辑
 * 适配 wsui 组件化协议与 DataBinding 自动化绑定
 */
enum class StepFileType(val type: Int, val nameKey: String, val suffix: String) {

    /** 聚合：所有音视频 (不带后缀) */
    MEDIA(300, "MEDIA", ""),
    /** 聚合：所有图片 (不带后缀) */
    IMAGE(400, "IMG", ""),
    /** 聚合：所有文档 (不带后缀) */
    DOC_ALL(500, "DOC_ALL", ""),
    /** 聚合：所有包文件 (不带后缀) */
    PACKAGE(600, "PKG", ""),
    /** 聚合：所有业务类型 (不带后缀) */
    BUSINESS(700, "BIZ", ""),

    // --- 基础多媒体 (1-9) ---
    MP3(1, "MP3", ".mp3"),
    MP4(2, "MP4", ".mp4"),
    MOV(3, "MOV", ".mov"),
    WAV(4, "WAV", ".wav"),

    // --- 图片类 (10-19) ---
    JPG(10, "JPG", ".jpg"),
    PNG(11, "PNG", ".png"),
    GIF(12, "GIF", ".gif"),
    WEBP(13, "WEBP", ".webp"),
    SVG(14, "SVG", ".svg"),

    // --- 文档类 (20-29) ---
    PDF(20, "PDF", ".pdf"),
    DOC(21, "DOC", ".doc"),
    DOCX(25, "DOCX", ".docx"),
    XLS(22, "XLS", ".xls"),
    XLSX(26, "XLSX", ".xlsx"),
    PPT(23, "PPT", ".ppt"),
    TXT(24, "TXT", ".txt"),

    // --- 压缩与安装包 (30-49) ---
    ZIP(30, "ZIP", ".zip"),
    RAR(31, "RAR", ".rar"),
    _7Z(32, "7Z", ".7z"),
    APK(40, "APK", ".apk"),

    // --- 业务与特殊类型 (50+) ---
    LINK(50, "LINK", ""),
    FOLDER(51, "FOLDER", ""),

    UNKNOWN(99, "UNKNOWN", "");

    /**
     * 返回纯小写后缀，不带点
     * 对齐后端逻辑
     */
    fun fileSuffix(): String {
        return if (suffix.isEmpty()) {
            nameKey.lowercase()
        } else {
            suffix.replace(".", "").lowercase()
        }
    }

    // --- 分类判定 (严格对齐后端方法名) ---

    fun isImage(): Boolean = IMAGE_SET.contains(this)

    fun isVideo(): Boolean = VIDEO_SET.contains(this)

    fun isAudio(): Boolean = AUDIO_SET.contains(this)

    fun isMedia(): Boolean = isVideo() || isAudio() || this == MEDIA

    fun isDoc(): Boolean = DOC_SET.contains(this) // 原 isDocument 修改为 isDoc

    fun isPackage(): Boolean = PKG_SET.contains(this)

    fun isBusiness(): Boolean = BIZ_SET.contains(this)

    companion object {
        // 使用 entries 获取枚举列表 (Kotlin 1.9+)
        private val TYPE_MAP = values().associateBy { it.type }

        // 严格对齐后端 SUFFIX_MAP 逻辑：只存有后缀的
        private val SUFFIX_MAP = values().filter { it.suffix.isNotEmpty() }
            .associateBy { it.suffix.replace(".", "").uppercase() }

        private val IMAGE_SET = setOf(JPG, PNG, GIF, WEBP, SVG, IMAGE)
        private val VIDEO_SET = setOf(MP4, MOV)
        private val AUDIO_SET = setOf(MP3, WAV)
        private val DOC_SET = setOf(PDF, DOC, DOCX, XLS, XLSX, PPT, TXT, DOC_ALL)
        private val PKG_SET = setOf(ZIP, RAR, _7Z, APK, PACKAGE)
        private val BIZ_SET = setOf(LINK, FOLDER, BUSINESS)

        @JvmStatic
        fun getByType(type: Int?): StepFileType = TYPE_MAP[type] ?: UNKNOWN

        @JvmStatic
        fun getBySuffix(suffixOrFileName: String?): StepFileType {
            if (suffixOrFileName.isNullOrEmpty()) return UNKNOWN
            val key = if (suffixOrFileName.contains(".")) {
                suffixOrFileName.substringAfterLast(".")
            } else {
                suffixOrFileName
            }
            // 移除 .trim() 以保证和后端 Java 逻辑路径完全一致（除非后端也加）
            return SUFFIX_MAP[key.uppercase()] ?: UNKNOWN
        }
    }
}
