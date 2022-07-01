plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.mikepenz.aboutlibraries.plugin")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
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
    implementation(aboutLibrariesLibs.bundles.libs)
    implementation(composeLibs.bundles.libs.normal)
    debugImplementation(composeLibs.bundles.libs.debug)
}