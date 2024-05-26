plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "YChatCore"
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.ktor.negotiation)
            api(libs.ktor.serialization)
            api(libs.ktor.core)
            api(libs.ktor.logging)

        }

        androidMain.dependencies {
            implementation(libs.ktor.okhttp)

        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }

    }
}

android {
    namespace = "co.yml.ychat.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

