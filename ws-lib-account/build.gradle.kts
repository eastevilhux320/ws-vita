@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library) // 业务组件作为库工程
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.wsvita.module.account"
    // 从 Version Catalog 统一获取版本号
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // 强制要求资源前缀，避免与其他组件（如广告）冲突
        resourcePrefix = "account_"

        // 核心：EventBus 索引配置
        kapt {
            arguments {
                // 指定生成的索引类名，建议遵循命名规范
                // 编译后会生成 com.wsvita.module.account.AccountEventIndex
                arg("eventBusIndex", "${namespace}.AccountEventIndex")
            }
        }
    }

    buildFeatures {
        // 开启数据绑定，方便 UI 开发
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        // 必须与 ws-sdk-framework 保持环境对齐
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
    // 1. 依赖内部功能 SDK
    implementation(project(":ws-sdk-core"))

    kapt(libs.eventbus.processor)

    // 2. 基础库依赖（通过 libs 统一管理）
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    // 3. 业务私有依赖（如果有）
    // implementation(libs.wechat.login.sdk)
}
