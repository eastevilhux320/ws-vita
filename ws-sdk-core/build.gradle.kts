@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // 聚合层通常需要处理 DataBinding 的中转
    alias(libs.plugins.kotlin.kapt)
    id("maven-publish")
}

android {
    namespace = "com.wsvita.core"
    // 建议从 libs 统一获取版本，保持全项目一致
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        // 开启 DataBinding，确保上层业务模块能正常引用 SDK 中的布局
        dataBinding = true
    }

    buildTypes {
        // 关键：必须保留环境对齐，否则业务模块在 SIT/UAT 模式下无法找到依赖
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

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // =======================================================
    // 核心聚合逻辑：使用 api 确保业务组件可直接访问
    // =======================================================

    // 1. 聚合底层能力 SDK
    api(project(":ws-sdk-framework")) // 提供 SDKViewModel, 扩展方法
    api(project(":ws-sdk-ui"))        // 提供 BaseDialog, 统一资源 ID
    api(project(":ws-sdk-network"))   // 提供网络能力

    // 2. 聚合常用公共库，业务模块无需重复声明
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.google.material)
    // eventbus
    api(libs.eventbus.core)
    api(libs.google.flexbox)
    api(libs.easypermissions)
    api(libs.pickerview)

    // 3. 测试相关保留 implementation
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                // 使用标准、正规的 groupId
                groupId = "com.wangshu.vota"
                // 聚合库名作为 artifactId
                artifactId = "ws-sdk-core"
                // 版本号保持 1.0.0
                version = "1.0.0"

                // 核心：这将 dependencies 闭包中 api 声明的所有底层 SDK 和公共库
                // 全部写入 POM 文件，实现“一键依赖”
                from(components["release"])
            }
        }
    }
}
