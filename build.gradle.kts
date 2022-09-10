plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.mikepenz.aboutlibraries.plugin")
}

@Suppress("UnstableApiUsage")
android {
    compileSdk = 33

    defaultConfig {
        applicationId = "me.supershadoe.onpa"
        minSdk = 23
        targetSdk = 33
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
        buildConfig = false
        aidl = false
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0-beta01"
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

aboutLibraries {
    duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.LINK
}

dependencies {
    implementation(androidxLibs.bundles.libs)
    implementation(googleLibs.material)
    implementation(googleLibs.bundles.accompanist)
    implementation(aboutLibs.core)
    implementation(composeLibs.bundles.libs.normal)
}
