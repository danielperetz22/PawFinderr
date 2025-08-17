import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    id("app.cash.sqldelight") version "2.0.2"

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
            implementation(libs.sqldelight.android)
            implementation("app.cash.sqldelight:android-driver:2.0.2")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
            implementation("com.google.firebase:firebase-auth-ktx:22.1.0")
            implementation("com.google.firebase:firebase-firestore-ktx:24.2.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")
            implementation("com.airbnb.android:lottie-compose:6.0.0")
            implementation("io.insert-koin:koin-android:4.0.4")
            implementation("io.insert-koin:koin-androidx-compose:4.0.4")
            implementation("com.google.android.gms:play-services-location:21.3.0")
            implementation("androidx.room:room-runtime:2.6.1")
            implementation("androidx.room:room-ktx:2.6.1")

        }
        commonMain.dependencies {
            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
            implementation(libs.sqldelight.runtime)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            implementation("dev.gitlive:firebase-app:2.1.0")
            implementation("dev.gitlive:firebase-auth:2.1.0")
            implementation("dev.gitlive:firebase-app:2.1.0")
            implementation("dev.gitlive:firebase-firestore:2.1.0")
            implementation("dev.gitlive:firebase-common:2.1.0")
            api("io.insert-koin:koin-core:4.0.4")
            implementation("io.insert-koin:koin-compose:4.0.4")
            implementation("io.insert-koin:koin-test:4.0.4")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
            implementation("dev.gitlive:firebase-firestore:1.13.1")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.0.2")
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
sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("org.example.project.data.report")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}
