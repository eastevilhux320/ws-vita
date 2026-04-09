@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("maven-publish")
}

android {
    // 命名空间应与你的项目包名结构一致
    namespace = "com.wangshu.mira"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 强制资源前缀，防止冲突
        resourcePrefix = "mira_"
    }

    buildTypes {
        // 与 framework 模块的环境配置完全对齐
        getByName("debug") {
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.dev.get())
        }

        create("sit") {
            matchingFallbacks.add("debug")
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.sit.get())
        }

        create("uat") {
            matchingFallbacks.add("release")
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.uat.get())
        }

        create("prod") {
            matchingFallbacks.add("release")
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.prod.get())
        }

        getByName("release") { }
    }

    compileOptions {
        val ver = JavaVersion.toVersion(libs.versions.javaVersion.get())
        sourceCompatibility = ver
        targetCompatibility = ver
    }
    kotlinOptions {
        jvmTarget = libs.versions.javaVersion.get()
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    // 依赖你的功能组件
    implementation(project(":ws-sdk-core"))

    // 基础依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    // 如果需要接入具体广告 SDK，取消下面的注释并确保 libs.versions.toml 中有对应定义
    // implementation(libs.pangle.sdk)
    //张鸿洋的flowlayout库(已不更新，但还挺好用)
    //api(libs.zhy.flowlayout)

    // 【最合理的终极写法】直接指向文件，避免 Gradle 乱找 .jar
    // 使用 rootProject.file 确保从根目录开始查找，路径绝对正确
    api(files("${rootProject.projectDir}/ws-third-libs/libs/ads-identifier-3.4.62.300.aar"))
    api(files("${rootProject.projectDir}/ws-third-libs/libs/deviceid-sdk-release-1.4.1.aar"))
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                // 使用常规且专业的项目前缀
                groupId = "com.wangshu.mira"
                // 使用模块名作为唯一的 artifactId
                artifactId = "mira-core"
                // 版本号
                version = "1.0.0"

                // 告诉插件发布 release 变体生成的 AAR
                from(components["release"])
            }
        }
    }
}
