@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("maven-publish")
}

android {
    namespace = "com.wsvita.framework"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        dataBinding = true
    }

    // 这里是你完整的、不可删除的多环境配置
    buildTypes {
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

    // 开启 DataBinding
    buildFeatures {
        dataBinding = true
    }

    // 如果你还在使用 ViewBinding，建议一并开启
    // buildFeatures {
    //    dataBinding = true
    //    viewBinding = true
    // }
}

dependencies {
    // 基础库
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    // 核心 API 转发
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.common.java8)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.bundles.navigation)
    api(libs.google.gson)

    // --- 修正后的 Glide 添加 ---
    // 确保你的 libs.versions.toml 中已定义 glide-core
    api(libs.glide.core)
    kapt(libs.glide.compiler)

    // 协程与核心库
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
    api(libs.androidx.activity.ktx)

    // 异步与生命周期
    api(libs.rxjava3)
    api(libs.rxandroid)
    api(libs.autodispose.core)
    api(libs.autodispose.android)

    api(libs.bundles.infrastructure)
    api(libs.android.oaid)
}

// ======================== 发布闭包 ========================
afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                // 使用常规且专业的 groupId
                groupId = "com.wangshu.vita"
                // 库的名字
                artifactId = "ws-sdk-framework"
                // 版本号
                version = "1.0.0"

                // 告诉插件发布 release 变体生成的 AAR
                from(components["release"])
            }
        }
    }
}
