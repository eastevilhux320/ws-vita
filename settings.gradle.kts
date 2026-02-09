pluginManagement {
    repositories {
        // 关键：在这里添加仓库，否则根目录 buildscript 里的 classpath 找不到 walle
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 建议这里也加上阿里云，确保子模块依赖下载也快
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle") }
        maven { url = uri("https://artifact.bytedance.com/repository/Volcengine/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/groups/public") }
    }
}

rootProject.name = "ws-vita"
include(":ws-sdk-framework")
include(":ws-sdk-network")
include(":ws-sdk-ui")
include(":ws-lib-account")
include(":ws-lib-advertise")
include(":ws-sdk-core")
include(":ws-lib-core")

