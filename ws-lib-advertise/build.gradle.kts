@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    // 命名空间应与你的项目包名结构一致
    namespace = "com.wsvita.advertise"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 强制资源前缀，防止冲突
        resourcePrefix = "ad_"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
}
