import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("io.realm.kotlin") version "1.16.0"
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.tstudioz.fax.fme"
        minSdk = 26
        targetSdk = 34
        versionCode = 26
        versionName = "3.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val localPropFileExists = File(rootDir, "local.properties").isFile

    signingConfigs {
        create("release") {
            storeFile = file("./../keystore.jks")
            storePassword = System.getenv("RELEASE_SIGNING_PASSWORD")
            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
        }
        if (localPropFileExists) {
            create("releaseDebug") {
                val localProperties = Properties().apply { load(File(rootDir, "local.properties").inputStream()) }
                storeFile = file("./../keystore.jks")
                storePassword = localProperties.getProperty("RELEASE_SIGNING_PASSWORD")
                keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS")
                keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        if (localPropFileExists) {
            create("releaseDebug") {
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
                signingConfig = signingConfigs.getByName("releaseDebug")
            }
        }

        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            isDebuggable = true
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
}

dependencies {

    val koinVersion = "4.0.0"
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinVersion")
    implementation("androidx.compose.material3:material3-android:1.2.1")

    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.legacy:legacy-preference-v14:1.0.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.github.doctoror.particlesdrawable:library:2.0.2")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.9.0")


    val lifecycleVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("io.realm.kotlin:library-base:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material:material:1.6.8")
    val composeVersion = "1.6.7"

    implementation("androidx.activity:activity-compose:1.9.0")

    //compose livedata state
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    //EncryptedSharedPreferences
    implementation("androidx.security:security-crypto:1.0.0")

    //pull to refresh compose
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-android:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.8.2")

    //choose calendar
    implementation("com.kizitonwose.calendar:compose:2.6.0")

    //weather deserialise
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.google.code.gson:gson:2.11.0")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.findbugs:jsr305:3.0.2")
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

tasks.register("getBuildVersionNumber") {
    println(android.defaultConfig.versionCode)
}

tasks.register("getNextBuildVersionNumber") {
    println(android.defaultConfig.versionCode?.plus(1) ?: -1)
}

tasks.register("getAppVersionName") {
    println(android.defaultConfig.versionName)
}
