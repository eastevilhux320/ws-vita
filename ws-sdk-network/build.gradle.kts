@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("maven-publish")
}

android {
    namespace = "com.wsvita.network"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        dataBinding = true
    }

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
}

dependencies {
    implementation(project(":ws-sdk-framework"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    // 新增网络核心库 Bundle
    api(libs.bundles.network)

    // 协程支持 (你 toml 里已有的定义)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
}


afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                // 使用标准、正规的 groupId
                groupId = "com.wangshu.vita"
                // 模块名作为 artifactId
                artifactId = "ws-sdk-network"
                // 版本号保持一致
                version = "1.0.0"

                // 告诉插件发布 release 变体生成的 AAR
                from(components["release"])
            }
        }
    }
}
