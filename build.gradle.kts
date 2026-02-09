buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        //百度地图仓库
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        // 确保你的 libs.versions.toml 里定义了 meituan-walle-plugin-path
        classpath(libs.meituan.walle.plugin.path.get())
    }
}

plugins {
    // 使用 Version Catalog (libs.versions.toml) 声明插件，apply false 表示只声明不立即应用到根项目
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    id("maven-publish")
}

// 注册 clean 任务，用于清理项目编译生成的 build 文件夹
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

/**
 * 全局统一配置：针对项目中所有的子模块（subprojects）进行配置
 */
subprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        // 百度私有仓库
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    // 监听插件应用事件：当子模块应用了 Android 基础插件（无论 App 还是 Library 插件）时触发
    // com.android.build.gradle.BasePlugin 是 AppPlugin 和 LibraryPlugin 的父类
    plugins.withType<com.android.build.gradle.BasePlugin> {

        // 强制配置 Android 扩展属性（即 build.gradle.kts 中的 android { ... } 闭包）
        configure<com.android.build.gradle.BaseExtension> {

            // 访问资源集配置
            sourceSets {
                // 针对 main 代码集进行操作
                getByName("main") {
                    // 核心逻辑：重新定义资源目录。
                    // 1. "src/main/res"：保留默认的资源路径，确保 layout、values 等正常工作。
                    // 2. "src/main/res-shapes"：新增自定义路径，专门存放 shape 背景，实现资源物理隔离。
                    // 注意：此方法会覆盖默认值，所以必须把原有的 "src/main/res" 也写进去。
                    res.setSrcDirs(listOf(
                        "src/main/res",
                        "src/main/res-shapes"
                    ))
                }
            }
        }
    }
}
