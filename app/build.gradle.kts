// 先全局排除老的 kotlin-stdlib-jdk7/jdk8 依赖
configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.dailyquoteapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dailyquoteapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose     = false
    }
}

dependencies {
    // 手动指定唯一的 kotlin-stdlib 版本（和 AGP 自带的 1.8.10 保持一致）
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // 纯 Java 库
    implementation("androidx.core:core:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")

    // 测试
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
