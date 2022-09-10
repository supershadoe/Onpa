pluginManagement {
    plugins {
        id("com.android.application") version "7.2.2" apply false
        id("org.jetbrains.kotlin.android") version "1.7.10" apply false
        id("com.mikepenz.aboutlibraries.plugin") version "10.3.1" apply false
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    versionCatalogs {
        create("androidxLibs") {
            library("core-ktx", "androidx.core:core-ktx:1.8.0")
            library("core-splashscreen",
                "androidx.core:core-splashscreen:1.0.0-rc01"
            )
            library("datastore-prefs",
                "androidx.datastore:datastore-preferences:1.0.0"
            )
            library(
                "media", "androidx.media:media:1.6.0"
            )

            bundle("libs", listOf(
                "core-ktx", "core-splashscreen", "datastore-prefs", "media"
            ))
        }

        create("googleLibs") {
            version("accompanist", "0.24.13-rc")

            // This lib exists only for being used as post-SplashScreen theme
            library("material",
                "com.google.android.material:material:1.7.0-alpha03"
            )
            library(
                "ac-systemuicontroller",
                "com.google.accompanist", "accompanist-systemuicontroller"
            ).versionRef("accompanist")
            bundle("accompanist", listOf("ac-systemuicontroller"))
        }

        create("aboutLibs") {
            // Also don't forget to change in plugins
            version("aboutLibraries", "10.4.0")

            library("core", "com.mikepenz", "aboutlibraries-core")
                .versionRef("aboutLibraries")
        }

        create("composeLibs") {
            version("material", "1.3.0-alpha01")
            version("material3", "1.0.0-alpha14")

            library(
                "activity-compose",
                "androidx.activity:activity-compose:1.6.0-alpha05"
            )
            library("compose-ui", "androidx.compose.ui", "ui")
                .versionRef("material")
            library(
                "compose-material3", "androidx.compose.material3", "material3"
            ).versionRef("material3")

            bundle("libs-normal", listOf(
                "activity-compose", "compose-ui", "compose-material3"
            ))
        }
    }
}

rootProject.name = "Onpa"
