import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version libs.versions.kotlin
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 35
    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        debug {
            isMinifyEnabled = false
        }
    }

    namespace = "com.tstudioz.fax.fme"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles = listOf(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.material3.android)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.preference)
    implementation(libs.legacy.preference.v14)
    implementation(libs.browser)
    implementation(libs.okhttp)
    implementation(libs.jsoup)
    implementation(libs.particlesdrawable.library)
    implementation(libs.persistentcookiejar)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    implementation(libs.fragment.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kizitownose.calendar)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)
    implementation(libs.androidx.security.crypto)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.ui.text.google.fonts)
    implementation(libs.dotsindicator)
    implementation(libs.glide)
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
