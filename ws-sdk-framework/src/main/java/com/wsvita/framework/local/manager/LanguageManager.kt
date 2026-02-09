package com.wsvita.framework.local.manager

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.wsvita.framework.local.BaseManager
import java.util.Locale

/**
 * **LanguageManager**
 * 国际化管理组件。
 * 核心职责：语言状态持久化(StorageManager)、Context 包装、系统环境匹配。
 */
class LanguageManager private constructor() : BaseManager() {

    companion object {
        private const val TAG = "WSVita_F_M_LanguageManager=>"

        // 挂载到 StorageManager 的持久化 Key
        private const val KEY_LANGUAGE_TAG = "framework_lang_tag"

        val instance: LanguageManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            LanguageManager()
        }
    }

    private var mCurrentLocale: Locale = Locale.getDefault()

    override fun onInit() {
        // 从 StorageManager 获取缓存的 Language Tag (例如 "zh-CN" 或 "en")
        val savedTag = StorageManager.instance.getString(KEY_LANGUAGE_TAG, "")
        mCurrentLocale = if (savedTag.isNotEmpty()) {
            Locale.forLanguageTag(savedTag)
        } else {
            Locale.getDefault()
        }
    }

    // =================================================================================
    // 外部调用方法 (所有功能逻辑内聚于此)
    // =================================================================================

    /**
     * 获取当前生效的 Locale
     */
    fun getCurrentLocale(): Locale = mCurrentLocale

    /**
     * 更新应用语言并持久化
     * @param context 建议传 Application Context
     * @param locale 目标语言环境
     */
    fun updateLanguage(context: Context, locale: Locale) {
        mCurrentLocale = locale
        // 1. 持久化到 StorageManager (三级缓存)
        StorageManager.instance.put(KEY_LANGUAGE_TAG, locale.toLanguageTag())
        // 2. 立即应用到资源配置
        applyLanguage(context, locale)
    }

    /**
     * 判断当前是否为简体中文 (供业务逻辑判定的公共方法)
     */
    fun isSimpChinese(): Boolean {
        return mCurrentLocale.language == "zh" && mCurrentLocale.country == "CN"
    }

    /**
     * 获取当前语言显示名称
     */
    fun getDisplayName(): String {
        return mCurrentLocale.getDisplayName(mCurrentLocale)
    }

    // =================================================================================
    // Context & Configuration 核心处理
    // =================================================================================

    /**
     * 核心方法：将 Locale 注入 Context
     * 必须在 BaseActivity 的 attachBaseContext 中调用
     */
    fun applyLanguage(context: Context, locale: Locale): Context {
        val resources = context.resources
        val config = Configuration(resources.configuration)

        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLocales(LocaleList(locale))
            context.createConfigurationContext(config)
        } else {
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
            context
        }
    }

    /**
     * 在 Activity 的 onConfigurationChanged 中调用，防止系统切换导致配置失效
     */
    fun handleConfigurationChanged(context: Context) {
        applyLanguage(context, mCurrentLocale)
    }
}
