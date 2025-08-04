import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
            implementation("com.google.firebase:firebase-auth-ktx:22.1.0")
            implementation("com.google.firebase:firebase-firestore-ktx:24.2.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
            implementation("com.airbnb.android:lottie-compose:6.0.0")
        }
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation("dev.gitlive:firebase-app:2.1.0")       // מכיל את ה‑Firebase object + initialize
            implementation("dev.gitlive:firebase-auth:2.1.0")      // אימות באמצעות מייל/סיסמה
            implementation("dev.gitlive:firebase-firestore:2.1.0")
            implementation("dev.gitlive:firebase-app:2.1.0")
            implementation("dev.gitlive:firebase-auth:2.1.0")
            implementation("dev.gitlive:firebase-firestore:2.1.0")
            implementation("dev.gitlive:firebase-common:2.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.example.project.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
