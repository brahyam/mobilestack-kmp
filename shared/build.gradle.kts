plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.compose.compiler)
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
            baseName = "shared"
            isStatic = true
            export(libs.decompose)
            export(libs.essenty.lifecycle)
            export(libs.calf)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            api(libs.decompose)
            api(libs.decompose.composeExtensions)
            implementation(libs.gitlive.firebase.common)
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.gitlive.firebase.auth)
            implementation(libs.gitlive.firebase.config)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.essenty.lifecycle)
            api(libs.napier)
            api(libs.calf)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.revenuecat.purchases.core)
            implementation(libs.revenuecat.purchases.datetime)
            implementation(libs.revenuecat.purchases.ui)
            implementation(libs.openai.client)
            implementation(libs.material.icons)
            implementation(projects.camera)
            implementation(projects.imagePicker)
        }
        androidMain.dependencies {
            implementation(libs.decompose.composeExtensions)
            api(project.dependencies.platform(libs.firebase.bom))
            api(libs.firebase.auth)
            api(libs.firebase.common)
            api(libs.firebase.analytics)
            api(libs.firebase.crashlytics)
            api(libs.firebase.config)
            implementation(libs.ktor.okhttp)
        }
        iosMain.dependencies {
            api(libs.decompose)
            api(libs.essenty.lifecycle)
            implementation(libs.ktor.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        // RevenueCat uses generated Kotlin bindings for native code on iOS
        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
    }

    task("testClasses") // Fix for cannot locate tasks that match ':shared:testClasses'
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.zenithapps.mobilestack.resources"
    generateResClass = always
}

android {
    namespace = "com.zenithapps.mobilestack"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
