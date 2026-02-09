package com.wsvita.framework.local.manager.device

import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.SLog
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * ### CPU 硬件信息管理组件
 * * **1. 主要职责**
 * - 负责采集 CPU 架构 (ABI)、核心数及厂商归类识别。
 * - 整合 `/proc/cpuinfo` 与 [android.os.Build.HARDWARE] 实现双重校验采集。
 *
 * **2. 扩展性设计**
 * - **特征库配置化**：通过 [VENDOR_FEATURES] 定义厂商与特征字符的映射关系。
 * - **多层匹配**：支持前缀匹配 (Prefix) 与包含匹配 (Contains)。
 *
 * @author Administrator
 * @createTime 2026/1/11 22:50
 */
class CpuManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_CpuManager=>"

        // --- 标准厂商定义 ---
        const val VENDOR_QUALCOMM = "Qualcomm"   // 高通
        const val VENDOR_MEDIATEK = "MediaTek"   // 联发科
        const val VENDOR_HUAWEI   = "Kirin"      // 华为海思
        const val VENDOR_SAMSUNG  = "Exynos"     // 三星
        const val VENDOR_UNISOC   = "Unisoc"     // 展锐 (原展讯)
        const val VENDOR_GOOGLE   = "Tensor"     // 谷歌
        const val VENDOR_UNKNOWN  = "Unknown"

        /**
         * 厂商特征映射表
         * Key: 厂商标准名
         * Value: 对应的特征字符串列表 (不区分大小写)
         */
        private val VENDOR_FEATURES = mapOf(
            VENDOR_QUALCOMM to listOf("qcom", "qualcomm", "snapdragon", "msm", "sdm", "sm"),
            VENDOR_MEDIATEK to listOf("mt", "mediatek", "helio", "dimensity"),
            VENDOR_HUAWEI   to listOf("kirin", "hisilicon"),
            VENDOR_SAMSUNG  to listOf("exynos", "samsungexynos"),
            VENDOR_UNISOC   to listOf("unisoc", "spreadtrum", "sc98", "ums"),
            VENDOR_GOOGLE   to listOf("tensor", "gs101", "gs201")
        )

        val instance: CpuManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CpuManager() }
    }

    private var cacheVendor: String = VENDOR_UNKNOWN
    private var cacheHardware: String = ""

    override fun init() {
        super.init()
        fetchHardwareInfo()
        SLog.d(TAG, "init: [Vendor: $cacheVendor] [RawHardware: $cacheHardware]")
    }

    override fun onInit() {}

    /**
     * 获取 CPU 厂商
     */
    fun vendor(): String {
        checkInit()
        return cacheVendor
    }

    /**
     * 获取原始硬件型号
     */
    fun hardwareName(): String {
        checkInit()
        return cacheHardware
    }

    /**
     * 获取 CPU 核心数 (Core Count)
     * * **1. 逻辑说明：**
     * - 优先通过扫描系统路径 `/sys/devices/system/cpu/` 下名为 `cpu[0-9]` 的文件夹数量来确定物理核心数。
     * - 若文件系统不可读，则降级使用 [Runtime.getRuntime().availableProcessors()]。
     * * * **2. 返回值可靠性：**
     * - 返回整型数值，至少为 1。
     * - **常见示例值**：`4`, `8`, `12`
     *
     * * **3. 注意事项：**
     * - 现代移动芯片多为 8 核（大中小核架构）。
     */
    fun cupCoreCount(): Int {
        checkInit()
        return fetchCoreCount()
    }

    /**
     * 物理采集核心数
     */
    private fun fetchCoreCount(): Int {
        return try {
            val dir = java.io.File("/sys/devices/system/cpu/")
            val files = dir.listFiles { pathname ->
                java.util.regex.Pattern.matches("cpu[0-9]+", pathname.name)
            }
            files?.size ?: Runtime.getRuntime().availableProcessors()
        } catch (e: Exception) {
            Runtime.getRuntime().availableProcessors()
        }
    }

    /**
     * 采集逻辑：优先读取文件系统，兜底使用系统常量
     */
    private fun fetchHardwareInfo() {
        // 1. 尝试从 /proc/cpuinfo 获取最真实的 Hardware 字段
        val procHardware = readHardwareFromCpuInfo()

        // 2. 如果文件读取不到，降级使用 Build.HARDWARE
        val finalHardware = if (procHardware.isNullOrEmpty()) {
            android.os.Build.HARDWARE
        } else {
            procHardware
        }

        cacheHardware = finalHardware ?: ""
        cacheVendor = identifyVendor(cacheHardware)
    }

    /**
     * 根据特征库识别厂商
     */
    private fun identifyVendor(hardware: String): String {
        if (hardware.isEmpty()) return VENDOR_UNKNOWN

        val h = hardware.lowercase(Locale.ENGLISH)

        // 遍历特征库进行匹配
        for ((vendor, features) in VENDOR_FEATURES) {
            for (feature in features) {
                // 如果硬件信息中包含特征码，即判定为该厂商
                if (h.contains(feature)) {
                    return vendor
                }
            }
        }
        return VENDOR_UNKNOWN
    }

    private fun readHardwareFromCpuInfo(): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/cpuinfo"))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line?.startsWith("Hardware", ignoreCase = true) == true) {
                    return line?.split(":")?.getOrNull(1)?.trim()
                }
            }
        } catch (e: Exception) {
            SLog.w(TAG, "readHardwareFromCpuInfo: Access denied or file missing.")
        } finally {
            try { reader?.close() } catch (_: IOException) {}
        }
        return null
    }
}
