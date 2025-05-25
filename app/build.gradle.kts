import org.gradle.api.JavaVersion

// 排除多余的 Kotlin Stdlib，避免 jdk7/jdk8 冲突
configurations.all {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}

plugins {
    // 仅使用 Android Application 插件
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
        // Java 11 兼容
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
        // 开启 ViewBinding；关闭 Compose
        viewBinding = true
        compose     = false
    }
}

dependencies {
    // 指定唯一的 Kotlin 标准库，仅供间接依赖使用
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // AndroidX 核心库
    implementation("androidx.core:core:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // 网络请求：Retrofit + Gson + 日志拦截
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // 本地数据库：Room
    implementation("androidx.room:room-runtime:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.5.0")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
