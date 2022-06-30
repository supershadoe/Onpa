pluginManagement {
    plugins {
        id("com.android.application") version "7.2.1"
        id("org.jetbrains.kotlin.android") version "1.7.0"
        id("com.mikepenz.aboutlibraries.plugin") version "10.3.1"
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    versionCatalogs {
        create("androidxLibs") {
            library("appcompat", "androidx.appcompat:appcompat:1.4.2")
            library("core-ktx", "androidx.core:core-ktx:1.8.0")
            library(
                "constrainedLayout",
                "androidx.constraintlayout:constraintlayout:2.1.4"
            )
            library(
                "preference-ktx", "androidx.preference:preference-ktx:1.2.0"
            )

            bundle("libs", listOf(
                "appcompat", "core-ktx", "constrainedLayout", "preference-ktx"
            ))
        }

        create("googleLibs") {
            library(
                "material", "com.google.android.material:material:1.7.0-alpha02"
            )
        }

        create("aboutLibrariesLibs") {
            // Also don't forget to change in plugins
            version("aboutLibraries", "10.3.1")

            library("core", "com.mikepenz", "aboutlibraries-compose")
                .versionRef("aboutLibraries")
            library("compose", "com.mikepenz", "aboutlibraries-compose")
                .versionRef("aboutLibraries")

            bundle("libs", listOf("core", "compose"))
        }

        create("composeLibs") {
            version("material", "1.2.0-rc03")
            version("material3", "1.0.0-alpha14")

            library(
                "activity-compose",
                "androidx.activity:activity-compose:1.6.0-alpha05"
            )
            library("compose-ui", "androidx.compose.ui", "ui")
                .versionRef("material")
            library(
                "compose-ui-tooling-preview",
                "androidx.compose.ui", "ui-tooling-preview"
            ).versionRef("material")
            library(
                "compose-material", "androidx.compose.material", "material"
            ).versionRef("material")
            library(
                "compose-material3", "androidx.compose.material3", "material3"
            ).versionRef("material3")

            library("compose-ui-tooling", "androidx.compose.ui", ":ui-tooling")
                .versionRef("material3")

            bundle("libs-normal", listOf(
                "activity-compose", "compose-ui", "compose-ui-tooling-preview",
                "compose-material", "compose-material3"
            ))
            bundle("libs-debug", listOf("compose-ui-tooling"))
        }
    }
}

rootProject.name = "Onpa"
