package com.wsvita.framework.local.manager.device

import android.content.Context
import android.os.Build
import com.wsvita.framework.local.BaseManager
import com.wsvita.framework.utils.SLog
import ext.TimeExt.systemTime

class DeviceManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_DeviceManager=>"
        // 业务层调用的唯一出口
        val instance: DeviceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { DeviceManager() }
    }

    /**
     * 重写初始化逻辑
     * 注意：建议在内部将 context 转换为 ApplicationContext 以防内存泄漏
     */
    fun init(context: Context) {
        super.init()
        SLog.d(TAG,"init,time:${systemTime()}")
        //初始化imei管理
        ImeiManager.instance.init(context);
        ImsiManager.instance.init(context);
        OAIDManager.instance.init();
        AndroidIdManager.instance.init(context);
        CpuManager.instance.init();
        BatteryManager.instance.init();
        NetworkInfoManager.instance.init();

        SLog.d(TAG,"init success");
    }

    // 如果基类定义了抽象方法 onInit，则在此实现，否则可删除
    override fun onInit() {
        // 执行不依赖 context 的通用初始化逻辑
    }

    /**
     * 获取设备 IMEI 标识
     * * **逻辑流程：**
     * 1. 调用 [checkInit] 确保 Manager 已完成初始化。
     * 2. 优先返回 [ImeiManager] 内部的内存缓存（L1）。
     * 3. 若内存缺失，则尝试从磁盘持久化（L2）获取。
     * 4. 若以上均缺失且系统版本 < Android 10，则触发物理采集并同步缓存。
     *
     * create by Eastevil at 2026/1/5 11:27
     * @author Eastevil
     *
     * @param context 局部上下文参数，用于在缓存失效时触法系统服务调用，严禁持久持有。
     * @return 返回设备 IMEI 或 MEID。在 Android 10+ 或权限缺失时返回 null。
     */
    fun imei(context: Context): String? {
        checkInit();
        return ImeiManager.instance.imei(context);
    }

    /**
     * 获取设备 IMSI (国际移动用户识别码)
     *
     * **1. 数据流向：**
     * - 调用 [checkInit] 拦截未初始化状态。
     * - 优先从 [ImsiManager] 的内部内存缓存获取。
     * - 若内存缺失，则尝试从磁盘持久化缓存获取。
     * - 若均不可用且系统版本 < Android 10，则通过物理 API 采集并同步至各级缓存。
     *
     * **2. 合规与安全：**
     * - 内部受 10 秒频率拦截器保护，防止由于业务 Bug 导致的短时间内高频物理采集触发系统隐私风险。
     * - **禁止持有 Context：** 传入的 [context] 仅供本次调用链中的系统服务获取使用，方法执行完毕即随栈帧释放。
     *
     * create by Eastevil at 2026/1/5 11:30
     * @author Eastevil
     *
     * @param context 局部上下文参数，建议传入 ApplicationContext。
     * @return 返回 SIM 卡识别码。在 Android 10+、未插卡或权限缺失时返回 null。
     */
    fun imsi(context: Context): String? {
        checkInit();
        return ImsiManager.instance.imsi(context);
    }

    /**
     * 获取 SIM 卡运营商名称 (SIM Operator Name)
     * * **1. 逻辑说明：**
     * - 通过 [ImsiManager] 获取当前注册的网络运营商名称。
     *
     * * **2. 返回值说明：**
     * - 返回运营商的 UTF-8 名称。若未读到则返回 "unknown"。
     * - **常见示例值**：`中国移动`, `China Unicom`, `T-Mobile`
     *
     * create by Administrator at 2026/1/12 00:20
     * @author Administrator
     * @param context 局部上下文
     * @return 运营商名称字符串
     */
    fun simOperatorName(context: Context): String {
        checkInit()
        return ImsiManager.instance.firstOperatorName(context);
    }

    /**
     * 获取 SIM 卡插槽占用状态 (SIM Status Flag)
     * * **1. 逻辑说明：**
     * - 返回一个代表卡槽组合的整数标识。
     * - 规则：0-无卡，1-有卡槽1，2-有卡槽2，12-双卡。
     *
     * * **2. 返回值说明：**
     * - `0`: 设备未检测到任何可用 SIM 卡。
     * - `1`: 仅卡槽 1 (Slot 0) 正常在位。
     * - `2`: 仅卡槽 2 (Slot 1) 正常在位。
     * - `12`: 两张卡均正常在位。
     *
     * * **3. 注意事项：**
     * - 该方法返回的是经过压缩的组合标识，方便后端直接进行设备画像分群。
     *
     * create by Administrator at 2026/1/12 01:05
     * @author Administrator
     * @param context 局部上下文
     * @return 组合状态码 (Int)
     */
    fun simStatus(context: Context): Int {
        checkInit()
        return ImsiManager.instance.simStatus(context)
    }

    /**
     * 【同步获取接口】获取当前内存中的 OAID 标识符
     * * [逻辑说明]:
     * 1. 优先返回内存缓存 [cacheOaid]，实现毫秒级响应。
     * 2. 若内存缓存为空，会静默触发一次物理获取 [fetchOaidFromSystem]，但不会阻塞当前线程。
     * 3. 物理获取的结果将在成功后自动更新至内存及 [StorageManager]。
     * * [作用与场景]:
     * - 适用于对实时性要求不高、允许首帧获取为 null 的场景（如常规数据埋点、非阻塞式日志上报）。
     * - 避免了因等待 SDK 异步回调而导致的 UI 卡顿或逻辑挂起。
     * * [注意事项]:
     * - 在 App 首次安装且 SDK 尚未完成初始化回调时，此方法可能返回 null。
     * - 如果业务逻辑强依赖 OAID（如广告归因），请改用异步回调接口 [getOAID]。
     * - 必须在确认用户已同意隐私协议后调用，否则可能触发合规监测异常。
     * create by Eastevil at 2026/1/5 13:27
     *
     * @author Eastevil
     * @return 匿名设备标识符字符串，获取失败或尚未获取到时返回 null。
     */
    fun oaid(): String? {
        checkInit();
        return OAIDManager.instance.oaid();
    }

    /**
     * 获取设备型号 (Model Name)
     * * **1. 逻辑说明：**
     * - 直接从系统常量 [android.os.Build.MODEL] 读取。
     * - 该值由设备制造商在 ROM 编译时的系统属性 `ro.product.model` 确定。
     *
     * - **不会返回 null**。系统类加载时已完成初始化。
     * - 若底层属性缺失（极罕见），系统通常返回 "unknown"。
     * - 示例值："ALN-AL00", "2311DRK48C", "(Find X7 Ultra"。
     *
     * * **3. 注意事项：**
     * - 属于非敏感静态参数，无需申请运行时权限，不涉及合规检测拦截。
     *
     * create by Administrator at 2026/1/11 21:46
     * @author Administrator
     * @return 硬件型号字符串
     */
    fun modelName(): String {
        checkInit();
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备品牌 (Brand Name)
     * * **1. 逻辑说明：**
     * - 直接从系统常量 [android.os.Build.BRAND] 读取。
     * - 该值代表产品推向市场时的品牌名称。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。
     * - **常见示例值**：
     * - `HUAWEI`, `Xiaomi`, `Redmi`, `OPPO`, `vivo`, `samsung`
     *
     * * **3. 注意事项：**
     * - 同一厂商下可能有多个品牌（如厂商为 Xiaomi，品牌可能为 Redmi）。
     *
     * create by Administrator at 2026/1/11 21:50
     * @author Administrator
     * @return 品牌名称字符串
     */
    fun brandName(): String {
        checkInit()
        return android.os.Build.BRAND
    }

    /**
     * 获取设备厂商 (Manufacturer Name)
     * * **1. 逻辑说明：**
     * - 直接从系统常量 [android.os.Build.MANUFACTURER] 读取。
     * - 该值代表硬件设备的实际生产制造厂商。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。
     * - **常见示例值**：
     * - `HUAWEI`, `Xiaomi`, `OPPO`, `samsung`
     *
     * * **3. 注意事项：**
     * - 通常用于区分不同 OEM 厂商的特殊逻辑处理（如推送通道适配）。
     *
     * create by Administrator at 2026/1/11 21:51
     * @author Administrator
     * @return 厂商名称字符串
     */
    fun manufacturerName(): String {
        checkInit()
        return android.os.Build.MANUFACTURER
    }

    /**
     * 获取 Android 系统版本号 (OS Version)
     * * **1. 逻辑说明：**
     * - 直接从系统常量 [android.os.Build.VERSION.RELEASE] 读取。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。
     * - **常见示例值**：
     * - `10`, `11`, `12`, `13`, `14`
     *
     * * **3. 注意事项：**
     * - 该值为用户可见的系统版本，若需判断 API 等级请使用 [android.os.Build.VERSION.SDK_INT]。
     *
     * create by Administrator at 2026/1/11 21:52
     * @author Administrator
     * @return 系统版本字符串
     */
    fun osVersion(): String {
        checkInit()
        return android.os.Build.VERSION.RELEASE
    }

    /**
     * 获取 Android 系统友好版本名
     * * **1. 逻辑说明：**
     * - 结合 "Android" 前缀与 [android.os.Build.VERSION.RELEASE] 拼接。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。
     * - **常见示例值**：
     * - `Android 10`, `Android 8.0.2`, `Android 14`
     *
     * * **3. 注意事项：**
     * - 该值主要用于 UI 展示或日志上报。
     *
     * create by Administrator at 2026/1/11 21:55
     * @author Administrator
     * @return 格式为 "Android x.x" 的字符串
     */
    fun osVersionName(): String {
        checkInit()
        return "Android ${android.os.Build.VERSION.RELEASE}"
    }

    /**
     * 获取系统 API 等级 (SDK Version)
     * * **1. 逻辑说明：**
     * - 直接读取系统常量 [android.os.Build.VERSION.SDK_INT]。
     *
     * * **2. 返回值可靠性：**
     * - 返回整型数值。
     * - **常见示例值**：
     * - `29` (Android 10), `31` (Android 12), `34` (Android 14)
     *
     * * **3. 注意事项：**
     * - 属于开发中判断系统兼容性的核心依据。
     *
     * create by Administrator at 2026/1/11 21:56
     * @author Administrator
     * @return API Level 整数
     */
    fun sdkVersion(): Int {
        checkInit()
        return android.os.Build.VERSION.SDK_INT
    }

    /**
     * 获取屏幕原始分辨率数值 (Width & Height)
     * * **1. 逻辑说明：**
     * - 通过 [WindowManager] 的 `getRealMetrics` 获取物理屏幕的真实像素。
     * * * **2. 返回值说明：**
     * - 返回一个大小为 2 的 IntArray。
     * - `index 0`: 屏幕宽度 (Pixels)
     * - `index 1`: 屏幕高度 (Pixels)
     * * * **3. 常见示例值：**
     * - `[1080, 2400]`, `[1440, 3200]`
     * * create by Administrator at 2026/1/11 22:15
     * @author Administrator
     * @param context 局部上下文
     * @return 包含宽高的整型数组
     */
    fun resolutionValues(context: Context): IntArray {
        checkInit()
        return try {
            val wm = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as? android.view.WindowManager
            val metrics = android.util.DisplayMetrics()
            @Suppress("DEPRECATION")
            wm?.defaultDisplay?.getRealMetrics(metrics)
            intArrayOf(metrics.widthPixels, metrics.heightPixels)
        } catch (e: Exception) {
            intArrayOf(0, 0)
        }
    }

    /**
     * 获取格式化的屏幕分辨率字符串
     * * **1. 逻辑说明：**
     * - 调用内部 [resolutionValues] 获取数值并拼接为字符串。
     * * * **2. 常见示例值：**
     * - `1080x2400`, `720x1280`
     * * create by Administrator at 2026/1/11 22:16
     * @author Administrator
     * @param context 局部上下文
     * @return 格式为 "宽x高" 的字符串
     */
    fun resolution(context: Context): String {
        val values = resolutionValues(context)
        if (values[0] == 0 && values[1] == 0) return "unknown"
        return "${values[0]}x${values[1]}"
    }

    /**
     * 获取 CPU 指令集架构 (Primary ABI)
     * * **1. 逻辑说明：**
     * - 从 [android.os.Build.SUPPORTED_ABIS] 中获取设备支持的首选架构。
     *
     * * **2. 返回值可靠性：**
     * - **常见示例值**：
     * - `arm64-v8a` (主流 64 位架构)
     * - `armeabi-v7a` (老旧 32 位架构)
     * - `x86_64` (模拟器常见)
     *
     * * **3. 注意事项：**
     * - 主要用于动态加载 SO 库或性能分析场景。
     *
     * create by Administrator at 2026/1/11 22:08
     * @author Administrator
     * @return CPU 架构字符串
     */
    fun cpuAbi(): String {
        checkInit()
        return if (android.os.Build.SUPPORTED_ABIS.isNotEmpty()) {
            android.os.Build.SUPPORTED_ABIS[0]
        } else {
            @Suppress("DEPRECATION")
            android.os.Build.CPU_ABI
        }
    }

    /**
     * 获取 CPU 厂商 (CPU Vendor)
     * * **1. 逻辑说明：**
     * - 通过 [CpuManager] 内部特征库识别得出。
     * - 识别依据：解析 `/proc/cpuinfo` 中的 Hardware 字段或 [android.os.Build.HARDWARE]。
     * - 能够识别高通 (Qualcomm)、联发科 (MediaTek)、海思 (Kirin) 等主流平台。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。若无法识别，则返回 "Unknown"。
     * - **常见示例值**：
     * - `Qualcomm` (高通骁龙系列)
     * - `MediaTek` (联发科天玑/曦力系列)
     * - `Kirin` (华为海思麒麟系列)
     * - `Exynos` (三星系列)
     *
     * * **3. 注意事项：**
     * - 用于业务层根据不同芯片平台执行特定的性能优化或兼容性处理。
     *
     * create by Administrator at 2026/1/11 22:55
     * @author Administrator
     * @return 识别后的厂商标准名称字符串
     */
    fun cpuVendor(): String {
        checkInit()
        return CpuManager.instance.vendor()
    }

    /**
     * 获取 CPU 原始硬件名 (CPU Hardware)
     * * **1. 逻辑说明：**
     * - 直接获取底层硬件代号。
     * - 优先从系统文件 `/proc/cpuinfo` 的 "Hardware" 标签中提取。
     *
     * * **2. 返回值可靠性：**
     * - **不会返回 null**。若提取失败则返回 "unknown" 或系统原生硬件标识。
     * - **常见示例值**：
     * - `SM8550` (骁龙 8 Gen 2)
     * - `SM8650` (骁龙 8 Gen 3)
     * - `MT6895` (天玑 8100)
     * - `MT6989` (天玑 9300)
     * - `kirin9000s` (麒麟 9000S)
     *
     * * **3. 注意事项：**
     * - 该值为底层代号，相较于市场推广名，更适合作为精确的硬件适配依据。
     *
     * create by Administrator at 2026/1/11 22:56
     * @author Administrator
     * @return 原始硬件标识字符串
     */
    fun cpuHardware(): String {
        checkInit()
        return CpuManager.instance.hardwareName()
    }

    /**
     * 获取 CPU 核心数 (CPU Core Count)
     * * **1. 逻辑说明：**
     * - 调用 [CpuManager] 获取物理核心总数。
     * - 核心采集逻辑：统计 `/sys/devices/system/cpu/` 目录下的 CPU 节点。
     *
     * * **2. 返回值可靠性：**
     * - 返回当前设备硬件具备的逻辑处理器总数。
     * - **常见示例值**：
     * - `8` (目前主流高通/联发科 8 核处理器)
     * - `4` (低端设备或老旧机型)
     *
     * * **3. 注意事项：**
     * - 属于静态硬件参数，无需动态监听。
     *
     * create by Administrator at 2026/1/11 23:10
     * @author Administrator
     * @return CPU 核心数量
     */
    fun cpuCoreCount(): Int {
        checkInit()
        return CpuManager.instance.cupCoreCount()
    }

    /**
     * 获取当前电池电量百分比 (Battery Level)
     * * **1. 逻辑说明：**
     * - 通过解析系统粘性广播 `ACTION_BATTERY_CHANGED` 获取。
     * - 计算公式：(level / scale) * 100。
     *
     * * **2. 返回值可靠性：**
     * - 返回 0 到 100 之间的整数。若获取失败返回 -1。
     *
     * * **3. 注意事项：**
     * - 实时性高，无需手动刷新缓存。
     *
     * create by Administrator at 2026/1/11 23:25
     * @author Administrator
     * @param context 局部上下文
     * @return 电量百分比 (0-100)
     */
    fun batteryLevel(context: Context): Int {
        checkInit()
        return BatteryManager.instance.level(context)
    }

    /**
     * 获取电池充电状态 (Battery Status)
     * * **1. 逻辑说明：**
     * - 识别当前设备是否处于充电、放电或充满电状态。
     *
     * * **2. 常见返回值：**
     * - `Charging`: 正在充电
     * - `Discharging`: 正在放电（使用电池中）
     * - `Full`: 已充满
     *
     * create by Administrator at 2026/1/11 23:27
     * @author Administrator
     * @param context 局部上下文
     * @return 状态描述字符串
     */
    fun batteryStatus(context: Context): String {
        checkInit()
        return BatteryManager.instance.status(context)
    }

    /**
     * 获取电池温度 (Battery Temperature)
     * * **1. 逻辑说明：**
     * - 返回电池当前的实时摄氏温度。
     *
     * * **2. 示例值：**
     * - `36.5`, `42.0`
     *
     * create by Administrator at 2026/1/11 23:28
     * @author Administrator
     * @param context 局部上下文
     * @return 摄氏度 (Float)
     */
    fun batteryTemperature(context: Context): Float {
        checkInit()
        return BatteryManager.instance.temperature(context)
    }

    /**
     * 获取设备电池总容量 (Battery Total Capacity)
     * * **1. 逻辑说明：**
     * - 利用反射技术访问系统隐藏类 `PowerProfile` 获取硬件标称容量。
     * - 该值代表设备出厂时的典型电池容量（mAh）。
     *
     * * **2. 返回值说明：**
     * - 返回双精度浮点数。若获取失败则返回 0.0。
     * - **常见示例值**：
     * - `5000.0` (目前主流安卓大电池设备)
     * - `4500.0` (轻薄款机型)
     *
     * * **3. 注意事项：**
     * - 该值为硬件固定参数，不随电池损耗而改变。
     * - 反射操作在某些极致精简的 ROM 上可能失效，已做兜底处理。
     *
     * create by Administrator at 2026/1/11 23:35
     * @author Administrator
     * @param context 局部上下文
     * @return 电池总容量 (mAh)
     */
    fun batteryTotalCapacity(context: Context): Double {
        checkInit()
        return BatteryManager.instance.totalCapacity(context)
    }

    /**
     * 获取设备传感器总数 (Sensor Count)
     * * **1. 逻辑说明：**
     * - 通过系统 [SensorManager] 实时获取所有传感器列表并统计大小。
     * * * **2. 返回值说明：**
     * - 返回整型数值。若获取失败则返回 0。
     * - **常见示例值**：`45` (真机), `2` (模拟器)
     * * create by Administrator at 2026/1/12 00:10
     * @author Administrator
     * @param context 局部上下文
     * @return 传感器总数
     */
    fun sensorCount(context: Context): Int {
        checkInit()
        val sm = context.applicationContext.getSystemService(Context.SENSOR_SERVICE) as? android.hardware.SensorManager
        return sm?.getSensorList(android.hardware.Sensor.TYPE_ALL)?.size ?: 0
    }


    /**
     * 判断设备是否已 Root (Is Rooted)
     * * **1. 逻辑说明：**
     * - 采用三重交叉校验，比单一判断更准确：
     * - 校验 [Build.TAGS] 是否包含 'test-keys'。
     * - 扫描系统常见目录下的 'su' 二进制文件。
     * - 尝试通过 Runtime 执行 'which su' 命令。
     *
     * * **2. 返回值说明：**
     * - `true`: 设备疑似已 Root。
     * - `false`: 未发现 Root 迹象。
     *
     * * **3. 注意事项：**
     * - 此方法为非侵入式检测，不会申请 Root 权限。
     * - 虽然能覆盖 95% 以上的情况，但无法 100% 逃过高级隐藏型 Root (如 Magisk 随机包名/隐藏模式)。
     *
     * create by Administrator at 2026/01/12 11:20
     * @author Administrator
     * @return 是否已 Root
     */
    fun isRooted(): Boolean {
        checkInit()

        // A. 校验 Build Tags
        val tags = Build.TAGS
        if (tags != null && tags.contains("test-keys")) {
            return true
        }

        // B. 扫描常见 su 路径
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        try {
            for (path in paths) {
                if (java.io.File(path).exists()) return true
            }
        } catch (e: Exception) {
            // 忽略 IO 异常
        }

        // C. 尝试执行命令判断
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
            reader.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * 判断设备是否开启了开发者模式 (Is Developer Options Enabled)
     * * **1. 逻辑说明：**
     * - 通过系统 [Settings.Global] 数据库读取 `DEVELOPMENT_SETTINGS_ENABLED` 字段。
     * - 该字段是系统标准定义的，反映了用户是否手动点击了版本号并开启了开发选项。
     *
     * * **2. 返回值说明：**
     * - `true`: 已开启开发者模式。
     * - `false`: 未开启开发者模式。
     *
     * * **3. 注意事项：**
     * - 不需要特殊权限即可读取。
     * - 建议在上报设备安全画像（wsui 属性集）时包含此项。
     *
     * create by Administrator at 2026/01/12 11:35
     * @author Administrator
     * @param context 局部上下文
     * @return 是否开启开发者模式
     */
    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        checkInit()
        return try {
            // Android 4.2+ 之后建议使用 Global，之前使用 Secure
            val enabled = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                android.provider.Settings.Global.getInt(
                    context.applicationContext.contentResolver,
                    android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
                )
            } else {
                android.provider.Settings.Secure.getInt(
                    context.applicationContext.contentResolver,
                    android.provider.Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0
                )
            }
            enabled != 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取设备当前网络连接类型 (Network Type)
     * * **1. 逻辑说明：**
     * - 0: 断开连接, 1: WiFi, 2: 蜂窝数据, 3: 以太网。
     *
     * * **2. 返回值说明：**
     * - 返回整型状态码，方便后端直接进行画像归类。
     *
     * create by Administrator at 2026/1/12 12:05
     * @author Administrator
     * @param context 局部上下文
     * @return 网络类型状态码
     */
    fun networkType(context: Context): Int {
        checkInit()
        return NetworkInfoManager.instance.getNetworkType(context)
    }

    /**
     * 获取设备本地 IPv4 地址 (Local IP)
     * * **1. 逻辑说明：**
     * - 遍历系统所有网络接口（Network Interfaces），排除回环地址。
     *
     * * **2. 返回值说明：**
     * - 字符串格式 IP。若获取失败返回 "0.0.0.0"。
     *
     * create by Administrator at 2026/1/12 12:07
     * @author Administrator
     * @return 本地 IP 字符串
     */
    fun networkIp(): String {
        checkInit()
        return NetworkInfoManager.instance.getLocalIpAddress()
    }
}
