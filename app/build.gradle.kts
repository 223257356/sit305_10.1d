plugins {
    id("com.sanjaya.buildlogic.app")
    id("com.sanjaya.buildlogic.compose")
    kotlin("plugin.serialization") version libs.versions.kotlin
}

android {
    namespace = "com.example.sit305101d"

    defaultConfig {
        applicationId = "com.example.sit305101d"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    dependencies {
        implementation(libs.androidx.constraintlayout)
        implementation(libs.retrofit.core)
        implementation(libs.retrofit.converter.kotlinx)
        implementation(essentials.bundles.stripe)
        implementation(ui.bundles.orbit.mvi)
    }
}
