plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "clc65.thanhtai.sportsnews"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "clc65.thanhtai.sportsnews"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation ("androidx.core:core-splashscreen:1.0.1")
        // Các dependencies khác của bạn

    // Thư viện giao diện Material (để làm ô nhập liệu đẹp)
    implementation("com.google.android.material:material:1.9.0")
// Thư viện load ảnh (Glide)
    implementation("com.github.bumptech.glide:glide:4.15.1")
// Thư viện parse HTML/XML (Jsoup)
    implementation("org.jsoup:jsoup:1.15.3")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}