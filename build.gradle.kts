plugins {
    id("com.android.application") version "7.2.1"
    id("org.jetbrains.kotlin.android") version "1.7.0"
    id("com.mikepenz.aboutlibraries.plugin") version "10.1.0"
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "me.supershadoe.onpa"
        minSdk = 24
        targetSdk =  31
        versionCode = 5
        versionName = "1.0.0-new"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

aboutLibraries {
    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.LINK
}

dependencies {
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.compose.ui:ui:1.3.0-alpha01")
    implementation("androidx.compose.ui:ui-tooling-preview:1.3.0-alpha01")
    implementation("androidx.compose.material:material:1.3.0-alpha01")
    implementation("androidx.compose.material3:material3:1.0.0-alpha14")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.mikepenz:aboutlibraries-core:10.1.0")
    implementation("com.mikepenz:aboutlibraries-compose:10.1.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.3.0-alpha01")
}