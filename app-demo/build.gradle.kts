import java.util.Date
import java.text.SimpleDateFormat

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // 必须加上，用于 DataBinding 和可能的 Room/Dagger
}

// 如果新 App 也需要瓦力多渠道打包，请保留；否则可移除
apply(plugin = "walle")

android {
    namespace = "com.wangshu.vita.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // 这里不写死 applicationId，由 productFlavors 决定
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 注入 MVVM 架构所需的环境常量（与旧 App 对齐）
        buildConfigField("long", "RELEASE_TIME", "${System.currentTimeMillis()}L")
        buildConfigField("int", "VERSION_DEV", libs.versions.env.dev.get())
        buildConfigField("int", "VERSION_SIT", libs.versions.env.sit.get())
        buildConfigField("int", "VERSION_UAT", libs.versions.env.uat.get())
        buildConfigField("int", "VERSION_RELEASE", libs.versions.env.prod.get())
        buildConfigField("int", "VERSION_PERSONAL", libs.versions.env.personal.get())

        // 默认环境
        buildConfigField("int", "VERSION_TYPE", libs.versions.env.dev.get())

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }
    }

    // 开启 DataBinding，这是你 Adapter 架构和 MVVM 的基础
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    // 如果新 App 有自己的签名，请在此配置
    signingConfigs {
        create("wsdemo") {
            storeFile = file("keystore/ws-demo.jks")
            storePassword = "demo123"
            keyAlias = "wsdemo"
            keyPassword = "demo123"
        }
    }

    // 2. 渠道维度配置
    flavorDimensions.add("wsvita")
    productFlavors {
        all {
            dimension = "wsvita"
        }

        create("wsdemo") {
            applicationId = "com.wangshu.vita.demo"
            versionCode = 100
            versionName = "1.0.0"
            signingConfig = signingConfigs.getByName("wsdemo")
        }
    }

    // 3. 自动化资源路径（用于存放不同渠道的特殊图标、字符串）
    sourceSets {
        productFlavors.all {
            getByName(name) {
                res.srcDirs("src/others/$name/res")
            }
        }
    }

    // 4. 环境变体配置（完全复刻旧 App，确保 BuildConfig.VERSION_TYPE 一致）
    buildTypes {
        getByName("debug") {
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.dev.get())
            buildConfigField("String", "VERSION_DESCRIPTION", "\"开发版本\"")
        }

        create("sit") {
            initWith(getByName("debug"))
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.sit.get())
            buildConfigField("String", "VERSION_DESCRIPTION", "\"SIT版本\"")
            matchingFallbacks.add("debug")
        }

        create("uat") {
            initWith(getByName("release"))
            isMinifyEnabled = true
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.uat.get())
            buildConfigField("String", "VERSION_DESCRIPTION", "\"UAT版本\"")
            matchingFallbacks.add("release")
        }

        create("prod") {
            initWith(getByName("release"))
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("int", "VERSION_TYPE", libs.versions.env.prod.get())
            buildConfigField("String", "VERSION_DESCRIPTION", "\"生产版本\"")
            matchingFallbacks.add("release")
        }
    }

    // 5. APK 命名规范对齐：ws_{渠道}_{环境}_v{版本}_{日期}.apk
    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            val time = SimpleDateFormat("yyyy_MMdd_HHmm").format(Date())
            output.outputFileName = "ws_${flavorName}_${buildType.name}_v${versionName}_$time.apk"
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
    // 引入业务组件（组件化核心）
    implementation(project(":ws-lib-account"))
    implementation(project(":ws-lib-advertise"))
    implementation(project(":ws-lib-core"))

    // 基础 SDK
    implementation(project(":ws-sdk-core"))

    // 依赖管理
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)

    // 功能组件
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.ui)
    implementation(libs.alipay.sdk)
    implementation(libs.banner.vp2)

    implementation(libs.meituan.walle.lib)

    //implementation("jp.wasabeef:richeditor-android:2.0.0")
    //implementation("com.github.yuruiyin:RichEditor:0.2.4")

    testImplementation("junit:junit:4.13.2")
}
